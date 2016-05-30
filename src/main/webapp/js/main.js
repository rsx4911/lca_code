require.config({
	enforceDefine: true,
	baseUrl: '/js',
	paths: {
		'jquery': 'libs/jquery',
		'jquery-ui': 'libs/jquery-ui',
		'coffee-script': 'libs/coffee-script',
		'cs': 'libs/cs',
		'underscore': 'libs/underscore-amd',
		'backbone': 'libs/backbone-amd',
		'jadeRuntime': 'libs/jadeRuntime',
		'bootstrap': 'libs/bootstrap',
		'datepicker': 'libs/bootstrap-datepicker',
		'numeral': 'libs/numeral',
		'moment': 'libs/moment',
		'pace': 'libs/pace.min',
		'tablesorter': 'libs/jquery-tablesorter',
		'open-layers': 'libs/open-layers',
		'requireLib': 'libs/require'
	},
	shim: {
  	'tablesorter': {
      exports: '$.fn.tablesorter'
    }
	} 
})

define(['cs!app/App', 'pace'], function(App, pace) {
	pace.start();
	App.initialize();
});
