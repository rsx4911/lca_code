define () ->

		announce: (announcement) ->
			unless $('#announcements').length
				$('body').append '<div id="announcements"><table><tbody><tr><td id="announcement-message"></td></tr></tbody></table></div>'
			$('body').addClass 'has-announcements'
			text = if $('#announcement-message').length then $('#announcement-message') else $('#announcements')
			text.html announcement
			text.attr 'title', announcement
			unless $('#dismiss-announcement').length
				$('body').append '<button id="dismiss-announcement" class="btn btn-default btn-sm">Dismiss</button>'
				$('#dismiss-announcement').on 'click', () -> $('body').removeClass 'has-announcements'

		clear: () ->
			$('body').removeClass 'has-announcements'
