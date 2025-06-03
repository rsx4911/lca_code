define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!models/CurrentUser'
				'templates/views/dashboard/add-library'
			]

	(Backbone, Router, Data, Events, Forms, Layers, Renderer, Status, currentUser, template) ->

		class AdminAddLibrary extends Backbone.View

			className: 'multi-box-view'

			events:
				'click [data-action=add]': 'addLibrary'

			render: (renderOptions) ->
				@$el.html template
					accessTypes: Data.getAccessTypes()
				Renderer.render @, renderOptions

			addLibrary: () ->
				Events.preventDefault event
				library = Forms.toFormData 'library-form'
				if !library.get('file')
					Forms.handleError 'library-form', {responseJSON: {field: 'file', message: 'Missing input: File'}}
					return
				if !library.get('access')
					Forms.handleError 'library-form', {responseJSON: {field: 'access', message: 'Missing input: Team'}}
					return
				Layers.showProgressIndicator ['Uploading']
				$.ajax
					type: 'POST'
					url: 'ws/libraries'
					cache: false
					contentType: false
					processData: false
					data: library
					success: (library) => 
						Layers.hideProgressIndicator()
						Status.success "Library #{library} successfully added"
						Router.navigate 'dashboard/libraries'
					error: (response) ->
						Layers.hideProgressIndicator()
						Forms.handleError 'library-form', response

)