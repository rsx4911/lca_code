define () ->

	# group notifications
	GROUP_DELETED: 'A group was deleted'
	REPOSITORY_CREATED: 'A new repository was created'
	ADDED_TO_GROUP_MEMBERS: 'I was added to a group'
	GROUP_ROLE_CHANGED: 'My role in a group was changed'
	REMOVED_FROM_GROUP_MEMBERS: 'I was removed from a group'
	ADDED_GROUP_MEMBER: 'A member was added to a group'
	GROUP_ROLE_OF_MEMBER_CHANGED: 'A members role in a group was changed'
	REMOVED_GROUP_MEMBER: 'A member was removed from a group'

	# repository notifications
	REPOSITORY_MOVED: 'A repository was moved'
	REPOSITORY_DELETED: 'A repository was deleted'
	DATA_PUSHED: 'Data was pushed to a repository'
	ADDED_TO_REPOSITORY_MEMBERS: 'I was added to a repository'
	REPOSITORY_ROLE_CHANGED: 'My role in a repository was changed'
	REMOVED_FROM_REPOSITORY_MEMBERS: 'I was removed from a repository'
	ADDED_REPOSITORY_MEMBER: 'A member was added to a repository'
	REPOSITORY_ROLE_OF_MEMBER_CHANGED: 'A members role in a repository was changed'
	REMOVED_REPOSITORY_MEMBER: 'A member was removed from a repository'
	FIELD_COMMENTED: 'A comment was made on a data set'

	# own membership notifications
	ADDED_TO_TEAM_MEMBERS: 'I was added to a team'
	REMOVED_FROM_TEAM_MEMBERS: 'I was removed from a team'
	ADDED_TEAM_MEMBER: 'A member was added to a team'
	REMOVED_TEAM_MEMBER: 'A member removed from a team'

	# task notifications
	TASK_STARTED: 'A task was started'
	TASK_ASSIGNED: 'A task was assigned to me'
	TASK_REVOKED: 'A task assignment was revoked from me'
	TASK_CANCELED: 'A task I participate in was canceled'
	TASK_COMPLETED: 'A task I participate in was completed'

	# manager notifications
	USER_CREATED: 'A user was created'
	USER_REGISTERED: 'A user registered himself'
	USER_DELETED: 'A user was deleted'
	TEAM_CREATED: 'A team was created'
	TEAM_DELETED: 'A team was deleted'
	GROUP_CREATED: 'A new group was created'
	NOTIFY_FOR_ALL: 'Apply notifications also to groups, repositories and teams I am not in'

	getAll: () ->
		all = []
		for key in Object.keys(@)
			if typeof @[key] is 'function'
				continue
			if $.inArray(key, ['GROUP_DELETED', 'REPOSITORY_CREATED', 'ADDED_TO_GROUP_MEMBERS', 'GROUP_ROLE_CHANGED', 'REMOVED_FROM_GROUP_MEMBERS', 'ADDED_GROUP_MEMBER', 'GROUP_ROLE_OF_MEMBER_CHANGED', 'REMOVED_GROUP_MEMBER']) isnt -1
				group = 'Group'
			else if $.inArray(key, ['REPOSITORY_MOVED', 'REPOSITORY_DELETED', 'DATA_PUSHED', 'ADDED_TO_REPOSITORY_MEMBERS', 'REPOSITORY_ROLE_CHANGED', 'REMOVED_FROM_REPOSITORY_MEMBERS', 'ADDED_REPOSITORY_MEMBER', 'REPOSITORY_ROLE_OF_MEMBER_CHANGED', 'REMOVED_REPOSITORY_MEMBER']) isnt -1
				group = 'Repository'
			else if $.inArray(key, ['ADDED_TO_TEAM_MEMBERS', 'REMOVED_FROM_TEAM_MEMBERS', 'ADDED_TEAM_MEMBER', 'REMOVED_TEAM_MEMBER', 'FIELD_COMMENTED']) isnt -1
				group = 'Team'
			else if $.inArray(key, ['TASK_ASSIGNED', 'TASK_REVOKED', 'TASK_CANCELED', 'TASK_COMPLETED', 'TASK_STARTED']) isnt -1
				group = 'Task'
			else if $.inArray(key, ['USER_CREATED', 'USER_REGISTERED', 'USER_DELETED', 'TEAM_CREATED', 'TEAM_DELETED', 'GROUP_CREATED', 'NOTIFY_FOR_ALL']) isnt -1
				group = 'Manager'
			else
				continue
			all.push
				id: key
				label: @[key]
				group: group
		return all