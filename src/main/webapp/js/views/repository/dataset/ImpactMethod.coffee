define([
				'cs!utils/Format'
				'cs!utils/Icons'
				'cs!views/repository/dataset/DatasetPrepare'
				'templates/views/repository/dataset/impact-factor-rows'
				'templates/views/repository/dataset/nw-factor-rows'
			] 

	(Format, Icons, DatasetPrepare, impactFactorsTemplate, nwFactorsTemplate) ->

		initCategory: (options) ->
			unless options.impactCategory
				options.callback()
				return
			urlPart = options.getUrlPart 'IMPACT_CATEGORY', options.impactCategory
			$.ajax
				type: 'GET'
				url: "ws/public/browse/#{urlPart}/#{options.commitId||'null'}"
				success: (impactCategory) =>
					group = options.repository.get 'group'
					name = options.repository.get 'name'
					DatasetPrepare.applyTo options.impactCategory
					$('#impact-category-description').html options.impactCategory.description
					$('#impact-category-unit').html options.impactCategory.referenceUnitName
					$('#impact-factors tbody').empty()
					$('#impact-factors tbody').append impactFactorsTemplate 
						dataset: options.impactCategory
						noToStr: Format.number
						getValue: options.getValue 
						getTypeAsEnum: options.getTypeAsEnum
						getIcon: Icons.get
						commitId: options.commitId
						baseUrl: "#{group}/#{name}/dataset"
					@initNwSet options

		initNwSet: (options) ->
			unless options.nwSet
				options.callback()
				return
			urlPart = options.getUrlPart 'NW_SET', options.nwSet
			$.ajax
				type: 'GET'
				url: "ws/public/browse/#{urlPart}/#{options.commitId||'null'}"
				success: (nwSet) =>
					DatasetPrepare.applyTo options.nwSet
					$('#nw-set-unit').html options.nwSet.weightedScoreUnit
					$('#nw-factors tbody').empty()
					$('#nw-factors tbody').append nwFactorsTemplate 
						nwSet: options.nwSet
						noToStr: Format.number
					options.callback()

)