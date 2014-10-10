<%@ include file="/WEB-INF/include.jsp"  %>

<form:form method="post" commandName="${domainClass.getLowerIdentifier()}" action="save${pathExtension}" role="form">
    <fieldset>
    <legend>${domainClass.getIdentifier()}</legend>
#foreach( $classVariable in $domainClass.listAll() )

  #set( $label = $classVariable.getUpperIdentifier() )
  #if ( $deOb )
    #set( $label = "${esc.d}{ " + ${domainClass.getSchema().getLowerLabel()} + ":deobfuscateColumn ( '"+ ${domainClass.getTableName()} + "', '" + ${classVariable.getAttribute().getSqlLabel()} + "') }" )
  #end

  #if ( $classVariable.isPrimary() ) 
	<form:hidden path="${classVariable.getIdentifier()}" />
  #else
    <spring:bind path="${classVariable.getIdentifier()}">        
	     <div class="form-group ${esc.d}{status.error ? 'has-error' : ''}">
	      <label for="${classVariable.getIdentifier()}">$label</label>
	      <form:input path="${classVariable.getIdentifier()}"  class="form-control#if ( $classVariable.getType().equalsIgnoreCase( "date" ) ) dateinput #end"/>
	      <form:errors path="${classVariable.getIdentifier()}" class="help-block"/>
	     </div>
	</spring:bind>	
  #end         
#end 
    <input type="submit" value="Save" class="btn btn-primary" />
    <a class="btn btn-default" href="list${pathExtension}">Cancel</a>
    </fieldset>
</form:form>