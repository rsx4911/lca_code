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
							url: "ws/public/browse/#{repositoryPath}?categoryPath=#{path}"
							success: (result) ->
								data = []
								if path
									result.data.sort (a, b) -> return if a.name < b.name then -1 else if a.name > b.name then 1 else 0
								for e in result.data
									data.push
										id: e.path
										refId: if e.typeOfEntry is 'DATASET' then e.refId else null
										text: if e.typeOfEntry is 'MODEL_TYPE' then ModelTypes[e.type] else e.name
										children: e.typeofEntry isnt 'DATASET'
										commitId: e.commitId
										icon: "images/model/small/#{if e.typeOfEntry isnt 'DATASET' then 'category/' else ''}#{e.type.toLowerCase()}.png"
								callback data

		# returns elements in three different types:
		# 1) ModelType elements, e.g. {path: 'FLOW'}
		# 2) Category elements, e.g. {path: $categoryType/$path}
		# 3) Model elements, e.g. {refId: '4321-...', type: 'FLOW'}
		# if a parent is already in the elements to be returned, child elements will not be added
		# because the tree is lazy loaded, the calling code must add missing (not selected in UI) elements anyway
		getSelection: (container, firstOnly) ->
			selected = $('#model-tree').jstree 'get_selected', true
			if firstOnly
				for e in selected
					if e.refId
						return refId
				return null
			paths = []
			for e in selected
				paths.push(e.original.id)
			paths = onlyRetainTopLevel paths			
			return paths
 
)