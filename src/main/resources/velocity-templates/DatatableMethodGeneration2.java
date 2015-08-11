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
	@ResponseBody
	@RequestMapping( value = "datatable" , produces = "application/json" )
	public DataTable datatable( @RequestBody DataTableRequest dataTableRequest, HttpServletRequest request,
		@RequestParam( value = "display" , required = false , defaultValue = "list" ) String display ) {
		
		String contextPath = request.getContextPath();
		GenericDaoListOptions options = dataTableRequest.getGenericDaoListOptions();

		try {

			Integer count = ${daoServiceName}.get${domainName}Service().count( options );
            List<${domainName}> ${lowerDomainName}List = ${daoServiceName}.get${domainName}Service().list( options );
            
			List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();

			for( ${domainName} ${lowerDomainName} : ${lowerDomainName}List ){
${datatableColumnForEach}
			}

			DataTable dataTable = new DataTable();
			dataTable.setDraw( dataTableRequest.getDraw() );
			dataTable.setRecordsFiltered( count );
			dataTable.setRecordsTotal( count );
			dataTable.setData( data );
			return dataTable;
			
		} catch ( Exception e ) {
			log.error( "error builing datatable json object for ${domainName}", e );
			return datatableError( e, dataTableRequest.getDraw() );
		}
		
	}