define([
				'leaflet',
			]

	(Leaflet) ->

		initMap: (dataset) ->
			if !dataset.longitude and !dataset.latitide
				return
			map = Leaflet.map('map').setView [dataset.latitude or 0, dataset.longitude or 0], 5
			Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
				attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
			}).addTo map
			Leaflet.circle([dataset.latitude, dataset.longitude], {
				color: 'red',
				fillColor: '#f03',
				fillOpacity: 0.5,
				radius: 1
			}).addTo map

)
