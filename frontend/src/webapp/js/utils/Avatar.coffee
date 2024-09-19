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

		initCropper: (type, id) ->
			at = @
			$('#avatar').on 'change', () ->
				if @files and @files[0]
					reader = new FileReader()
					reader.onload = (e) ->
						at.openCropper e.target.result, type, id
					reader.readAsDataURL @files[0]

		openCropper: (data, type, id) ->
			Layers.showMessageInLayer
				title: 'Avatar selection'
				body: '<img class="image-crop" src="' + data + '">'
				buttons: [
					{text: 'Cancel', callback: () => @resetForm()}
					{text: 'Save', className: 'btn-success', callback: () => @onSave(type, id)}
				]
			@cropper = $('.image-crop').cropper 
				aspectRatio: 1
				dragMode: 'move'

		onSave: (type, id) ->
			formData = @toFormData @cropper.cropper('getCroppedCanvas')
			Layers.closeActive()
			@uploadData type, id, formData

		resetForm: () ->
			$('form#avatar-form')[0].reset()
			Layers.closeActive()

		toFormData: (canvas) ->
			data = []
			compression = 1
			while !data.length or data.length > 256000
				url = canvas.toDataURL('image/jpeg', compression)
				data = url.substring(url.indexOf(';') + 8)
				compression *= 0.95
			formData = new FormData()
			formData.append 'file', @base64toBlob data
			return formData

		base64toBlob: (data, contentType = 'image/jpeg', sliceSize = 512) ->
			byteCharacters = atob data
			byteArrays = []
			offset = 0
			while offset < byteCharacters.length
				slice = byteCharacters.slice offset, offset + sliceSize
				byteNumbers = new Array slice.length
				i = 0
				while i < slice.length
					byteNumbers[i] = slice.charCodeAt i++
				byteArray = new Uint8Array byteNumbers
				byteArrays.push byteArray
				offset += sliceSize
			return new Blob byteArrays, {type: contentType}


)