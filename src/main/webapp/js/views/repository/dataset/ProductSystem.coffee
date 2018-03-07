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
			@addNode model, dataset.referenceProcess, dataset, true
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

		selectImpactMethod: (repository, dataset) ->
			repositoryPath = repository.get('group') + '/' + repository.get('name')
			Layers.selectModel 
				repositoryPath: repositoryPath
				multipleSelection: false
				selectVersion: true
				type: 'IMPACT_METHOD'
				callback: (methodId, commitId) =>
					Layers.closeActive()
					Layers.showProgressIndicator 'Loading'
					$.ajax
						type: 'GET'
						url: "ws/public/browse/#{repositoryPath}/IMPACT_METHOD/#{methodId}"
						success: (impactMethod) => @applyImpactMethod dataset, impactMethod
						error: () -> Layers.hideProgressIndicator()

		applyImpactMethod: (dataset, method) ->
			$('.impact-method').html method.name
			table = $ 'table.impact-result-table'
			$('tbody', table).empty()
			for category in method.impactCategories
				category.result = @calculateResult dataset, category
				$('tbody', table).append "<tr><td>#{category.name}</td><td>#{Format.scientific(category.result)} #{category.referenceUnitName}</td></tr>"
			table.unbind('appendCache applyWidgetId applyWidgets sorton update updateCell').removeClass('tablesorter').find('thead th').unbind('click mousedown').removeClass('header headerSortDown headerSortUp')
			table.tablesorter()
			table.show()
			Layers.hideProgressIndicator()

		calculateResult: (dataset, category) ->
			result = 0
			for factor in category.impactFactors
				for exchange in dataset.inventory
					if exchange.flow.id is factor.flow['@id']
						result += factor.value * exchange.amount
			return result

)