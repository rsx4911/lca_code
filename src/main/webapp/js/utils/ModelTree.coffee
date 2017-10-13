define([
				'cs!utils/ModelTypes'
				'jstree'
			] 

	(ModelTypes) ->

		init: (container, repositoryPath) ->
			$(container).jstree 
				plugins: ['checkbox']
				core:
					themes:
						dots: false
					data: (node, callback) ->
						path = if node.id is '#' then '' else node.id
						$.ajax
							type: 'GET'
							url: "ws/public/browse/#{repositoryPath}?categoryPath=#{path}"
							success: (result) ->
								data = []
								if path
									result.entries.sort (a, b) -> return if a.name < b.name then -1 else if a.name > b.name then 1 else 0
								for e in result.entries
									if path
										if e.type is 'CATEGORY'
											data.push 
												id: "#{e.categoryType}/#{e.fullPath}"
												text: e.name
												children: true
												icon: "images/model/small/category/#{e.categoryType.toLowerCase()}.png"
												refId: e.refId
												type: 'CATEGORY'
												categoryType: e.categoryType
										else
											data.push 
												id: e.refId
												text: e.name
												commitId: e.commitId
												fullPath: e.fullPath
												icon: "images/model/small/#{e.type.toLowerCase()}.png"
												type: e.type
									else
										data.push
											id: e
											text: ModelTypes[e]
											children: true
											icon: "images/model/small/category/#{e.toLowerCase()}.png"
								callback data

		# returns elements in three different types:
		# 1) ModelType elements, e.g. {type: 'FLOW'}
		# 2) Category elements, e.g. {id: $path, type: 'CATEGORY'}
		# 3) Model elements, e.g. {id: '4321-...', type: 'FLOW'}
		# if a parent is already in the elements to be returned, child elements will not be added
		# because the tree is lazy loaded, the calling code must add missing (not selected in UI) elements anyway
		getSelection: (container) ->
			selected = $('#model-tree').jstree 'get_selected', true
			elements = []
			types = []
			paths = []
			for e in selected
				if !e.original.type 
					types.push e.original.id
					elements.push {type: e.original.id, id: null}
			for e in selected
				if e.original.type is 'CATEGORY'
					if $.inArray(e.original.categoryType, types) isnt -1
						continue
					paths.push e.original.id
					elements.push {type: 'CATEGORY', id: e.original.id}
			for e in selected
				if e.original.type && e.original.type isnt 'CATEGORY'
					if $.inArray(e.original.type, types) isnt -1
						continue
					skip = false
					for path in paths
						if "#{e.original.type}/#{e.original.fullPath}".indexOf(path) is 0
							skip = true
							break
					if skip
						continue
					elements.push {type: e.original.type, id: e.original.id, name: e.original.text, commitId: e.commitId}
			return elements
 
)