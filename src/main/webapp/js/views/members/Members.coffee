define([
				'backbone'
				'cs!utils/Data'
				'cs!utils/Events'
				'cs!utils/Filter'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'templates/views/members/members'
				'templates/views/members/member-list'
			]

	(Backbone, Data, Events, Filter, Layers, Renderer, Roles, template, memberTemplate) ->

		class MembersView extends Backbone.View

			className: 'members-view'

			events: 
				'click [data-route]': (event) -> Events.followRoute event
				'click [data-action=add-members]': (event) -> @showAddMembersLayer event

			showAddMembersLayer: (event) ->
				type = if @group then 'group' else 'repository'
				existingUsers = []
				existingTeams = []
				for member in @members
					if member.team
						existingTeams.push member.team
					else
						existingUsers.push member.user
				Data.getUsersAndTeams (users, teams) =>
					Layers.showTemplateInLayer
						template: 'members/add'
						title: "Add #{type} members"
						model: {type: type, users: Data.usersToOptions(users, existingUsers), teams: Data.teamsToOptions(teams, existingTeams), roles: Roles.getAll()}
						buttons: [{id: 'add-members', className: 'btn-success', text: "Add to #{type}", callback: () => @addMembers()}]

			addMembers: () ->
				console.log 'ADD'

			beforeRender: (type, result) ->
				if type is 'group-members' and @group
					@members = result.data
				else if type is 'repository-members'
					@members = result.data
				for member in result.data
					member.role = Roles[member.role]
				filtered = []
				for member in result.data
					if type is 'group-members'
						if member.memberOf.indexOf('/') is -1
							filtered.push member
					else if type is 'repository-members'
						if member.memberOf.indexOf('/') isnt -1
							filtered.push member
				$(".subheader-box[data-type=#{type}] .count").html filtered.length
				result.data = filtered

			initialize: (options) ->
				if options.group
					@group = options.group
					name = options.group.get 'name'
					@filter1 = new Filter
						type: 'group-members'
						callback: (type, result) => @beforeRender type, result
						container: '#group-members'
						template: memberTemplate
						filterId: 'filter'
						url: (page, filter) -> "/ws/membership/#{name}/NULL?filter=#{filter}"
				else if options.repository
					@repository = options.repository
					group = options.repository.get 'group'
					name = options.repository.get 'name'
					@filter1 = new Filter
						type: 'repository-members'
						callback: (type, result) => @beforeRender type, result
						container: '#repository-members'
						template: memberTemplate
						filterId: 'filter'
						url: (page, filter) -> "/ws/membership/#{group}/#{name}?filter=#{filter}"
					@filter2 = new Filter
						type: 'group-members'
						callback: (type, result) => @beforeRender type, result
						container: '#group-members'
						template: memberTemplate
						filterId: 'filter'
						url: (page, filter) -> "/ws/membership/#{name}/NULL?filter=#{filter}"

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
				@$el.html template
					showRepositoryMembers: showRepositoryMembers
					showGroupMembers: showGroupMembers
					group: group
				Renderer.render @, renderOptions
				@filter1.init()
				if @filter2
					@filter2.init()

)