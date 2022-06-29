define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'templates/views/admin/libraries'
			]

	(Backbone, Router, Events, Layers, Renderer, Status, template) ->

		class AdminLibraries extends Backbone.View

			loadData = (callback) ->
				$.ajax
					type: 'GET'
					url: 'ws/libraries'
					success: (libraries) ->
						$.ajax
							type: 'GET'
							url: 'ws/libraries/missing'
							success: (missingLibraries) ->
								callback libraries, missingLibraries

			deleteLibrary = (event) ->
				target = $ Events.target event
				id = target.attr 'data-id'
				Layers.askDeleteQuestion "library #{id}", '', () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "ws/libraries/#{id}"
						success: () -> 
							Layers.hideProgressIndicator()
							Status.success "Successfully deleted library #{id}"
							Backbone.history.loadUrl()
						error: (response) ->
							Layers.hideProgressIndicator()
							Status.error response.responseText
				
			className: 'admin-libraries multi-box-view'

			events:
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=add]': () -> Router.navigate 'administration/libraries/add'
				'click [data-action=delete]': deleteLibrary

			render: (renderOptions) ->
				loadData (libraries, missingLibraries) =>
					@$el.html template
						libraries: libraries
						missingLibraries: missingLibraries
					Renderer.render @, renderOptions

)