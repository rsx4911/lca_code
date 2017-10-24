define([
				'cs!utils/DataQuality'
				'cs!utils/Layers'
				'cs!views/repository/dataset/DatasetSort'
			]

	(DataQuality, Layers, Sort) ->

		open: (repo, commitId, systemId, entry) ->
			$.ajax
				type: 'GET'
				url: "ws/public/browse/#{repo.group}/#{repo.name}/DQ_SYSTEM/#{systemId}/#{commitId}"
				success: (system) ->
					Sort.indicatorsAndScores system
					Layers.showTemplateInLayer
						title: 'Data quality'
						template: 'repository/model/dataset/data-quality-entry'
						dialogType: 'modal-large'
						model: 
							system: system
							entry: entry
							getDQColor: DataQuality.getColor

)