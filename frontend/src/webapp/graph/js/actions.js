window.Actions = {
	
	_mouse: new THREE.Vector3(),
	_raycaster: new THREE.Raycaster(),
	
	init: function (diagram) {
		diagram.container.addEventListener('resize', function () {
			diagram._renderer.setSize(window.innerWidth*diagram._WIDTH, window.innerHeight); 				
			diagram.camera.aspect = (window.innerWidth*diagram._WIDTH)/window.innerHeight;
			diagram.camera.updateProjectionMatrix();
			diagram.render();
		});
		var _this = this;
		diagram.container.addEventListener('click', function () {
			_this._mouse.x = (event.clientX / diagram._renderer.domElement.clientWidth) * 2 - 1;
			_this._mouse.y = - (event.clientY / diagram._renderer.domElement.clientHeight) * 2 + 1;	
			_this._raycaster.setFromCamera(_this._mouse, diagram.camera);
			var objects = $.map(diagram.nodes, function(value, index) {
				return [value];
			});
			var intersects = _this._raycaster.intersectObjects(objects);
			if (intersects.length > 0) {
				var node = intersects[0].object;
				diagram.clearLinks();
				diagram.showAllLinks(node.data.id);
			} else {
				diagram.clearLinks();				
			}
		});
	}
	
}