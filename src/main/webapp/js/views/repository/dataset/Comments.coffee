define([
				'cs!utils/Events'
			]

	(Events) ->

		init: (parent) ->
			for element in $('[data-path]', parent)
				field = @toLabel $(element).attr 'data-path'
				title = if field then "Comment '#{field}'" else 'Comment data set'
				$(element).append '<img title="' + title + '" src="images/comment.png" data-action="comment"></a>'
			$('[data-path] [data-action=comment]', parent).on 'click', (event) => 
				Events.preventDefault event
				target = $ Events.target event
				while !target.attr('data-path')
					target = target.parent()
				path = target.attr 'data-path'
				@showComments path

		showComments: (path) ->
			console.log path

		toLabel: (field) ->
			if field.indexOf('.') isnt -1
				field = field.substring field.indexOf('.') + 1
			if field.indexOf('[') isnt -1
				field = field.substring 0, field.indexOf('[')
				if field is 'impactCategories'
					field = 'impactCategory'
				else if field is 'processes'
					field = 'process'
				else if field.charAt(field.length - 1) is 's'
					field = field.substring 0, field.length - 1
			result = ''
			for character, index in field
				if index is 0
					result = character.toUpperCase()
				else if character.toLowerCase() is character
					result += character
				else 
					result += ' ' + character.toLowerCase()
			return result

)