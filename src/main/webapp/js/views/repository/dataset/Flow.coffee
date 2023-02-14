define([				
				'cs!utils/Filter'
				'cs!utils/Icons'
				'cs!models/Settings'
				'templates/views/repository/dataset/structures/flow-references'
			]

	(Filter, Icons, settings, template) ->

		init: (repository, refId, commitId, flowType) ->
			outType = if flowType is 'ELEMENTARY_FLOW' then 'emitted-by' else 'produced-by'
			@initReferences repository, refId, commitId, 'used-by'
			@initReferences repository, refId, commitId, outType

		initReferences: (repository, refId, commitId, type) ->
			if !settings.is('SEARCH_LINKS_ENABLED')
				return
			group = repository.get 'group'
			name = repository.get 'name'
			direction = if type is 'used-by' then 'INPUT' else 'OUTPUT' 
			commitIdParam = if commitId then "&commitId=#{commitId}" else ''
			filter = new Filter
				container: "##{type}-data"
				filterId: "#{type}-filter"
				template: template
				pageSize: 25
				pageSizeId: direction + '-page-size'
				url: "ws/public/search/flowLinks/#{refId}?repositoryId=#{group}/#{name}#{commitIdParam}&direction=#{direction}&"
				beforeRender: (result) ->
					result.getIcon = Icons.get
					result.commitId = commitId
					result.baseUrl = "#{group}/#{name}/dataset"
			filter.init (result) ->
				if result.resultInfo.totalCount > 0
					$("[href='##{type}']").html $("[href='##{type}']").html() + " (#{result.resultInfo.totalCount})"
				else
					$("[href='##{type}'], ##{type}").hide()

)
