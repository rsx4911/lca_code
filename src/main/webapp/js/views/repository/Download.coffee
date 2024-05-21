define([
				'cs!utils/Layers'
			]

	(Layers) ->

		repository: (group, repo, commitId, categoryPath, format = 'json', selection) ->
			$('iframe#download-frame').remove()
			url = "ws/public/download/#{format}/prepare/#{group}/#{repo}"
			if commitId
				url += '?commitId=' + commitId
			if categoryPath
				url += if commitId then '&' else '?'
				url += 'path=' + categoryPath
			Layers.showProgressIndicator ['Collecting', 'data sets']
			$.ajax
				type: if selection then 'POST' else 'GET'
				url: url
				contentType: if selection then 'application/json' else null
				data: if selection then JSON.stringify(selection) else null
				success: (token) =>
					Layers.hideProgressIndicator()
					$('body').append '<iframe id="download-frame" class="hidden" border="0" height="0" width="0" src="ws/public/download/' + format + '/' + token + '"></iframe>'
				error: () =>
					Layers.hideProgressIndicator()

		dataset: (group, repo, type, refId, commitId, format = 'json') ->
			$('iframe#download-frame').remove()
			Layers.showProgressIndicator ['Collecting', 'data sets']
			url = "ws/public/download/#{format}/prepare/#{group}/#{repo}/#{type}/#{refId}"
			if commitId
				url+= "?commitId=#{commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (token) =>
					Layers.hideProgressIndicator()
					$('body').append '<iframe id="download-frame" class="hidden" border="0" height="0" width="0" src="ws/public/download/' + format + '/' + token + '"></iframe>'

		changelog: (group, repo, commitId) ->
			$('iframe#download-frame').remove()
			Layers.showProgressIndicator ['Preparing', 'change log']
			url = "ws/changelog/#{group}/#{repo}"
			if commitId
				url += "/#{commitId}"
			$.ajax
				type: 'GET'
				url: url
				success: (token) =>
					Layers.hideProgressIndicator()
					$('body').append '<iframe id="download-frame" class="hidden" border="0" height="0" width="0" src="ws/changelog/' + token + '"></iframe>'

)