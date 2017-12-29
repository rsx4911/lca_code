define([
				'cs!utils/DataQuality'
				'cs!utils/Layers'
				'cs!views/repository/dataset/DatasetSort'
			]

	(DataQuality, Layers, Sort) ->

		open: (repo, commitId, systemId, entry, getValue) ->
			url = "ws/public/browse/#{repo.group}/#{repo.name}/DQ_SYSTEM/#{systemId}"
			if commitId
				url += "?commitId=#{commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (system) ->
					Sort.indicatorsAndScores system
					Layers.showTemplateInLayer
						title: 'Data quality'
						template: 'repository/dataset/layer/data-quality-entry'
						dialogType: 'modal-large'
						model: 
							system: system
							entry: entry
							getValue: getValue
							getDQColor: DataQuality.getColor

)