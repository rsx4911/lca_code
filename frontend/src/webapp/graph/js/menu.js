window.Menu = {

	_locations: {},
	_categories: {},
	_flowProperties: {},
	_diagram: null,
	
	init: function (diagram, processes) {
		this._diagram = diagram;
		this._initMaps(processes);
		this._initMulti();
		this._initDetails();
	},

	_initMaps: function (processes) {
		var modelIds = Object.keys(processes);
		for (var i = 0; i < modelIds.length; i++) {
			var process = processes[modelIds[i]];
			this._addToMap(this._flowProperties, process.flowProperty);
			if (process.categories) {
				this._addToMap(this._categories, process.categories[0]);
			}
			this._addToMap(this._locations, process.location);
		}		
	},
	
	_addToMap: function (map, value) {
		var rand = function () { return parseInt(Math.random() * 255); };
		if (!value) {
			return;
		}
		if (map[value]) {
			map[value].count++;
		} else {
			map[value] = {count: 1, color: 'rgb(' + rand() + ', ' + rand() + ', ' + rand() + ')'};
		}
	},
	
	_initMulti: function () {
		this._addMultiOptions('Flow property', 'flowProperties');
		this._addMultiOptions('Category', 'categories');
		this._addMultiOptions('Location', 'locations');	
		$('#multi-highlighter').on('change', function (event) {Menu._highlight(event);});				
	},
	
	_addMultiOptions: function (label, type) {
		$('#multi-highlighter').append('<option value="' + type + '">' + label + '</option>');				
	},
		
	_initDetails: function () {
		this._addDetailOptions('Flow property', 'flowProperties');
		this._addDetailOptions('Category', 'categories');
		this._addDetailOptions('Location', 'locations');	
		$('#detail-highlighter').on('change', function (event) {Menu._highlight(event);});		
	},

	_addDetailOptions: function (label, type) {
		var map = this['_' + type];
		$('#detail-highlighter').append('<optgroup label="' + label + '">');
		var ids = Object.keys(map);
		for (var i = 0; i < ids.length; i++) {
			var value = map[ids[i]];
			$('#detail-highlighter').append('<option style="color: ' + value.color + '" value="' + type + '-' + ids[i] + '">&nbsp;&nbsp;&nbsp;&nbsp;' + ids[i] + ' (' + value.count + ')</option>');				
		}
		$('#detail-highlighter').append('</optgroup>');
	},
	
	_highlight: function (event) {
		var val = $(event.src || event.target).val();
		if (!val) {
			this._diagram.highlight();	
			return;			
		}
		var type = val;
		var value = null;
		if (type.indexOf('-') != -1) {
			type = val.substring(0, val.indexOf('-'));
			value = val.substring(val.indexOf('-') + 1);
		}
		var colors = this._createColorMap(type, value);
		this._diagram.highlight(type, value, colors);
	},
	
	_createColorMap: function (type, value) {
		if (!type) {
			return {};
		}
		var map = this['_' + type];
		var keys = Object.keys(map);
		var colorMap = {};
		if (value) {
			colorMap[value] = map[value].color;
		} else {
			for (var i = 0; i < keys.length; i++) {
				colorMap[keys[i]] = map[keys[i]].color;
			}
		}
		return colorMap;
	}
	
}

