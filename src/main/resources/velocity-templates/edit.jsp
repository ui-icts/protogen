<%--
  #%L
  Protogen
  %%
  Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
  --%>
<%@ include file="/WEB-INF/include.jsp"  %>

<c:url value="${pathPrefix}/${domainClass.getLowerIdentifier().toLowerCase()}/save${pathExtension}" var="formActionUrl" />
<c:url value="${pathPrefix}/${domainClass.getLowerIdentifier().toLowerCase()}/list${pathExtension}" var="cancelUrl" />

<div class="row">
	<div class="col-xs-12 col-sm-8 col-md-6 col-lg-4">
		<form:form method="post" commandName="${domainClass.getLowerIdentifier()}" action="${esc.d}{ formActionUrl }" role="form">
    		<fieldset>
    		
    			<legend>${domainClassLabel}</legend>
#foreach( $classVariable in $domainClass.listAll() )
 #if ( $domainClass.isUsesCompositeKey() && $classVariable.isPrimary() )
  #foreach( $a in $domainClass.getEntity().getPrimaryKeyAttributes() )
    #if ( $a.isForeign() )
    #else
      #set( $path = "id." + $a.getLowerLabel() )
	  #set( $label = $a.getLowerLabel() )
	  #if ( $deOb )
	    #set( $label = "${esc.d}{ " + ${domainClass.getSchema().getLowerLabel()} + ":deobfuscateColumn ( '"+ ${domainClass.getTableName()} + "', '" + ${a.getSqlLabel()} + "') }" )
	  #end
	  			<spring:bind path="${path}">        
	     			<div class="form-group ${esc.d}{status.error ? 'has-error' : ''}">
	      				<label for="${path}" class="control-label">$label</label>
	      				<form:input path="${path}"  class="form-control" />
	      				<form:errors path="${path}" class="help-block" element="span" />
		 			</div>
				</spring:bind>
	#end
  #end
 #else
  #set( $path = $classVariable.getIdentifier() )
  #if ( $classVariable.getAttribType() == "FOREIGNATTRIBUTE" )
    #set( $path = $classVariable.getIdentifier() + "." + $classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier() )
  #end

  #set( $label = $generatorUtil.splitCapitalizedWords( $classVariable.getUpperIdentifier() ) )
  #if ( $deOb )
    #set( $label = "${esc.d}{ " + ${domainClass.getSchema().getLowerLabel()} + ":deobfuscateColumn ( '"+ ${domainClass.getTableName()} + "', '" + ${classVariable.getAttribute().getSqlLabel()} + "') }" )
  #end

  #if ( $classVariable.isPrimary() ) 
				<form:hidden path="${path}" />
  #elseif ($classVariable.getAttribType() == "CHILD")	
  #else
    			<spring:bind path="${path}">        
	     			<div class="form-group ${esc.d}{status.error ? 'has-error' : ''}">
	      				<label for="${path}" class="control-label">$label</label>
    #if ( $classVariable.getAttribType() == "FOREIGNATTRIBUTE" )
					<form:select path="${path}" items="${esc.d}{${classVariable.getDomainClass().getLowerIdentifier()}List}" itemValue="${classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier()}" itemLabel="${classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier()}" class="form-control"/>
					<form:errors path="${classVariable.getLowerIdentifier()}" class="help-block" element="span" />
    #else     
					<form:input path="${path}"  class="form-control#if ( $classVariable.getType().equalsIgnoreCase( "date" ) ) dateinput#end"#if ( $classVariable.getType().equalsIgnoreCase( "date" ) ) data-provide="datepicker" data-date-format="yyyy-mm-dd" data-date-autoclose="true"#end/>
    				<form:errors path="${path}" class="help-block" element="span" />
    #end
	     			</div>
				</spring:bind>	
  #end         
 #end
#end 
    			
    			<input type="submit" value="Save" class="btn btn-primary" />
    			<a class="btn btn-default" href="${esc.d}{ cancelUrl }">Cancel</a>
    			
    		</fieldset>
		</form:form>
	</div>
</div>