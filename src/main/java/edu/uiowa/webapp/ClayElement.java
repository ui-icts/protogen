/*
 * Created on May 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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

import java.util.StringTokenizer;

public class ClayElement {

    protected String sqlLabel = null;
    protected String label = null;
    protected String uid = null;
    protected String lowerLabel = null;
    protected String upperLabel = null;

    public ClayElement() {
        super();
    }

    public String getLabel() {
        return label;
    }

    public String getUnqualifiedLabel() {
        return label.replace('.', '_');
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSqlLabel() {
        return sqlLabel;
    }

    public void setSqlLabel(String sqlLabel) {
        this.sqlLabel = sqlLabel;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getLowerLabel() {
        if (lowerLabel == null) {
            if (label.equals("ID"))
                lowerLabel = "id";
            else
                lowerLabel = Character.toLowerCase(label.charAt(0)) + label.substring(1);
        }
        
        return lowerLabel;
    }
    
    public String getUnqualifiedLowerLabel() {
        return getLowerLabel().replace('.', '_');
    }
    
    public String getUpperLabel() {
        if (upperLabel == null) {
            upperLabel = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        }
        
        return upperLabel;
    }
    
    public void relabel(boolean leadingCapital) {
        sqlLabel = label;
        StringBuffer result = new StringBuffer();
        StringTokenizer theTokenizer = new StringTokenizer(label, "_-");
        while(theTokenizer.hasMoreTokens()) {
            String theToken = theTokenizer.nextToken().toLowerCase();
            result.append(Character.toUpperCase(theToken.charAt(0)));
            result.append(theToken.substring(1));
        }
        if (!leadingCapital)
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        label = result.toString();
    }
    
    public void relabelFull(boolean leadingCapital) {
        sqlLabel = label;
        StringBuffer result = new StringBuffer();
        StringTokenizer theTokenizer = new StringTokenizer(label, "_-");
        while(theTokenizer.hasMoreTokens()) {
            String theToken = theTokenizer.nextToken().toLowerCase();
            result.append(Character.toUpperCase(theToken.charAt(0)));
            result.append(theToken.substring(1));
        }
        if (!leadingCapital)
            result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        label = result.toString();
    }

    public String toString() {
        return getLabel();
    }

}