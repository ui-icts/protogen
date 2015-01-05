<%@ include file="/WEB-INF/include.jsp"  %>
<div class="row">
  <div class="col-xs-12 col-sm-8 col-md-6 col-lg-4">
<form:form method="post" commandName="${domainClass.getLowerIdentifier()}" action="save${pathExtension}" role="form">
    <fieldset>
    <legend>${domainClass.getIdentifier()}</legend>
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
	      <label for="${path}" style="display: inline;">$label
		      <form:input path="${path}"  class="form-control"/>
		      <form:errors path="${path}" class="help-block"/>
		  </label>
		 </div>
		</spring:bind>	
	#end
  #end
 #else
  #set( $path = $classVariable.getIdentifier() )
  #if ( $classVariable.getAttribType() == "FOREIGNATTRIBUTE" )
    #set( $path = $classVariable.getIdentifier() + "." + $classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier() )
  #end

  #set( $label = $classVariable.getUpperIdentifier() )
  #if ( $deOb )
    #set( $label = "${esc.d}{ " + ${domainClass.getSchema().getLowerLabel()} + ":deobfuscateColumn ( '"+ ${domainClass.getTableName()} + "', '" + ${classVariable.getAttribute().getSqlLabel()} + "') }" )
  #end

  #if ( $classVariable.isPrimary() ) 
	<form:hidden path="${path}" />
  #else
    <spring:bind path="${path}">        
	     <div class="form-group ${esc.d}{status.error ? 'has-error' : ''}">
	      <label for="${path}">$label</label>
	 #if ( $classVariable.getAttribType() == "FOREIGNATTRIBUTE" )
	      <form:select path="${path}" items="${esc.d}{${classVariable.getDomainClass().getLowerIdentifier()}List}" itemValue="${classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier()}" itemLabel="${classVariable.getDomainClass().getPrimaryKeys().iterator().next().getLowerIdentifier()}" class="form-control"/>
	 #else     
	      <form:input path="${path}"  class="form-control#if ( $classVariable.getType().equalsIgnoreCase( "date" ) ) dateinput #end"/>
	 #end
	      <form:errors path="${path}" class="help-block"/>
	     </div>
	</spring:bind>	
  #end         
 #end
#end 
    <input type="submit" value="Save" class="btn btn-primary" />
    <a class="btn btn-default" href="list${pathExtension}">Cancel</a>
    </fieldset>
</form:form>
</div>
</div>