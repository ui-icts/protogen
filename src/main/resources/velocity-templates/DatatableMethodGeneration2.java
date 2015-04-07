	@ResponseBody
	@RequestMapping( value = "datatable" , produces = "application/json" )
	public String datatable( HttpServletRequest request, 
		@RequestParam( value = "length" , required = false ) Integer limit,
		@RequestParam( value = "start" , required = false ) Integer start,
		@RequestParam( value = "draw" , required = false ) String draw,
		@RequestParam( value = "search[regex]" , required = false , defaultValue = "false" ) Boolean searchRegularExpression,
		@RequestParam( value = "search[value]" , required = false ) String search,
		@RequestParam( value = "columnCount" , required = false , defaultValue = "0" ) Integer columnCount,
		@RequestParam( value = "individualSearch" , required = false , defaultValue = "false" ) Boolean individualSearch,
		@RequestParam( value = "display" , required = false , defaultValue = "list" ) String display ) {
		
		List<DataTableHeader> headers = new ArrayList<DataTableHeader>();
		for ( int i = 0; i < columnCount; i++ ) {
			DataTableHeader dth = new DataTableHeader();
			dth.setData( request.getParameter( "columns[" + i + "][data]" ) );
			dth.setName( request.getParameter( "columns[" + i + "][name]" ) );
			dth.setOrderable( Boolean.valueOf( request.getParameter( "columns[" + i + "][orderable]" ) ) );
			dth.setSearchable( Boolean.valueOf( request.getParameter( "columns[" + i + "][searchable]" ) ) );
			dth.setSearchValue( request.getParameter( "columns[" + i + "][search][value]" ) );
			dth.setSearchRegex( Boolean.valueOf( request.getParameter( "columns[" + i + "][search][regex]" ) ) );
			headers.add( dth );
		}

		ArrayList<SortColumn> sorts = new ArrayList<SortColumn>();
		
		JSONObject ob = new JSONObject();
		
		try {

			for ( int i = 0; i < columnCount; i++ ) {
				Integer columnIndex = null;
				String columnIndexString = request.getParameter( "order[" + i + "][column]" );
				if ( columnIndexString != null ) {
					try {
						columnIndex = Integer.parseInt( columnIndexString );
					} catch ( NumberFormatException e ) {
						continue;
					}
					if ( columnIndex != null ) {
						sorts.add( new SortColumn( headers.get( columnIndex ).getName(), request.getParameter( "order[" + i + "][dir]" ) ) );
					}
				}
			}

			GenericDaoListOptions options = new GenericDaoListOptions();

			if ( !individualSearch ) {
				ArrayList<String> searchColumns = new ArrayList<String>();
				for ( int i = 0; i < columnCount; i++ ) {
					if ( headers.get( i ).getSearchable() ) {
						searchColumns.add( headers.get( i ).getName() );
					}
				}
				options.setSearch( search );
				options.setSearchColumns( searchColumns );
			} else {
				Map<String, List<Object>> likes = new HashMap<String, List<Object>>();
				for ( DataTableHeader header : headers ) {
					if ( header.getSearchable() && header.getSearchValue() != null ) {
						List<Object> values = new ArrayList<Object>();
						for ( String splitColumnValue : StringUtils.split( header.getSearchValue().trim(), ' ' ) ) {
							values.add( splitColumnValue.trim() );
						}
						likes.put( header.getName(), values );
					}
				}
				options.setLikes( likes );
			}

			Integer count = ${daoServiceName}.get${domainName}Service().count( options );

            options.setLimit( limit );
            options.setStart( start );
            options.setSorts( sorts );

            List<${domainName}> ${lowerDomainName}List = ${daoServiceName}.get${domainName}Service().list( options );

			ob.put( "draw", draw );
			ob.put( "recordsFiltered", count );
			ob.put( "recordsTotal", count );
			JSONArray jsonArray = new JSONArray();
			for( ${domainName} ${lowerDomainName} : ${lowerDomainName}List ){
${datatableColumnForEach}
			}
			ob.put( "data", jsonArray );

		} catch ( Exception e ) {
			log.error( "error builing datatable json object for ${domainName}", e );
			try {
				String stackTrace = e.getMessage() + String.valueOf( '\n' );
				for ( StackTraceElement ste : e.getStackTrace() ) {
					stackTrace += ste.toString() + String.valueOf( '\n' );
				}
				JSONObject error = new JSONObject();
				error.put( "draw", draw );
				error.put( "recordsFiltered", 0 );
				error.put( "recordsTotal", 0 );
				error.put( "error", stackTrace );
				return error.toString();
			} catch ( JSONException je ) {
				log.error( "error building json error object for ${domainName}", je );
			}
		}
		
		return ob.toString();
	}