define([
				'cs!utils/ModelTypes'
				'jstree'
			] 

	(ModelTypes) ->

		countChar = (str, find) ->
			count = 0
			for c in str
				if c is find
					count++
			return count

		onlyRetainTopLevel = (paths) -> 
			conjuncted = []
			if paths.find((path) -> path is '')
				return conjuncted
			paths.sort (p1, p2) -> countChar(p1, '/') -  countChar(p2, '/')
			paths.forEach (path) ->
				unless conjuncted.find((candidate) -> path.startsWith(candidate + '/'))
					conjuncted.push(path)
			return conjuncted
	
		init: (container, repositoryPath, options) ->
			defaultPath = options?.defaultPath || ''
			multipleSelection = options?.multipleSelection || false
			commitId = if options.commitId then "&commitId=#{options.commitId}" else ''
			$(container).jstree 
				plugins: if multipleSelection then ['checkbox'] else []
				core:
					multiple: multipleSelection,
					themes:
						dots: false
					data: (node, callback) ->
						path = if node.id is '#' then defaultPath else node.id
						$.ajax
							type: 'GET'
							url: "ws/public/browse/#{repositoryPath}?categoryPath=#{path}#{commitId}"
							success: (result) ->
								data = []
								if path
									result.data.sort (a, b) -> return if a.name < b.name then -1 else if a.name > b.name then 1 else 0
								for e in result.data
									if e.isRepositoryInfo or e.isLibrary
										continue
									data.push
										id: e.path
										refId: if e.isDataset then e.refId else null
										text: if e.isModelType then ModelTypes[e.type] else e.name
										children: !e.isDataset
										commitId: e.commitId
										icon: "images/model/small/#{if !e.isDataset then 'category/' else ''}#{e.type.toLowerCase()}.png"
								callback data

		getSelection: (container, firstOnly) ->
			selected = $('#model-tree').jstree 'get_selected', true
			if firstOnly
				for e in selected
					if e.original.refId
						return e.original.refId
				return null
			paths = []
			for e in selected
				paths.push(e.original.id)
			paths = onlyRetainTopLevel paths			
			return paths
 
)