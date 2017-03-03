define([
				'backbone'
				'cs!utils/Forms'
				'cs!utils/Layers'
				'cs!utils/Status'
			]

	(Backbone, Forms, Layers, Status) ->

		save: (type, id) ->
			file = $('#avatar', '#avatar-form')[0].files[0]
			unless file
				Layers.askQuestion
					title: 'Reset avatar'
					question: 'You did not select a new image, do you want to replace your current avatar with the default?'
					answers: ['No', 'Yes']
					onAnswer: (index) => 
						if index is 1
							@upload type, id
			else
				@upload type, id, file
			return false

		upload: (type, id, file) ->
			formData = new FormData()
			if file
				formData.append 'file', file
			else
				formData.append 'dummy', true
			@uploadData type, id, formData

		uploadData: (type, id, formData) ->
			$.ajax
				type: 'PUT'
				url: "ws/#{type}/avatar/#{id}"
				data: formData
				processData: false
				contentType: false
				success: (response) -> 
					Status.success "Successfully updated #{type} #{id}"
					if type is 'user'
						window.location.reload()
					else
						Backbone.history.loadUrl()
				error: (response) -> 
					Forms.handleError 'avatar-form', response

)