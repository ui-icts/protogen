package edu.uiowa.icts.plugin.protogen.util;

import org.codehaus.plexus.util.StringUtils;

public class GeneratorUtil {

	public String splitCapitalizedWords( String substring ) {
		String newString = "";
		for ( char c : substring.toCharArray() ) {
			if ( Character.isUpperCase( c ) ) {
				newString += " ";
			}
			newString += String.valueOf( c );
		}
		return StringUtils.trim( newString );
	}

}