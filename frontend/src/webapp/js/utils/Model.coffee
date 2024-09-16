define([
				'backbone'
				'cs!utils/Synchronized'
			]

	(Backbone, Synchronized) ->

		invalidate: (model) ->
			model.fetched = false
			if model instanceof Backbone.Collection
				for element in model.models
					element.fetched = false

		waitFor: (model, callback) ->
			if model.syncing
				setTimeout (() => @waitFor(model, callback)), 10
			else
				callback?()

		fetchAll: (models, callback) ->
			load = (model, finish) => 
				@fetch model, 
					success: finish
			Synchronized.forEach load, models, callback

		fetch: (model, options) ->
			unless model
				options?.success?()
				return
			options = options or {}
			if model.fetched and not options.force
					options.success?()
			else
				if model.syncing
					setTimeout (() => @fetch(model, options)), 10
				else
					model.syncing = true
					model.fetch 
						success: () ->
							model.fetched = true
							model.syncing = false
							options.success?()
						error: () ->
							model.syncing = false
							options.error?()

		save: (model, options) ->
			unless model
				options?.success?()
				return
			options = options or {}
			if model.syncing
				setTimeout (() => @save(model, options)), 10
			else
				model.syncing = true
				model.save null,
					success: () ->
						model.fetched = true
						model.syncing = false
						options.success?()
					error: (model, response) ->
						model.syncing = false
						options.error?(model, response)

		createFrom: (collection) ->
			# this is used to create new models which dont have its own defined class without adding them directly to the collection
			model = collection.add {}, {silent: true}		
			collection.remove model, {silent: true}
			if $.isFunction(collection.modelUrlRoot)
				model.urlRoot = collection.modelUrlRoot()
			else if collection.modelUrlRoot
				model.urlRoot = collection.modelUrlRoot
			else
				model.urlRoot = collection.url()
			return model

		create: (url) ->
			model = Backbone.Model.extend {urlRoot: url} 
			return new model()

		copyFields: (from, to) ->
			fields = Object.keys from
			for field in fields
				value = if typeof(from.get) is 'function' then from.get(field) else from[field]
				if typeof(to.set) is 'function'
					to.set field, value
				else
					to[field] = value

)