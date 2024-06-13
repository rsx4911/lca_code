define([
				'backbone'
			]

	(Backbone) ->

		class CurrentUser extends Backbone.Model

			url: 'ws/public'

			isLoggedIn: () ->
				if @get('id')
					return true
				return false

			isAdmin: () ->
				if @get('settings')?.admin
					return true
				return false

			isUserManager: () ->
				if @isAdmin()
					return true
				if @get('settings')?.userManager
					return true
				return false

			isDataManager: () ->
				if @isAdmin()
					return true
				if @get('settings')?.dataManager
					return true
				return false

			isLibraryManager: () ->
				if @isAdmin()
					return true
				if @get('settings')?.libraryManager
					return true
				return false

			isBlocked: (username) ->
				for user in @get('settings').blockedUsers
					if user.username is username
						return true
				return false

		return new CurrentUser()

)