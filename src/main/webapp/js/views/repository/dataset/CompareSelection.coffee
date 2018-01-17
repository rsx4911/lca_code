define([
				'cs!utils/Format'
				'cs!utils/Layers'
				'cs!utils/ModelTree'
			]

	(Format, Layers, ModelTree) ->

		openCommitSelection: (commits, commitId, callback) ->
			Layers.showTemplateInLayer
				title: 'Select version'
				template: 'repository/dataset/layer/commit-selection'
				model:
					commits: commits
					commitId: commitId
				buttons: [
					{id: 'close', className: 'btn-default', text: 'Close', callback: () -> Layers.closeActive()}
					{id: 'select', className: 'btn-primary', text: 'Select', callback: () -> 
						selection = $('#commit-selection #commitId').val()
						Layers.closeActive()
						callback selection
					}
				]

		openModelSelection: (repositoryPath, type, callback) ->
			Layers.showTemplateInLayer
				title: 'Select dataset'
				template: 'repository/dataset/layer/model-selection'
				callback: () ->	
					ModelTree.init '#model-tree', repositoryPath, 
						defaultPath: type
					$('#select-model').prop 'disabled', true
					$('#model-tree').on 'activate_node.jstree', (event, data) ->
						isType = data?.node?.original?.type is type
						if !isType
							$('#select-model').prop 'disabled', true
						else
							refId = data.node.original.id
							Layers.showProgressIndicator 'Loading<br>versions'
							$.ajax
								type: 'GET'
								url: "ws/history/#{repositoryPath}/#{type}/#{refId}"
								success: (commits) ->
									$('#select-model').prop 'disabled', (!commits || !commits.length)
									$('#model-selection #commitId').empty()
									if commits?.length
										for commit, index in commits
											$('#model-selection #commitId').append '<option value="' + commit.id + '">' + (if index is 0 then 'Latest' else commit.id) + '</option>'
											$('#model-selection #commitId').append '<optgroup class="additional-info" label="&nbsp; &nbsp;' + Format.formatCommitDescription(commit.message) + '"></optgroup>'
									Layers.hideProgressIndicator()
								error: () -> 
									$('#select-model').prop 'disabled', true
									Layers.hideProgressIndicator()
				buttons: [
					{id: 'close', className: 'btn-default', text: 'Close', callback: () -> Layers.closeActive()}
					{id: 'select-model', className: 'btn-primary', text: 'Select', callback: () -> 
						refId = ModelTree.getSelection('#model-tree', true).id
						commitId = $('#model-selection #commitId').val()
						Layers.closeActive()
						callback refId, commitId
					}
				]

)