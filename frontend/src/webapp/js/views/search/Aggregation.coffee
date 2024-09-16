define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'cs!views/search/Util'
				'templates/views/search/aggregation'
			]

	(Backbone, Router, Events, Renderer, Util, template) ->

		class AggregationView extends Backbone.View

			className: 'search-view'

			events: 
				'click a:not([href="#"])': (event) -> Events.followLink event
				'click .toggle-show': 'toggleShow'
				'click .multi-select': 'toggleMultiSelect'
				'change input[type=checkbox]': 'toggleSelection'

			initialize: (options) ->
				{@aggregation, @aggregations, @selectedAggregations, @query, @pageSize} = options
				@multiSelect = false
				@showAll = false 

			render: (renderOptions) ->
				isSelected = @selectedAggregations[@aggregation.name]?.length > 0
				@$el.html template
					aggregation: @aggregation
					showAll: @showAll
					multiSelect: @multiSelect
					label: Util.getAggregationLabel @aggregation.name
					getLabel: Util.getLabel
					isSelected: isSelected
					isSelectedAggregationValue: (type, value) => return @selectedAggregations[type] and $.inArray(value, @selectedAggregations[type]) isnt -1
					getSubCount: @getSubCount
					totalCount: @getTotalCount()
					getAggregationUrl: (type, value, without = false) => 
						aggregations = if without then Util.aggregationsWithout(@selectedAggregations, type, value) else Util.aggregationsWith(@selectedAggregations, type, value)
						return Util.getUrlPart 'search/', @query, 1, @pageSize, aggregations, @aggregations
				Renderer.render @, renderOptions

			toggleShow: (event) ->
				Events.preventDefault event
				@showAll = !@showAll
				@render()

			getTotalCount: () =>
				count = @aggregation.entries.length
				for entry in @aggregation.entries
					count += @getSubCount entry
				return count

			getSubCount: (entry) =>
				unless entry.subEntries
					return 0
				count = entry.subEntries.length
				for subEntry in entry.subEntries
					count += @getSubCount subEntry
				return count

			toggleSelection: (event) ->
				target = $ Events.target event
				type = target.attr 'data-aggregation'
				if type is 'group'
					subEntries = @$ 'input[type=checkbox][data-aggregation=repositoryId]'
				else if type is 'repositoryId'
					subEntries = @$ 'input[type=checkbox][data-aggregation=group]'
				else
					subEntries = $ 'ul input[type=checkbox]', target.parent().parent()
				value = target.is(':checked')
				subEntries.prop 'checked', false
				subEntries.attr 'disabled', value

			toggleMultiSelect: (event) ->
				Events.preventDefault event
				if !@multiSelect
					@multiSelect = true
					@render()
				else
					@multiSelect = false
					selection = @$ 'input[type=checkbox]:checked'
					if selection?.length
						@applyMultiSelection selection
					else
						@render()

			applyMultiSelection: (selection) ->
				aggregations = Util.aggregationsWithout(@selectedAggregations, @aggregation.name)
				for selected in selection
					elem = $ selected
					type = elem.attr 'data-aggregation'
					value = elem.attr 'data-value'
					aggregations = Util.aggregationsWith aggregations, type, value, true
				Router.navigate Util.getUrlPart 'search/', @query, 1, @pageSize, aggregations, @aggregations


)