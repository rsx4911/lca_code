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
		'qrcode': 'libs/qrcode',
		'requireLib': 'libs/require',
		'underscore': 'libs/underscore',
		'd3': 'libs/d3',
		'd3cloud': 'libs/d3.layout.cloud',
		'sockjs': 'libs/sockjs',
		'stomp': 'libs/stomp'
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
			'stomp': {
				exports: 'Stomp'
			},
			'jstree': {
				deps: ['jquery'],
				exports: '$.fn.jstree'
			}
	} 
})

define(['cs!app/App'], function(App) {
	App.initialize();
});
