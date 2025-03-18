define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, clearUrl, data, getAggregationUrl, getHighlightedRepoIndex, getHighlightedVersionIndex, getIcon, getLabel, getPagingUrl, query, resultInfo, selectedAggregations, values) {



























































pug_mixins["select"] = pug_interp = function(id, label, options, defaultSelection, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([inline?'form-inline':'form-group'], [true]), false, false)+pug_attr("id", id + '-group', true, false)+pug_attr("style", pug_style(inline?'display:inline-block':null), true, false)) + "\u003E";
if (label) {
pug_html = pug_html + "\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Flabel\u003E";
}
pug_html = pug_html + "\u003Cselect" + (pug_attrs(pug_merge([{"class": "form-control","id": pug_escape(id),"name": pug_escape(id)},attributes]), false)) + "\u003E";
if (options) {
pug_mixins["options"](options, defaultSelection);
}
else {
block && block();
}
pug_html = pug_html + "\u003C\u002Fselect\u003E";
if (options) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["optgroup"] = pug_interp = function(label, options, defaultSelection, groupId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Coptgroup" + (pug_attr("label", label, false, false)) + "\u003E";
pug_mixins["options"](options, defaultSelection, groupId);
pug_html = pug_html + "\u003C\u002Foptgroup\u003E";
};
pug_mixins["options"] = pug_interp = function(options, defaultSelection, groupId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
// iterate options
;(function(){
  var $$obj = options;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var option = $$obj[pug_index0];
var value = option;
var label = option;
var disabled = false;
if (Object.prototype.toString.call(option) === '[object Array]') {
value = option[0];
label = option[1];
if (option.length === 3 && option[2]) {
disabled = true;
}
}
if (defaultSelection === value) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("selected", true, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("disabled", disabled, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var option = $$obj[pug_index0];
var value = option;
var label = option;
var disabled = false;
if (Object.prototype.toString.call(option) === '[object Array]') {
value = option[0];
label = option[1];
if (option.length === 3 && option[2]) {
disabled = true;
}
}
if (defaultSelection === value) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("selected", true, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("disabled", disabled, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

};


























































































































































pug_mixins["paging"] = pug_interp = function(resultInfo, getUrl, pageSizeId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var page = resultInfo.currentPage;
var pageCount = resultInfo.pageCount;
var pageSize = resultInfo.pageSize;
if (!getUrl) {
getUrl = function() {return '#';};
}
if (pageCount > 1 || pageSize > 10 || pageSize === 0) {
pug_html = pug_html + "\u003Cdiv class=\"paging\"\u003E";
var startPage = 1;
var endPage = 10;
if (page > 4) {
startPage = page - 4;
endPage = page + 5;
}
if (pageCount <= endPage) {
endPage = pageCount;
}
if (page > 1) {
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"page\""+pug_attr("href", getUrl(1), true, false)+" aria-label=\"First page\" data-page=\"1\"") + "\u003E[First page]\u003C\u002Fa\u003E\u003C\u002Fspan\u003E\u003Cspan\u003E\u003Ca" + (" class=\"page\""+pug_attr("href", getUrl(page-1), true, false)+" aria-label=\"Previous page\""+pug_attr("data-page", page-1, true, false)) + "\u003E[Previous page]\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
}
var p = startPage
while (p <= endPage) {
var isActive = (p === page)
var isFirstAfterActive = (p === page + 1)
if (isActive) {
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = p) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([isFirstAfterActive?'indent':''], [true]), false, false)) + "\u003E\u003Ca" + (" class=\"page\""+pug_attr("href", getUrl(p), true, false)+pug_attr("aria-label", 'Page '+p, true, false)+pug_attr("data-page", p, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = p) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
}
p++;
}
if (page < pageCount) {
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"page\""+pug_attr("href", getUrl(page+1), true, false)+" aria-label=\"Next page\""+pug_attr("data-page", page+1, true, false)) + "\u003E[Next page]\u003C\u002Fa\u003E\u003C\u002Fspan\u003E\u003Cspan\u003E\u003Ca" + (" class=\"page\""+pug_attr("href", getUrl(pageCount), true, false)+" aria-label=\"Last page\""+pug_attr("data-page", pageCount, true, false)) + "\u003E[Last page]\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
}
values = [10, 25, 50, 100];
pageSizeId = pageSizeId || 'page-size'
pug_mixins["select"].call({
attributes: {"class": "input-sm","aria-label": "Select page size"}
}, pageSizeId, null, [10, 25, 50, 100], pageSize, true);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};








































pug_mixins["result"] = pug_interp = function(dataset){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var versionIndex = getHighlightedVersionIndex(dataset);
var version = dataset.versions[versionIndex];
var repoIndex = getHighlightedRepoIndex(version);
var repo = version.repos[repoIndex];
var iconData = Object.assign({}, dataset);
iconData.processType = version.processType;
iconData.flowType = version.flowType;
pug_html = pug_html + "\u003Cdiv class=\"result\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(iconData), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"result-content\"\u003E\u003Ch4\u003E";
pug_mixins["link"](dataset, repo, version.name, null, true);
pug_html = pug_html + "\u003C\u002Fh4\u003E";
if (dataset.type === 'FLOW' && version.flowType === 'ELEMENTARY_FLOW' && version.category && version.category.indexOf('/') !== -1) {
var categories = version.category.split('/')
pug_html = pug_html + "\u003Cdiv class=\"category\"\u003E" + (pug_escape(null == (pug_interp = categories[categories.length - 2] + ' / ' + categories[categories.length - 1]) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
}
if (version.location || version.validFromYear || version.validUntilYear) {
pug_html = pug_html + "\u003Cdiv class=\"result-details\"\u003E";
pug_mixins["location"](version);
pug_mixins["validity"](version);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_mixins["repositories"](dataset, versionIndex);
pug_mixins["tags"](version, repo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
pug_mixins["actions"](dataset, version, repo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["link"] = pug_interp = function(dataset, repo, title, defaultLink, highlight){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var url = repo.path + '/dataset/' + dataset.type + '/' + dataset.refId;
if (repo.commitId) {
url += '?commitId=' + repo.commitId;
}
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([defaultLink?'default-link':null], [true]), false, false)+pug_attr("href", url, true, false)) + "\u003E\u003Cspan" + (pug_attr("class", pug_classes([highlight?'result-text':null], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
};
pug_mixins["repositories"] = pug_interp = function(dataset, versionIndex){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var version = dataset.versions[versionIndex];
var versions = dataset.versions.length;
pug_html = pug_html + "\u003Cdiv class=\"result-repositories\"\u003Efound in " + (pug_escape(null == (pug_interp = version.repos.length === 1 ? 'repository' : 'repositories') ? "" : pug_interp)) + " \u003Cspan class=\"comma-separated\"\u003E";
// iterate version.repos
;(function(){
  var $$obj = version.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var repo = $$obj[pug_index5];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index5];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
if (versions > 1) {
pug_html = pug_html + "\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = versions - 1) ? "" : pug_interp)) + " other " + (pug_escape(null == (pug_interp = versions > 2 ? 'versions' : 'version') ? "" : pug_interp)) + " found in  \u003Cspan class=\"comma-separated\"\u003E";
// iterate dataset.versions
;(function(){
  var $$obj = dataset.versions;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var v = $$obj[index];
if (index !== versionIndex) {
// iterate v.repos
;(function(){
  var $$obj = v.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index7 = 0, $$l = $$obj.length; pug_index7 < $$l; pug_index7++) {
        var repo = $$obj[pug_index7];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index7 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index7];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var v = $$obj[index];
if (index !== versionIndex) {
// iterate v.repos
;(function(){
  var $$obj = v.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index8 = 0, $$l = $$obj.length; pug_index8 < $$l; pug_index8++) {
        var repo = $$obj[pug_index8];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index8 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index8];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["validity"] = pug_interp = function(version){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (version.validFromYear || version.validUntilYear) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Cimg src=\"images\u002Fcalendar.png\" aria-label=\"Calendar icon\"\u002F\u003E";
if (version.validFromYear && version.validUntilYear && version.validFromYear != version.validUntilYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validFromYear) ? "" : pug_interp)) + " - " + (pug_escape(null == (pug_interp = version.validUntilYear) ? "" : pug_interp)));
}
else
if (version.validFromYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validFromYear) ? "" : pug_interp)));
}
else
if (version.validUntilYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validUntilYear) ? "" : pug_interp)));
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["location"] = pug_interp = function(version){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (version.location) {
pug_html = pug_html + "\u003Cdiv\u003E \u003Cimg src=\"images\u002Fmodel\u002Fsmall\u002Flocation.png\" aria-label=\"Location icon\"\u002F\u003E " + (pug_escape(null == (pug_interp = version.location) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["tags"] = pug_interp = function(version, repo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var repoTags = repo.tags || [] 
var versionTags = version.tags || []
if (repoTags.length || versionTags.length) {
pug_html = pug_html + "\u003Cdiv class=\"result-tags\"\u003E";
// iterate repoTags
;(function(){
  var $$obj = repoTags;
  if ('number' == typeof $$obj.length) {
      for (var pug_index9 = 0, $$l = $$obj.length; pug_index9 < $$l; pug_index9++) {
        var tag = $$obj[pug_index9];
pug_html = pug_html + "\u003Cspan class=\"badge badge-primary\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index9 in $$obj) {
      $$l++;
      var tag = $$obj[pug_index9];
pug_html = pug_html + "\u003Cspan class=\"badge badge-primary\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

// iterate versionTags
;(function(){
  var $$obj = versionTags;
  if ('number' == typeof $$obj.length) {
      for (var pug_index10 = 0, $$l = $$obj.length; pug_index10 < $$l; pug_index10++) {
        var tag = $$obj[pug_index10];
pug_html = pug_html + "\u003Cspan class=\"badge\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index10 in $$obj) {
      $$l++;
      var tag = $$obj[pug_index10];
pug_html = pug_html + "\u003Cspan class=\"badge\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["actions"] = pug_interp = function(dataset, version, repo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success btn-sm dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload\u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
var repoName = repo.path.split('/')[1]
pug_html = pug_html + "\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json\" data-datatype=\"dataset\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-type", dataset.type, true, false)+pug_attr("data-ref-id", dataset.refId, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003EDataset as JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json1\" data-datatype=\"dataset\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-type", dataset.type, true, false)+pug_attr("data-ref-id", dataset.refId, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003EDataset as JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json\" data-datatype=\"repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003ERepository as JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json1\" data-datatype=\"repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003ERepository as JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["badge"] = pug_interp = function(key, value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"badge\"\u003E" + (pug_escape(null == (pug_interp = getLabel(key, value)) ? "" : pug_interp)) + " \u003Ca" + (pug_attr("href", getAggregationUrl(key, value, true), true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-remove\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var keys = Object.keys(selectedAggregations);
pug_html = pug_html + (pug_escape(null == (pug_interp = resultInfo.totalCount) ? "" : pug_interp)) + " results";
if (resultInfo.indexing) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Csmall\u003Edatasets are currently being reindexed, more search results might be available in some minutes \u003Cbutton class=\"btn btn-xs btn-default\" data-action=\"refresh\"\u003ERefresh\u003C\u002Fbutton\u003E\u003C\u002Fsmall\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv\u003E ";
if (query) {
pug_mixins["badge"]('query', query);
}
// iterate keys
;(function(){
  var $$obj = keys;
  if ('number' == typeof $$obj.length) {
      for (var pug_index11 = 0, $$l = $$obj.length; pug_index11 < $$l; pug_index11++) {
        var key = $$obj[pug_index11];
// iterate selectedAggregations[key]
;(function(){
  var $$obj = selectedAggregations[key];
  if ('number' == typeof $$obj.length) {
      for (var pug_index12 = 0, $$l = $$obj.length; pug_index12 < $$l; pug_index12++) {
        var value = $$obj[pug_index12];
pug_mixins["badge"](key, value);
      }
  } else {
    var $$l = 0;
    for (var pug_index12 in $$obj) {
      $$l++;
      var value = $$obj[pug_index12];
pug_mixins["badge"](key, value);
    }
  }
}).call(this);

      }
  } else {
    var $$l = 0;
    for (var pug_index11 in $$obj) {
      $$l++;
      var key = $$obj[pug_index11];
// iterate selectedAggregations[key]
;(function(){
  var $$obj = selectedAggregations[key];
  if ('number' == typeof $$obj.length) {
      for (var pug_index13 = 0, $$l = $$obj.length; pug_index13 < $$l; pug_index13++) {
        var value = $$obj[pug_index13];
pug_mixins["badge"](key, value);
      }
  } else {
    var $$l = 0;
    for (var pug_index13 in $$obj) {
      $$l++;
      var value = $$obj[pug_index13];
pug_mixins["badge"](key, value);
    }
  }
}).call(this);

    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (keys.length || query) {
pug_html = pug_html + "\u003Csmall\u003E\u003Ca" + (pug_attr("href", clearUrl, true, false)) + "\u003Eclear all filters\u003C\u002Fa\u003E\u003C\u002Fsmall\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"search-results\"\u003E";
// iterate data
;(function(){
  var $$obj = data;
  if ('number' == typeof $$obj.length) {
      for (var pug_index14 = 0, $$l = $$obj.length; pug_index14 < $$l; pug_index14++) {
        var dataset = $$obj[pug_index14];
pug_mixins["result"](dataset);
      }
  } else {
    var $$l = 0;
    for (var pug_index14 in $$obj) {
      $$l++;
      var dataset = $$obj[pug_index14];
pug_mixins["result"](dataset);
    }
  }
}).call(this);

pug_mixins["paging"](resultInfo, getPagingUrl);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"aggregations\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"clearUrl" in locals_for_with?locals_for_with.clearUrl:typeof clearUrl!=="undefined"?clearUrl:undefined,"data" in locals_for_with?locals_for_with.data:typeof data!=="undefined"?data:undefined,"getAggregationUrl" in locals_for_with?locals_for_with.getAggregationUrl:typeof getAggregationUrl!=="undefined"?getAggregationUrl:undefined,"getHighlightedRepoIndex" in locals_for_with?locals_for_with.getHighlightedRepoIndex:typeof getHighlightedRepoIndex!=="undefined"?getHighlightedRepoIndex:undefined,"getHighlightedVersionIndex" in locals_for_with?locals_for_with.getHighlightedVersionIndex:typeof getHighlightedVersionIndex!=="undefined"?getHighlightedVersionIndex:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getPagingUrl" in locals_for_with?locals_for_with.getPagingUrl:typeof getPagingUrl!=="undefined"?getPagingUrl:undefined,"query" in locals_for_with?locals_for_with.query:typeof query!=="undefined"?query:undefined,"resultInfo" in locals_for_with?locals_for_with.resultInfo:typeof resultInfo!=="undefined"?resultInfo:undefined,"selectedAggregations" in locals_for_with?locals_for_with.selectedAggregations:typeof selectedAggregations!=="undefined"?selectedAggregations:undefined,"values" in locals_for_with?locals_for_with.values:typeof values!=="undefined"?values:undefined));;return pug_html;} return template; });