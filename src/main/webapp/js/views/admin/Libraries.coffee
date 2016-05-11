define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'templates/views/admin/libraries'
			]

	(Backbone, Events, Forms, Layers, Renderer, template) ->

		class AdminLibraries extends Backbone.View

			className: 'admin-libraries content-box'

			events: 
				'click [data-action=add]': (event) -> @showAddDialog event, true
				'click [data-action=delete]': (event) -> @deleteLibrary event
				'click [data-action=replace]': (event) -> @showAddDialog event, false
				'click [data-action=show]': (event) -> @showRefIds event

			render: (renderOptions) ->
				@loadLibraries (libraries) =>				
					@$el.html template
						libraries: libraries
					Renderer.render @, renderOptions

			loadLibraries: (callback) ->
				$.ajax
					type: 'GET'
					url: '/ws/admin/library'
					success: (libraries) ->
						callback libraries

			showRefIds: (event) ->
				target = $ Events.target event
				name = target.attr 'data-library'
				Layers.showProgressIndicator 'Loading'
				$.ajax
					type: 'GET'
					url: "/ws/admin/library/#{name}"
					success: (refIds) -> 
						Layers.hideProgressIndicator()
						content = ''
						for refId in refIds
							content += refId + '\n'
						Layers.showMessageInLayer
							title: "Data set reference ids of library #{name}"
							body: "<textarea class=\"form-control\" rows=\"10\" disabled>#{content}</textarea>"
							buttons: [{text: 'Close'}]

			deleteLibrary: (event) ->
				target = $ Events.target event
				name = target.attr 'data-library'
				$.ajax
					type: 'DELETE'
					url: "/ws/admin/library/#{name}"
					success: () -> Backbone.history.loadUrl()

			showAddDialog: (event, isNew) ->
				unless isNew
					target = $ Events.target event
					name = target.attr 'data-library'					
				Layers.showTemplateInLayer
					title: (if isNew then 'Add data set library' else "Replace reference ids for data set library #{name}")
					template: 'admin/add-library'
					model: {isNew: isNew}
					buttons: [{
						text: 'Add', className: 'btn-success', callback: () ->
							if isNew
								name = $('#add-library #name').val()
							refIdsRaw = $('#add-library #ref-ids').val()
							unless name
								Forms.handleError 'add-library', {responseJSON: {field: 'name', message: 'No name specified'}}
								return
							unless refIdsRaw
								Forms.handleError 'add-library', {responseJSON: {field: 'ref-ids', message: 'No reference ids specified'}}
								return
							refIds = refIdsRaw.split '\n'
							$.ajax
								type: 'PUT'
								url: "/ws/admin/library/#{name}"
								data: JSON.stringify refIds
								contentType: 'application/json'
								success: () ->
									Layers.closeActive()
									Backbone.history.loadUrl()
					}]

)