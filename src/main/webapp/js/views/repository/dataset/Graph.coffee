define () ->

	getModel: (dataset) ->
		model = {}
		@addNode model, dataset.referenceProcess, dataset, true
		console.log model
		return model

	addNode: (model, process, dataset, isRef) ->
		if model[process.id]
			return model[process.id]
		model[process.id] = {id: process.id, name: process.name, incoming: [], outgoing: [], isRef: isRef}
		for link in dataset.processLinks
			console.log 1
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