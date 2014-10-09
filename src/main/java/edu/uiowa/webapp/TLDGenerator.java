/*
 * Created on May 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.webapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TLDGenerator {

    String webAppPath = null;
    String packagePrefix = null;
    String projectName = null;

    Properties props = null;
    File theTLD = null;
    BufferedWriter out = null;

	private static final Log log = LogFactory.getLog(TLDGenerator.class);

	
    File packagePrefixDirectory = null;
    File tagDirectory = null;

    StringBuffer functionBuffer = new StringBuffer();

    String intersectionFunction = "";
    
    public TLDGenerator(String webAppPath, String packagePrefix, String projectName) {
        this.webAppPath = webAppPath;
        this.packagePrefix = packagePrefix;
        this.projectName = projectName;
        
        log.debug(webAppPath + "\t" + packagePrefix + "\t" + projectName);
    }

    
    public TLDGenerator(Properties props) {
        this.props = props;
        this.webAppPath = props.getProperty("tld.file.location");
        this.packagePrefix =  props.getProperty("package.name");
        this.projectName = props.getProperty("project.name");
        
        log.debug(webAppPath + "\t" + packagePrefix + "\t" + projectName);
    }
    public void generateTLD(Database theDatabase) throws IOException {
        theTLD = new File(webAppPath);
        FileWriter fstream = new FileWriter(theTLD);
        out = new BufferedWriter(fstream);
//        <?xml version="1.0" encoding="ISO-8859-1" ?>
//        <taglib xmlns="http://java.sun.com/xml/ns/j2ee"
//            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//            xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd"
//            version="2.0">
//            <jspversion>2.1</jspversion>
//            <shortname>simp</shortname>
//            <uri>http://icts.uiowa.edu/amoeba</uri>
//            <info>AmoebaTagLib tag library</info>

        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n");
        out.write("<taglib xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n");
        out.write("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        out.write("\txsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd\"\n");
        out.write("\tversion=\"2.0\">\n");
        out.write("\t<tlib-version>2.1</tlib-version>\n");
        out.write("\t<short-name>simp</short-name>\n");
        out.write("\t<uri>http://icts.uiowa.edu/" + packagePrefix.substring(packagePrefix.lastIndexOf('.')+1) + "</uri>\n");
//        out.write("\t<info>" + projectName + " tag library</info>\n");

        
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>getSequenceNumber</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + ".Sequence</tag-class>\n");
        out.write("\t\t<body-content>empty</body-content>\n");
//        out.write("\t\t<info>generic sequence number generator hook</info>\n");

        out.write("\n\t\t<attribute>\n");
        out.write("\t\t\t<name>var</name>\n");
        out.write("\t\t\t<required>true</required>\n");
        out.write("\t\t\t<rtexprvalue>false</rtexprvalue>\n");
        out.write("\t\t</attribute>\n");
        out.write("\t</tag>\n");
        
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>dbtest</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + ".DBTest</tag-class>\n");
        out.write("\t\t<body-content>empty</body-content>\n");
        out.write("\t</tag>\n");

        for (int i = 0; i < theDatabase.getSchemas().size(); i++) {
            for (int j = 0; j < theDatabase.getSchemas().elementAt(i).getEntities().size(); j++)
                generateEntityTags(theDatabase.getSchemas().elementAt(i).getEntities().elementAt(j));
        }
        
        generateSupplementalTags(webAppPath);

        out.write(functionBuffer.toString());
        
        generateSupplementalFunctions(webAppPath);

        out.write("</taglib>");
        out.close();
    }
    
    private void generateEntityTags(Entity theEntity) throws IOException {
        // emit the iterator
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>foreach" + theEntity.getUnqualifiedLabel() + "</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</tag-class>\n");
        out.write("\t\t<body-content>JSP</body-content>\n");
//        out.write("\t\t<info>iterator tag for " + theEntity.getUnqualifiedLabel() + "</info>\n");
        generateAttribute("var", true, false);
        generateAttribute("sortCriteria", false, true);
        generateAttribute("limitCriteria", false, true);
        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
            if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
            else
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
        }
        
        ArrayList<String> entityList = new ArrayList<String>();
        if (theEntity.getParents().size() > 1) {
            for (int i = 0; i < theEntity.getParents().size(); i++){
                Entity parent = theEntity.getParents().elementAt(i).getSourceEntity();
                if( !entityList.contains(parent.getUpperLabel()) ){
                	generateAttribute("use" + parent.getUpperLabel(), false, true);
                	entityList.add(parent.getUpperLabel());
                }
            }
            out.write("\n");
        }
        out.write("\t</tag>\n");

        if (theEntity.getParents().size() == 0)
            generateRootEntityFunctions(theEntity);
        generateEntityExistenceFunctions(theEntity);
        
        entityList = new ArrayList<String>();
        for (int i = 0; i < theEntity.getParents().size(); i++) {
        	Entity ent = theEntity.getParents().elementAt(i).getSourceEntity();
        	if( !entityList.contains(ent.getLabel()) ){
        		generateEntityFunctions(theEntity, ent);
        		entityList.add(ent.getLabel());
        	}
        }
        
        if (theEntity.getParents().size() > 1) {
            for (int i = 0; i < theEntity.getParents().size() - 1; i++) {
                Entity parentEntityOne = theEntity.getParents().elementAt(i).getSourceEntity();
                Entity parentEntityTwo = theEntity.getParents().elementAt(i + 1).getSourceEntity();
                generateIntersectionFunction(theEntity, parentEntityOne, parentEntityTwo);
            }
        }

        // emit the main tag
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>" + theEntity.getUnqualifiedLowerLabel() + "</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "</tag-class>\n");
        out.write("\t\t<body-content>JSP</body-content>\n");
//        out.write("\t\t<info>demographic information for a " + theEntity.getUnqualifiedLabel() + "</info>\n");

        generateAttribute("var", false, false);
        
        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
            if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
            else
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
        }
        out.write("\t</tag>\n");

        // emit the deleter tag        
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>delete" + theEntity.getUnqualifiedLabel() + "</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Deleter</tag-class>\n");
        out.write("\t\t<body-content>JSP</body-content>\n");
        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
            if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
            else
                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
        }
        out.write("\t</tag>\n");

        if (theEntity.hasCounter()) {
	        // emit the shifter tag        
	        out.write("\n\t<tag>\n");
	        out.write("\t\t<name>shift" + theEntity.getUnqualifiedLabel() + "</name>\n");
	        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Shifter</tag-class>\n");
	        out.write("\t\t<body-content>JSP</body-content>\n");
	        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
	            if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
	                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
	            else
	                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
	        }
	        generateAttribute("newNumber", false, true);
	        out.write("\t</tag>\n");
        }
        
        if (theEntity.hasBinaryDomainAttribute() || theEntity.hasImage()) {
            // emit the upload tag
            out.write("\n\t<tag>\n");
            out.write("\t\t<name>upload" + theEntity.getUnqualifiedLabel() + "</name>\n");
            out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Upload</tag-class>\n");
            out.write("\t\t<body-content>JSP</body-content>\n");
            out.write("\t</tag>\n");
        }

        for (int i = 0; i < theEntity.getAttributes().size(); i++) {
            generateAttributeTag(theEntity, theEntity.getAttributes().elementAt(i));
            if (! (theEntity.hasBinaryDomainAttribute() || theEntity.hasImage())) {
                // generateAttributeFunction(theEntity, theEntity.getAttributes().elementAt(i));
            }
            if (theEntity.getAttributes().elementAt(i).isDateTime()) {
                generateAttributeToNowTag(theEntity, theEntity.getAttributes().elementAt(i));
            }
            if (theEntity.hasBinaryDomainAttribute() || theEntity.hasImage()) {
                generateUploadAttributeTag(theEntity, theEntity.getAttributes().elementAt(i));
            }
        }
    }
    
    private void generateRootEntityFunctions(Entity theEntity) throws IOException {
        functionBuffer.append("\n\t<function>\n");
        functionBuffer.append("\t\t<name>" + theEntity.getLowerLabel() + "Count</name>\n");
        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</function-class>\n");
        functionBuffer.append("\t\t<function-signature>java.lang.String " + theEntity.getLowerLabel() + "Count(");
        functionBuffer.append(")</function-signature>\n");
        functionBuffer.append("\t</function>\n");
    }
    
    private void generateEntityExistenceFunctions(Entity theEntity) throws IOException {
        functionBuffer.append("\n\t<function>\n");
        functionBuffer.append("\t\t<name>" + theEntity.getLowerLabel() + "Exists</name>\n");
        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</function-class>\n");
        functionBuffer.append("\t\t<function-signature>java.lang.String " + theEntity.getLowerLabel() + "Exists(");
        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
            functionBuffer.append((i == 0 ? "" : ", ") + "java.lang.String");
        }
        functionBuffer.append(")</function-signature>\n");
        functionBuffer.append("\t</function>\n");
    }
    
    private void generateEntityFunctions(Entity theEntity, Entity theParent) throws IOException {
    	
        functionBuffer.append("\n\t<function>\n");
        functionBuffer.append("\t\t<name>" + theEntity.getLowerLabel() + "CountBy" + theParent.getLabel() + "</name>\n");
        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</function-class>\n");
        functionBuffer.append("\t\t<function-signature>java.lang.String " + theEntity.getLowerLabel() + "CountBy" + theParent.getLabel() + "(");
        for (int i = 0; i < theParent.getPrimaryKeyAttributes().size(); i++) {
            functionBuffer.append((i == 0 ? "" : ", ") + "java.lang.String");
        }
        functionBuffer.append(")</function-signature>\n");
        functionBuffer.append("\t</function>\n");
        functionBuffer.append("\n\t<function>\n");
        functionBuffer.append("\t\t<name>" + theParent.getLowerLabel() + "Has" + theEntity.getLabel() + "</name>\n");
        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</function-class>\n");
        functionBuffer.append("\t\t<function-signature>java.lang.Boolean " + theParent.getLowerLabel() + "Has" + theEntity.getLabel() + "(");
        for (int i = 0; i < theParent.getPrimaryKeyAttributes().size(); i++) {
            functionBuffer.append((i == 0 ? "" : ", ") + "java.lang.String");
        }
        functionBuffer.append(")</function-signature>\n");
        functionBuffer.append("\t</function>\n");
    }
    
    private void generateIntersectionFunction(Entity theEntity, Entity parentEntityOne, Entity parentEntityTwo) throws IOException {
        int attrCount = 0;
        if (!intersectionFunction.contains(parentEntityOne.getLowerLabel() + parentEntityTwo.getLabel())) {
	        intersectionFunction= parentEntityOne.getLowerLabel() + parentEntityTwo.getLabel() + "," + intersectionFunction;
	        functionBuffer.append("\n\t<function>\n");
	        functionBuffer.append("\t\t<name>" + parentEntityOne.getLowerLabel() + parentEntityTwo.getLabel() + "Exists</name>\n");
	        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Iterator</function-class>\n");
	        functionBuffer.append("\t\t<function-signature>java.lang.Boolean " + parentEntityOne.getLowerLabel() + parentEntityTwo.getLabel() + "Exists(");
	        for (int i = 0; i < parentEntityOne.getPrimaryKeyAttributes().size(); i++) {
	            functionBuffer.append((attrCount++ == 0 ? "" : ", ") + "java.lang.String");
	        }
	        for (int i = 0; i < parentEntityTwo.getPrimaryKeyAttributes().size(); i++) {
	            functionBuffer.append((attrCount++ == 0 ? "" : ", ") + "java.lang.String");
	        }
	        functionBuffer.append(")</function-signature>\n");
	        functionBuffer.append("\t</function>\n");
        }
    }
    
    /**
     * @deprecated - no longer used
     */
    @Deprecated
    @SuppressWarnings( "unused" )
	private void generateAttributeFunction(Entity theEntity, Attribute theAttribute) {
        functionBuffer.append("\n\t<function>\n");
        functionBuffer.append("\t\t<name>" + theEntity.getLowerLabel() + theAttribute.getUpperLabel() + "Value</name>\n");
        functionBuffer.append("\t\t<function-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "</function-class>\n");
        functionBuffer.append("\t\t<function-signature>java.lang." + theAttribute.getJavaTypeClass() + " " + theAttribute.getLabel() + "Value()</function-signature>\n");
        functionBuffer.append("\t</function>\n");
    }
    
    private void generateAttributeTag(Entity theEntity, Attribute theAttribute) throws IOException {
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>" + theEntity.getUnqualifiedLowerLabel() + theAttribute.getUpperLabel() + "</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + theAttribute.getUpperLabel() + "</tag-class>\n");
        out.write("\t\t<body-content>empty</body-content>\n");
//        out.write("\t\t<info>nested tag to emit " + theEntity.getUnqualifiedLowerLabel() + " " + theAttribute.getLowerLabel() + "</info>\n");
        if (theAttribute.isDomain() || theAttribute.isImage()) {
	        for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
	        	if(theAttribute.getDomain().getJavaType().equals("byte[]") == false)
	        		 generateAttribute(theAttribute.getLowerLabel(), false, true);
	        	else if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
	                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
	            else
	                generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
	        }        	
        } else if (theAttribute.getLabel().equals("ID"))
            generateAttribute(theAttribute.getLabel(), false, true);
        else
            generateAttribute(theAttribute.getLowerLabel(), false, true);
        if (theAttribute.isDateTime()) {
        	generateAttribute("type", false, true);
        	generateAttribute("dateStyle", false, true);
        	generateAttribute("timeStyle", false, true);
        	generateAttribute("pattern", false, true);
        }
        out.write("\t</tag>\n");
    }
    
    private void generateUploadAttributeTag(Entity theEntity, Attribute theAttribute) throws IOException {
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>" + theEntity.getUnqualifiedLowerLabel() + "Upload" + theAttribute.getUpperLabel() + "</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + "Upload" + theAttribute.getUpperLabel() + "</tag-class>\n");
        out.write("\t\t<body-content>empty</body-content>\n");
//        out.write("\t\t<info>nested tag to emit " + theEntity.getUnqualifiedLowerLabel() + " " + theAttribute.getLowerLabel() + "</info>\n");
        if (theAttribute.isDomain() || theAttribute.isImage()) {
            for (int i = 0; i < theEntity.getPrimaryKeyAttributes().size(); i++) {
                if (theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel().equals("ID"))
                    generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLabel(), false, true);
                else
                    generateAttribute(theEntity.getPrimaryKeyAttributes().elementAt(i).getLowerLabel(), false, true);
            }           
        } else if (theAttribute.getLabel().equals("ID"))
            generateAttribute(theAttribute.getLabel(), false, true);
        else
            generateAttribute(theAttribute.getLowerLabel(), false, true);
        out.write("\t</tag>\n");
    }
    
    private void generateAttributeToNowTag(Entity theEntity, Attribute theAttribute) throws IOException {
        out.write("\n\t<tag>\n");
        out.write("\t\t<name>" + theEntity.getUnqualifiedLowerLabel() + theAttribute.getUpperLabel() + "ToNow</name>\n");
        out.write("\t\t<tag-class>" + packagePrefix + "." + theEntity.getUnqualifiedLowerLabel() + "." + theEntity.getUnqualifiedLabel() + theAttribute.getUpperLabel() + "ToNow</tag-class>\n");
        out.write("\t\t<body-content>empty</body-content>\n");
//        out.write("\t\t<info>nested tag to emit " + theEntity.getUnqualifiedLowerLabel() + " " + theAttribute.getLowerLabel() + "</info>\n");
        if (theAttribute.getLabel().equals("ID"))
            generateAttribute(theAttribute.getLabel(), false, true);
        else
            generateAttribute(theAttribute.getLowerLabel(), false, true);
        out.write("\t</tag>\n");
    }
    
    private void generateAttribute(String name, boolean required, boolean rtexprvalue) throws IOException {
        out.write("\n\t\t<attribute>\n");
        out.write("\t\t\t<name>" + name + "</name>\n");
        out.write("\t\t\t<required>" + required + "</required>\n");
        out.write("\t\t\t<rtexprvalue>" + rtexprvalue + "</rtexprvalue>\n");
        out.write("\t\t</attribute>\n");
    }
    
    private void generateSupplementalTags(String prefix) {
        generateSupplementalEntry(prefix, "tags");
    }
    
    private void generateSupplementalFunctions(String prefix) {
        generateSupplementalEntry(prefix, "functions");
    }

    private void generateSupplementalEntry(String prefix, String type) {
        String buffer = null;
        File tagFile = null;
        if (props.getProperty(type + ".file.location") != null) {
        	tagFile = new File (props.getProperty(type + ".file.location"));
        } else {
        	tagFile = new File(prefix + "resources/" + type + ".tld");
        }

        if (!tagFile.exists())
            return;
        
        try {
            BufferedReader IODesc = new BufferedReader(new FileReader(tagFile));
            out.write("\n");
            while ((buffer = IODesc.readLine()) != null) {
                out.write(buffer + "\n");
            }
            IODesc.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("IO Exception", e);
        }

    }

}
