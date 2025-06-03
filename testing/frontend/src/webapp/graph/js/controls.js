window.Controls = {
	
	_zoom: function (e, diagram) {
		var dir = e.wheelDelta;
		if (dir > 0) {
			diagram.camera.position.z += 100;
		} else if (dir < 0) {
			diagram.camera.position.z -= 100;
		}
		diagram.render();
	},
	
	addTo: function (diagram) {
		diagram.container.addEventListener('mousewheel', function (e) { Controls._zoom(e, diagram); });		
		diagram.container.addEventListener('mousedown', function (e) {
			diagram.mouseDown = true;
			diagram.clientX = e.clientX;
			diagram.clientY = e.clientY;
		});	
		diagram.container.addEventListener('mouseup', function (e) {
			diagram.mouseDown = false;
		});
		diagram.container.addEventListener('mousemove', function (e) {
			if (diagram.mouseDown) {
				diagram.camera.position.x += (e.clientX - diagram.clientX);
				diagram.clientX = e.clientX;
				diagram.camera.position.y += (e.clientY - diagram.clientY);
				diagram.clientY = e.clientY;
				diagram.render();
			}
		});	
	}
	
}