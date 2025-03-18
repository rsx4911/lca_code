define([
				'cs!models/Settings'
			] 

	(settings) ->

		READER: 
			id: 'READER'
			name: 'Reader'
			descriptionForGroup: 'A reader can access the group and its repositories and fetch the contents'
			descriptionForRepository: 'A reader can access the repository and fetch its contents'

		CONTRIBUTOR:
			id: 'CONTRIBUTOR'
			name: 'Contributor'
			descriptionForGroup: 'A contributor can commit data to all repositories in the group'
			descriptionForRepository: 'A contributor can commit data to the repository'

		REVIEWER:
			id: 'REVIEWER'
			name: 'Reviewer'
			descriptionForGroup: 'A reviewer can comment specific fields and review data sets'
			descriptionForRepository: 'A reviewer can comment specific fields and review data sets'

		EDITOR:
			id: 'EDITOR'
			name: 'Editor'
			descriptionForGroup: 'An editor can approve comments and manage tasks'
			descriptionForRepository: 'An editor can approve comments and manage tasks'

		RELEASE_MANAGER:
			id: 'RELEASE_MANAGER'
			name: 'Release manager'
			descriptionForGroup: 'A release manager can release commits of a repository to the public'
			descriptionForRepository: 'A release manager can release commits of a repository to the public'

		OWNER:
			id: 'OWNER'
			name: 'Owner'
			descriptionForGroup: 'An owner can delete the group, add and delete repositories within the group and edit its members'
			descriptionForRepository: 'An owner can delete the repository and edit its members'

		isAvailable: (role) ->
			return role.id isnt 'RELEASE_MANAGER' or settings.is 'RELEASES_ENABLED'

		getAll: () ->
			return [@READER, @CONTRIBUTOR, @REVIEWER, @EDITOR, @RELEASE_MANAGER, @OWNER]

)