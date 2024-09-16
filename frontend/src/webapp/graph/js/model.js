window.Model = {
	
	get: function(maxProcesses, maxLinks, noCycles) {
		var processes = {1: {id: 1, name: 'Ref process', incoming: [], outgoing: []}};
		this._createLinks(processes, 1, maxProcesses, maxLinks, noCycles);		
		return processes;
	},

	_createLinks: function (processes, recipientId, maxProcesses, maxLinks, noCycles) {
		var noOfLinks = Math.ceil(Math.random() * maxLinks);
		for (var i = 0; i < noOfLinks; i++) {
			var providerId = parseInt(Math.floor(1 + Math.random() * (maxProcesses - 1)));
			var provider = processes[providerId];
			var create = !noCycles || !provider;
			if (!provider) {
				provider = {id: providerId, name: 'Process ' + providerId, incoming: [], outgoing: []};
				processes[providerId] = provider;
				this._createLinks(processes, providerId, maxProcesses, maxLinks, noCycles);
			}
			if (create) {
				var recipient = processes[recipientId];
				recipient.incoming.push(providerId);
				provider.outgoing.push(recipient.id);				
			}
		}
	}
	
};
