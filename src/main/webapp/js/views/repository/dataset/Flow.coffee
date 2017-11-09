define([				
				'cs!utils/Filter'
				'cs!utils/Icons'
				'templates/views/repository/dataset/flow-references'
			]

	(Filter, Icons, template) ->

		init: (repository, refId, commitId) ->
			@initReferences repository, refId, commitId, 'in'
			@initReferences repository, refId, commitId, 'out'

		initReferences: (repository, refId, commitId, direction) ->
			group = repository.get 'group'
			name = repository.get 'name'
			filter = new Filter
				container: "##{direction}-data"
				filterId: "#{direction}-filter"
				template: template
				pageSize: 25
				url: "ws/public/search/flowLinks/#{refId}?repositoryId=#{group}/#{name}&direction=#{direction}&"
				callback: (result) ->
					result.getIcon = Icons.get
					result.commitId = commitId
					result.baseUrl = "#{group}/#{name}/dataset"
			filter.init (result) ->
				if result.resultInfo.totalCount > 0
					$("[href=##{direction}]").html $("[href=##{direction}]").html() + " (#{result.resultInfo.totalCount})"
				else
					$("[href=##{direction}], ##{direction}").hide()

)
