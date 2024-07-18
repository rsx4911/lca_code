define([
				'backbone'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'cs!utils/Synchronized'
				'templates/views/members/members'
				'templates/views/members/member-list'
			]

	(Backbone, Data, Events, Filter, Layers, Renderer, Roles, Synchronized, template, memberTemplate) ->

		class MembersView extends Backbone.View

			className: 'members-view'

			events: 
				'click [data-route]': (event) -> Events.followRoute event
				'click [data-action=add-members]': 'showAddMembersLayer'
				'click [data-action=set-role]': 'showSetRoleLayer'
				'click [data-action=remove-member]': 'removeMember'

			showAddMembersLayer: (event) ->
				type = if @group then 'group' else 'repository'
				existingUsers = []
				existingTeams = []
				for member in @members
					if member.team
						existingTeams.push member.team
					else
						existingUsers.push member.user
				Data.getUsersAndTeams 'members', null, (users, teams) =>
					Layers.showTemplateInLayer
						template: 'members/set-role'
						title: "Add #{type} members"
						model: {type: type, users: Data.usersToOptions(users, existingUsers), teams: Data.teamsToOptions(teams, existingTeams), roles: Roles.getAll(), isRoleAvailable: Roles.isAvailable }
						buttons: [{id: 'add-members', className: 'btn-success', text: "Add to #{type}", callback: () => @addMembers()}]

			addMembers: () ->
				users = []
				teams = []
				selection = $('#set-role-form #user-selection-name option:selected')
				role = $('#set-role-form #role').val()
				for option in selection
					option = $ option
					if option.attr('data-group-id') is 'user'
						users.push {id: option.val(), role: role}
					else if option.attr('data-group-id') is 'team'
						teams.push {id: option.val(), role: role}
				Synchronized.forEach ((user, finish) => @setRole('user', user, true, finish)), users, () =>
					Synchronized.forEach ((team, finish) => @setRole('team', team, true, finish)), teams, () =>
						Layers.closeActive()
						Backbone.history.loadUrl()

			showSetRoleLayer: (event) ->
				target = $ Events.target event, 'button'
				id = target.attr 'data-id'
				memberType = target.attr 'data-type'
				type = if @group then 'group' else 'repository'
				Layers.showTemplateInLayer
					template: 'members/set-role'
					title: "Set role for #{memberType} #{id}"
					model: {type: type, roles: Roles.getAll(), isRoleAvailable: Roles.isAvailable}	
					buttons: [{
						id: 'set-role'
						className: 'btn-success'
						text: 'Set role'
						callback: () => 
							role = $('#set-role-form #role').val()
							@setRole memberType, {id: id, role: role}, false, () => 
								Layers.closeActive()
								Backbone.history.loadUrl()
					}]				

			setRole: (type, member, isNew, finish) ->
				path = @getPath()
				url = "#{type}/#{member.id}/#{member.role}"
				$.ajax
					type: (if isNew then 'POST' else 'PUT')
					url: "#{path}/#{url}"
					success: finish

			removeMember: (event) ->
				target = $ Events.target event, 'button'
				type = target.attr 'data-type'
				id = target.attr 'data-id'
				path = @getPath()
				$.ajax
					type: 'DELETE'
					url: "#{path}/#{type}/#{id}"
					success: () -> Backbone.history.loadUrl()

			getPath: () ->
				if @group
					return 'ws/membership/' + @group.get('name')
				return 'ws/membership/' + @repository.get('group') + '/' + @repository.get('name')

			beforeRender: (type, result) ->
				if type is 'group-members' and @group
					@members = result.data
				else if type is 'repository-members'
					@members = result.data
				for member in result.data
					member.role = Roles[member.role]
				filtered = []
				ownerMembers = 0
				for member in result.data
					if type is 'group-members'
						if member.memberOf.indexOf('/') is -1
							filtered.push member
					else if type is 'repository-members'
						if member.memberOf.indexOf('/') isnt -1
							filtered.push member
					if member.role.id is 'OWNER'
						ownerMembers++
				$(".subheader-box[data-type=#{type}] .count").html filtered.length
				result.data = filtered
				if (type is 'group-members' and @group)
					result.canEdit = @group.get 'userCanEditMembers'
				if (type is 'repository-members' and @repository)		
					result.canEdit = @repository.get 'userCanEditMembers'
					result.group = @repository.get 'group'
				result.onlyOneOwner = ownerMembers is 1
				result.type = type

			initialize: (options) ->
				if options.group
					@group = options.group
					name = options.group.get 'name'
					@filter1 = new Filter
						type: 'group-members'
						beforeRender: (result, type) => @beforeRender type, result
						container: '#group-members'
						template: memberTemplate
						filterId: 'filter'
						noPaging: true
						url: () -> "ws/membership/#{name}?"
				else if options.repository
					@repository = options.repository
					group = options.repository.get 'group'
					name = options.repository.get 'name'
					@filter1 = new Filter
						type: 'repository-members'
						beforeRender: (result, type) => @beforeRender type, result
						container: '#repository-members'
						template: memberTemplate
						filterId: 'filter'
						noPaging: true
						url: () -> "ws/membership/#{group}/#{name}?"
					@filter2 = new Filter
						type: 'group-members'
						beforeRender: (result, type) => @beforeRender type, result
						container: '#group-members'
						template: memberTemplate
						filterId: 'filter'
						noPaging: true
						url: () -> "ws/membership/#{group}?"

			render: (renderOptions) ->
				showRepositoryMembers = false
				showGroupMembers = false
				group = null
				if @repository
					showRepositoryMembers = true
					if !@repository.get('groupIsUserNamespace')
						showGroupMembers = true
						group = @repository.get 'group'
				else if @group
					showGroupMembers = true
				canEdit = false
				if @group
					canEdit = @group.get 'userCanEditMembers'
				else if @repository		
					canEdit = @repository.get 'userCanEditMembers'
				@$el.html template
					showRepositoryMembers: showRepositoryMembers
					showGroupMembers: showGroupMembers
					group: group
					canEdit: canEdit
				Renderer.render @, renderOptions
				@filter1.init()
				if @filter2 && !@repository.get('groupIsUserNamespace')
					@filter2.init()

)