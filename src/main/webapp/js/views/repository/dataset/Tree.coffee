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
						actions.push label: 'Open process', action: () => @openProcess @toId(node.id)
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
							element = @toElement @dataset.referenceProcess
							element.state = opened: true
							elements.push element
						else
							id = id.substring 0, id.indexOf('@')
							for link in @dataset.processLinks
								if link.process.id is id
									elements.push @toElement link.provider
						callback elements

		toElement: (process) ->
			count = @nodeCount[process.id]
			unless count
				count = 0
			count++
			@nodeCount[process.id] = count
			element =
				id: process.id + '@' + count
				text: process.name
				children: true # todo
				icon: "images/model/small/process.png"
			return element

		openProcess: (id) ->
			group = @repository.get 'group'
			name = @repository.get 'name'
			Router.navigate "#{group}/#{name}/dataset/PROCESS/#{id}?commitId=" + @commitId

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