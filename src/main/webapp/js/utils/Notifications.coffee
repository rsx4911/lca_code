define () ->

	# group notifications
	GROUP_DELETED: 'A group was deleted'
	REPOSITORY_CREATED: 'A new repository was created'

	# repository notifications
	REPOSITORY_DELETED: 'A repository was deleted'
	DATA_COMMITTED: 'Data was commited to a repository'

	# membership notifications
	ADDED_TO_GROUP_MEMBERS: 'I was added to a group'
	REMOVED_FROM_GROUP_MEMBERS: 'I was removed from a group'
	ADDED_TO_REPOSITORY_MEMBERS: 'I was added to a repository'
	REMOVED_FROM_REPOSITORY_MEMBERS: 'I was removed from a repository'
	ADDED_TO_TEAM_MEMBERS: 'I was added to a team'
	REMOVED_FROM_TEAM_MEMBERS: 'I was removed from a team'

	# admin notifications
	USER_CREATED: 'A user was created'
	USER_DELETED: 'A user was deleted'
	TEAM_CREATED: 'A team was created'
	TEAM_DELETED: 'A team was deleted'
	GROUP_CREATED: 'A new group was created'
	NOTIFY_FOR_ALL: 'Apply notifications to all groups and repositories'

	getAll: () ->
		all = []
		for key in Object.keys(@)
			if typeof @[key] is 'function'
				continue
			if $.inArray(key, ['GROUP_DELETED', 'REPOSITORY_CREATED']) isnt -1
				group = 'Group'
			else if $.inArray(key, ['REPOSITORY_DELETED', 'DATA_COMMITTED']) isnt -1
				group = 'Repository'
			else if $.inArray(key, ['ADDED_TO_REPOSITORY_MEMBERS', 'REMOVED_FROM_REPOSITORY_MEMBERS', 'ADDED_TO_GROUP_MEMBERS', 'REMOVED_FROM_GROUP_MEMBERS', 'ADDED_TO_TEAM_MEMBERS', 'REMOVED_FROM_TEAM_MEMBERS']) isnt -1
				group = 'Member'
			else if $.inArray(key, ['USER_CREATED', 'USER_MODIFIED', 'TEAM_CREATED', 'TEAM_MODIFIED', 'ADDED_TEAM_MEMBER', 'REMOVED_TEAM_MEMBER', 'GROUP_CREATED', 'NOTIFY_FOR_ALL']) isnt -1
				group = 'Admin'
			else
				continue
			all.push
				id: key
				label: @[key]
				group: group
		return all