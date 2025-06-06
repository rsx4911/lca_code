define([
				'backbone'
				'cs!utils/Layers'
				'cs!utils/Renderer'
				'cs!utils/Status'
				'templates/views/dashboard/library-updater'
			]

	(Backbone, Layers, Renderer, Status, template) ->

        class LibraryUpdater extends Backbone.View

            disableOption: (selectId, disabled) ->
                for o in $("##{selectId} option")
                    option = $ o
                    value = option.attr('value')
                    unless value
                        continue
                    $(option).prop 'disabled', value is disabled

            onChange: () ->
                toReplaceId = @$('#toReplace').val()
                @disableOption 'replaceWith', toReplaceId
                replaceWithId = @$('#replaceWith').val()
                @disableOption 'toReplace', replaceWithId
                toReplace = @libraries.find((lib) -> lib.name is toReplaceId)
                replaceWith = @libraries.find((lib) -> lib.name is replaceWithId)
                noReposLabel = @$ '#no-repos-label'
                pleaseSelectLabel = @$ '#please-select-label'
                pleaseSelectLabel.removeClass 'hidden'
                noReposLabel.addClass 'hidden'
                if toReplaceId and replaceWithId
                    pleaseSelectLabel.addClass 'hidden'
                    repos = toReplace.currentlyLinkedIn.filter((repo) -> replaceWith and !replaceWith.currentlyLinkedIn.find((r) -> r is repo))
                    if !repos.length
                        noReposLabel.removeClass 'hidden'
                for c in @$('input[type=checkbox]')
                    checkbox = $ c
                    div = checkbox.parent().parent()
                    div.addClass 'hidden'
                    if (repos or []).find((repo) -> repo is checkbox.attr('id'))
                        div.removeClass 'hidden'
                @updateButton()

            getSelectedRepos: () ->
                selectedRepos = []
                for c in @$('input[type=checkbox]:checked')
                    checkbox = $ c
                    if !checkbox.parent().parent().hasClass('hidden')
                        selectedRepos.push checkbox.attr 'id'
                return selectedRepos

            updateButton: () ->
                canUpdate = @getSelectedRepos().length and @$('#message').val()
                $('#btn-update-library').prop 'disabled', !canUpdate

            update: () ->
                toReplace = @$('#toReplace').val()
                replaceWith = @$('#replaceWith').val()
                repos = @getSelectedRepos()
                message = @$('#message').val()
                Layers.showProgressIndicator 'Updating'
                $.ajax
                    method: 'POST',
                    url: "ws/libraries/replace/#{toReplace}/#{replaceWith}"
                    contentType: 'application/json'
                    data: JSON.stringify 
                        repositories: repos
                        message: message
                    success: (repos) ->
                        Layers.hideProgressIndicator()
                        Layers.closeActive()
                        Status.success "Successfully updated repositories: #{repos.join(', ')}"
                        Backbone.history.loadUrl()

            events:
                'change #toReplace, #replaceWith': 'onChange'
                'change input[type=checkbox]': 'updateButton'
                'keyup #message': 'updateButton'

            initialize: (options) ->
                {@libraries} = options

            render: (renderOptions) ->
                @$el.html template
                    libraries: @libraries
                $('#btn-update-library').prop 'disabled', true
                Renderer.render @, renderOptions

)