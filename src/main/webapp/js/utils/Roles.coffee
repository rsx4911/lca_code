define () ->

	READER: 
		name: 'Reader'
		description: 'A reader can access a a group or repository and also fetch its contents'

	CONTRIBUTOR:
		name: 'Contributor'
		description: 'A contributor can add new repositories to a group and contribute data to a repository'

	OWNER:
		name: 'Owner'
		description: 'An owner can delete groups and repositories as well as edit their members'