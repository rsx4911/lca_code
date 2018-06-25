define([
				'open-layers'
			]

	(OpenLayers) ->

		initMap: (dataset) ->
			if !dataset.longitude and !dataset.latitide
				return
			map = new OpenLayers.Map
				layers: [
					new OpenLayers.layer.Tile
						source: new OpenLayers.source.OSM()
				]
				target: 'map'
				view: new OpenLayers.View
					center: OpenLayers.proj.transform [dataset.longitude or 0, dataset.latitude or 0], 'EPSG:4326', 'EPSG:3857'
					zoom: 5

)