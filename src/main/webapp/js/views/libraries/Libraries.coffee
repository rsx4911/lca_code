define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'templates/views/user/libraries'
				'templates/views/admin/libraries'
			]

	(Backbone, Router, Events, Layers, Renderer, Status, userTemplate, adminTemplate) ->

		class AdminLibraries extends Backbone.View

			loadData = (isAdminArea, callback) ->
				$.ajax
					type: 'GET'
					url: if isAdminArea then 'ws/libraries' else 'ws/libraries/teams'
					success: (libraries) ->
						$.ajax
							type: 'GET'
							url: 'ws/libraries/missing'
							success: (missingLibraries) ->
								callback libraries, missingLibraries

			groupLibraries = (libraries) ->
				groups = {}
				for library in libraries
					for access in library.accessTypes
						group = groups[access] or [] 
						group.push library
						groups[access] = group
				return groups

			deleteLibrary = (event) ->
				target = $ Events.target event
				id = target.attr 'data-id'
				access = target.attr 'data-access'
				Layers.askDeleteQuestion "library #{id}", '', () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "ws/libraries/#{id}/#{access}"
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
				'click [data-action=add]': () -> 
					if @isAdminArea
						Router.navigate 'administration/libraries/add'
					else
						Router.navigate 'user/libraries/add'
				'click [data-action=delete]': deleteLibrary

			initialize: (options) ->
				@isAdminArea = options?.isAdminArea

			render: (renderOptions) ->
				loadData @isAdminArea, (libraries, missingLibraries) =>
					templ = if @isAdminArea then adminTemplate else userTemplate
					@$el.html templ
						groups: groupLibraries(libraries)
						missingLibraries: missingLibraries
					Renderer.render @, renderOptions

)