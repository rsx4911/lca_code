define () ->

	READER: 
		id: 'READER'
		name: 'Reader'
		descriptionForGroup: 'A reader can access the group and its repositories and fetch the contents'
		descriptionForRepository: 'A reader can access the repository and fetch its contents'

	CONTRIBUTOR:
		id: 'CONTRIBUTOR'
		name: 'Contributor'
		descriptionForGroup: 'A contributor can add new repositories to the group and commit content'
		descriptionForRepository: 'A contributor can commit data to the repository'

	OWNER:
		id: 'OWNER'
		name: 'Owner'
		descriptionForGroup: 'An owner can delete the group and repositories within the group and edit its members'
		descriptionForRepository: 'An owner can delete the repository and edit its members'

	getAll: () ->
		return [@READER, @CONTRIBUTOR, @OWNER]