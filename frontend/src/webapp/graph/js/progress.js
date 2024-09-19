window.Progress = {
	
	_total: 0,
	_subTotal: 0,
	_current: 0,
	
	start: function (total) {
		this._total = total;
		$('#loader').show();		
	},
	
	worked: function (work) {
		this._current += work;
		var perc = 100*(this._current/this._total);
		$('#loaderbar').width(perc + '%');
	},
	
	done: function () {
		$('#loader').hide();
		this._total = 0;
	}
	
};