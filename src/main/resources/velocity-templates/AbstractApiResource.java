package ${packageName};

#*
 * %L
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
 * L%
 *#

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.uiowa.icts.exception.EntityNotFoundException;
import ${abstractResourcePackageName}.${abstractResourceClassName};

/**
 * Generated by Protogen 
 * @see <a href="https://github.com/ui-icts/protogen">https://github.com/ui-icts/protogen</a>
 * @since ${date}
 */
public abstract class ${abstractApiResourceClassName} extends ${abstractResourceClassName} {

	@ExceptionHandler( value = EntityNotFoundException.class )	
	public ResponseEntity<Map<String, Object>> mappingNotFound( HttpServletRequest request ) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put( "message", request.getRequestURI() + " could not be found." );
		return new ResponseEntity<Map<String, Object>>( map, HttpStatus.NOT_FOUND );
	}
	
	@ExceptionHandler( value = Exception.class )
	public ResponseEntity<Map<String, Object>> handleException( Exception exception ) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put( "message", exception.getMessage() );
		return new ResponseEntity<Map<String, Object>>( map, HttpStatus.INTERNAL_SERVER_ERROR );
	}

	
}