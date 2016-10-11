define([
				'backbone'
				'cs!utils/Events'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!app/Router'
				'cs!models/CurrentUser'
				'templates/views/user-menu'
			]

	(Backbone, Events, Layers, Renderer, Router, currentUser, template) ->

		class UserMenu extends Backbone.View

			logout: (event) ->
				Events.preventDefault event
				$.ajax
					type: 'POST' 
					url: '/ws/public/logout'
					success: () -> window.location.href = '/login'
					error: () -> window.location.href = '/login'

			onSearchKeyUp: (event) ->
				if Events.keyCode(event) isnt 13
					return
				input = $ Events.target event, 'input'
				query = input.val()
				input.val ''
				if query
					Router.navigate "/search/query=#{query}"
				else
					Router.navigate '/search'

			events: 
				'click a[href]:not([target=_blank]):not(.logout):not([data-action])': (event) -> Events.followLink event
				'click a.logout': (event) -> @logout event
				'click a[data-action=upgrade]': (event) -> @openUpgradeDialog event
				'keyup #global-search': (event) -> @onSearchKeyUp event

			render: (renderOptions) ->
				if currentUser.isAdmin()
					$.ajax
						type: 'GET' 
						url: '/ws/admin/area/upgradeAvailable'
						success: (upgradeAvailable) =>
							@doRender renderOptions, upgradeAvailable
				else
					@doRender renderOptions

			doRender: (renderOptions, upgradeAvailable) ->
				@$el.html template 
					isAdmin: currentUser.isAdmin()
					upgradeAvailable: upgradeAvailable is 'true'
				Renderer.render @, renderOptions
				@$('[data-toggle=tooltip]').tooltip()

			openUpgradeDialog: (event) ->
				Events.preventDefault event
				loc = window.location
				schema = if loc.protocol is 'https' then 'wss' else 'ws'
				host = loc.host
				Layers.showProgressInLayer 
					title: 'Upgrading repositories' 
					url: "#{schema}://#{host}/sockets/admin/upgrade"
					message: 'Some repositories need to be upgraded to be used with the current version.'
					pageReloadOnClose: true
)