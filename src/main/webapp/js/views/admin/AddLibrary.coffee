define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'templates/views/admin/add-library'
			]

	(Backbone, Router, Events, Forms, Layers, Renderer, Status, template) ->

		class AdminAddLibrary extends Backbone.View

			className: 'multi-box-view'

			events:
				'click [data-action=add]': 'addLibrary'

			render: (renderOptions) ->
				@$el.html template()
				Renderer.render @, renderOptions

			addLibrary: () ->
				Events.preventDefault event
				library = Forms.toFormData 'library-form'
				if !library.get('file')
					Forms.handleError 'library-form', {responseJSON: {field: 'file', message: 'Missing input: File'}}
					return
				Layers.showProgressIndicator ['Uploading']
				$.ajax
					type: 'POST'
					url: 'ws/datamanager/libraries'
					cache: false
					contentType: false
					processData: false
					data: library
					success: (library) -> 
						Layers.hideProgressIndicator()
						Status.success "Library #{library} successfully added"
						Router.navigate 'administration/libraries'
					error: (response) ->
						Layers.hideProgressIndicator()
						Forms.handleError 'library-form', response

)