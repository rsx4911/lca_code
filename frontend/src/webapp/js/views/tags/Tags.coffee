define([
				'backbone'
				'd3'
				'd3cloud'
				'cs!utils/Events'
				'cs!utils/Renderer'
				'templates/views/tags/tags'
			]

	(Backbone, D3, D3Cloud, Events, Renderer, template) ->

		class Tags extends Backbone.View

			className: 'tags-view multi-box-view'

			events: 
				'click a[href]:not([href="#"]):not([data-action])': (event) -> Events.followLink event

			initialize: (options) ->
				if options
					{@group, @repository} = options

			render: (renderOptions) ->
				url = 'ws/public/search?page=1&pageSize=1&mostRecent=true'
				if @group
					url += "&group=#{@group.get('name')}"
				else if @repository
					url += "&repositoryId=#{@repository.get('group')}/#{@repository.get('name')}"
				$.ajax
					type: 'GET'
					url: url
					success: (result) =>
						tags = []
						for aggregation in result.aggregations
							if aggregation.name is 'tags' or aggregation.name is 'repositoryTags'
								for entry in aggregation.entries
									tags.push 
										value: entry.key
										count: entry.count
						@$el.html template()
						Renderer.render @, renderOptions
						@renderTags tags

			renderTags: (tags) ->
				tags = tags.sort (a, b) -> b.count - a.count
				totalCount = 0
				for tag in tags
					totalCount += tag.count
				layout = D3Cloud()
					.size([750, 750])
					.words(tags.map((tag, index) => {
						text: tag.value,
						index: index,
						size: 20 + (tag.count/totalCount) * 200,
						color: 'rgb(' + @getColor() + ', ' + @getColor() + ', ' + @getColor() + ')'
					}))
					.padding(1)
					.rotate((element) -> 
						switch (element.index % 4)
							when 0 then return 0
							when 1 then return 90
							when 2 then return 45
							when 3 then return -45
						return 0
					)
					.spiral('rectangular')
					.font('Impact')
					.random(() -> 0.51)
					.fontSize((element) -> element.size)
					.on('end', (words) => @draw(layout, words))
				layout.start()

			getColor: (min = 100, max = 200) ->
				return parseInt(min + Math.random() * (max - min))

			draw: (layout, words) ->
				D3.select('.tag-container').append('svg')
					.attr('width', layout.size()[0])
					.attr('height', layout.size()[1])
					.append('g')
					.attr('transform', 'translate(' + layout.size()[0] / 2 + ',' + layout.size()[1] / 2 + ')')
					.selectAll('text')
					.data(words)
					.enter().append('text')
					.style('font-size', (element) -> element.size + 'px')
					.style('font-family', 'Impact')
					.style('fill', (element) -> element.color)
					.attr('text-anchor', 'middle')
					.attr('transform', (element) -> 'translate(' + [element.x, element.y] + ') rotate(' + element.rotate + ')')
					.text((element) -> element.text)

)