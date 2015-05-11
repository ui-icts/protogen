	@ResponseBody
	@RequestMapping( value = "datatable" , produces = "application/json" )
	public DataTable datatable( @RequestBody DataTableRequest dataTableRequest, HttpServletRequest request,
		@RequestParam( value = "display" , required = false , defaultValue = "list" ) String display ) {
		
		String contextPath = request.getContextPath();
		DataTable dt = new DataTable();
		GenericDaoListOptions options = dataTableRequest.getGenericDaoListOptions();

		try {

			Integer count = ${daoServiceName}.get${domainName}Service().count( options );
            List<${domainName}> ${lowerDomainName}List = ${daoServiceName}.get${domainName}Service().list( options );
            
			List<LinkedHashMap<String, Object>> data = new ArrayList<LinkedHashMap<String, Object>>();

			for( ${domainName} ${lowerDomainName} : ${lowerDomainName}List ){
${datatableColumnForEach}
			}

			dt.setDraw( dataTableRequest.getDraw() );
            dt.setRecordsFiltered( count );
            dt.setRecordsTotal( count );
			dt.setData( data );

		} catch ( Exception e ) {
			log.error( "error builing datatable json object for ${domainName}", e );
			return datatableError( e, dataTableRequest.getDraw() );
		}
		
		return dt;
	}