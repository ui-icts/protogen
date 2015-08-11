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

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Attribute extends ClayElement {

    @Override
	public String toString() {
		return "Attribute [domain=" + domain + ", sqlType=" + sqlType
				+ ", type=" + type + ", remarks=" + remarks + ", origLabel="
				+ origLabel + ", mandatory=" + mandatory + ", primary="
				+ primary + ", autoIncrement=" + autoIncrement + ", foreign="
				+ foreign + ", sequence=" + sequence + ", counter=" + counter
				+ ", sequenceName=" + sequenceName + ", dominantEntity="
				+ dominantEntity + ", referencedEntityName="
				+ referencedEntityName + ", referencedEntity="
				+ referencedEntity + ", entity=" + entity
				// + ", foreignAttribute=" + foreignAttribute
				+ ", childAttributes=" + childAttributes + "]";
	}

	private Domain domain = null;
	private String sqlType = null;
	private String type = null;
	private String remarks = null;
	private String origLabel = null;
	private boolean mandatory = false;
	private boolean primary = false;
	private boolean autoIncrement = false;
	private boolean foreign = false;
	private boolean sequence = false;
	private boolean counter = false;
	private String sequenceName = null;
	private Entity dominantEntity = null;
	private String referencedEntityName = null;
	private Entity referencedEntity = null;
	private Entity entity = null;
	private Attribute foreignAttribute = null;
	private Vector<Attribute> childAttributes = new Vector<Attribute>();
	
	private static final Log log = LogFactory.getLog(Attribute.class);

	

	public Vector<Attribute> getChildAttributes() {
		return childAttributes;
	}

	public void setChildAttributes(Vector<Attribute> childAttributes) {
		this.childAttributes = childAttributes;
	}

	public Attribute getParentAttribute() {
		
		return foreignAttribute;
	}
	public void setParentAttribute(Attribute a)
	{
		setForeign(true);
		foreignAttribute = a ;
	}
	public Attribute getForeignAttribute() {
		return foreignAttribute;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getReferencedEntity() {
		return referencedEntity;
	}

	public void setReferencedEntity(Entity referencedEntity) {
		this.referencedEntity = referencedEntity;
	}

	public String getReferencedEntityName() {
		return referencedEntityName;
	}

	public void setReferencedEntityName(String referencedEntityName) {
		this.referencedEntityName = referencedEntityName;
	}

	public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks.trim();
    }
    
    public void matchRemarks() {
        if (remarks == null)
            return;
        else if (remarks.startsWith("counter ")) {
            counter = true;
            String schemaName = remarks.substring(remarks.indexOf(' ') + 1, remarks.indexOf('.'));
            String entityName = remarks.substring(remarks.indexOf('.') + 1);
            log.debug("schema: " + schemaName + " - " + Generator.getDatabase().getSchemaByName(schemaName));
            log.debug("entity: " + entityName + " - " + Generator.getDatabase().getSchemaByName(schemaName).getEntityByLabel(entityName));
            dominantEntity = Generator.getDatabase().getSchemaByName(schemaName).getEntityByLabel(entityName);
        }
        else if (remarks.startsWith("sequence ")) {
            sequence = true;
            sequenceName = remarks.substring(remarks.indexOf(' ') + 1);
        }
    }
    
    public String getInitializer() {
        if (isInt() || isLong())
            return "0";
        else if (isDouble())
            return "0.0";
        else if (isFloat())
            return "0.0f";
        else if (isBoolean())
            return "false";
        else
        {
            return "null";
        }
    }

    public String getDefaultValue() {
        if (isInt())
            return "Sequence.generateID()";
        else if (isLong())
            return "0";
        else if (isDouble())
            return "0.0";
        else if (isFloat())
            return "0.0f";
        else if (isDateTime())
            return "new Date()";
        else if (isBoolean())
            return "false";
        else
            return "null";
    }



	public boolean isCounter() {
        return counter;
    }
    
    public Entity getDominantEntity() {
    	if(dominantEntity == null){
    		log.debug("DominantEntity is null");
    	}
        return dominantEntity;
    }

    public boolean isSequence() {
        return sequence;
    }
    
    public String getSequence() {
        return sequenceName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isForeign() {
        return foreign;
    }

    public void setForeign(boolean foreign) {
//    	StackTraceElement[] cause = Thread.currentThread().getStackTrace();
//    	for( StackTraceElement ste : cause ){
//    		log.debug(ste.getMethodName());
//    	}
//    	
//    	log.debug("Attribute.setForeign called");
        this.foreign = foreign;
    }

    public void setForeignAttribute(Attribute foreignAttribute) {
        setForeign(true);
        this.foreignAttribute = foreignAttribute;
    }

    public void relabel() {
    	log.debug("relabeling: label="+label +"   type="+type);
        sqlLabel = label;
        if (label.toLowerCase().equals("id"))
                label = "ID";
        else
            relabel(false);
        
        sqlType = Character.toUpperCase(type.charAt(0)) + type.toLowerCase().substring(1);
        if (type.toLowerCase().equals("int"))
            type = "int";
        else if (type.toLowerCase().equals("int identity"))
            type = "int";
        else if (type.toLowerCase().equals("smallint"))
            type = "int";
        else if (type.toLowerCase().equals("tinyint"))
            type = "int";
        else if (type.toLowerCase().equals("integer"))
            type = "int";
        else if (type.toLowerCase().equals("int4"))
            type = "int";
        else if (type.toLowerCase().equals("numeric"))
            type = "float"; // could be double
        else if (type.toLowerCase().equals("money"))
            type = "float"; // floats are single precision, do not need double precision for money 
        else if (type.toLowerCase().equals("numeric identity") || type.toLowerCase().equals("numeric() identity"))
        	type = "int"; // usually primary key column
        else if (type.toLowerCase().equals("number"))
            type = "int";
        else if (type.toLowerCase().equals("decimal"))
            type = "double";
        else if (type.toLowerCase().equals("bigint"))
            type = "long";
        else if (type.toLowerCase().equals("int8"))
            type = "long";
        else if (type.toLowerCase().equals("text"))
            type = "String";
        else if (type.toLowerCase().equals("string"))
            type = "String";
        else if (type.toLowerCase().equals("char"))
            type = "String";
        else if (type.toLowerCase().equals("varchar") || type.toLowerCase().equals("varchar2") || type.toLowerCase().equals("clob") || type.toLowerCase().equals("char"))
            type = "String";
        else if (type.toLowerCase().equals("datetime"))
            type = "Date";
        else if (type.toLowerCase().equals("date"))
            type = "Date";
        else if (type.toLowerCase().equals("time"))
            type = "Date";
        else if (type.toLowerCase().equals("timetz"))
            type = "Date";
        else if (type.toLowerCase().equals("timestamp"))
            type = "Date";
        else if (type.toLowerCase().equals("timestamptz"))
            type = "Date";
        else if (type.toLowerCase().equals("double"))
            type = "double";
        else if (type.toLowerCase().equals("double precision"))
            type = "double";
        else if (type.toLowerCase().equals("float"))
            type = "float";
        else if (type.toLowerCase().equals("boolean"))
            type = "boolean";
        else if (type.toLowerCase().equals("bool"))
            type = "boolean";
        else if (type.toLowerCase().equals("bit"))
            type = "boolean";
        else if (type.toLowerCase().equals("real"))
            type = "float";
        else
            type = "Object";
        log.debug("           new label: label="+label +"   type="+type);
    }
    
    public boolean isInt() {
        return type.equals("int");
    }
    
    public boolean isLong() {
        return type.equals("long");
    }
    
    public boolean isText() {
        return type.equals("String");
    }
    
    public boolean isBoolean() {
        return type.equals("boolean");
    }
    
    public boolean isDouble() {
    	return type.equals("double");
    }
    
	private boolean isFloat() {

		   return type.equals("float");
	}
    
    public boolean isDateTime() {
        return type.equals("Date");
    }
    
    public boolean isTime() {
        return sqlType.equals("Timestamp");
    }
    
    public boolean isImage() {
        return domain != null && domain.getLabel().equals("Image");
    }
    
    public boolean isDomain() {
        return domain != null && !domain.getLabel().equals("Image");
    }
    public boolean isBinaryDomain() {
        return domain != null && domain.getJavaType().equals("byte[]");
    }
    
    public boolean isByteA() {
    	return sqlType.toLowerCase().equals("bytea");
    }
    
    public String getJavaTypeClass() {
    	log.debug("###"+getLabel()+":"+type);
        if (isInt())
            return "Integer";
        if (isLong())
            return "Long";
        if (isFloat())
            return "Float";
        if (isText())
            return "String";
        if (isBoolean())
            return "Boolean";
        if (isDouble())
            return "Double";
        if (isDateTime())
            return "Date";
        return type;
    }
    
    public String getSQLMethod(boolean get) {
        if (type.equals("double"))
            return (get ? "get" : "set") + "Double";
        if (type.equals("float"))
            return (get ? "get" : "set") + "Float";
        else if (sqlType.equals("Numeric"))
            return (get ? "get" : "set") + "Int";
        else if (sqlType.equals("Text") || sqlType.toLowerCase().equals("char") || sqlType.toLowerCase().equals("varchar"))
            return (get ? "get" : "set") + "String";
        else if (sqlType.toLowerCase().equals("int4") || sqlType.toLowerCase().equals("integer") || sqlType.toLowerCase().equals("smallint"))
            return (get ? "get" : "set") + "Int";
        else if (sqlType.toLowerCase().equals("int8") || sqlType.toLowerCase().equals("bigint"))
            return (get ? "get" : "set") + "Long";
        else if (sqlType.toLowerCase().equals("decimal"))
            return (get ? "get" : "set") + "Int";
        else if (sqlType.toLowerCase().equals("bool"))
            return (get ? "get" : "set") + "Boolean";
        else if (sqlType.toLowerCase().equals("bit"))
            return (get ? "get" : "set") + "Boolean";
        else if (sqlType.toLowerCase().equals("bytea"))
            return (get ? "get" : "set") + "Bytes";
        else if (sqlType.toLowerCase().equals("timestamptz"))
            return (get ? "get" : "set") + "Date";
        else
            return (get ? "get" : "set") + sqlType;
    }

    public String parseValue() {
    	return parseValue(getLabel());
    }

    public String parseValue(String label) {
        if (type.equals("double") || type.equals("float"))
            return "Double.parseDouble(" + label + ")";
        else if (sqlType.equals("Numeric"))
            return "Integer.parseInt(" + label + ")";
        else if (sqlType.equals("Text") || sqlType.toLowerCase().equals("char") || sqlType.toLowerCase().equals("varchar"))
            return label;
        else if (sqlType.toLowerCase().equals("int4") || sqlType.toLowerCase().equals("integer") || sqlType.toLowerCase().equals("int") || sqlType.toLowerCase().equals("smallint"))
            return "Integer.parseInt(" + label + ")";
        else if (sqlType.toLowerCase().equals("int8") || sqlType.toLowerCase().equals("bigint"))
            return "Long.parseLong(" + label + ")";
        else if (sqlType.toLowerCase().equals("decimal"))
            return "Integer.parseInt(" + label + ")";
        else if (sqlType.toLowerCase().equals("boolean"))        	
            return "Boolean.parseBoolean(" + label + ")";
        else if (sqlType.toLowerCase().equals("timestamp"))
            return "new java.util.Date(Integer.parseInt(" + label + "))";
        else if (sqlType.toLowerCase().equals("bytea"))
            return label;
        else
            return label;
    }

    public void dump() {
        log.debug("\t\t\tattribute: " + label + "\tuid: " + uid + "\ttype: " + type + "\tmandatory: " + mandatory + "\tprimary: " + primary + "\tauto-increment: " + autoIncrement + "\tremarks: " + remarks + "\tcounter: " + counter + "\tsequence: " + sequence + "\tsequence name: " + sequenceName);
    }


}
