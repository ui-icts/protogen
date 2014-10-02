package edu.uiowa.icts.plugin.protogen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author rrlorent
 * @since Jul 3, 2014
 * @goal clean
 * @requiresDependencyResolution test
 */
public class Clean extends AbstractMojo {
	
	private static final Log log = LogFactory.getLog( Clean.class );

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		log.debug( "cleaning" );
	}
	
}
