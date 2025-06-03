define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Menu'
				'cs!utils/Renderer'
				'cs!models/CurrentUser'
				'templates/views/navigation'
			]

	(Backbone, Events, Menu, Renderer, currentUser, template) ->

		class Navigation extends Backbone.View

			doRender: (items, active, repository) ->
				@$el.html template
					username: currentUser.get 'username'
					name: currentUser.get 'name'
					items: items
					active: active
					repository: repository
				@menu = Menu.init @$('.menu-left')

			events: 
				'click a[href]:not([target=_blank])': (event) -> Events.followLink event

			render: (renderOptions) ->
				@doRender()
				Renderer.render @, renderOptions

			setItems: (type, items, active, repository) ->
				if type isnt @lastType or !@isSameRepository(repository)
					@doRender items, active, repository
				else
					@$('li.active').removeClass 'active'
					activeLi = @$ "li[data-nav-id=#{active}]"
					activeLi.addClass 'active'
					Menu.onClick @menu, activeLi
				@lastType = type
				@lastRepository = repository

			isSameRepository: (repository) ->
				if !repository && !@lastRepository
					return true 
				if !repository || !@lastRepository
					return false
				if repository.group isnt @lastRepository.group
					return false
				return repository.name is @lastRepository.name
)