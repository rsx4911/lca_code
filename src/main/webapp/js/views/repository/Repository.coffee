define([
				'backbone'
				'cs!utils/Avatar'
				'cs!utils/Events'
				'cs!utils/Format'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/repository/repository'
			]

	(Backbone, Avatar, Events, Format, Forms, Layers, Renderer, Status, Router, currentUser, template) ->

		class RepositoryView extends Backbone.View

			className: 'repository-view multi-box-view'

			events:
				'submit #avatar-form': (event) -> 
					Events.preventDefault event
					Avatar.save 'repository', @repository.get('group') + '/' + @repository.get('name')
				'change [data-setting][type=checkbox]': 'toggleSetting'
				'change #maxSize': 'updateMaxSize'
				'change #unit': 'updateMaxSize'
				'keydown #maxSize': (event) -> Events.validateNumber event
				'click [data-action=delete-repository]': 'deleteRepository'
				'click [data-action=clone-repository]': 'openCloneLayer'
				'click [data-action=move-repository]': 'openMoveLayer'
				'click [data-action=export-repository]': 'exportRepository'

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@$el.html template
					repository: repository
				Renderer.render @, renderOptions
				Avatar.initCropper 'repository', @repository.get('group') + '/' + @repository.get('name')
				@setMaxSize parseFloat repository.settings.maxSize

			setMaxSize: (size) ->
				unless size
					return
				if size % 1073741824 is 0
					@$('#maxSize-group #unit').val('1073741824')
					@$('#maxSize').val(size / 1073741824)
				else
					@$('#maxSize-group #unit').val('1048576')
					@$('#maxSize').val(parseInt(size / 1048576))

			toggleSetting: (event) ->
				target = $ Events.target event
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				setting = target.attr 'data-setting'
				value = target.is ':checked'
				repository.settings[setting] = value
				$.ajax
					type: 'PUT'
					url: "ws/repository/settings/#{fullPath}/#{setting}/#{value}"

			updateMaxSize: (event) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				size = @$('#maxSize').val()
				unit = parseInt @$('#maxSize-group #unit').val()
				if unit is 1073741824
					@$('#size-value').html(parseInt(1000*repository.size/unit)/1000)
				else
					@$('#size-value').html(parseInt(repository.size/unit))
				if size
					percentage = parseInt(100*repository.size/(size*unit))
					@$('#size-group .size-indicator-overlay').css 'width', (100 - percentage) + '%'					
					@$('#size-group .size-indicator-overlay').css 'margin-left', percentage + '%'					
					@$('#size-group .size-indicator').show()
				else
					@$('#size-group .size-indicator').hide()
				if size isnt parseInt(size).toString()
					@setMaxSize parseInt repository.settings.maxSize
					return
				size = parseInt size
				value = size * unit
				repository.settings.maxSize = value
				$.ajax
					type: 'PUT'
					url: "ws/repository/settings/#{fullPath}/maxSize/#{value}"

			deleteRepository: (event) ->
				Events.preventDefault event
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				Layers.askDeleteQuestion "repository #{fullPath}", fullPath, () =>
					Layers.showProgressIndicator 'Deleting'
					@repository.destroy
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard/repositories'

			openCloneLayer: (event) ->
				Events.preventDefault event
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				@loadCommitsAndGroups (commits, groups) =>
					Layers.showTemplateInLayer
						title: "Clone #{fullPath}"
						template: 'repository/clone'
						model: {commits: commits, groups: groups, formatCommitDescription: Format.formatCommitDescription}
						buttons: [{text: 'Clone', className: 'btn-success', callback: () => @cloneRepository()}]
						callback: () =>
							$('.modal #name').val repository.name
							$('.modal #group').select repository.group

			openMoveLayer: (event) ->
				Events.preventDefault event
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				@loadGroups (groups) =>
					Layers.showTemplateInLayer
						title: "Move #{fullPath}"
						template: 'repository/move'
						model: {groups: groups}
						buttons: [{text: 'Move', className: 'btn-success', callback: () => @moveRepository()}]
						callback: () =>
							$('.modal #name').val repository.name
							$('.modal #group').val repository.group

			exportRepository: (event) ->
				Events.preventDefault event
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				@$('iframe#export-frame').remove()
				@$el.append '<iframe id="export-frame" class="hidden" border="0" height="0" width="0" src="ws/repository/export/' + fullPath + '"></iframe>'

			loadCommitsAndGroups: (callback) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				$.ajax
					type: 'GET'
					url: "ws/history/#{fullPath}"
					success: (commits) =>
						@loadGroups (groups) =>
							callback commits, groups

			loadGroups: (callback) ->
				$.ajax
					type: 'GET'
					url: 'ws/group?onlyIfCanWrite=true'
					success: (result) =>
						options = []
						if currentUser.get('username')
							options.push currentUser.get 'username'
						for group in result.data
							options.push group.name
						callback options

			cloneRepository: () ->
				repo = @repository.toJSON()
				newGroup = $('#group').val()
				newName = $('#name').val()
				commitId = $('#commit').val()
				unless newName
					Forms.handleError 'clone-form', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
				Layers.showProgressIndicator 'Cloning'
				$.ajax
					type: 'POST'
					url: "ws/repository/clone/#{repo.group}/#{repo.name}/#{commitId}/#{newGroup}/#{newName}"
					success: () -> 
						Layers.hideProgressIndicator()
						Layers.closeActive()
						Status.success 'Successfully cloned repository'
						Router.navigate "#{newGroup}/#{newName}"
					error: (response) -> 
						Layers.hideProgressIndicator()
						Forms.handleError 'clone-form', response

			moveRepository: () ->
				repo = @repository.toJSON()
				newGroup = $('#group').val()
				newName = $('#name').val()
				unless newName
					Forms.handleError 'move-form', {responseJSON: {field: 'name', message: 'Missing input: Name'}}
				Layers.showProgressIndicator 'Moving'
				$.ajax
					type: 'POST'
					url: "ws/repository/move/#{repo.group}/#{repo.name}/#{newGroup}/#{newName}"
					success: () -> 
						Layers.hideProgressIndicator()
						Layers.closeActive()
						Status.success 'Successfully moved repository'
						Router.navigate "#{newGroup}/#{newName}"
					error: (response) -> 
						Layers.hideProgressIndicator()
						Forms.handleError 'move-form', response

)