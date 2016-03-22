define([
				'backbone'
				'cs!utils/Renderer'
				'cs!utils/Roles'
				'templates/views/members/members'
			]

	(Backbone, Renderer, Roles, template) ->

		class MembersView extends Backbone.View

			loadMembers: (callback) ->
				group = @groupOrRepository.get 'group'
				name = @groupOrRepository.get 'name'
				path = if group then "#{group}/#{name}" else "#{name}/NULL"
				$.ajax
					type: 'GET'
					url: "/ws/membership/#{path}"
					success: callback

			createModel: (members) ->
				repoMembers = []
				groupMembers = []
				path = if group then "#{group}/#{name}" else name
				group = path
				if path.indexOf('/') isnt -1
					group = path.substring 0, path.indexOf('/')
				for member in members
					member.role = Roles[member.role]
					if member.memberOf is group
						groupMembers.push member
					else
						repoMembers.push member
				return {
					repoMembers: repoMembers
					groupMembers: groupMembers
				}

			className: 'members-view'

			initialize: (options) ->
				{@groupOrRepository} = options

			render: (renderOptions) ->
				@loadMembers (members) =>
					@$el.html template @createModel members
					Renderer.render @, renderOptions

)