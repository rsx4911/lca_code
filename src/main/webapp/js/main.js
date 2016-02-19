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
		'open-layers': 'libs/open-layers',
		'requireLib': 'libs/require'
	}
})

define(['cs!app/App'], function(App) {
	App.initialize();
});
