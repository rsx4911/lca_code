define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/CurrentUser'
				'templates/views/dashboard/libraries'
			]

	(Backbone, Router, Data, Events, Layers, Renderer, Status, currentUser, template) ->

		class AdminLibraries extends Backbone.View

			groupLibraries = (libraries) ->
				groups = {}
				for library in libraries
					if library and library.access
						group = groups[library.access] or [] 
						group.push library
						groups[library.access] = group
				return groups

			changeAccess = (event) ->
				target = $ Events.target event
				name = target.attr 'data-name'
				access = target.attr 'data-access'
				Layers.promptInput
					type: 'select'
					label: 'Access'
					value: access
					options: Data.getAccessTypes()
					callback: (value) ->
						Layers.showProgressIndicator 'Updating'
						$.ajax
							type: 'PUT'
							url: "ws/libraries/#{name}/#{value}"
							success: () ->
								Layers.hideProgressIndicator()
								Status.success "Successfully updated library #{name}"
								Backbone.history.loadUrl()
							error: (response) ->
								Layers.hideProgressIndicator()
								Status.error response.responseText

			deleteLibrary = (event) ->
				target = $ Events.target event
				name = target.attr 'data-name'
				Layers.askDeleteQuestion "library #{name}", '', () =>
					Layers.showProgressIndicator 'Deleting'
					$.ajax
						type: 'DELETE'
						url: "ws/libraries/#{name}"
						success: () -> 
							Layers.hideProgressIndicator()
							Status.success "Successfully deleted library #{name}"
							Backbone.history.loadUrl()
						error: (response) ->
							Layers.hideProgressIndicator()
							Status.error response.responseText
				
			className: 'admin-libraries multi-box-view'

			events:
				'click a[href].follow': (event) -> Events.followLink event
				'click [data-action=add]': () -> Router.navigate 'dashboard/libraries/add'
				'click [data-action=delete]': deleteLibrary
				'click [data-action=change-access]': changeAccess

			initialize: (options) ->
				@isAdminArea = options?.isAdminArea

			render: (renderOptions) ->
				$.ajax
					type: 'GET'
					url: 'ws/libraries'
					success: (libraries) =>
						$.ajax
							type: 'GET'
							url: 'ws/libraries/missing'
							success: (missingLibraries) =>
								@$el.html template
									groups: groupLibraries(libraries)
									missingLibraries: missingLibraries
									isLibraryOwner: (library) ->
										if currentUser.isLibraryManager()
											return true
										return library.owner and library.owner is currentUser.get('username') 
								Renderer.render @, renderOptions

)