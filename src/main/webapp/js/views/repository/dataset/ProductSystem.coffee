define([
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!views/repository/dataset/Tree'
			]

	(Format, Layers, Tree) ->

		initGraph: (dataset) ->
			setTimeout () =>
				frameWindow = $('iframe')[0].contentWindow
				frameWindow.processes = @getModel dataset 
				frameWindow.modelIds = Object.keys(frameWindow.processes)
				frameWindow.render('2d', 15)
			, 100

		getModel: (dataset) ->
			model = {}
			@addNode model, dataset.refProcess, dataset, true
			return model

		addNode: (model, process, dataset, isRef) ->
			if model[process.id]
				return model[process.id]
			model[process.id] = {id: process.id, name: process.name, incoming: [], outgoing: [], isRef: isRef}
			for link in dataset.processLinks
				if link.process.id is process.id
					if $.inArray(link.provider.id, model[process.id].incoming) is -1
						model[process.id].incoming.push link.provider.id 
						newModel = @addNode model, link.provider, dataset
						newModel.outgoing.push link.process.id
				if link.provider.id is process.id
					if $.inArray(link.process.id, model[process.id].outgoing) is -1
						model[process.id].outgoing.push link.process.id 
						newModel = @addNode model, link.process, dataset
						newModel.incoming.push link.provider.id
			return model[process.id]

		initTree: (repository, dataset, commitId) ->
			Tree.init repository, dataset, commitId

)