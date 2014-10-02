	@RequestMapping( value = "datatable.html" , method = RequestMethod.GET )
	public void datatable( HttpServletRequest request, HttpServletResponse response,
		@RequestParam( value = "iDisplayLength" ) Integer limit,
		@RequestParam( value = "iDisplayStart" ) Integer start,
		@RequestParam( value = "iColumns" ) Integer numberColumns,
		@RequestParam( value = "sColumns" ) String columns,
		@RequestParam( value = "sEcho" ) String echo,
		@RequestParam( value = "bFilter" ) String bFilter,
		@RequestParam( value = "iSortingCols" , required = false ) Integer sortingColsCount,
		@RequestParam( value = "sSearch" , required = false ) String search,
		@RequestParam( value = "display" , required = false , defaultValue = "list" ) String display ) {

		ArrayList<SortColumn> sorts = new ArrayList<SortColumn>();
		try {

			response.setContentType( "application/json" );

			String[] colArr = columns.split( "," );

			if ( sortingColsCount != null ) {
				for ( int i = 0; i < sortingColsCount; i++ ) {
					if ( i < colArr.length ) {
						Integer colnum = null;
						String col = request.getParameter( "iSortCol_" + i );
						if ( col != null ) {
							try {
								colnum = Integer.parseInt( col );
							} catch ( NumberFormatException e ) {
								continue;
							}
							if ( colnum != null ) {
								sorts.add( new SortColumn( colArr[colnum], request.getParameter( "sSortDir_" + i ) ) );
							}
						}
					}
				}
			}

			GenericDaoListOptions options = new GenericDaoListOptions();

			if ( Boolean.valueOf( bFilter ) ) {
				ArrayList<String> searchColumns = new ArrayList<String>();
				for ( int i = 0; i < numberColumns; i++ ) {
					if ( Boolean.valueOf( request.getParameter( "bSearchable_" + i ) ) ) {
						searchColumns.add( colArr[i] );
					}
				}
				options.setSearch( search );
				options.setSearchColumns( searchColumns );
			} else {
				HashMap<String, Object> likes = new HashMap<String, Object>();
				for ( String col : colArr ) {
					String colValue = request.getParameter( col );
					if ( colValue != null ) {
						likes.put( col, colValue );
					}
				}
				options.setIndividualLikes( likes );
			}

			Integer count = ${daoServiceName}.get${domainName}Service().count( options );

            options.setLimit( limit );
            options.setStart( start );
            options.setSorts( sorts );

            List<${domainName}> ${lowerDomainName}List = ${daoServiceName}.get${domainName}Service().list( options );

			JSONObject ob = new JSONObject();
			ob.put( "sEcho", echo );
			ob.put( "iTotalDisplayRecords", count );
			ob.put( "iTotalRecords", count );
			JSONArray jsonArray = new JSONArray();
			for( ${domainName} ${lowerDomainName} : ${lowerDomainName}List ){
${datatableColumnForEach}
			}
			ob.put( "aaData", jsonArray );

			StringReader reader = new StringReader( ob.toString() );
			try {
				IOUtils.copy( reader, response.getOutputStream() );
			} finally {
				reader.close();
			}
		} catch ( Exception e ) {
			log.error( "error builing datatable json object for ${domainName}", e );
			try {
				String stackTrace = e.getMessage() + "<br/>";
				for ( StackTraceElement ste : e.getStackTrace() ) {
					stackTrace += ste.toString() + "<br/>";
				}
				JSONObject ob = new JSONObject();
				ob.put( "sEcho", echo );
				ob.put( "iTotalDisplayRecords", 0 );
				ob.put( "iTotalRecords", 0 );
				ob.put( "error", e.getMessage() );
				ob.put( "stackTrace", stackTrace );
				StringReader reader = new StringReader( ob.toString() );
				try {
					IOUtils.copy( reader, response.getOutputStream() );
				} finally {
					reader.close();
				}
			} catch ( JSONException je ) {
				log.error( "error writing json error to page for ${domainName}", je );
			} catch ( IOException ioe ) {
				log.error( "error writing json error to page for ${domainName}", ioe );
			}
		}
	}