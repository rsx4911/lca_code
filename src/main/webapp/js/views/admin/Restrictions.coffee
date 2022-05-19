define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'templates/views/admin/restrictions'
			]

	(Backbone, Events, Forms, Layers, Renderer, template) ->

		class AdminRestrictions extends Backbone.View

			className: 'admin-restrictions content-box'

			events: 
				'click [data-action=add]': (event) -> @showAddDialog event, true
				'click [data-action=replace]': (event) -> @showAddDialog event, false
				'click [data-action=delete]': 'deleteRestriction'
				'click [data-action=show]': 'showRefIds'

			render: (renderOptions) ->
				@loadRestrictions (restrictions) =>				
					@$el.html template
						restrictions: restrictions
					Renderer.render @, renderOptions

			loadRestrictions: (callback) ->
				$.ajax
					type: 'GET'
					url: 'ws/datamanager/restrictions'
					success: (restrictions) ->
						callback restrictions

			showRefIds: (event) ->
				target = $ Events.target event
				name = target.attr 'data-restriction'
				Layers.showProgressIndicator 'Loading'
				$.ajax
					type: 'GET'
					url: "ws/datamanager/restrictions/#{name}"
					success: (refIds) -> 
						Layers.hideProgressIndicator()
						content = ''
						for refId in refIds
							content += refId + '\n'
						Layers.showMessageInLayer
							title: "Restricted data set reference ids for #{name}"
							body: "<textarea class=\"form-control\" rows=\"10\" disabled>#{content}</textarea>"
							buttons: [{text: 'Close'}]

			deleteRestriction: (event) ->
				target = $ Events.target event
				name = target.attr 'data-restriction'
				$.ajax
					type: 'DELETE'
					url: "ws/datamanager/restrictions/#{name}"
					success: () -> Backbone.history.loadUrl()

			showAddDialog: (event, isNew) ->
				unless isNew
					target = $ Events.target event
					name = target.attr 'data-restriction'					
				Layers.showTemplateInLayer
					title: (if isNew then 'Add restricted ref ids' else "Replace restricted ref ids for #{name}")
					template: 'admin/add-restriction'
					model: {isNew: isNew}
					buttons: [{
						text: 'Add', className: 'btn-success', callback: () ->
							if isNew
								name = $('#add-restriction #name').val()
							refIdsRaw = $('#add-restriction #ref-ids').val()
							unless name
								Forms.handleError 'add-restriction', {responseJSON: {field: 'name', message: 'No name specified'}}
								return
							unless refIdsRaw
								Forms.handleError 'add-restriction', {responseJSON: {field: 'ref-ids', message: 'No reference ids specified'}}
								return
							refIds = refIdsRaw.split '\n'
							$.ajax
								type: 'PUT'
								url: "ws/datamanager/restrictions/#{name}"
								data: JSON.stringify refIds
								contentType: 'application/json'
								success: () ->
									Layers.closeActive()
									Backbone.history.loadUrl()
								error: (response) -> 
									Forms.handleError 'add-restriction', response
					}]

)