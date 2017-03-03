define () ->

	getUsersAndTeams: (module, callback) ->
		@getUsers module, (users) =>
			@getTeams module, (teams) =>
				callback users, teams

	getUsers: (module, callback) ->
		$.ajax
			type: 'GET'
			url: "ws/user?module=#{module.toUpperCase()}"
			success: (users) =>
				callback users

	getTeams: (module, callback) ->
		$.ajax
			type: 'GET'
			url: "ws/team?module=#{module.toUpperCase()}"
			success: (teams) =>
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