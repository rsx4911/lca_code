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
				return
			urlPart = options.getUrlPart 'IMPACT_CATEGORY', options.impactCategory
			url = "ws/public/browse/#{urlPart}"
			if options.commitId
				url += "?commitId=#{options.commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (impactCategory) =>
					group = options.repository.get 'group'
					name = options.repository.get 'name'
					DatasetPrepare.applyTo impactCategory
					$('#impact-category-description').html impactCategory.description
					$('#impact-category-unit').html impactCategory.referenceUnitName
					$('#impact-category-comment-container').html '<span data-path="impactCategories[' + impactCategory.id + ']"></span>'
					$('#impact-factors-comment-container').html '<span data-path="impactCategories[' + impactCategory.id + '].impactFactors"></span>'
					$('#impact-factors tbody').empty()
					$('#impact-factors tbody').append impactFactorsTemplate 
						dataset: impactCategory
						noToStr: Format.number
						getValue: options.getValue 
						getTypeAsEnum: options.getTypeAsEnum
						getIcon: Icons.get
						commitId: options.commitId
						baseUrl: "#{group}/#{name}/dataset"
					options.initTableSorting '#impact-factors'
					options.initComments()
					$('#impact-factors').trigger('update')

		initNwSet: (options) ->
			unless options.nwSet
				return
			urlPart = options.getUrlPart 'NW_SET', options.nwSet
			url = "ws/public/browse/#{urlPart}"
			if options.commitId
				url += "?commitId=#{options.commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (nwSet) =>
					DatasetPrepare.applyTo nwSet
					$('#nw-set-unit').html nwSet.weightedScoreUnit
					$('#nw-factors-comment-container').html '<span data-path="nwSets[' + nwSet.id + '].impactFactors"></span>'
					$('#nw-factors tbody').empty()
					$('#nw-factors tbody').append nwFactorsTemplate 
						nwSet: nwSet
						noToStr: Format.number
					options.initTableSorting '#nw-factors'
					options.initComments()
					$('#nw-factors').trigger('update')

)