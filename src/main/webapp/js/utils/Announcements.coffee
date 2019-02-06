define([
				'cs!utils/LocalStorage'
				'cs!models/CurrentUser'
			]

	(LocalStorage, currentUser) ->

		announce: (announcement, id) ->
			lastAnnouncementId = LocalStorage.getString 'last-read-announcement'
			if !currentUser.isAdmin() and lastAnnouncementId and lastAnnouncementId is id
				return
			unless $('#announcements').length
				$('body').append '<div id="announcements"><table><tbody><tr><td id="announcement-message"></td></tr></tbody></table></div>'
			$('body').addClass 'has-announcements'
			text = if $('#announcement-message').length then $('#announcement-message') else $('#announcements')
			text.html announcement
			text.attr 'title', announcement
			unless $('#dismiss-announcement').length
				$('body').append '<button id="dismiss-announcement" class="btn btn-default btn-sm">Dismiss</button>'
				$('#dismiss-announcement').on 'click', () -> $('body').removeClass 'has-announcements'
				if !currentUser.isAdmin()
					LocalStorage.setValue 'last-read-announcement', id

		clear: () ->
			$('body').removeClass 'has-announcements'

)