define () ->

		Layouts = () ->

		Layouts:: = (() ->

			# private

			renderInLayout = (layout, options) ->
				renderParts = (parts) ->
					dependenciesAndParts = getDependenciesAndParts parts, options.getDependencyFunction, options.parentOptions
					dependencies = dependenciesAndParts[0]
					partQueue = dependenciesAndParts[1]
					if dependencies
						require dependencies, (Dependencies...) ->
							for dependency in Dependencies
								options.renderDependency dependency, partQueue.pop()
							options.callback?()
				renderLayoutView layout, renderParts, options.parentOptions.container

			getDependenciesAndParts = (parts, getDependencyFunction, options) ->
				dependencies = []
				partQueue = []
				for part in parts
					dependency = getDependencyFunction part, options
					if dependency 
						dependencies.push dependency
						partQueue.unshift part
				return [dependencies, partQueue]

			renderLayoutView = (layout, callback, container = '#main') ->
				$(container).empty()
				require(["templates/layouts/#{layout}"], (layoutTemplate) ->
					layoutDiv = layoutTemplate()
					layoutName = $(layoutDiv).attr('data-name')
					$(container).append layoutDiv
					parts = $("> [data-name=#{layoutName}]", container).children()
					callback?(parts)
				)

			getViewPath = (part, options) ->
				partName = $(part).attr 'data-name'
				unless options?.views?[partName]
					return null
				viewPath = options.views[partName]
				return "cs!views/#{viewPath}"

			getTemplatePath = (part, options) ->
				partName = $(part).attr 'data-name'
				unless options?.templates?[partName]
					return null
				templatePath = options.templates[partName]
				return "templates/views/#{templatePath}" 						

			createView = (View, viewOptions) ->
				@cache = @cache or {}
				existing = @cache[View.name]
				if existing
					if $.isFunction existing.destroy 
						existing.destroy()
					delete @cache[View.name]
				view = new View(viewOptions)
				@cache[View.name] = view
				return view

			# public

			constructor: Layouts

			renderMessage: (title, message) ->
				(@_ renderLayoutView) 'full-size', (part) ->
					part.append "<h3>#{title}</h3><p>#{message}</p>"

			renderViewInLayout: (layout, options) ->
				(@_ renderInLayout) layout,
					parentOptions: options
					getDependencyFunction: (@_ getViewPath)
					renderDependency: (View, part) =>		
						view = (@_ createView) View, options?.viewOptions
						view.render 
							container: part

			renderTemplateInLayout: (layout, options) ->
				(@_ renderInLayout) layout,
					parentOptions: options
					getDependencyFunction: (@_ getTemplatePath)
					renderDependency: (template, part) ->		
						$(part).html template options.model
					callback: options.callback

			_: (callback) ->
				() =>
					callback.apply @, arguments

		)()