package edu.uiowa.webapp;

/*
 * #%L
 * Protogen
 * %%
 * Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import edu.uiowa.loaders.generic;

public class ClayLoader extends generic implements DatabaseSchemaLoader {
    enum mode {DOMAIN, TABLE};
	
	Database currentDatabase = null;
	Schema currentSchema = null;
	Domain currentDomain = null;
	Entity currentEntity = null;
	Attribute currentAttribute = null;
	Relationship currentRelationship = null;
	
	private static final Log log = LogFactory.getLog(ClayLoader.class);

	mode currentMode = mode.DOMAIN;
	private String referencedEntityName;
	
	public ClayLoader() {
		super();
		debug = true;
		verbose = true;
	}

    public void startElement (String uri, String name, String qName, Attributes atts) {
		if (debug) {
			log.debug("Start clay element: " + name);
			for (int i = 0; i < atts.getLength(); i++){
				log.debug("\tattribute " + i + ": " + atts.getQName(i) + " > " + atts.getValue(i));
			}
		}
		if (qName.equals("database-model")) {
			currentDatabase = new Database();
			//TODO this is a temporary patch to support forward linkage matching of dominant entities
			Generator.theDatabase = currentDatabase;
			currentDatabase.setLabel(getAttByName(atts, "name"));
			currentDatabase.setUid(getAttByName(atts, "uid"));
			if (verbose){
				log.debug("database: " + currentDatabase.getLabel() + "\tuid: " + currentDatabase.getUid());
			}
		} else if (qName.equals("schema")) {
			currentSchema = new Schema();
			currentSchema.setLabel(getAttByName(atts,"name"));
			currentSchema.setUid(getAttByName(atts, "uid"));
			if (verbose){
				log.debug("\tschema: " + currentSchema.getLabel() + "\tuid: " + currentSchema.getUid());
			}
			currentDatabase.getSchemas().add(currentSchema);
		} else if (qName.equals("domain")) {
		    currentDomain = new Domain();
		    currentDomain.setLabel(getAttByName(atts,"name"));
			currentDomain.setUid(getAttByName(atts, "uid"));
			if (verbose){
				log.debug("\t\tentity: " + currentDomain.getLabel() + "\tuid: " + currentDomain.getUid());
			}
			currentSchema.getDomains().add(currentDomain);
			currentMode = mode.DOMAIN;
        } else if (qName.equals("table")) {
            currentEntity = new Entity();
            currentEntity.setSchema(currentSchema);
            currentEntity.setLabel(getAttByName(atts,"name"));
            currentEntity.setUid(getAttByName(atts, "uid"));
            if (verbose)
                log.debug("\t\tentity: " + currentEntity.getLabel() + "\tuid: " + currentEntity.getUid());
            currentSchema.getEntities().add(currentEntity);
            currentMode = mode.TABLE;
		} else if (qName.equals("column")) {
			currentAttribute = new Attribute();
			currentAttribute.setLabel(getAttByName(atts,"name"));
            currentAttribute.setDomain(currentSchema.getDomainByLabel(getAttByName(atts,"domain")));
			currentAttribute.setType(getAttByName(atts, "alias"));
            currentAttribute.setRemarks(getAttByName(atts, "remarks"));
            currentAttribute.setUid(getAttByName(atts, "uid"));
			if (getAttByName(atts,"mandatory").equals("true"))
				currentAttribute.setMandatory(true);
            if (getAttByName(atts,"auto-increment").equals("true"))
                currentAttribute.setAutoIncrement(true);
			if (debug){
				log.debug("\t\t\tattribute: " + currentAttribute.getLabel() + "\tuid: " + currentAttribute.getUid() + "\tmandatory: " + currentAttribute.isMandatory() + "\tauto-increment: " + currentAttribute.isAutoIncrement() + "\tdomain: " + currentAttribute.getDomain());
			}
			currentEntity.getAttributes().add(currentAttribute);
		} else if (qName.equals("data-type")) {
			if (currentMode == mode.TABLE) {
			    currentAttribute.setType(getAttByName(atts,"name"));
			    if (verbose){
			    	log.debug("\t\t\tattribute: " + currentAttribute.getLabel() + "\tuid: " + currentAttribute.getUid() + "\tmandatory: " + currentAttribute.isMandatory() + "\ttype: " + currentAttribute.getType());
			    }
			} else {
			    currentDomain.setType(getAttByName(atts,"name"));
                if (verbose){
                	log.debug("\t\t\tdomain: " + currentDomain.getLabel() + "\tuid: " + currentDomain.getUid() + "\tmandatory: " + currentDomain.isMandatory() + "\ttype: " + currentDomain.getType());
                }
			}
		} else if (qName.equals("primary-key-column")) {
		    currentAttribute = currentEntity.getAttributeByLabel(getAttByName(atts,"name"));
		    currentAttribute.setPrimary(true);
		    currentEntity.getPrimaryKeyAttributes().add(currentAttribute);
		    if (verbose){
		    	log.debug("\t\t\tprimary key: " + currentAttribute.getLabel());
		    }
        } else if (qName.equals("foreign-key")) {
        	Schema targetSchema = currentDatabase.getSchemaByName(getAttByName(atts, "referenced-table-schema"));
            currentRelationship = new Relationship();
            currentRelationship.setSourceEntity(targetSchema.getEntityByLabel(getAttByName(atts, "referenced-table")));
            referencedEntityName = getAttByName(atts, "referenced-table");
            currentRelationship.setSourceEntityName(getAttByName(atts, "referenced-table"));
            currentRelationship.setTargetEntity(currentEntity);
            currentRelationship.setUid(getAttByName(atts, "uid"));
            currentEntity.setParent(currentRelationship);
            currentSchema.getRelationships().add(currentRelationship);
            if (verbose){
            	log.debug("\t\t\tforeign key: " + getAttByName(atts,"name")  + "\tuid: " + getAttByName(atts, "uid")  + "\treferenced table: " + getAttByName(atts, "referenced-table"));
            }
        } else if (qName.equals("foreign-key-column")) {
        	
            currentRelationship.setForeignReferencedAttributeMapping(getAttByName(atts,"column-name"), getAttByName(atts, "referenced-key-column-name"));
            currentEntity.getAttributeByLabel(getAttByName(atts,"column-name")).setForeign(true); 
            currentEntity.getAttributeByLabel(getAttByName(atts,"column-name")).setReferencedEntityName(referencedEntityName);
            referencedEntityName = null;
            
            if (verbose) {
            	log.debug("\t\t\tforeign key column: " + getAttByName(atts,"column-name")  + "\treferenced column: " + getAttByName(atts, "referenced-key-column-name"));
            }
		}
    }
    
    public void endElement(String uri, String name, String qName) {
        if (debug) {
            log.debug("End element: " + name);
        }
        if (qName.equals("database-model")) {
            // clean up forward references in the foreign key declarations
            for (int j = 0; j < currentDatabase.getSchemas().size(); j++) {
                currentSchema = currentDatabase.getSchemas().elementAt(j);
                for (int i = 0; i < currentSchema.getRelationships().size(); i++) {
                    currentRelationship = currentSchema.getRelationships().elementAt(i);
                    if (currentRelationship.sourceEntity == null) {
                        if (debug)
                            log.debug("source entity is null for " + currentRelationship.getSourceEntityName() + " -> " + currentSchema.getEntityByLabel(currentRelationship.getSourceEntityName()));
                        currentRelationship.setSourceEntity(currentSchema.getEntityByLabel(currentRelationship.getSourceEntityName()));
                    }
                    currentRelationship.getSourceEntity().setChild(currentRelationship);
                }
                // generate our partitioned pools of key attributes
                for (int i = 0; i < currentSchema.getEntities().size(); i++) {
                    currentEntity = currentSchema.getEntities().elementAt(i);
                    currentEntity.generateParentKeys();
                    currentEntity.generateSubKeys();
                    currentEntity.matchRemarks();
                }
            }
            // map over to Java-style element labels
            currentDatabase.relabel();
        }
    }
    public Database getDatabase() {
        return currentDatabase;
    }

	@Override
	public void run(Properties props) throws Exception {
		// TODO Auto-generated method stub
	}
}
