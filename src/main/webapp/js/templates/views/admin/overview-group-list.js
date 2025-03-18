define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, data, pageSizeId, resultInfo, values) {



























































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
// iterate data
;(function(){
  var $$obj = data;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var group = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", 'groups/' + group.name, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = group.label) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var group = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", 'groups/' + group.name, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = group.label) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_mixins["paging"](resultInfo, null, pageSizeId);}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"data" in locals_for_with?locals_for_with.data:typeof data!=="undefined"?data:undefined,"pageSizeId" in locals_for_with?locals_for_with.pageSizeId:typeof pageSizeId!=="undefined"?pageSizeId:undefined,"resultInfo" in locals_for_with?locals_for_with.resultInfo:typeof resultInfo!=="undefined"?resultInfo:undefined,"values" in locals_for_with?locals_for_with.values:typeof values!=="undefined"?values:undefined));;return pug_html;} return template; });