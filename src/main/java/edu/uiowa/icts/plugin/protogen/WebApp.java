package edu.uiowa.icts.plugin.protogen;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;
import edu.uiowa.webapp.Generator;

/**
 * Build TagLib or Spring demo project
 * @goal generate
 * @requiresDependencyResolution test
 */
public class WebApp  extends AbstractMojo {

	private static final String PROPS_FILE="protogen.properties";
	private Log log = getLog();
	
	
    /**
     * Prop File Name
     * 
     * @parameter  default-value=""
     */ 
    private String propertyFile;
    
    
    
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;
		
    /**
     * My Properties.
     *
     * @parameter
     */
    private Properties props;

    /**
     * Location of the file.
     * @parameter expression="${basedir}"
     * @required
     */
    private String buildDirectory;
    
    
	DatabaseSchemaLoader theLoader = null;
	static Database theDatabase = null;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		/****
		 * James Schappet @ 2010-12-29 8:17
		 * 
		 * If props, ie properties in the pom file exist, those are used. 					
		 * If propertyFile is passed in, it checks that, full path, then resources dir.
		 * If no propertyFile is set then it uses protogen.properties in the resources dir.
		 * 
		 */
		
		if (props == null) {
			props = loadProperties();
		}
		
		log.debug("Project Name: " + props.getProperty("project.name"));
		log.debug("Package Name: " + props.getProperty("package.name"));
		log.info("Generator mode: " + props.getProperty("mode"));		
		log.info("jsp tag library prefix: " + props.getProperty("jsp.taglibrary.prefix"));		
		
		if (Boolean.parseBoolean(props.getProperty("generator.enabled", "true"))) {
			Generator gen = new Generator();
			gen.runGenerator(props);
		} else {
			log.info("Generator has been disabled: generator.enabled = " +props.getProperty("generator.enabled","false"));
		}
	}
	
	
	
	@SuppressWarnings("rawtypes")
	private Properties loadProperties() {
    	Properties propFile = new Properties();
    	    	
    	List stuff = project.getResources();
    	
    	for (int i = 0; i < stuff.size(); ++i) {
    		Resource thing = (Resource) stuff.get(i);
    		log.debug("Project Resource: " +  thing.getDirectory());

        	if (propertyFile == null) {
        		propertyFile = thing.getDirectory() + "/" + PROPS_FILE;
        	} else {
        		File file = new File(propertyFile);
        		if (!file.exists()){
        			propertyFile = thing.getDirectory() + "/" + propertyFile;
        		}
        	}
     	}
    	
		InputStream f=null;
		try {
			f = new FileInputStream(propertyFile);
			log.info("Loading properties from: " + propertyFile);
			propFile.load(f);
		} catch (Exception e) {
			log.error("Could not load file: " + propertyFile, e);
		}
		
		 Enumeration<Object> keys = propFile.keys();
		 while(keys.hasMoreElements()) {
			 String key = keys.nextElement().toString();
			 String value =propFile.getProperty(key);
			 
			 String strreplace = "basedir";
			 if (value.contains(strreplace)){
				 propFile.setProperty(key, value.replace(strreplace,buildDirectory));
			 }
			 strreplace = "${project.artifactId}";
			 if (value.contains(strreplace)){
				 propFile.setProperty(key, value.replace(strreplace,project.getArtifactId()));
			 }
			 log.debug("Item: " + key + "=" + propFile.getProperty(key));
		 }
		 return propFile;
	}

	static Database getDatabase() {
		return theDatabase;
	}
}