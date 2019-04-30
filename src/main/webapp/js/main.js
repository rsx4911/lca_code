require.config({
	enforceDefine: true,
	baseUrl: 'js',
	paths: {
		'backbone': 'libs/backbone',
		'bootstrap': 'libs/bootstrap',
		'coffee-script': 'libs/coffee-script',
		'cropper': 'libs/cropper',
		'cs': 'libs/cs',
		'tablesorter': 'libs/jquery.tablesorter',
		'jquery': 'libs/jquery',
		'jstree': 'libs/jstree',
		'leaflet': 'libs/leaflet',
		'moment': 'libs/moment',
		'pace': 'libs/pace',
		'qrcode': 'libs/qrcode',
		'requireLib': 'libs/require',
		'underscore': 'libs/underscore'
	},
	shim: {
			'bootstrap': {
				deps: ['jquery'],
				exports: '$.fn.affix'				
			},
			'tablesorter': {
				exports: '$.fn.tablesorter'
			},
			'qrcode': {
				exports: 'QRCode'
			},
			'jstree': {
				deps: ['jquery'],
				exports: '$.fn.jstree'
			}
	} 
})

define(['cs!app/App', 'pace'], function(App, pace) {
	// pace.on('start', function() {$('body').append('<div id="block-interaction"></div>')});
	// pace.on('hide', function() {$('#block-interaction').remove()});
	pace.start();
	App.initialize();
});
