package edu.uiowa.icts.protogen.springhibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uiowa.webapp.Attribute;

public class ClassVariable
{
	public enum RelationshipType {
		ONETOMANY, ONETOONE, MANYTOONE, MANYTOMANY, NONE
	}

	public enum AttributeType {
		COMPOSITEKEY, PRIMARYKEY, FOREIGNPRIMARYKEY, LOCALATTRIBUTE, FOREIGNATTRIBUTE, CHILD
	}

	private String modifier = "";
	private String type = "";
	private String identifier = "";
	private String initializer = "";
	private List<String> getterAnnotations = new ArrayList<String>();
	private String comment = "";
	private AttributeType attribType;
	private RelationshipType relationshipType;
	private Attribute attribute = null;
	private ClassVariable referenedClassVariable;
	private DomainClass domainClass = null;
	private String targetEntityClassLabel;
	private String mappedByVariableLabel;
	private String joinColumnLabel;

	public String getTargetEntityClassLabel() {
		return targetEntityClassLabel;
	}

	public void setTargetEntityClassLabel( String targetEntityClassLabel ) {
		this.targetEntityClassLabel = targetEntityClassLabel;
	}

	public String getMappedByVariableLabel() {
		return mappedByVariableLabel;
	}

	public void setMappedByVariableLabel( String mappedByVariableLabel ) {
		this.mappedByVariableLabel = mappedByVariableLabel;
	}

	public String getJoinColumnLabel() {
		return joinColumnLabel;
	}

	public void setJoinColumnLabel( String joinColumnLabel ) {
		this.joinColumnLabel = joinColumnLabel;
	}

	public ClassVariable getReferenedClassVariable() {
		return referenedClassVariable;
	}

	public void setReferenedClassVariable( ClassVariable referenedClassVariable ) {
		this.referenedClassVariable = referenedClassVariable;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute( Attribute attribute ) {
		this.attribute = attribute;
	}

	public ClassVariable()
	{
		this.modifier = "";
		this.type = "";
		this.identifier = "";
		this.initializer = "";

	}

	public ClassVariable( String modifier, String type, String identifier, String initializer )
	{
		this.modifier = modifier;
		setType( type );
		this.identifier = identifier;
		this.initializer = initializer;

	}

	public ClassVariable( String modifier, String type, String identifier )
	{
		this.modifier = modifier;
		setType( type );
		this.identifier = identifier;
		this.initializer = "";
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier( String modifier ) {
		this.modifier = modifier;
	}

	public String getType() {
		if ( type.equals( "Object" ) )
			return "byte[]";

		if ( domainClass != null && domainClass.isNullablePrimitives() )
		{
			if ( type.equalsIgnoreCase( "int" ) )
				return "Integer";
			else if ( type.equalsIgnoreCase( "boolean" ) )
				return "Boolean";
			else if ( type.equalsIgnoreCase( "float" ) )
				return "Float";
			else if ( type.equalsIgnoreCase( "double" ) )
				return "Double";
			else if ( type.equalsIgnoreCase( "long" ) )
				return "Long";
		}

		return type;
	}

	public void setType( String type ) {
		if ( type.equals( "Object" ) )
			this.type = "byte[]";
		else
			this.type = type;
	}

	public String getIdentifier() {

		return identifier;
	}

	public void setIdentifier( String identifier ) {
		this.identifier = identifier;
	}

	public String getInitializer() {
		return initializer;
	}

	public boolean isPrimary()
	{
		return attribType == AttributeType.COMPOSITEKEY || attribType == AttributeType.PRIMARYKEY || attribType == AttributeType.FOREIGNPRIMARYKEY;
	}

	public void setInitializer( String initializer ) {
		this.initializer = initializer;
	}

	public List<String> getGetterAnnotations() {
		return getterAnnotations;
	}

	public void setGetterAnnotations( List<String> getterAnnotations ) {
		this.getterAnnotations = getterAnnotations;
	}

	public String getComment() {
		return comment;
	}

	public void setComment( String comment ) {
		this.comment = comment;
	}

	public AttributeType getAttribType() {
		return attribType;
	}

	public void setAttribType( AttributeType attribType ) {
		this.attribType = attribType;
	}

	public RelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType( RelationshipType relationshipType ) {
		this.relationshipType = relationshipType;
	}

	public DomainClass getDomainClass() {
		return domainClass;
	}

	public void setDomainClass( DomainClass domainClass ) {
		this.domainClass = domainClass;
	}

	public String getUpperIdentifier()
	{
		return identifier.substring( 0, 1 ).toUpperCase() + identifier.substring( 1 );
	}

	public String getLowerIdentifier()
	{
		return identifier.substring( 0, 1 ).toLowerCase() + identifier.substring( 1 );
	}

	public String toDeclaration()
	{

		return modifier + " " + getType() + " " + identifier + initializer + ";\n";
	}

	public String toAnnotationDeclaration() {
		String annotation = "";
		if ( type.equals( "Date" ) ) {
			annotation = "@DateTimeFormat( pattern = \"yyyy-MM-dd\" )\n";
			if ( attribute != null ) {
				if ( attribute.getType().equalsIgnoreCase( "timestamp" ) ) {
					annotation = " @DateTimeFormat( pattern = \"yyyy-MM-dd hh:mm:ss\" )\n";
				}
			}
		}
		return annotation;
	}

	public String getterAnnotationsToString( String indent ) {
		String output = "";
		Iterator<String> iter = getterAnnotations.iterator();
		while ( iter.hasNext() ) {
			output += indent + iter.next() + "\n";
		}
		return output;
	}

	String toGetter( String indent ) {
		String output = "";
		if (this.attribType == AttributeType.CHILD){
			output += indent + "@JsonIgnore\n" ;
		}
		if ( attribType == AttributeType.CHILD && getReferenedClassVariable() != null && relationshipType != RelationshipType.MANYTOMANY ) {
			if ( domainClass.getIdentifier().equals( getAttribute().getEntity().getUnqualifiedLabel() ) ) {
				output += indent + "@OneToMany(fetch = FetchType.LAZY, mappedBy = \"" + getReferenedClassVariable().getIdentifier() + "\",targetEntity = " + getAttribute().getEntity().getUnqualifiedLabel() + ".class)\n";
			} else {
				output += indent + "@OneToMany(fetch = FetchType.LAZY, mappedBy = \"" + getReferenedClassVariable().getDomainClass().getLowerIdentifier() + "\", targetEntity = " + getAttribute().getEntity().getUnqualifiedLabel() + ".class)\n";
			}
		} else {
			output += getterAnnotationsToString( indent );
		}

		if ( attribType == AttributeType.COMPOSITEKEY ) {
			output += indent + "public " + getType() + " get" + "Id(){\n";
		} else {
			output += indent + "public " + getType() + " get" + getUpperIdentifier() + "(){\n";
		}
		output += indent + indent + "return " + identifier + ";\n";
		output += indent + "}\n";
		return output;
	}

	String toSetter( String indent ) {
		String output = "\n";
		if ( attribType == AttributeType.COMPOSITEKEY ) {
			output += indent + "public void setId( " + type + " " + identifier + " ){\n";
		} else {
			output += indent + "public void set" + getUpperIdentifier() + "(" + getType() + " " + identifier + "){\n";
		}

		output += indent + indent + "this." + identifier + " = " + identifier + ";\n";
		output += indent + "}\n";

		if ( type.equalsIgnoreCase( "date" ) ) {
			output += getDateStringSetter( indent );
		}
		return output;
	}

	String getDateStringSetter( String indent ) {
		String output = "\n";
		output += indent + "@JsonIgnore\n";
		output += indent + "public void set" + getUpperIdentifier() + "( String " + identifier + " ){\n";
		output += indent + indent + "try{\n";
		output += indent + indent + indent + "DateFormat formatter = new SimpleDateFormat( \"MM/dd/yyyy\" );\n";
		output += indent + indent + indent + "formatter.setLenient(true);\n";
		output += indent + indent + indent + "this." + identifier + " = formatter.parse(" + identifier + ");\n";
		output += indent + indent + "} catch ( ParseException e ) { \n";
		output += indent + indent + indent + "log.error( \"ParseException setting date for " + getUpperIdentifier() + "\", e );\n";
		output += indent + indent + "}\n";
		output += indent + "}\n";
		return output;
	}

}