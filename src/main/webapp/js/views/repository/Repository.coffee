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
				'click [data-action=reset-avatar]': (event) -> 
					Events.preventDefault event
					Avatar.upload 'repository', @repository.get('group') + '/' + @repository.get('name')
				'click [data-action=delete-repository]': (event) -> @deleteRepository event
				'click [data-action=clone-repository]': (event) -> @openCloneLayer event
				'click [data-action=move-repository]': (event) -> @openMoveLayer event

			initialize: (options) ->
				{@repository} = options

			render: (renderOptions) ->
				repository = @repository.toJSON()
				@$el.html template
					repository: repository
				Renderer.render @, renderOptions
				view = @
				$('#avatar').on 'change', () ->
					if @files and @files[0]
						reader = new FileReader()
						reader.onload = (e) ->
							 view.openCropper.call view, e.target.result
						reader.readAsDataURL @files[0]

			deleteRepository: (event) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				Layers.askDeleteQuestion "repository #{fullPath}", fullPath, () =>
					Layers.showProgressIndicator 'Deleting'
					@repository.destroy
						success: () =>
							Layers.hideProgressIndicator()
							Router.navigate 'dashboard/repositories'

			openCloneLayer: (event) ->
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

			loadCommitsAndGroups: (callback) ->
				repository = @repository.toJSON()
				fullPath = "#{repository.group}/#{repository.name}"
				$.ajax
					type: 'GET'
					url: "ws/history/#{fullPath}/null"
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

			openCropper: (data) ->
				Layers.showMessageInLayer
					title: 'Avatar selection'
					body: '<img class="image-crop" src="' + data + '">'
					buttons: [
						{text: 'Cancel', callback: () => @resetForm()}
						{text: 'Save', className: 'btn-success', callback: () => @saveCropped()}
					]
				@cropper = $('.image-crop').cropper 
					aspectRatio: 1
					dragMode: 'move'

			resetForm: () ->
				$('form#avatar-form')[0].reset()
				Layers.closeActive()

			saveCropped: () ->
				@cropper.cropper('getCroppedCanvas').toBlob (blob) =>
					formData = new FormData()
					formData.append 'file', blob
					Layers.closeActive()
					Avatar.uploadData 'repository', @repository.get('group') + '/' + @repository.get('name'), formData

)