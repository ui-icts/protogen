/*
 * Created on Jan 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.webapp;

public class Domain extends ClayElement {

    String sqlType = null;
    String type = null;
    boolean mandatory = false;

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
    public String getType() {
        return type;
    }

    
    public String getJavaType() {
    	if (type.equals("BYTEA") )
    		return "byte[]";
    	else if (type.equals("VARCHAR") )
    		return "String";
        return "byte[]";
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String toString() {
        return "(" + getLabel() + " : " + getType() + ")";
    }
    
}
