define([
				'cs!models/CurrentUser'
			]

	(currentUser) ->

		getUsersAndTeams: (module, repositoryPath, callback) ->
			@getUsers module, repositoryPath, (users) =>
				@getTeams module, (teams) ->
					callback users, teams

		getUsers: (module, repositoryPath, callback) ->
			unless repositoryPath 
				repositoryPath = ''
			$.ajax
				type: 'GET'
				url: "ws/user?module=#{module.toUpperCase()}&repositoryPath=#{repositoryPath}"
				success: (users) ->
					callback users

		getTeams: (module, callback) ->
			$.ajax
				type: 'GET'
				url: "ws/team?module=#{module.toUpperCase()}"
				success: (teams) ->
					callback teams

		usersToOptions: (users, existing = [], skipExisting = false) ->
			options = []
			for user in users
				exists = false
				for u in existing
					if u.username is user.username
						exists = true
				if exists and skipExisting
					continue
				options.push [user.username, user.name, exists]
			return options

		teamsToOptions: (teams, existing = [], skipExisting = false) ->
			options = []
			for team in teams
				exists = false
				for t in existing
					if t.teamname is team.teamname
						exists = true
				if exists and skipExisting
					continue
				options.push [team.teamname, team.name, exists]
			return options

		getAccessTypes: () ->
			accessTypes = []
			if currentUser.isLibraryManager()
				accessTypes.push ['PUBLIC', 'Public']				
				accessTypes.push ['USER', 'All users']
			accessTypes.push ['MEMBER', 'All members of linking repositories']
			return accessTypes

)