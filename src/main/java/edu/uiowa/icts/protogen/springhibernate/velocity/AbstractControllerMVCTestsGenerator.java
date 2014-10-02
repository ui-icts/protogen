package edu.uiowa.icts.protogen.springhibernate.velocity;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

public class AbstractControllerMVCTestsGenerator {
	private String packageName;
	
	public AbstractControllerMVCTestsGenerator(String packageName) {
		this.packageName = packageName;
	}

	public String javaSourceCode() {
        /* lets make a Context and put data into it */
		/* 
		 * init the runtime engine, which only takes affect with first call to .init(p) 
		 * subsequent calls to init are ignored.
		 * */
		Properties p = new Properties();
	    p.setProperty("resource.loader", "class");
	    p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	    p.setProperty("class.resource.loader.cache", "true");
	    p.setProperty("runtime.log.logsystem.log4j.logger","Apache Velocity");
	    Velocity.init( p );
		
		VelocityContext context = new VelocityContext();
        context.put("packageName", this.packageName);
        context.put("date", new Date().toString()); // can be done with Velocity tools but let's keep it simple to start

        /* lets render a template loaded from the classpath */
        StringWriter w = new StringWriter();
        Velocity.mergeTemplate("/velocity-templates/AbstractControllerMVCTests.java", Velocity.ENCODING_DEFAULT, context, w );
        
        return w.toString();
	}

}
