define([
				'cs!utils/ModelTypes'
				'jstree'
			] 

	(ModelTypes) ->

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
									if path
										if e.typeOfEntry is 'CATEGORY'
											data.push 
												id: "#{e.type}/#{e.fullPath}"
												text: e.name
												children: true
												icon: "images/model/small/category/#{e.type.toLowerCase()}.png"
												refId: e.refId
												type: 'CATEGORY'
												categoryType: e.type
												commitId: e.commitId
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
											id: e.type
											commitId: e.commitId
											text: ModelTypes[e.type]
											children: true
											icon: "images/model/small/category/#{e.type.toLowerCase()}.png"
								callback data

		# returns elements in three different types:
		# 1) ModelType elements, e.g. {fullPath: 'FLOW'}
		# 2) Category elements, e.g. {fullPath: $categoryType/$path}
		# 3) Model elements, e.g. {refId: '4321-...', type: 'FLOW'}
		# if a parent is already in the elements to be returned, child elements will not be added
		# because the tree is lazy loaded, the calling code must add missing (not selected in UI) elements anyway
		getSelection: (container, firstOnly) ->
			selected = $('#model-tree').jstree 'get_selected', true
			elements = []
			types = []
			paths = []
			for e in selected
				if !e.original.type # is model type 
					types.push e.original.id
					elements.push {fullPath: e.original.id, commitId: e.commitId}
			for e in selected
				if e.original.typeOfEntry is 'CATEGORY' # is category
					if $.inArray(e.original.type, types) isnt -1
						continue
					paths.push e.original.id
					elements.push {fullPath: "#{e.original.type}/#{e.original.id}", commitId: e.commitId}
			for e in selected
				if e.original.type && e.original.typeOfEntry isnt 'CATEGORY' # is model
					if $.inArray(e.original.type, types) isnt -1
						continue
					skip = false
					for path in paths
						if "#{e.original.type}/#{e.original.fullPath}".indexOf(path) is 0
							skip = true
							break
					if skip
						continue
					elements.push {type: e.original.type, refId : e.original.id, commitId: e.commitId}
			if firstOnly
				if elements.length
					return elements[0]
				return null
			return elements
 
)