window.FullDiagram = {
	
	get: function() {
		return {

			_BG_COLOR: 'rgb(0, 0, 0)',
			_NODE_COLOR: 'rgb(255, 255, 255)',
			_NODE_HIGHLIGHT_COLOR: 'rgb(50, 200, 50)',
			_CONNECTION_COLOR: 'rgb(100, 100, 100)',
			_CONNECTION_HIGHLIGHT_COLOR: 'rgb(50, 200, 50)',
			_WIDTH: 1,
			_GEOMETRY: new THREE.SphereGeometry(5, 5, 5),
			_colors: {},
			_tempConnections: [],
			_coloredConnections: [],
			nodes: {},
			connections: {},
			isComplete: false,
			
			init: function (parent, distance) {
				if (!parent) {
					parent = document.body;
				}
				this.distance = distance ? distance : 1;
				this._scene = new THREE.Scene();
				var canvas = document.createElement('canvas');
				var webglAvailable = false;
				try {
					webglAvailable = !!(window.WebGLRenderingContext && (canvas.getContext( 'webgl' ) || canvas.getContext( 'experimental-webgl' )));
				} catch (e) {
					webglAvailable = false;
				}
				if (webglAvailable) {
					this._renderer = new THREE.WebGLRenderer({antialias: true});
				} else {
					this._renderer = new THREE.SVGRenderer();									
				}
				this._renderer.setSize(window.innerWidth*this._WIDTH, window.innerHeight); 
				this._renderer.setClearColor(this._BG_COLOR);
				this._renderer.setPixelRatio(window.devicePixelRatio);
				this.container = $(parent)[0];
				this.container.appendChild(this._renderer.domElement);
				this.camera = new THREE.PerspectiveCamera(75, (window.innerWidth*this._WIDTH)/window.innerHeight, 0.1, 10000);
				this.camera.position.x = 0; 
				this.camera.position.y = 0; 
				this.camera.position.z = 0; 
				Actions.init(this);
				Controls.addTo(this);
			},

			render: function () {
				if (!this.isComplete) {
					return;
				}
				this._renderer.render(this._scene, this.camera);
			},	
					
			createNode: function (data, refNode) {
				if (this.nodes[data.id]) {
					return;					
				}
				var material = new THREE.MeshBasicMaterial({color: this._NODE_COLOR}); 
				var node = new THREE.Mesh(this._GEOMETRY, material); 
				node.position.x = 0;
				node.position.y = 0;
				node.position.z = 0;
				node.data = data;
				node.incoming = [];
				node.outgoing = [];
				this.nodes[data.id] = node;
				this._scene.add(node);
				var n = this.graph.addNode(data.id);
				n.parentId = data.parentId;
				n.isNew = true;
				return node;
			},
			
			createConnection: function (sourceId, targetId) {
				if (this.connections[sourceId + "_" + targetId]) {
					return;
				}				
				var line = this._createLine(sourceId, targetId, this._CONNECTION_COLOR);
				line.sourceNode = this.nodes[sourceId];
				line.targetNode = this.nodes[targetId];
				line.sourceNode.outgoing.push(line);
				line.targetNode.incoming.push(line);
				this.connections[sourceId + '_' + targetId] = line;
				this._scene.add(line);
				this.graph.addLink(sourceId, targetId);
				return line;
			},
			
			_createLine: function (sourceId, targetId, color) {
				var source = this.nodes[sourceId];
				var target = this.nodes[targetId];
				if (!source || !target) {
					return;
				}
				var material = new THREE.LineBasicMaterial({color: color, transparent: true, opacity: 0.75});
				var geometry = new THREE.Geometry();
				geometry.vertices.push(source.position.clone(), target.position.clone());
				return new THREE.Line(geometry, material);
			},

			positionNode: function (node, x, y, z) {
				node.position.x = x;
				node.position.y = y;
				node.position.z = z;
				for (var i = 0; i < node.incoming.length; i++) {
					this.positionAnchor(node.incoming[i], x, y, z, false);
				}
				for (var i = 0; i < node.outgoing.length; i++) {
					this.positionAnchor(node.outgoing[i], x, y, z, true);
				}
			},

			positionAnchor: function (connection, x, y, z, source) {
				var index = source ? 0 : 1;
				connection.geometry.verticesNeedUpdate = true;
				connection.geometry.vertices[index].x = x;
				connection.geometry.vertices[index].y = y;
				connection.geometry.vertices[index].z = z;
			},
			
			highlight: function (property, value, colorMap) {
				var keys = Object.keys(this.nodes);
				for (var i = 0; i < keys.length; i++) {
					var node = this.nodes[keys[i]];
					var highlight = false;
					var cValue = value;
					if (property) {						
						if (!cValue) {
							highlight = true;
						}
						if (property === 'flowProperties') {
							if (cValue) {
								highlight = node.data.flowProperty === cValue;								
							} else {
								cValue = node.data.flowProperty;
							}
						} else if (property === 'categories') {
							if (cValue) {
								highlight = node.data.categories && node.data.categories[0] === cValue;								
							} else {
								cValue = node.data.categories ? node.data.categories[0] : null;
							}
						} else if (property === 'locations') {
							if (cValue) {
								highlight = node.data.location === cValue;								
							} else {
								cValue = node.data.location;
							}
						}
						if (!cValue) {
							highlight = false;
						}
					}
					if (highlight) {
						node.material.color.set(colorMap[cValue]);
					} else {
						node.material.color.set(this._NODE_COLOR);						
					}
				}
				this.render();
			},
			
			clearLinks: function () {
				for (var i = 0; i < this._tempConnections.length; i++) {
					var con = this._tempConnections[i];
					con.material.dispose();
					con.geometry.dispose();
					this._scene.remove(con);
				}
				this._tempConnections = [];
				for (var i = 0; i < this._coloredConnections.length; i++) {
					this._coloredConnections[i].material.color.set(this._CONNECTION_COLOR);
				}
				this._coloredConnections = [];
				this.render();
			},
			
			showAllLinks: function (nodeId, subCall) {
				var node = this.nodes[nodeId];
				this._createTempConnections(nodeId, node.data.incoming, true);
				if (subCall === (window.max || 1)) {
					return;
				}
				for (var i = 0; i < node.data.incoming.length; i++) {
					this.showAllLinks(node.data.incoming[i], subCall ? subCall + 1 : 1);
				}
				this.render();
			},
			
			_createTempConnections: function (nodeId, ids, incoming) {
				for (var i = 0; i < ids.length; i++) {
					var sourceId = incoming ? ids[i] : nodeId;
					var targetId = incoming ? nodeId: ids[i];
					var line = this.connections[sourceId + '_' + targetId];
					if (line) {
						line.material.color.set(this._CONNECTION_HIGHLIGHT_COLOR);
						this._coloredConnections.push(line);
						continue;
					}
					line = this._createLine(sourceId, targetId, this._CONNECTION_HIGHLIGHT_COLOR);
					this._tempConnections.push(line);
					this._scene.add(line);
				}				
			}
			
		}
	}
	
}