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
				url: (page, filter) -> "ws/public/search/flowLinks/#{refId}?repositoryId=#{group}/#{name}&direction=#{direction}&page=#{page}&pageSize=25&filter=#{filter}"
				callback: (type, result) ->
					result.getIcon = Icons.get
					result.commitId = commitId
			filter.init (result) ->
				if result.resultInfo.totalCount > 0
					$("[href=##{direction}]").html $("[href=##{direction}]").html() + " (#{result.resultInfo.totalCount})"
				else
					$("[href=##{direction}], ##{direction}").hide()

)
