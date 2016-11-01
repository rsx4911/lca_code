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