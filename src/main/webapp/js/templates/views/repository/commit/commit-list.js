define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, canCreateReleases, formatDate, groups, releasesEnabled, repository, resultInfo, standalone, values) {pug_mixins["commit-info-entry"] = pug_interp = function(value, label, clazz, first){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value) {
if (!first) {
pug_html = pug_html + ", ";
}
pug_html = pug_html + ("\u003Cspan" + (pug_attr("class", pug_classes(["commit-info",clazz], [false,true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E " + (pug_escape(null == (pug_interp = value === 1 ? label : label + 's') ? "" : pug_interp)));
}
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
pug_mixins["user-info"] = pug_interp = function(id, name, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var lcType = type ? type.toLowerCase() : 'user';		
var ucType = type ? type : 'User';
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left user-info-content-box\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
if (!groups || !groups.length) {
pug_html = pug_html + "\u003Cdiv class=\"no-content-message\"\u003ENo commits found\t\u003C\u002Fdiv\u003E";
}
else {
// iterate groups
;(function(){
  var $$obj = groups;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var group = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv class=\"commit-group\"\u003E\u003Cdiv class=\"date-and-count\"\u003E\u003Cdiv class=\"date\"\u003E \u003Cimg src=\"images\u002Fcalendar.png\" aria-label=\"Calendar icon\"\u002F\u003E " + (pug_escape(null == (pug_interp = formatDate(group.date)) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"count\"\u003E" + (pug_escape(null == (pug_interp = group.count) ? "" : pug_interp)) + " commit";
if (group.count > 1) {
pug_html = pug_html + "s";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"commits\"\u003E";
// iterate group.commits
;(function(){
  var $$obj = group.commits;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var commit = $$obj[pug_index5];
pug_html = pug_html + "\u003Cdiv class=\"commit\"\u003E\u003Cdiv class=\"commit-details pull-left\"\u003E\u003Cdiv class=\"commit-message\"\u003E\u003Cspan\u003E" + (pug_escape(null == (pug_interp = commit.message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (!standalone && commit.isReleased) {
pug_html = pug_html + "\u003Cspan class=\"badge badge-success\"\u003EReleased\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (!standalone) {
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(commit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed"}
}, commit.user, commit.userDisplayName);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"commit-link pull-right\"\u003E\u003Cdiv class=\"commit-id pull-right\"\u003E" + (pug_escape(null == (pug_interp = commit.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"pull-right commit-info-container\""+pug_attr("data-commit-id", commit.id, true, false)) + "\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (standalone) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", commit.id + '.html', true, false)) + "\u003EDetails\u003Csmall\u003E\u003E\u003E\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", repository.group + '/' + repository.name + '/commit/' + commit.id, true, false)) + "\u003EDetails &raquo;\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (canCreateReleases) {
if (!commit.isReleased && releasesEnabled) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-success release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"unreleased\"") + "\u003ERelease\u003C\u002Fbutton\u003E";
}
else
if (commit.isReleased) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-warning release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"released\"") + "\u003EEdit release info\u003C\u002Fbutton\u003E";
}
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var commit = $$obj[pug_index5];
pug_html = pug_html + "\u003Cdiv class=\"commit\"\u003E\u003Cdiv class=\"commit-details pull-left\"\u003E\u003Cdiv class=\"commit-message\"\u003E\u003Cspan\u003E" + (pug_escape(null == (pug_interp = commit.message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (!standalone && commit.isReleased) {
pug_html = pug_html + "\u003Cspan class=\"badge badge-success\"\u003EReleased\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (!standalone) {
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(commit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed"}
}, commit.user, commit.userDisplayName);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"commit-link pull-right\"\u003E\u003Cdiv class=\"commit-id pull-right\"\u003E" + (pug_escape(null == (pug_interp = commit.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"pull-right commit-info-container\""+pug_attr("data-commit-id", commit.id, true, false)) + "\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (standalone) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", commit.id + '.html', true, false)) + "\u003EDetails\u003Csmall\u003E\u003E\u003E\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", repository.group + '/' + repository.name + '/commit/' + commit.id, true, false)) + "\u003EDetails &raquo;\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (canCreateReleases) {
if (!commit.isReleased && releasesEnabled) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-success release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"unreleased\"") + "\u003ERelease\u003C\u002Fbutton\u003E";
}
else
if (commit.isReleased) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-warning release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"released\"") + "\u003EEdit release info\u003C\u002Fbutton\u003E";
}
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var group = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv class=\"commit-group\"\u003E\u003Cdiv class=\"date-and-count\"\u003E\u003Cdiv class=\"date\"\u003E \u003Cimg src=\"images\u002Fcalendar.png\" aria-label=\"Calendar icon\"\u002F\u003E " + (pug_escape(null == (pug_interp = formatDate(group.date)) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"count\"\u003E" + (pug_escape(null == (pug_interp = group.count) ? "" : pug_interp)) + " commit";
if (group.count > 1) {
pug_html = pug_html + "s";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"commits\"\u003E";
// iterate group.commits
;(function(){
  var $$obj = group.commits;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var commit = $$obj[pug_index6];
pug_html = pug_html + "\u003Cdiv class=\"commit\"\u003E\u003Cdiv class=\"commit-details pull-left\"\u003E\u003Cdiv class=\"commit-message\"\u003E\u003Cspan\u003E" + (pug_escape(null == (pug_interp = commit.message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (!standalone && commit.isReleased) {
pug_html = pug_html + "\u003Cspan class=\"badge badge-success\"\u003EReleased\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (!standalone) {
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(commit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed"}
}, commit.user, commit.userDisplayName);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"commit-link pull-right\"\u003E\u003Cdiv class=\"commit-id pull-right\"\u003E" + (pug_escape(null == (pug_interp = commit.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"pull-right commit-info-container\""+pug_attr("data-commit-id", commit.id, true, false)) + "\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (standalone) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", commit.id + '.html', true, false)) + "\u003EDetails\u003Csmall\u003E\u003E\u003E\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", repository.group + '/' + repository.name + '/commit/' + commit.id, true, false)) + "\u003EDetails &raquo;\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (canCreateReleases) {
if (!commit.isReleased && releasesEnabled) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-success release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"unreleased\"") + "\u003ERelease\u003C\u002Fbutton\u003E";
}
else
if (commit.isReleased) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-warning release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"released\"") + "\u003EEdit release info\u003C\u002Fbutton\u003E";
}
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index6 in $$obj) {
      $$l++;
      var commit = $$obj[pug_index6];
pug_html = pug_html + "\u003Cdiv class=\"commit\"\u003E\u003Cdiv class=\"commit-details pull-left\"\u003E\u003Cdiv class=\"commit-message\"\u003E\u003Cspan\u003E" + (pug_escape(null == (pug_interp = commit.message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (!standalone && commit.isReleased) {
pug_html = pug_html + "\u003Cspan class=\"badge badge-success\"\u003EReleased\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (!standalone) {
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(commit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed"}
}, commit.user, commit.userDisplayName);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"commit-link pull-right\"\u003E\u003Cdiv class=\"commit-id pull-right\"\u003E" + (pug_escape(null == (pug_interp = commit.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"pull-right commit-info-container\""+pug_attr("data-commit-id", commit.id, true, false)) + "\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (standalone) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", commit.id + '.html', true, false)) + "\u003EDetails\u003Csmall\u003E\u003E\u003E\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"pull-right\""+pug_attr("href", repository.group + '/' + repository.name + '/commit/' + commit.id, true, false)) + "\u003EDetails &raquo;\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (canCreateReleases) {
if (!commit.isReleased && releasesEnabled) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-success release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"unreleased\"") + "\u003ERelease\u003C\u002Fbutton\u003E";
}
else
if (commit.isReleased) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"pull-right btn btn-warning release\""+pug_attr("data-commit-id", commit.id, true, false)+" data-status=\"released\"") + "\u003EEdit release info\u003C\u002Fbutton\u003E";
}
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_mixins["paging"](resultInfo);
}}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"canCreateReleases" in locals_for_with?locals_for_with.canCreateReleases:typeof canCreateReleases!=="undefined"?canCreateReleases:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"groups" in locals_for_with?locals_for_with.groups:typeof groups!=="undefined"?groups:undefined,"releasesEnabled" in locals_for_with?locals_for_with.releasesEnabled:typeof releasesEnabled!=="undefined"?releasesEnabled:undefined,"repository" in locals_for_with?locals_for_with.repository:typeof repository!=="undefined"?repository:undefined,"resultInfo" in locals_for_with?locals_for_with.resultInfo:typeof resultInfo!=="undefined"?resultInfo:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"values" in locals_for_with?locals_for_with.values:typeof values!=="undefined"?values:undefined));;return pug_html;} return template; });