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