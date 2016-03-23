define () ->

	getUsersAndTeams: (callback) ->
		@getUsers (users) =>
			@getTeams (teams) =>
				callback users, teams

	getUsers: (callback) ->
		$.ajax
			type: 'GET'
			url: '/ws/user'
			success: (users) =>
				callback users.data

	getTeams: (callback) ->
		$.ajax
			type: 'GET'
			url: '/ws/team'
			success: (teams) =>
				callback teams.data

	usersToOptions: (users, existing) ->
		options = []
		for user in users
			exists = false
			for u in existing
				if u.username is user.username
					exists = true
			unless exists
				options.push [user.username, user.name]
		return options

	teamsToOptions: (teams, existing) ->
		options = []
		for team in teams
			exists = false
			for t in existing
				if t.teamname is team.teamname
					exists = true
			unless exists
				options.push [team.teamname, team.name]
		return options