define([
				'cs!utils/Events'
			]

	(Events) ->

		Forms = () ->

		Forms:: = (() ->

			getValidator = (validator) ->
				if typeof validator == 'function'
					return validator
				return @["validate#{validator}"]

			constructor: Forms

			toJson: (form) ->
				values = {}
				fields = $ 'input, select, textarea', "##{form}"
				for field in fields
					field = $ field
					name = field.attr 'name'
					type = field.attr 'type'
					if type is 'checkbox'
						values[name] = field.is ':checked'
					else if type isnt 'file'
						values[name] = field.val()
				return values

			fill: (form, json) ->
				fields = $ 'input, select, textarea', "##{form}"
				for field in fields
					field = $ field
					name = field.attr 'name'
					type = field.attr 'type'
					value = json[name]
					if value or value is 0
						if type is 'checkbox'
							field.prop 'checked', value
						else if type isnt 'file'
							field.val value				

			fileInputs: (form) ->
				result = []
				inputs = $ 'input[type=file]', "##{form}"
				for input in inputs
					if $(input).val()
						result.push input
				return result

			getFileName: (form, id) ->
				fileName = $("##{id}").val()
				if fileName.indexOf('\\') isnt -1
					fileName = fileName.substring(fileName.lastIndexOf('\\') + 1)
				if fileName.indexOf('/') isnt -1
					fileName = fileName.substring(fileName.lastIndexOf('/') + 1)
				return fileName

			message: (form, selector, message) ->
				$("#{selector} span.message, #{selector}.label", "##{form}")?.html message
				$("#{selector}.alert, #{selector}.label", "##{form}")?.show()

			handleError: (form, response) ->
				@clear form
				if response.responseJSON
					error = response.responseJSON
					@markWithMessage form, error.field, error.message
				else
					message = response.responseText
					unless message
						message = 'Unknown error'
					@markGroup 
						form: form
						id: "#{form}-general-message"
						type: 'error'
						message: message

			markWithMessage: (form, field, message, validator) ->
				@markGroup 
					form: form
					id: form + '-general-message'
					type: 'error'
					message: message
					validator: validator
					validateId: field
				@markGroup 
					form: form
					id: field
					type: 'error'
					validator: validator

			markGroup: (options) ->
				if options.message
					@message options.form, "##{options.id}-group", options.message
				widget = $ "##{options.id}-group", "##{options.form}"
				switch options.type
					when 'error' 
						if widget.hasClass 'alert'
							clazz = 'alert-danger'
						else if widget.hasClass 'label'
							clazz = 'label-danger'
						else
							clazz = 'has-error'
					when 'warning'
						if widget.hasClass 'alert'
							clazz = 'alert-warning'
						else if widget.hasClass 'label'
							clazz = 'label-warning'
						else
							clazz = 'has-warning'
					when 'success'
						if widget.hasClass 'alert'
							clazz = 'alert-success'
						else if widget.hasClass 'label'
							clazz = 'label-success'
						else
							clazz = 'has-success'
				widget.addClass clazz
				if options.validator
					if options.validateElement 
						elements = options.validateElement
					else if options.validateId
						elements = $ "##{options.validateId}", "##{options.form}"
					else
						elements = $ "##{options.id}", "##{options.form}"
					eventName = 'keyup'
					for element in elements
						element = $ element
						type = element.attr('type')
						isDatepicker = element.data('datepicker')
						if type is 'checkbox' or type is 'file' or isDatepicker
							eventName = 'change'
						element.on "#{eventName}.validator", (event) => 
							validator = (@_ getValidator) options.validator
							if validator element
								@clearGroup options.form, options.id
								$(Events.target event).off "#{eventName}.validator"
								options.onValid?()

			clearGroup: (form, id) ->
				$("##{id}-group", "##{form}").removeClass 'has-error has-warning has-success'
				$("##{id}-group span.message", "##{form}").empty()
				$("##{id}-group.alert, ##{id}-group.label", "##{form}")?.hide()

			clear: (form) ->
				$('*', "##{form}").removeClass 'has-error has-warning has-success alert-danger alert-warning alert-success label-danger label-warning label-success'
				$('span.message', "##{form}").empty()
				$('.alert', "##{form}")?.hide()

			validateNotEmpty: (element) ->
				if element.val()
					return true
				return false

			validateChecked: (element) ->
				if element.is ':checked'
					return true
				return false

			_: (callback) ->
				() =>
					callback.apply @, arguments

		)()

)