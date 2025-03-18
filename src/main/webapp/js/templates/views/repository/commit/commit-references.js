define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, baseUrl, commitId, data, getIcon, getTypeLabel, resultInfo, standalone, values) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Col" + (pug_attrs(pug_merge([{"class": "breadcrumb"},attributes]), false)) + "\u003E";
if (paths && paths.length) {
var link = baseUrl || '';
if (depth && paths.length > depth) {
pug_html = pug_html + "\u003Cli\u003E...\u003C\u002Fli\u003E";
}
// iterate paths
;(function(){
  var $$obj = paths;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
link += '/' + category
if (!depth || paths.length - index <= depth) {
if (!baseUrl || disabled || (index === paths.length - 1 && !linkLast)) {
pug_html = pug_html + "\u003Cli class=\"active\"\u003E" + (pug_escape(null == (pug_interp = category) ? "" : pug_interp)) + "\u003C\u002Fli\u003E";
}
else {
var actualLink = link + (commitId ? '?commitId=' + commitId : '')
pug_html = pug_html + "\u003Cli\u003E\u003Ca" + (" class=\"default-link\""+pug_attr("href", actualLink, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = category) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
link += '/' + category
if (!depth || paths.length - index <= depth) {
if (!baseUrl || disabled || (index === paths.length - 1 && !linkLast)) {
pug_html = pug_html + "\u003Cli class=\"active\"\u003E" + (pug_escape(null == (pug_interp = category) ? "" : pug_interp)) + "\u003C\u002Fli\u003E";
}
else {
var actualLink = link + (commitId ? '?commitId=' + commitId : '')
pug_html = pug_html + "\u003Cli\u003E\u003Ca" + (" class=\"default-link\""+pug_attr("href", actualLink, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = category) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
}
    }
  }
}).call(this);

}
else {
pug_html = pug_html + "\u003Cli\u003E&nbsp;\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Fol\u003E";
};




























































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
      for (var pug_index1 = 0, $$l = $$obj.length; pug_index1 < $$l; pug_index1++) {
        var option = $$obj[pug_index1];
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
    for (var pug_index1 in $$obj) {
      $$l++;
      var option = $$obj[pug_index1];
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
pug_mixins["dataset-label"] = pug_interp = function(dataset){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var imgSrc = 'images/model/small/' + getIcon(dataset);
if (!standalone && (dataset.diffType === 'ADDED' || dataset.diffType === 'DELETED')) {
imgSrc = 'ws/public/overlay?image=' + imgSrc + '&overlay=images/model/' + (dataset.diffType === 'ADDED' ? 'add' : 'delete') + '.png';
}
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", imgSrc, true, false)) + "\u002F\u003E";
if (dataset.diffType === 'DELETED') {
pug_html = pug_html + "\u003Cdel\u003E" + (pug_escape(null == (pug_interp = dataset.name) ? "" : pug_interp)) + "\u003C\u002Fdel\u003E";
}
else {
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = dataset.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
};
// iterate data
;(function(){
  var $$obj = data;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var dataset = $$obj[pug_index5];
var url = baseUrl;
if (dataset.isCategory) {
url += 's/' + getTypeLabel(dataset.type) + '/' + (dataset.category ? dataset.category + '/' : '') + dataset.name;
}
else {
url += '/' + dataset.type + '/' + dataset.refId;
}
url += '?commitId=' + commitId;
var linkDataset = dataset.diffType !== 'DELETED' || (standalone && dataset.diffType !== 'ADDED');
pug_html = pug_html + "\u003Cdiv" + (" class=\"file-reference\""+pug_attr("id", dataset.id, true, false)) + "\u003E\u003Cdiv class=\"header\"\u003E";
if (linkDataset) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([standalone?'standalone':null], [true]), false, false)+pug_attr("data-type", dataset.diffType==='MODIFIED'||dataset.diffType==='MOVED'?'changed':dataset.diffType.toLowerCase(), true, false)+pug_attr("href", standalone?commitId+'-'+dataset.refId+'.html':url, true, false)) + "\u003E";
pug_mixins["dataset-label"](dataset);
pug_html = pug_html + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan\u003E";
pug_mixins["dataset-label"](dataset);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (dataset.category && dataset.category.length) {
var catUrl = null;
if (linkDataset) {
catUrl = baseUrl + 's/' + getTypeLabel(dataset.type);
}
pug_mixins["category-breadcrumb"](dataset.category.split('/'), catUrl, true);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var dataset = $$obj[pug_index5];
var url = baseUrl;
if (dataset.isCategory) {
url += 's/' + getTypeLabel(dataset.type) + '/' + (dataset.category ? dataset.category + '/' : '') + dataset.name;
}
else {
url += '/' + dataset.type + '/' + dataset.refId;
}
url += '?commitId=' + commitId;
var linkDataset = dataset.diffType !== 'DELETED' || (standalone && dataset.diffType !== 'ADDED');
pug_html = pug_html + "\u003Cdiv" + (" class=\"file-reference\""+pug_attr("id", dataset.id, true, false)) + "\u003E\u003Cdiv class=\"header\"\u003E";
if (linkDataset) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([standalone?'standalone':null], [true]), false, false)+pug_attr("data-type", dataset.diffType==='MODIFIED'||dataset.diffType==='MOVED'?'changed':dataset.diffType.toLowerCase(), true, false)+pug_attr("href", standalone?commitId+'-'+dataset.refId+'.html':url, true, false)) + "\u003E";
pug_mixins["dataset-label"](dataset);
pug_html = pug_html + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan\u003E";
pug_mixins["dataset-label"](dataset);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (dataset.category && dataset.category.length) {
var catUrl = null;
if (linkDataset) {
catUrl = baseUrl + 's/' + getTypeLabel(dataset.type);
}
pug_mixins["category-breadcrumb"](dataset.category.split('/'), catUrl, true);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

if (!standalone) {
pug_mixins["paging"](resultInfo);
}}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"data" in locals_for_with?locals_for_with.data:typeof data!=="undefined"?data:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"resultInfo" in locals_for_with?locals_for_with.resultInfo:typeof resultInfo!=="undefined"?resultInfo:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"values" in locals_for_with?locals_for_with.values:typeof values!=="undefined"?values:undefined));;return pug_html;} return template; });