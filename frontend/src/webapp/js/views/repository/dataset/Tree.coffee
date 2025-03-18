define([
				'cs!app/Router'
				'jstree'
			] 

	(Router) ->

		init: (repository, dataset, commitId) ->
			@repository = repository
			@dataset = dataset
			@commitId = commitId
			@nodeCount = {}
			$('#process-tree-container').jstree 
				plugins: ['contextmenu', 'sort', 'state']
				contextmenu: 
					items: (node, callback) =>
						actions = []
						actions.push label: 'Open model', action: () => @openProcess node.original.type, @toId(node.id)
						if document.queryCommandSupported('copy')
							actions.push label: 'Copy name', action: () => @copyName node.text
						callback actions
					show_at_node: false
				state: 
					key: @getKey()
				core:
					multiple: false
					themes: 
						dots: false
					data: (node, callback) =>
						id = node.id
						elements = []
						if id is '#'
							element = @toElement @dataset.refProcess
							element.state = opened: true
							elements.push element
						else
							id = id.substring 0, id.indexOf('@')
							for link in @dataset.processLinks
								if link.process.id is id
									elements.push @toElement link.provider
						callback elements

		toElement: (provider) ->
			count = @nodeCount[provider.id]
			unless count
				count = 0
			count++
			@nodeCount[provider.id] = count
			element =
				id: provider.id + '@' + count
				text: provider.name
				type: provider.type
				children: @hasChildren(provider)
				icon: "images/model/small/#{@getImage(provider)}"
			return element

		getImage: (provider) ->
			if provider.type is 'Process' 
				if provider.processType is 'LCI_RESULT' or provider.processType is 'SYSTEM'
					return 'process_system.png'
				return 'process.png'
			return 'product_system.png'

		hasChildren: (provider) ->
			if provider.type is 'ProductSystem'
				return false
			for link in @dataset.processLinks
				if link.process.id is provider.id
					return true
			return false


		openProcess: (type, id) ->
			group = @repository.get 'group'
			name = @repository.get 'name'
			dsType = if type is 'Process' then 'PROCESS' else 'PRODUCT_SYSTEM'
			Router.navigate "#{group}/#{name}/dataset/#{dsType}/#{id}?commitId=" + @commitId

		copyName: (name) ->
			randomId = 'id-' + Math.random()
			randomId = randomId.replace '.', '-'
			$('body').append('<textarea id="' + randomId + '">' + name + '</textarea>')
			$("##{randomId}")[0].select()
			document.execCommand('copy')
			$("##{randomId}").remove()

		toId: (id) ->
			return id.substring 0, id.indexOf('@')

		getKey: () ->
			group = @repository.get 'group'
			name = @repository.get 'name'
			refId = @dataset.id
			return "process-tree/#{group}/#{name}/#{refId}/" + @commitId

)