define([
				'backbone'
				'cs!app/Router'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'templates/views/libraries/add-library'
			]

	(Backbone, Router, Data, Events, Forms, Layers, Renderer, Status, template) ->

		class AdminAddLibrary extends Backbone.View

			getAccessTypes = (isAdminArea, callback) ->
				if isAdminArea
					callback [['PUBLIC', 'Public'], ['USER', 'Only users'], ['MEMBER', 'Only members of linking repository']]
					return
				Data.getTeams 'TEAM_LIBRARIES', (teams) ->
					options = []
					for team in teams
						options.push([team.teamname, team.name])
					callback options

			className: 'multi-box-view'

			events:
				'click [data-action=add]': 'addLibrary'

			initialize: (options) ->
				@isAdminArea = options?.isAdminArea

			render: (renderOptions) ->
				getAccessTypes @isAdminArea, (accessTypes) =>
					@$el.html template
						isAdminArea: @isAdminArea
						accessTypes: accessTypes
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
						if @isAdminArea
							Router.navigate 'administration/libraries'
						else
							Router.navigate 'user/libraries'
					error: (response) ->
						Layers.hideProgressIndicator()
						Forms.handleError 'library-form', response

)