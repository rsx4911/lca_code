define([
				'cs!utils/Events'
			]

	(Events) ->

    markAsReviewed: (event, reviewId) ->
      target = $ Events.target event, 'input'
      id = target.attr('data-id') or ''
      value = target.is ':checked'
      $.ajax
        type: 'PUT'
        url: "ws/task/review/#{reviewId}/markAsReviewed/#{id}/#{value}"
        success: (response) => $("[data-id=#{id}]").attr 'checked', response

    byType: (references) ->
      referencesMap = {}				
      if references?.length
        for ref in references
          forType = referencesMap[ref.type] or []
          forType.push ref
          referencesMap[ref.type] = forType
      references = []
      for type in Object.keys(referencesMap)
        list = referencesMap[type]
        list.sort (a, b) -> a.name.localeCompare(b.name) 
        references.push list
      return references

)