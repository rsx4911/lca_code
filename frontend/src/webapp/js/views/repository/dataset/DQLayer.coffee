define([
				'cs!utils/DataQuality'
				'cs!utils/Layers'
				'cs!views/repository/dataset/DatasetRendering'
				'cs!views/repository/dataset/DatasetSort'
			]

	(DataQuality, Layers, Rendering, Sort) ->

		open: (repo, commitId, systemId, entry, dataset) ->
			url = "ws/public/browse/#{repo.group}/#{repo.name}/DQ_SYSTEM/#{systemId}"
			if commitId
				url += "?commitId=#{commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (system) ->
					Sort.indicatorsAndScores system
					model = 
						system: system
						entry: entry
						getDQColor: DataQuality.getColor
					$.extend model, Rendering.getFunctions(dataset)
					Layers.showTemplateInLayer
						title: 'Data quality'
						template: 'repository/dataset/layer/data-quality-entry'
						dialogType: 'modal-large'
						model: model


)