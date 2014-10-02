package edu.uiowa.webapp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.loaders.DBConnect;
import edu.uiowa.loaders.PropertyLoader;

public class JDBCLoader implements DatabaseSchemaLoader {

	private Database database = null;
	private Connection conn = null;
	// private String currentSchema=null;

	public JDBCLoader() {

	}

	private static final Log log = LogFactory.getLog(JDBCLoader.class);



	public Database getDatabase() {
		return database;
	}

	public void run(Properties prop) throws Exception {

		String schema = prop.getProperty("db.schema");

		try {
			connect(prop);
		} catch ( Exception e ){
			log.error("could not connect to database, exiting",e);
			return;
		}
		
		DatabaseMetaData dbMeta = conn.getMetaData();

		log.debug("Database: "+dbMeta.getDatabaseProductName()+" "+dbMeta.getDatabaseProductVersion());
		log.debug("JDBC Version: "+dbMeta.getDriverName()+"."+dbMeta.getDriverVersion());

		setupDatabase(dbMeta);

		/*
		 * if schema is defined, use, otherwise run on all schemas
		 */
		if(schema != null) {
			Schema s = createSchema(dbMeta, schema);
			database.getSchemas().add(s);
		} else {
			ResultSet schemasRS = dbMeta.getSchemas();
			log.debug("All Schemas");
			while(schemasRS.next()) {
				Schema s = createSchema(dbMeta, schemasRS.getString(1));
				database.getSchemas().add(s);
			}
		}

		updateForeignKeys(dbMeta);

		for(Schema s : database.getSchemas()) {
			for(Entity e : s.getEntities()) {
				for( Attribute a : e.getAttributes() ){
					if( a.getForeignAttribute() != null ){
						log.debug(e.label + " - " + a.getForeignAttribute().getEntity().label);
					}
				}
			}
		}
		
		for(Schema s:database.getSchemas()) {
			for (int i = 0; i < s.getRelationships().size(); i++) {
				Relationship currentRelationship = s.getRelationships().elementAt(i);
				if (currentRelationship.sourceEntity == null) {
					log.debug("source entity is null for " + currentRelationship.getSourceEntityName() + " -> " + s.getEntityByLabel(currentRelationship.getSourceEntityName()));
					currentRelationship.setSourceEntity(s.getEntityByLabel(currentRelationship.getSourceEntityName()));
				}
				currentRelationship.getSourceEntity().setChild(currentRelationship);
			}
			
			for(Entity e : s.getEntities()) {
				e.generateParentKeys();
				e.generateSubKeys();
				e.matchRemarks();
			}
		}
		
		for(Schema s : database.getSchemas()) {
			for(Entity e:s.getEntities()) {
				for( Attribute a : e.getAttributes() ){
					if( a.getChildAttributes() != null ){
						for( Attribute aa : a.getChildAttributes() ){
							log.debug(aa.label);
						}
					}
				}
			}
		}
		
		conn.close();
	}

	public void run(String filename) throws Exception {
		Properties prop = PropertyLoader.loadProperties(filename);
		run(prop);
	}

	private void updateForeignKeys(DatabaseMetaData dbMeta) throws SQLException
	{

		log.debug("Updating foreign keys");
		if(dbMeta.getDatabaseProductName().equalsIgnoreCase("oracle"))
		{

			log.debug("...using custom oracle lookup");
			Schema schema=null;
			if(database.getSchemas().size()==1)
				schema = database.getSchemas().iterator().next();
			log.debug("schemaname:"+schema.getLabel());

			String query = ""
				+ " select"
				+ "    col.table_name, col.column_name,"
				+ "    rel.table_name,rel.column_name"
				+ " from "
				+ "    all_tab_columns col"
				+ "    join all_cons_columns con "
				+ "      on col.table_name = con.table_name "
				+ "     and col.column_name = con.column_name"
				+ "    join all_constraints cc "
				+ "      on con.constraint_name = cc.constraint_name"
				+ "    join all_cons_columns rel "
				+ "      on cc.r_constraint_name = rel.constraint_name "
				+ "     and con.position = rel.position"
				+ " where "
				+ "    cc.constraint_type = 'R'";

			log.debug(".......executing query");
			Statement statement = conn.createStatement();
			statement.execute(query);
			log.debug(".......query complete");

			int count =0;
			ResultSet rs= statement.getResultSet();

			if(count==0)
				printColumns(rs);
			while(rs.next())
			{
				log.debug(""+rs);

				String pkschema= schema.getLabel();
				String pktable= rs.getString(1);
				String pkcolumn = rs.getString(2);
				String fkschema=  schema.getLabel();
				String fktable= rs.getString(3);
				String fkcolumn = rs.getString(4);
				log.debug(".........."+pktable+"."+pkcolumn+" -> "+fktable+"."+fkcolumn);

				if(pktable.equalsIgnoreCase(fktable) && pkcolumn.equalsIgnoreCase(fkcolumn))
					continue;
				Schema pks = getSchema(pkschema);
				Entity pke = getEntity(pks, pktable);
				Attribute  pka = getAttribute(pke, pkcolumn);


				//	pka.set


				Schema fks = getSchema(fkschema);
				Entity fke = getEntity(fks, fktable);
				Attribute  fka = getAttribute(fke, fkcolumn);
				

				pka.setParentAttribute(fka);
				fka.getChildAttributes().add(pka);

				Relationship r = new Relationship();
				r.setSourceEntity(fke);
				r.setSourceEntityName(fke.getLabel());
				r.setTargetEntity(pke);
				r.setForeignReferencedAttributeMapping(pkcolumn, fkcolumn);
				r.setLabel("foreign_key");
				pka.setReferencedEntityName(fktable);
			//	r.setRelationshipCardinality(CardinalityEnum.ONE_TO_MANY);
				log.debug("Relationship:"+r);
				schema.getRelationships().add(r);

			}
			count++;
			rs.close();
			return;
		} else if(dbMeta.getDatabaseProductName().equalsIgnoreCase("Teiid Server")) {
			log.debug("...using teiid server lookup");
			
			ArrayList<String> rels = new ArrayList<String>();
			
			String query = "select PKTABLE_SCHEM, PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME from SYS.ReferenceKeyColumns";
			Statement statement = conn.createStatement();
			statement.execute(query);
			ResultSet rs = statement.getResultSet();
			while(rs.next()){
				String pkschema = rs.getString(1);
				String pktable  = rs.getString(2);
				String pkcolumn = rs.getString(3);
				String fkschema = rs.getString(4);
				String fktable  = rs.getString(5);
				String fkcolumn = rs.getString(6);
				
				log.debug(".........."+pktable+"."+pkcolumn+" -> "+fktable+"."+fkcolumn);

				if(pktable.equalsIgnoreCase(fktable) && pkcolumn.equalsIgnoreCase(fkcolumn))
					continue;
				
				Schema    pks = getSchema(pkschema);
				Entity    pke = getEntity(pks, pktable);
				Attribute pka = getAttribute(pke, pkcolumn);

				Schema 	  fks = getSchema(fkschema);
				Entity 	  fke = getEntity(fks, fktable);
				Attribute fka = getAttribute(fke, fkcolumn);

				Relationship r = new Relationship();
				r.setSourceEntity(pke);
				r.setSourceEntityName(pke.getLabel());
				
				r.setTargetEntity(fke);
				
				r.setLabel("foreign_key");
				
				if(!rels.contains(pktable+"."+pkcolumn+"->"+fktable+"."+fkcolumn)){
					rels.add(pktable+"."+pkcolumn+"->"+fktable+"."+fkcolumn);
					
					fke.setParent(r);
					fks.getRelationships().add(r);
					
					r.setForeignReferencedAttributeMapping(fka.getLabel(),pka.getLabel());
				}else{
					log.debug("relationship exists: "+pktable+"."+pkcolumn+" -> "+fktable+"."+fkcolumn); 
				}
				
				fka.setReferencedEntityName(pktable);
				
				log.debug("Relationship:"+r);
			}
			rs.close();
			return;
		}

		int count =0;
		for(Schema s:database.getSchemas())
			for(Entity e:s.getEntities()) {
				
				log.debug(s.getLabel());
				log.debug(e.getLabel());
				
				ResultSet rs = dbMeta.getExportedKeys(null, s.getLabel(), e.getLabel());

				if(count==0) {
					printColumns(rs);
				}
				
				ArrayList<String> rels = new ArrayList<String>();
				while(rs.next()) {

					/*
					pktable_cat = null
					pktable_schem = rrlorentzen
					pktable_name = movie
					pkcolumn_name = movie_id
					fktable_cat = null
					fktable_schem = rrlorentzen
					fktable_name = review
					fkcolumn_name = movie_id
					key_seq = 1
					update_rule = 3
					delete_rule = 3
					fk_name = fk_review_2
					pk_name = movie_pkey
					deferrability = 7
					*/
					
					log.debug("------------------------------------------");
					for( int i=1; i <= rs.getMetaData().getColumnCount(); i++ ){
						log.debug(rs.getMetaData().getColumnLabel(i)+" = "+rs.getString(i));
					}
					log.debug("------------------------------------------");
					
					String pkschema = rs.getString("pktable_schem");
					String pktable= rs.getString("pktable_name");
					String pkcolumn = rs.getString("pkcolumn_name");
					
					String fkschema= rs.getString("fktable_schem");
					String fktable= rs.getString("fktable_name");
					String fkcolumn = rs.getString("fkcolumn_name");
					
					log.debug("...."+pktable+"."+pkcolumn+" -> "+fktable+"."+fkcolumn);
					
					if(pktable.equalsIgnoreCase(fktable) && pkcolumn.equalsIgnoreCase(fkcolumn))
						continue;

					Schema pks = getSchema(pkschema);
					Entity pke = getEntity(pks, pktable);
					Attribute  pka = getAttribute(pke, pkcolumn);

					Schema fks = getSchema(fkschema);
					Entity fke = getEntity(fks, fktable);
					Attribute  fka = getAttribute(fke, fkcolumn);

					fka.setForeignAttribute(pka);

					Relationship r = new Relationship();
					r.setSourceEntity(pke);
					r.setSourceEntityName(pke.getLabel());
					
					r.setTargetEntity(fke);
					
					r.setLabel("foreign_key");
					
					fke.setParent(r);
					fks.getRelationships().add(r);
					
					if(!rels.contains(pktable+"."+pkcolumn+"->"+fktable+"."+fkcolumn)){
						rels.add(pktable+"."+pkcolumn+"->"+fktable+"."+fkcolumn);
						
						fke.setParent(r);
						fks.getRelationships().add(r);
						
						r.setForeignReferencedAttributeMapping(fka.getLabel(),pka.getLabel());
					}else{
						log.debug("relationship exists: "+pktable+"."+pkcolumn+" -> "+fktable+"."+fkcolumn); 
					}
					
					fka.setReferencedEntityName(pktable);
					
					log.debug("Relationship:"+r);
				}
				count++;

				rs.close();
			}




	}

	private boolean relationshipExists(Relationship r, Vector<Relationship> relationships) {
		for (Relationship rl : relationships) {
			if(r.getSourceEntity().getLabel().equals(rl.getSourceEntity().getLabel()) && 
					r.getTargetEntity().getLabel().equals(rl.getTargetEntity().getLabel())){
				return true;
			}
		}
		return false;
	}

	private Schema getSchema(String schemaName)
	{
		for(Schema s:database.getSchemas())
		{
			if (s.getLabel().equalsIgnoreCase(schemaName))
				return s;

		}
		return null;


	}

	private Entity getEntity(Schema s, String name)
	{
		for(Entity e: s.getEntities())
		{


			if (e.getLabel().equalsIgnoreCase(name))
				return e;

		}
		return null;


	}

	private Attribute getAttribute(Entity e,String columnName)
	{
		for(Attribute a : e.getAttributes())
		{
			if ( a.getLabel().equalsIgnoreCase(columnName)){
				return a;
			}

		}
		return null;


	}

	private void setupDatabase(DatabaseMetaData dbMeta) throws SQLException
	{

		ResultSet rs = dbMeta.getCatalogs();
		database = new Database();
		log.debug("------------");
		log.debug("Database");
		while(rs.next())
		{
			log.debug(""+rs.getString(1));
			database.setLabel(rs.getString(1));
		}
		rs.close();


	}

	private Schema createSchema(DatabaseMetaData dbMeta, String label) throws SQLException {
		Schema schema = new Schema();
		schema.setLabel(label);
		String[] types = {"TABLE"};
		ResultSet rs = dbMeta.getTables(null, label, "%",types);
		log.debug("------------");
		// log.debug("Tables in "+schema);
		while(rs.next()) {
			String x = rs.getString(3);
			if(!x.endsWith("_pkey")){
				Entity e = createEntity(dbMeta, label, rs.getString(3));
				e.setSchema(schema);
				schema.getEntities().add(e);
			}else{
				log.error("This table may be an index: "+x+", code not being generated.");
			}
		}

		rs.close();
		return schema;
	}

	private Entity createEntity(DatabaseMetaData dbMeta, String schema, String tableLabel) throws SQLException
	{
		log.debug("...Table: "+tableLabel);

		Entity e = new Entity();
		e.setLabel(tableLabel);

		log.debug(schema);
		log.debug(tableLabel);
		
		ResultSet rs= dbMeta.getColumns(null, schema, tableLabel, "%");
		printColumns(rs);

		while(rs.next())
		{
			Attribute a = new Attribute();

			String aLabel = rs.getString(4);
			String type = rs.getString(6);
			log.debug("......"+aLabel+" type = "+type);



			if(getAttribute(e, aLabel) == null) {
				a.setLabel(aLabel);
				a.setType(type);
				a.setEntity(e);
				e.getAttributes().add(a);
			}

			log.debug("Creating new attribute" + "\n     label:   "+aLabel+ "\n     type:    "+type+ "\n     eLabel:  "+e.getUnqualifiedLabel());

		}
		rs.close();
		log.debug("......Primary Keys:");
		ResultSet rs2 = dbMeta.getPrimaryKeys(null, schema,tableLabel );
		printColumns(rs2);

		while(rs2.next())
		{
			String aLabel = rs2.getString(4);
			log.debug("......"+aLabel);
			for(Attribute a :e.getAttributes()) {
				if(aLabel.equalsIgnoreCase(a.getLabel())) {
					a.setPrimary(true);
					log.debug("..........is primary");
					boolean exists = false;
					for(Attribute aa : e.getPrimaryKeyAttributes()) {
						if(aa.getLabel().equalsIgnoreCase(aLabel)) {
							exists=true;
							continue;
						}
					}
					if(!exists) {
						log.debug("................adding");
						e.getPrimaryKeyAttributes().add(a);
					}
				}
			}
		}
		rs2.close();
		return e;
	}

	private void connect(Properties prop) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String connection_url= prop.getProperty("db.url");
		String username= prop.getProperty("db.username");
		String password= prop.getProperty("db.password");
		boolean ssl= Boolean.parseBoolean(prop.getProperty("db.ssl"));
		String driver= prop.getProperty("db.driver");
		log.debug(prop.toString());
		DBConnect dbConn = new DBConnect(driver,connection_url,username,password,ssl);
		dbConn.connect();
		conn = dbConn.getConn();



	}

	private void printColumns(ResultSet rs) throws SQLException {
		int count = rs.getMetaData().getColumnCount();
		if(log.isDebugEnabled()) {
			StringBuffer  temp = new StringBuffer("......Columns:");
			for (int i =0;i<count;i++) {
				temp.append(""+i+"-"+rs.getMetaData().getColumnName(i+1));
				if(i<count -1){
					temp.append(",");
				} else {
					temp.append("");
				}

			}
			log.debug(temp);
		}

	}
}