define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function ($, Math, Object, activityUnit, activityVariable, baseUrl, collapseLongText, commitId, commits, compare, compareTo, compareUncertainty, comparisonCommitId, dataset, exchangeDqSystem, exchangeMap, findValue, formatCommitDescription, formatDate, formatRelative, formatScientific, getArrayValues, getIcon, getIconByType, getLabel, getTypeAsEnum, getTypeLabel, getValue, hasAtLeastOne, hasOneOf, isPublic, otherExchangeMap, otherValue, releases, reviewMode, socialDqSystem, standalone, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var release = $$obj[pug_index4];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var release = $$obj[pug_index4];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var v = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var v = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var v = $$obj[pug_index6];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index6 in $$obj) {
      $$l++;
      var v = $$obj[pug_index6];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index7 = 0, $$l = $$obj.length; pug_index7 < $$l; pug_index7++) {
        var v = $$obj[pug_index7];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index7 in $$obj) {
      $$l++;
      var v = $$obj[pug_index7];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index8 = 0, $$l = $$obj.length; pug_index8 < $$l; pug_index8++) {
        var other = $$obj[pug_index8];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index8 in $$obj) {
      $$l++;
      var other = $$obj[pug_index8];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index9 = 0, $$l = $$obj.length; pug_index9 < $$l; pug_index9++) {
        var v = $$obj[pug_index9];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index9 in $$obj) {
      $$l++;
      var v = $$obj[pug_index9];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index10 = 0, $$l = $$obj.length; pug_index10 < $$l; pug_index10++) {
        var other = $$obj[pug_index10];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index10 in $$obj) {
      $$l++;
      var other = $$obj[pug_index10];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["exchange-list"] = pug_interp = function(map, otherMap){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (map.refProduct || (otherMap && otherMap.refProduct)) {
pug_mixins["exchange-sublist"]('Reference product', map ? [map.refProduct] : null, otherMap ? [otherMap.refProduct] : null);
}
pug_mixins["exchange-sublist"]('By-products', map.byProducts, getValue(otherMap, 'byProducts'));
pug_mixins["exchange-sublist"]('Produced waste', map.producedWaste, getValue(otherMap, 'producedWaste'));
pug_mixins["exchange-sublist"]('Used products', map.usedProducts, getValue(otherMap, 'usedProducts'));
pug_mixins["exchange-sublist"]('Recyled waste', map.recycledWaste, getValue(otherMap, 'recycledWaste'));
pug_mixins["exchange-sublists"](map, otherMap, 'resources');
pug_mixins["exchange-sublists"](map, otherMap, 'emissions');
};
pug_mixins["exchange-sublists"] = pug_interp = function(map, otherMap, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var cats1 = [];
if (map && map[path]) {
// iterate Object.keys(map[path])
;(function(){
  var $$obj = Object.keys(map[path]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index12 = 0, $$l = $$obj.length; pug_index12 < $$l; pug_index12++) {
        var key = $$obj[pug_index12];
cats1.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index12 in $$obj) {
      $$l++;
      var key = $$obj[pug_index12];
cats1.push(key);
    }
  }
}).call(this);

}
if (otherMap && otherMap[path]) {
// iterate Object.keys(otherMap[path])
;(function(){
  var $$obj = Object.keys(otherMap[path]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index13 = 0, $$l = $$obj.length; pug_index13 < $$l; pug_index13++) {
        var key = $$obj[pug_index13];
if ($.inArray(key, cats1) === -1) {
cats1.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index13 in $$obj) {
      $$l++;
      var key = $$obj[pug_index13];
if ($.inArray(key, cats1) === -1) {
cats1.push(key);
}
    }
  }
}).call(this);

}
// iterate cats1.sort()
;(function(){
  var $$obj = cats1.sort();
  if ('number' == typeof $$obj.length) {
      for (var pug_index14 = 0, $$l = $$obj.length; pug_index14 < $$l; pug_index14++) {
        var cat1 = $$obj[pug_index14];
var cats2 = [];
if (map && map[path] && map[path][cat1]) {
// iterate Object.keys(map[path][cat1])
;(function(){
  var $$obj = Object.keys(map[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index15 = 0, $$l = $$obj.length; pug_index15 < $$l; pug_index15++) {
        var key = $$obj[pug_index15];
cats2.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index15 in $$obj) {
      $$l++;
      var key = $$obj[pug_index15];
cats2.push(key);
    }
  }
}).call(this);

}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
// iterate Object.keys(otherMap[path][cat1])
;(function(){
  var $$obj = Object.keys(otherMap[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index16 = 0, $$l = $$obj.length; pug_index16 < $$l; pug_index16++) {
        var key = $$obj[pug_index16];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index16 in $$obj) {
      $$l++;
      var key = $$obj[pug_index16];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
    }
  }
}).call(this);

}
// iterate cats2.sort()
;(function(){
  var $$obj = cats2.sort();
  if ('number' == typeof $$obj.length) {
      for (var pug_index17 = 0, $$l = $$obj.length; pug_index17 < $$l; pug_index17++) {
        var cat2 = $$obj[pug_index17];
var rLabel = cat1 + (cat2 ? ', ' + cat2 : '');
var value = null;
if (map && map[path] && map[path][cat1]) {
value = map[path][cat1][cat2];
}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
otherValue = otherMap[path][cat1][cat2];
}
pug_mixins["exchange-sublist"](rLabel, value, otherValue);
      }
  } else {
    var $$l = 0;
    for (var pug_index17 in $$obj) {
      $$l++;
      var cat2 = $$obj[pug_index17];
var rLabel = cat1 + (cat2 ? ', ' + cat2 : '');
var value = null;
if (map && map[path] && map[path][cat1]) {
value = map[path][cat1][cat2];
}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
otherValue = otherMap[path][cat1][cat2];
}
pug_mixins["exchange-sublist"](rLabel, value, otherValue);
    }
  }
}).call(this);

      }
  } else {
    var $$l = 0;
    for (var pug_index14 in $$obj) {
      $$l++;
      var cat1 = $$obj[pug_index14];
var cats2 = [];
if (map && map[path] && map[path][cat1]) {
// iterate Object.keys(map[path][cat1])
;(function(){
  var $$obj = Object.keys(map[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index18 = 0, $$l = $$obj.length; pug_index18 < $$l; pug_index18++) {
        var key = $$obj[pug_index18];
cats2.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index18 in $$obj) {
      $$l++;
      var key = $$obj[pug_index18];
cats2.push(key);
    }
  }
}).call(this);

}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
// iterate Object.keys(otherMap[path][cat1])
;(function(){
  var $$obj = Object.keys(otherMap[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index19 = 0, $$l = $$obj.length; pug_index19 < $$l; pug_index19++) {
        var key = $$obj[pug_index19];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index19 in $$obj) {
      $$l++;
      var key = $$obj[pug_index19];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
    }
  }
}).call(this);

}
// iterate cats2.sort()
;(function(){
  var $$obj = cats2.sort();
  if ('number' == typeof $$obj.length) {
      for (var pug_index20 = 0, $$l = $$obj.length; pug_index20 < $$l; pug_index20++) {
        var cat2 = $$obj[pug_index20];
var rLabel = cat1 + (cat2 ? ', ' + cat2 : '');
var value = null;
if (map && map[path] && map[path][cat1]) {
value = map[path][cat1][cat2];
}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
otherValue = otherMap[path][cat1][cat2];
}
pug_mixins["exchange-sublist"](rLabel, value, otherValue);
      }
  } else {
    var $$l = 0;
    for (var pug_index20 in $$obj) {
      $$l++;
      var cat2 = $$obj[pug_index20];
var rLabel = cat1 + (cat2 ? ', ' + cat2 : '');
var value = null;
if (map && map[path] && map[path][cat1]) {
value = map[path][cat1][cat2];
}
if (otherMap && otherMap[path] && otherMap[path][cat1]) {
otherValue = otherMap[path][cat1][cat2];
}
pug_mixins["exchange-sublist"](rLabel, value, otherValue);
    }
  }
}).call(this);

    }
  }
}).call(this);

};
pug_mixins["exchange-sublist"] = pug_interp = function(title, array, otherArray){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var allValues = getArrayValues(array, otherArray, 'EXCHANGE');
if (allValues.length) {
pug_html = pug_html + "\u003Cdiv class=\"exchange-list-part\"\u003E\u003Ch4\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Ctable\u003E";
// iterate allValues
;(function(){
  var $$obj = allValues;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var values = $$obj[index];
pug_mixins["exchange"](values[0], values[1], index);
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var values = $$obj[index];
pug_mixins["exchange"](values[0], values[1], index);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftable\u003E";
if (allValues.length > 10) {
pug_html = pug_html + "\u003Ca class=\"toggle-control\" href=\"#\"\u003EShow " + (pug_escape(null == (pug_interp = allValues.length - 10) ? "" : pug_interp)) + " more\u003C\u002Fa\u003E\u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\"\u003EShow less\u003C\u002Fa\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["exchange"] = pug_interp = function(exchange, other, index){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var amount = getValue(exchange, 'amount');
amount = amount || amount === 0
var otherAmount = getValue(other, 'amount');
otherAmount = otherAmount || otherAmount === 0
var unit = getValue(exchange, 'unit');
var otherUnit = getValue(other, 'unit');
var flow = getValue(exchange, 'flow');
var otherFlow = getValue(other, 'flow');
var location = getValue(exchange, 'location');
var otherLocation = getValue(other, 'location');
var title = amount ? amount + (otherAmount && otherAmount != amount ? ' / ' + otherAmount : '') : (otherAmount ? otherAmount : '');
var amountChanged = compare(amount, otherAmount);
var unitChanged = compare(unit, otherUnit) || compare(getValue(unit, 'name'), getValue(otherUnit, 'name'));
var flowChanged = compare(flow, otherFlow) || compare(getValue(flow, 'name'), getValue(otherFlow, 'name'));
var locationChanged = compare(location, otherLocation) || compare(getValue(location, 'name'), getValue(otherLocation, 'name'));
var changed = amountChanged || unitChanged || flowChanged || locationChanged;
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([index>=10?'toggleable':null], [true]), false, false)+pug_attr("style", pug_style(index>=10?'display:none':null), true, false)) + "\u003E\u003Ctd\u003E\u003Cspan" + (" class=\"same-size-font\""+pug_attr("title", title, true, false)+pug_attr("data-compare", amountChanged, true, false)) + "\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + ((exchange?exchange.isInput:other&&other.isInput)?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + ((exchange?exchange.isInput:other&&other.isInput)?'input':'output'), true, false)) + "\u002F\u003E";
if (exchange) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (exchange.amount > 0) {
pug_html = pug_html + "&nbsp;";
}
pug_html = pug_html + (pug_escape(null == (pug_interp = formatScientific(exchange.amount, true)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
if (other && (amountChanged || !exchange)) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
if (other.amount > 0) {
pug_html = pug_html + "&nbsp;";
}
pug_html = pug_html + (pug_escape(null == (pug_interp = formatScientific(other.amount, true)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", unitChanged, true, false)) + "\u003E";
if (unit) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = unit.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
if (otherUnit && (unitChanged || !unit)) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = otherUnit.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cspan" + (pug_attr("data-compare", flowChanged, true, false)) + "\u003E";
pug_mixins["ref"](flow, otherFlow, null, true);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if ((location || otherLocation)) {
pug_html = pug_html + " in \u003Cspan" + (pug_attr("data-compare", locationChanged, true, false)) + "\u003E";
pug_mixins["ref"](location, otherLocation, null, true);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index23 = 0, $$l = $$obj.length; pug_index23 < $$l; pug_index23++) {
        var option = $$obj[pug_index23];
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
    for (var pug_index23 in $$obj) {
      $$l++;
      var option = $$obj[pug_index23];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index26 = 0, $$l = $$obj.length; pug_index26 < $$l; pug_index26++) {
        var release = $$obj[pug_index26];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index26 in $$obj) {
      $$l++;
      var release = $$obj[pug_index26];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index27 = 0, $$l = $$obj.length; pug_index27 < $$l; pug_index27++) {
        var v = $$obj[pug_index27];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index27 in $$obj) {
      $$l++;
      var v = $$obj[pug_index27];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index28 = 0, $$l = $$obj.length; pug_index28 < $$l; pug_index28++) {
        var v = $$obj[pug_index28];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index28 in $$obj) {
      $$l++;
      var v = $$obj[pug_index28];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index29 = 0, $$l = $$obj.length; pug_index29 < $$l; pug_index29++) {
        var v = $$obj[pug_index29];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index29 in $$obj) {
      $$l++;
      var v = $$obj[pug_index29];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index30 = 0, $$l = $$obj.length; pug_index30 < $$l; pug_index30++) {
        var other = $$obj[pug_index30];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index30 in $$obj) {
      $$l++;
      var other = $$obj[pug_index30];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index31 = 0, $$l = $$obj.length; pug_index31 < $$l; pug_index31++) {
        var v = $$obj[pug_index31];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index31 in $$obj) {
      $$l++;
      var v = $$obj[pug_index31];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index32 = 0, $$l = $$obj.length; pug_index32 < $$l; pug_index32++) {
        var other = $$obj[pug_index32];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index32 in $$obj) {
      $$l++;
      var other = $$obj[pug_index32];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["sub-field-ref"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};











pug_mixins["sub-field-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-ref-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-cell"] = pug_interp = function(ref, ref2, path, alternativePath, formatter, ignoreNull, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath, formatter);
var value2 = getValue(ref2, path, alternativePath, formatter); 
var changed = (ref && ref2) || ignoreNull ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
if (collapseLongText === true) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["long-text"](value, collapseLongText);
}
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["sub-field-ref-cell"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["boolean-sub-field"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = compareTo&&ref&&ref2&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["sub-field-li"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !value2 ? 'added' : (compareTo && !value ? 'removed' : compareTo && value != value2 ? 'changed' : null);
pug_html = pug_html + "\u003Cli" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_mixins["exchange-table"] = pug_interp = function(field, isInput){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var exchanges = [];
var show = {costs: false, uncertainty: false, dqEntry: false, defaultProvider: false, isAvoidedProduct: false, description: false, location: false};
// iterate getArrayValues(dataset, compareTo, 'EXCHANGE', field)
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'EXCHANGE', field);
  if ('number' == typeof $$obj.length) {
      for (var pug_index34 = 0, $$l = $$obj.length; pug_index34 < $$l; pug_index34++) {
        var values = $$obj[pug_index34];
var exchange = values[0] || values[1];
if (exchange.isInput === isInput) {
exchanges.push(values);
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.isAvoidedProduct = show.isAvoidedProduct || exchange.isAvoidedProduct;
show.description = show.description || exchange.description;
show.location = show.location || exchange.location;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index34 in $$obj) {
      $$l++;
      var values = $$obj[pug_index34];
var exchange = values[0] || values[1];
if (exchange.isInput === isInput) {
exchanges.push(values);
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.isAvoidedProduct = show.isAvoidedProduct || exchange.isAvoidedProduct;
show.description = show.description || exchange.description;
show.location = show.location || exchange.location;
}
    }
  }
}).call(this);

if (exchanges.length > 0) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ctable" + (pug_attr("class", pug_classes(["table",isInput?'inputs':'outputs'], [false,true]), false, false)) + "\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.flow')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.flow.category')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.amount')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.costs?getLabel('exchanges.costs'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.uncertainty?getLabel('exchanges.uncertainty'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.dqEntry?getLabel('exchanges.dqEntry'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.defaultProvider?getLabel('exchanges.defaultProvider'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.isAvoidedProduct?(isInput?getLabel('exchanges.isAvoidedWaste'):getLabel('exchanges.isAvoidedProduct')):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.location?(getLabel('exchanges.location')):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.description?(getLabel('exchanges.description')):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate exchanges
;(function(){
  var $$obj = exchanges;
  if ('number' == typeof $$obj.length) {
      for (var count = 0, $$l = $$obj.length; count < $$l; count++) {
        var values = $$obj[count];
pug_mixins["exchange-row"](values[0], values[1], count, isInput, show);
      }
  } else {
    var $$l = 0;
    for (var count in $$obj) {
      $$l++;
      var values = $$obj[count];
pug_mixins["exchange-row"](values[0], values[1], count, isInput, show);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
if (exchanges.length > 10 && !compareTo) {
pug_html = pug_html + "\u003Ca class=\"toggle-control\" href=\"#\"\u003EShow " + (pug_escape(null == (pug_interp = exchanges.length - 10) ? "" : pug_interp)) + " more\u003C\u002Fa\u003E\u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\"\u003EShow less\u003C\u002Fa\u003E";
if (isInput) {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["exchange-row"] = pug_interp = function(exchange, other, count, isInput, show){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var clazz = getValue(exchange, 'isQuantitativeReference') || getValue(exchange, 'isRefFlow') ? 'reference' : '';
var style = null;
if (count >= 10 && !compareTo) {
clazz += ' toggleable';
style = 'display:none';
}
var flow = getValue(exchange, 'flow');
var otherFlow = getValue(other, 'flow');
var unit = getValue(exchange, 'unit');
var otherUnit = getValue(other, 'unit');
var category = getValue(flow, 'category');
var otherCategory = getValue(otherFlow, 'category');
var changed = compareTo && !other ? 'added' : (compareTo && !exchange ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([clazz?clazz:null], [true]), false, false)+pug_attr("data-compare", changed, true, false)+pug_attr("style", pug_style(style), true, false)) + "\u003E\u003Ctd\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + (isInput?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + (isInput?'input':'output'), true, false)) + "\u002F\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["category-field"](flow, otherFlow, true, 'Flow');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
var amount = getValue(exchange, 'amount');
amount = amount || amount === 0
var otherAmount = getValue(other, 'amount');
otherAmount = otherAmount || otherAmount === 0
var amountTitle = amount ? amount + (otherAmount && otherAmount != amount ? ' / ' + otherAmount : '') : (otherAmount ? otherAmount : '');
pug_mixins["sub-field-cell"].call({
block: function(){
pug_html = pug_html + "  ";
pug_mixins["sub-field"](unit, otherUnit, 'name');
},
attributes: {"title": pug_escape(amountTitle)}
}, exchange, other, 'amount', null, formatScientific);
var cost = getValue(exchange, 'costValue');
cost = cost || cost === 0
var otherCost = getValue(other, 'costValue');
otherCost = otherCost || otherCost === 0
var costTitle = cost ? cost + (otherCost && otherCost != cost ? ' / ' + otherCost : '') : (otherCost ? otherCost : '');
pug_mixins["sub-field-cell"].call({
block: function(){
pug_html = pug_html + " ";
pug_mixins["sub-field-ref"](exchange, other, 'currency');
},
attributes: {"title": pug_escape(costTitle)}
}, exchange, other, 'costValue', null, formatScientific);
pug_mixins["uncertainty-cell"](exchange, other, formatScientific);
pug_html = pug_html + "\u003Ctd\u003E";
var otherSystem = getValue(compareTo, exchangeDqSystem);				
pug_mixins["dq-field"](exchange, other, 'dqEntry', dataset.exchangeDqSystem, otherSystem, null, true);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](exchange, other, 'defaultProvider');
pug_html = pug_html + "\u003Ctd\u003E";
if (show.isAvoidedProduct) {
pug_mixins["boolean-sub-field"](exchange, other, 'isAvoidedProduct');
}
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](exchange, other, 'location');
pug_mixins["sub-field-cell"](exchange, other, 'description', null, null, false, 75);
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", exchange?'exchanges[' + exchange.internalId +']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index37 = 0, $$l = $$obj.length; pug_index37 < $$l; pug_index37++) {
        var option = $$obj[pug_index37];
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
    for (var pug_index37 in $$obj) {
      $$l++;
      var option = $$obj[pug_index37];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index40 = 0, $$l = $$obj.length; pug_index40 < $$l; pug_index40++) {
        var release = $$obj[pug_index40];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index40 in $$obj) {
      $$l++;
      var release = $$obj[pug_index40];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index41 = 0, $$l = $$obj.length; pug_index41 < $$l; pug_index41++) {
        var v = $$obj[pug_index41];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index41 in $$obj) {
      $$l++;
      var v = $$obj[pug_index41];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index42 = 0, $$l = $$obj.length; pug_index42 < $$l; pug_index42++) {
        var v = $$obj[pug_index42];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index42 in $$obj) {
      $$l++;
      var v = $$obj[pug_index42];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index43 = 0, $$l = $$obj.length; pug_index43 < $$l; pug_index43++) {
        var v = $$obj[pug_index43];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index43 in $$obj) {
      $$l++;
      var v = $$obj[pug_index43];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index44 = 0, $$l = $$obj.length; pug_index44 < $$l; pug_index44++) {
        var other = $$obj[pug_index44];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index44 in $$obj) {
      $$l++;
      var other = $$obj[pug_index44];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index45 = 0, $$l = $$obj.length; pug_index45 < $$l; pug_index45++) {
        var v = $$obj[pug_index45];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index45 in $$obj) {
      $$l++;
      var v = $$obj[pug_index45];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index46 = 0, $$l = $$obj.length; pug_index46 < $$l; pug_index46++) {
        var other = $$obj[pug_index46];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index46 in $$obj) {
      $$l++;
      var other = $$obj[pug_index46];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["sub-field-ref"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};











pug_mixins["sub-field-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-ref-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-cell"] = pug_interp = function(ref, ref2, path, alternativePath, formatter, ignoreNull, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath, formatter);
var value2 = getValue(ref2, path, alternativePath, formatter); 
var changed = (ref && ref2) || ignoreNull ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
if (collapseLongText === true) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["long-text"](value, collapseLongText);
}
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["sub-field-ref-cell"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["boolean-sub-field"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = compareTo&&ref&&ref2&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["sub-field-li"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !value2 ? 'added' : (compareTo && !value ? 'removed' : compareTo && value != value2 ? 'changed' : null);
pug_html = pug_html + "\u003Cli" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_mixins["non-causal-allocation-factor-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ch4\u003EPhysical & Economic allocation\u003C\u002Fh4\u003E\u003Ctable class=\"table non-causal-allocation-factors\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.product')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.physical')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.economic')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'nonCausalAllocationFactors')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'nonCausalAllocationFactors');
  if ('number' == typeof $$obj.length) {
      for (var pug_index48 = 0, $$l = $$obj.length; pug_index48 < $$l; pug_index48++) {
        var values = $$obj[pug_index48];
pug_mixins["non-causal-allocation-factor-row"](values[0], values[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index48 in $$obj) {
      $$l++;
      var values = $$obj[pug_index48];
pug_mixins["non-causal-allocation-factor-row"](values[0], values[1]);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["non-causal-allocation-factor-row"] = pug_interp = function(factor, other){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var physical = factor ? (factor.physical && (factor.physical.index || factor.physical.index === 0) ? factor.physical : null) : null; 
var economic = factor ? (factor.economic && (factor.economic.index || factor.economic.index === 0) ? factor.economic : null) : null; 
var otherPhysical = other ? (other.physical && (other.physical.index || other.physical.index === 0) ? other.physical : null) : null; 
var otherEconomic = other ? (other.economic && (other.economic.index || other.economic.index === 0) ? other.economic : null) : null; 
var product = getValue(factor, 'product');
var otherProduct = getValue(other, 'product');
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E";
pug_mixins["ref"](product, otherProduct);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
if (physical || otherPhysical) {
pug_mixins["sub-field-cell"].call({
attributes: {"data-path": pug_escape(product?'allocationFactors[physical-' + product.id + ']':null)}
}, physical, otherPhysical, 'value', null, formatRelative, true);
}
else {
pug_html = pug_html + "\u003Ctd\u003E\u003C\u002Ftd\u003E";
}
if (economic || otherEconomic) {
pug_mixins["sub-field-cell"].call({
attributes: {"data-path": pug_escape(product?'allocationFactors[economic-' + product.id + ']':null)}
}, economic, otherEconomic, 'value', null, formatRelative, true);
}
else {
pug_html = pug_html + "\u003Ctd\u003E\u003C\u002Ftd\u003E";
}
pug_html = pug_html + "\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index50 = 0, $$l = $$obj.length; pug_index50 < $$l; pug_index50++) {
        var option = $$obj[pug_index50];
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
    for (var pug_index50 in $$obj) {
      $$l++;
      var option = $$obj[pug_index50];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index53 = 0, $$l = $$obj.length; pug_index53 < $$l; pug_index53++) {
        var release = $$obj[pug_index53];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index53 in $$obj) {
      $$l++;
      var release = $$obj[pug_index53];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index54 = 0, $$l = $$obj.length; pug_index54 < $$l; pug_index54++) {
        var v = $$obj[pug_index54];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index54 in $$obj) {
      $$l++;
      var v = $$obj[pug_index54];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index55 = 0, $$l = $$obj.length; pug_index55 < $$l; pug_index55++) {
        var v = $$obj[pug_index55];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index55 in $$obj) {
      $$l++;
      var v = $$obj[pug_index55];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index56 = 0, $$l = $$obj.length; pug_index56 < $$l; pug_index56++) {
        var v = $$obj[pug_index56];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index56 in $$obj) {
      $$l++;
      var v = $$obj[pug_index56];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index57 = 0, $$l = $$obj.length; pug_index57 < $$l; pug_index57++) {
        var other = $$obj[pug_index57];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index57 in $$obj) {
      $$l++;
      var other = $$obj[pug_index57];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index58 = 0, $$l = $$obj.length; pug_index58 < $$l; pug_index58++) {
        var v = $$obj[pug_index58];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index58 in $$obj) {
      $$l++;
      var v = $$obj[pug_index58];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index59 = 0, $$l = $$obj.length; pug_index59 < $$l; pug_index59++) {
        var other = $$obj[pug_index59];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index59 in $$obj) {
      $$l++;
      var other = $$obj[pug_index59];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["sub-field-ref"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};











pug_mixins["sub-field-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-ref-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-cell"] = pug_interp = function(ref, ref2, path, alternativePath, formatter, ignoreNull, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath, formatter);
var value2 = getValue(ref2, path, alternativePath, formatter); 
var changed = (ref && ref2) || ignoreNull ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
if (collapseLongText === true) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["long-text"](value, collapseLongText);
}
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["sub-field-ref-cell"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["boolean-sub-field"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = compareTo&&ref&&ref2&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["sub-field-li"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !value2 ? 'added' : (compareTo && !value ? 'removed' : compareTo && value != value2 ? 'changed' : null);
pug_html = pug_html + "\u003Cli" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_mixins["causal-allocation-factor-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ch4\u003ECausal allocation\u003C\u002Fh4\u003E\u003Ctable class=\"table causal-allocation-factors\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.product')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E";
var factor = dataset && dataset.causalAllocationFactors ? dataset.causalAllocationFactors[0] : null;
var otherFactor = compareTo && compareTo.causalAllocationFactors ? compareTo.causalAllocationFactors[0]: null;
var products = getArrayValues(factor, otherFactor, 'PRODUCT', 'products');
// iterate products
;(function(){
  var $$obj = products;
  if ('number' == typeof $$obj.length) {
      for (var pug_index61 = 0, $$l = $$obj.length; pug_index61 < $$l; pug_index61++) {
        var values = $$obj[pug_index61];
var flow = getValue(values[0], 'flow');
var otherFlow = getValue(values[1], 'flow');
pug_html = pug_html + "\u003Cth\u003E";
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Fth\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index61 in $$obj) {
      $$l++;
      var values = $$obj[pug_index61];
var flow = getValue(values[0], 'flow');
var otherFlow = getValue(values[1], 'flow');
pug_html = pug_html + "\u003Cth\u003E";
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Fth\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
var isValid = function(value){ return value && value.exchange && value.exchange.flow && value.products && value.products.length; };
// iterate getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'causalAllocationFactors')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'causalAllocationFactors');
  if ('number' == typeof $$obj.length) {
      for (var pug_index62 = 0, $$l = $$obj.length; pug_index62 < $$l; pug_index62++) {
        var values = $$obj[pug_index62];
if (isValid(values[0]) || isValid(values[1])) {
pug_mixins["causal-allocation-factor-row"](values[0], values[1], products);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index62 in $$obj) {
      $$l++;
      var values = $$obj[pug_index62];
if (isValid(values[0]) || isValid(values[1])) {
pug_mixins["causal-allocation-factor-row"](values[0], values[1], products);
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["causal-allocation-factor-row"] = pug_interp = function(factor, other, products){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var exchange = getValue(factor, 'exchange'); 
var otherExchange = getValue(other, 'exchange'); 
var flow = getValue(exchange, 'flow'); 
var otherFlow = getValue(otherExchange, 'flow');  
pug_html = pug_html + "\u003Ctr\u003E";
var isInput = exchange ? exchange.isInput : otherExchange && otherExchange.isInput;
pug_html = pug_html + "\u003Ctd\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + (isInput?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + (isInput?'input':'output'), true, false)) + "\u002F\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
// iterate products
;(function(){
  var $$obj = products;
  if ('number' == typeof $$obj.length) {
      for (var pug_index63 = 0, $$l = $$obj.length; pug_index63 < $$l; pug_index63++) {
        var values = $$obj[pug_index63];
var product = factor ? findValue('PRODUCT', values[0], factor.products) : null;
var otherProduct = other ? findValue('PRODUCT', values[1], other.products) : null;
var setPath = product && (product.index || product.index === 0) && exchange;
var value = getValue(product, 'value', null, formatRelative);
var value2 = getValue(otherProduct, 'value', null, formatRelative); 
var changed = compare(value, value2);
pug_mixins["sub-field-cell"].call({
attributes: {"data-path": pug_escape(setPath?'allocationFactors[causal-' + product.id + '-' + exchange.internalId + ']':null)}
}, product, otherProduct, 'value', null, formatRelative, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index63 in $$obj) {
      $$l++;
      var values = $$obj[pug_index63];
var product = factor ? findValue('PRODUCT', values[0], factor.products) : null;
var otherProduct = other ? findValue('PRODUCT', values[1], other.products) : null;
var setPath = product && (product.index || product.index === 0) && exchange;
var value = getValue(product, 'value', null, formatRelative);
var value2 = getValue(otherProduct, 'value', null, formatRelative); 
var changed = compare(value, value2);
pug_mixins["sub-field-cell"].call({
attributes: {"data-path": pug_escape(setPath?'allocationFactors[causal-' + product.id + '-' + exchange.internalId + ']':null)}
}, product, otherProduct, 'value', null, formatRelative, true);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index65 = 0, $$l = $$obj.length; pug_index65 < $$l; pug_index65++) {
        var option = $$obj[pug_index65];
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
    for (var pug_index65 in $$obj) {
      $$l++;
      var option = $$obj[pug_index65];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index68 = 0, $$l = $$obj.length; pug_index68 < $$l; pug_index68++) {
        var release = $$obj[pug_index68];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index68 in $$obj) {
      $$l++;
      var release = $$obj[pug_index68];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index69 = 0, $$l = $$obj.length; pug_index69 < $$l; pug_index69++) {
        var v = $$obj[pug_index69];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index69 in $$obj) {
      $$l++;
      var v = $$obj[pug_index69];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index70 = 0, $$l = $$obj.length; pug_index70 < $$l; pug_index70++) {
        var v = $$obj[pug_index70];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index70 in $$obj) {
      $$l++;
      var v = $$obj[pug_index70];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index71 = 0, $$l = $$obj.length; pug_index71 < $$l; pug_index71++) {
        var v = $$obj[pug_index71];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index71 in $$obj) {
      $$l++;
      var v = $$obj[pug_index71];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index72 = 0, $$l = $$obj.length; pug_index72 < $$l; pug_index72++) {
        var other = $$obj[pug_index72];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index72 in $$obj) {
      $$l++;
      var other = $$obj[pug_index72];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index73 = 0, $$l = $$obj.length; pug_index73 < $$l; pug_index73++) {
        var v = $$obj[pug_index73];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index73 in $$obj) {
      $$l++;
      var v = $$obj[pug_index73];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index74 = 0, $$l = $$obj.length; pug_index74 < $$l; pug_index74++) {
        var other = $$obj[pug_index74];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index74 in $$obj) {
      $$l++;
      var other = $$obj[pug_index74];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["sub-field-ref"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};











pug_mixins["sub-field-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-ref-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-cell"] = pug_interp = function(ref, ref2, path, alternativePath, formatter, ignoreNull, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath, formatter);
var value2 = getValue(ref2, path, alternativePath, formatter); 
var changed = (ref && ref2) || ignoreNull ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
if (collapseLongText === true) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["long-text"](value, collapseLongText);
}
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["sub-field-ref-cell"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["boolean-sub-field"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = compareTo&&ref&&ref2&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["sub-field-li"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !value2 ? 'added' : (compareTo && !value ? 'removed' : compareTo && value != value2 ? 'changed' : null);
pug_html = pug_html + "\u003Cli" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_mixins["social-aspect-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table social-aspects\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.socialIndicator.name')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.rawValue')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.riskLevel')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.activityVariable')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.quality')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.comment')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.source')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'SOCIAL_ASPECT', 'socialAspects')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'SOCIAL_ASPECT', 'socialAspects');
  if ('number' == typeof $$obj.length) {
      for (var pug_index76 = 0, $$l = $$obj.length; pug_index76 < $$l; pug_index76++) {
        var values = $$obj[pug_index76];
pug_mixins["social-aspect-row"](values[0], values[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index76 in $$obj) {
      $$l++;
      var values = $$obj[pug_index76];
pug_mixins["social-aspect-row"](values[0], values[1]);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["social-aspect-row"] = pug_interp = function(aspect, other){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !other ? 'added' : (compareTo && !aspect ? 'removed' : null);
var indicator = getValue(aspect, 'socialIndicator')
var otherIndicator = getValue(other, 'socialIndicator');
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](indicator, otherIndicator);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"].call({
block: function(){
if (getValue(indicator, 'unitOfMeasurement') || getValue(otherIndicator, 'unitOfMeasurement')) {
pug_html = pug_html + " [";
pug_mixins["sub-field"](aspect, other, 'socialIndicator.unitOfMeasurement');
pug_html = pug_html + " ]";
}
}
}, aspect, other, 'rawAmount');
pug_mixins["sub-field-cell"](aspect, other, 'rawAmount');
pug_html = pug_html + "\u003Ctd\u003E";
pug_mixins["sub-field"](aspect, other, 'activityValue');
if ((getValue(indicator, activityUnit) && getValue(indicator, activityVariable)) || (getValue(otherIndicator, 'activityUnit') && getValue(otherIndicator, 'activityVariable'))) {
pug_html = pug_html + " ";
pug_mixins["sub-field"](aspect, other, 'socialIndicator.activityUnit.name');
pug_html = pug_html + ", ";
pug_mixins["sub-field"](aspect, other, 'socialIndicator.activityVariable');
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
var otherSystem = getValue(compareTo, socialDqSystem);				
pug_mixins["dq-field"](aspect, other, 'quality', dataset.socialDqSystem, otherSystem, null, true);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](aspect, other, 'comment');
pug_mixins["sub-field-ref-cell"](aspect, other, 'source');
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", indicator?'socialAspects[' + indicator.id + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index78 = 0, $$l = $$obj.length; pug_index78 < $$l; pug_index78++) {
        var option = $$obj[pug_index78];
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
    for (var pug_index78 in $$obj) {
      $$l++;
      var option = $$obj[pug_index78];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index81 = 0, $$l = $$obj.length; pug_index81 < $$l; pug_index81++) {
        var release = $$obj[pug_index81];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index81 in $$obj) {
      $$l++;
      var release = $$obj[pug_index81];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index82 = 0, $$l = $$obj.length; pug_index82 < $$l; pug_index82++) {
        var v = $$obj[pug_index82];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index82 in $$obj) {
      $$l++;
      var v = $$obj[pug_index82];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index83 = 0, $$l = $$obj.length; pug_index83 < $$l; pug_index83++) {
        var v = $$obj[pug_index83];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index83 in $$obj) {
      $$l++;
      var v = $$obj[pug_index83];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index84 = 0, $$l = $$obj.length; pug_index84 < $$l; pug_index84++) {
        var v = $$obj[pug_index84];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index84 in $$obj) {
      $$l++;
      var v = $$obj[pug_index84];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index85 = 0, $$l = $$obj.length; pug_index85 < $$l; pug_index85++) {
        var other = $$obj[pug_index85];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index85 in $$obj) {
      $$l++;
      var other = $$obj[pug_index85];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index86 = 0, $$l = $$obj.length; pug_index86 < $$l; pug_index86++) {
        var v = $$obj[pug_index86];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index86 in $$obj) {
      $$l++;
      var v = $$obj[pug_index86];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index87 = 0, $$l = $$obj.length; pug_index87 < $$l; pug_index87++) {
        var other = $$obj[pug_index87];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index87 in $$obj) {
      $$l++;
      var other = $$obj[pug_index87];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["sub-field-ref"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};











pug_mixins["sub-field-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-ref-row"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var field = path && path.indexOf('.') !== -1 ? path.substring(path.lastIndexOf('.') + 1) : path;
var value = getValue(ref, field);
var value2 = getValue(ref2, field); 
if (value || value2 || reviewMode) {
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](ref, ref2, field);
pug_html = pug_html + "\u003C\u002Ftr\u003E";
}
};
pug_mixins["sub-field-cell"] = pug_interp = function(ref, ref2, path, alternativePath, formatter, ignoreNull, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath, formatter);
var value2 = getValue(ref2, path, alternativePath, formatter); 
var changed = (ref && ref2) || ignoreNull ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
if (collapseLongText === true) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["long-text"](value, collapseLongText);
}
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["sub-field-ref-cell"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["boolean-sub-field"] = pug_interp = function(ref, ref2, path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = compareTo&&ref&&ref2&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["sub-field-li"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !value2 ? 'added' : (compareTo && !value ? 'removed' : compareTo && value != value2 ? 'changed' : null);
pug_html = pug_html + "\u003Cli" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_mixins["parameter-table"] = pug_interp = function(isInput){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var parameters = getValue(dataset, 'parameters');
var isEmpty = true;
var putPath = true;
if (parameters) {
// iterate parameters
;(function(){
  var $$obj = parameters;
  if ('number' == typeof $$obj.length) {
      for (var pug_index89 = 0, $$l = $$obj.length; pug_index89 < $$l; pug_index89++) {
        var parameter = $$obj[pug_index89];
if (parameter.isInputParameter && !isInput) {
putPath = false;
}
if (parameter.isInputParameter === isInput) {
isEmpty = false;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index89 in $$obj) {
      $$l++;
      var parameter = $$obj[pug_index89];
if (parameter.isInputParameter && !isInput) {
putPath = false;
}
if (parameter.isInputParameter === isInput) {
isEmpty = false;
}
    }
  }
}).call(this);

}
if (!isEmpty) {
var filter = function(param) {return param.isInputParameter !== isInput;};
pug_html = pug_html + "\u003Ch4\u003E" + (pug_escape(null == (pug_interp = isInput?'Input parameters':'Dependant parameters') ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Ctable class=\"table table-parameters\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E";
if (hasAtLeastOne(dataset, compareTo, 'parameters', 'description', filter) || hasAtLeastOne(dataset, compareTo, 'parameters', 'uncertainty', filter)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameters.name') + '/' + getLabel('parameters.value')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E\u003Cth\u003E";
if (hasAtLeastOne(dataset, compareTo, 'parameters', 'description', filter)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameters.description')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E";
if (isInput) {
pug_html = pug_html + "\u003Cth\u003E";
if (hasAtLeastOne(dataset, compareTo, 'parameters', 'uncertainty', filter)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameters.uncertainty')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E";
}
pug_html = pug_html + "\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'PARAMETER', 'parameters')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'PARAMETER', 'parameters');
  if ('number' == typeof $$obj.length) {
      for (var pug_index90 = 0, $$l = $$obj.length; pug_index90 < $$l; pug_index90++) {
        var values = $$obj[pug_index90];
if ((values[0] ? values[0].isInputParameter : values[1].isInputParameter) === isInput) {
pug_mixins["parameter-row"](values[0], values[1], isInput);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index90 in $$obj) {
      $$l++;
      var values = $$obj[pug_index90];
if ((values[0] ? values[0].isInputParameter : values[1].isInputParameter) === isInput) {
pug_mixins["parameter-row"](values[0], values[1], isInput);
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
};
pug_mixins["parameter-row"] = pug_interp = function(parameter, other, isInput){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !other ? 'added' : (compareTo && !parameter ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + (pug_escape(null == (pug_interp = parameter?parameter.name:other.name) ? "" : pug_interp));
if (!isInput) {
pug_html = pug_html + " = ";
pug_mixins["sub-field"](parameter, other, 'formula');
}
pug_html = pug_html + " = ";
pug_mixins["sub-field"](parameter, other, 'value');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](parameter, other, 'description');
if (isInput) {
pug_mixins["uncertainty-cell"](parameter, other);
}
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", parameter?'parameters[' + parameter.name + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index92 = 0, $$l = $$obj.length; pug_index92 < $$l; pug_index92++) {
        var option = $$obj[pug_index92];
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
    for (var pug_index92 in $$obj) {
      $$l++;
      var option = $$obj[pug_index92];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index95 = 0, $$l = $$obj.length; pug_index95 < $$l; pug_index95++) {
        var release = $$obj[pug_index95];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index95 in $$obj) {
      $$l++;
      var release = $$obj[pug_index95];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index96 = 0, $$l = $$obj.length; pug_index96 < $$l; pug_index96++) {
        var v = $$obj[pug_index96];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index96 in $$obj) {
      $$l++;
      var v = $$obj[pug_index96];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index97 = 0, $$l = $$obj.length; pug_index97 < $$l; pug_index97++) {
        var v = $$obj[pug_index97];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index97 in $$obj) {
      $$l++;
      var v = $$obj[pug_index97];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index98 = 0, $$l = $$obj.length; pug_index98 < $$l; pug_index98++) {
        var v = $$obj[pug_index98];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index98 in $$obj) {
      $$l++;
      var v = $$obj[pug_index98];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index99 = 0, $$l = $$obj.length; pug_index99 < $$l; pug_index99++) {
        var other = $$obj[pug_index99];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index99 in $$obj) {
      $$l++;
      var other = $$obj[pug_index99];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index100 = 0, $$l = $$obj.length; pug_index100 < $$l; pug_index100++) {
        var v = $$obj[pug_index100];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index100 in $$obj) {
      $$l++;
      var v = $$obj[pug_index100];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index101 = 0, $$l = $$obj.length; pug_index101 < $$l; pug_index101++) {
        var other = $$obj[pug_index101];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index101 in $$obj) {
      $$l++;
      var other = $$obj[pug_index101];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["aspect-table"] = pug_interp = function(aspects, pathPrefix){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.aspect')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.value')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate aspects
;(function(){
  var $$obj = aspects;
  if ('number' == typeof $$obj.length) {
      for (var pug_index103 = 0, $$l = $$obj.length; pug_index103 < $$l; pug_index103++) {
        var values = $$obj[pug_index103];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index103 in $$obj) {
      $$l++;
      var values = $$obj[pug_index103];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index105 = 0, $$l = $$obj.length; pug_index105 < $$l; pug_index105++) {
        var option = $$obj[pug_index105];
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
    for (var pug_index105 in $$obj) {
      $$l++;
      var option = $$obj[pug_index105];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index108 = 0, $$l = $$obj.length; pug_index108 < $$l; pug_index108++) {
        var release = $$obj[pug_index108];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index108 in $$obj) {
      $$l++;
      var release = $$obj[pug_index108];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index109 = 0, $$l = $$obj.length; pug_index109 < $$l; pug_index109++) {
        var v = $$obj[pug_index109];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index109 in $$obj) {
      $$l++;
      var v = $$obj[pug_index109];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index110 = 0, $$l = $$obj.length; pug_index110 < $$l; pug_index110++) {
        var v = $$obj[pug_index110];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index110 in $$obj) {
      $$l++;
      var v = $$obj[pug_index110];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index111 = 0, $$l = $$obj.length; pug_index111 < $$l; pug_index111++) {
        var v = $$obj[pug_index111];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index111 in $$obj) {
      $$l++;
      var v = $$obj[pug_index111];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index112 = 0, $$l = $$obj.length; pug_index112 < $$l; pug_index112++) {
        var other = $$obj[pug_index112];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index112 in $$obj) {
      $$l++;
      var other = $$obj[pug_index112];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index113 = 0, $$l = $$obj.length; pug_index113 < $$l; pug_index113++) {
        var v = $$obj[pug_index113];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index113 in $$obj) {
      $$l++;
      var v = $$obj[pug_index113];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index114 = 0, $$l = $$obj.length; pug_index114 < $$l; pug_index114++) {
        var other = $$obj[pug_index114];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index114 in $$obj) {
      $$l++;
      var other = $$obj[pug_index114];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index117 = 0, $$l = $$obj.length; pug_index117 < $$l; pug_index117++) {
        var option = $$obj[pug_index117];
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
    for (var pug_index117 in $$obj) {
      $$l++;
      var option = $$obj[pug_index117];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index120 = 0, $$l = $$obj.length; pug_index120 < $$l; pug_index120++) {
        var release = $$obj[pug_index120];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index120 in $$obj) {
      $$l++;
      var release = $$obj[pug_index120];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index121 = 0, $$l = $$obj.length; pug_index121 < $$l; pug_index121++) {
        var v = $$obj[pug_index121];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index121 in $$obj) {
      $$l++;
      var v = $$obj[pug_index121];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index122 = 0, $$l = $$obj.length; pug_index122 < $$l; pug_index122++) {
        var v = $$obj[pug_index122];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index122 in $$obj) {
      $$l++;
      var v = $$obj[pug_index122];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index123 = 0, $$l = $$obj.length; pug_index123 < $$l; pug_index123++) {
        var v = $$obj[pug_index123];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index123 in $$obj) {
      $$l++;
      var v = $$obj[pug_index123];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index124 = 0, $$l = $$obj.length; pug_index124 < $$l; pug_index124++) {
        var other = $$obj[pug_index124];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index124 in $$obj) {
      $$l++;
      var other = $$obj[pug_index124];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index125 = 0, $$l = $$obj.length; pug_index125 < $$l; pug_index125++) {
        var v = $$obj[pug_index125];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index125 in $$obj) {
      $$l++;
      var v = $$obj[pug_index125];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index126 = 0, $$l = $$obj.length; pug_index126 < $$l; pug_index126++) {
        var other = $$obj[pug_index126];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index126 in $$obj) {
      $$l++;
      var other = $$obj[pug_index126];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["aspect-table"] = pug_interp = function(aspects, pathPrefix){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.aspect')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.value')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate aspects
;(function(){
  var $$obj = aspects;
  if ('number' == typeof $$obj.length) {
      for (var pug_index128 = 0, $$l = $$obj.length; pug_index128 < $$l; pug_index128++) {
        var values = $$obj[pug_index128];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index128 in $$obj) {
      $$l++;
      var values = $$obj[pug_index128];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["scope-table"] = pug_interp = function(scopes){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.reviews.scope')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.reviews.methods')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate scopes
;(function(){
  var $$obj = scopes;
  if ('number' == typeof $$obj.length) {
      for (var pug_index129 = 0, $$l = $$obj.length; pug_index129 < $$l; pug_index129++) {
        var scope = $$obj[pug_index129];
var changed = compareTo && !scope[1] ? 'added' : (compareTo && !scope[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["sub-field-cell"](scope[0], scope[1], 'name');
pug_html = pug_html + "\u003Ctd\u003E\u003Cul\u003E";
var methods = getArrayValues(scope[0], scope[1], 'SCOPE_METHOD', 'methods')
// iterate methods
;(function(){
  var $$obj = methods;
  if ('number' == typeof $$obj.length) {
      for (var pug_index130 = 0, $$l = $$obj.length; pug_index130 < $$l; pug_index130++) {
        var method = $$obj[pug_index130];
pug_mixins["sub-field-li"](method[0], method[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index130 in $$obj) {
      $$l++;
      var method = $$obj[pug_index130];
pug_mixins["sub-field-li"](method[0], method[1]);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index129 in $$obj) {
      $$l++;
      var scope = $$obj[pug_index129];
var changed = compareTo && !scope[1] ? 'added' : (compareTo && !scope[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["sub-field-cell"](scope[0], scope[1], 'name');
pug_html = pug_html + "\u003Ctd\u003E\u003Cul\u003E";
var methods = getArrayValues(scope[0], scope[1], 'SCOPE_METHOD', 'methods')
// iterate methods
;(function(){
  var $$obj = methods;
  if ('number' == typeof $$obj.length) {
      for (var pug_index131 = 0, $$l = $$obj.length; pug_index131 < $$l; pug_index131++) {
        var method = $$obj[pug_index131];
pug_mixins["sub-field-li"](method[0], method[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index131 in $$obj) {
      $$l++;
      var method = $$obj[pug_index131];
pug_mixins["sub-field-li"](method[0], method[1]);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["review-tables"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var doc = dataset ? dataset.processDocumentation : null;
var otherDoc = compareTo ? compareTo.processDocumentation : null;
var reviews = getArrayValues(doc, otherDoc, 'REVIEW', 'reviews');
// iterate reviews
;(function(){
  var $$obj = reviews;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var values = $$obj[index];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
var review = values[0];
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.review')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.reviews.reviewType');
pug_mixins["sub-field-ref-row"](values[0], values[1], 'processDocumentation.reviews.report');
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.reviews.details');
var getReviewerId = function(reviewer) {return reviewer.id};
var reviewers = getValue(values[0], 'reviewers') 
var otherReviewers = getValue(values[1], 'reviewers') 
pug_mixins["array-field-row"](reviewers, otherReviewers, 'processDocumentation.reviews.reviewers', 'ACTOR', getReviewerId);
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
var scopes = getArrayValues(values[0], values[1], 'SCOPE', 'scopes')
if (scopes.length) {
pug_mixins["scope-table"](scopes);
}
var assessments = getArrayValues(values[0], values[1], 'ASPECT', 'assessment')
if (assessments.length) {
pug_mixins["aspect-table"](assessments, 'processDocumentation.reviews.assessment');
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (index !== reviews.length - 1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var values = $$obj[index];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
var review = values[0];
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.review')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.reviews.reviewType');
pug_mixins["sub-field-ref-row"](values[0], values[1], 'processDocumentation.reviews.report');
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.reviews.details');
var getReviewerId = function(reviewer) {return reviewer.id};
var reviewers = getValue(values[0], 'reviewers') 
var otherReviewers = getValue(values[1], 'reviewers') 
pug_mixins["array-field-row"](reviewers, otherReviewers, 'processDocumentation.reviews.reviewers', 'ACTOR', getReviewerId);
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
var scopes = getArrayValues(values[0], values[1], 'SCOPE', 'scopes')
if (scopes.length) {
pug_mixins["scope-table"](scopes);
}
var assessments = getArrayValues(values[0], values[1], 'ASPECT', 'assessment')
if (assessments.length) {
pug_mixins["aspect-table"](assessments, 'processDocumentation.reviews.assessment');
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (index !== reviews.length - 1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
    }
  }
}).call(this);

};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index134 = 0, $$l = $$obj.length; pug_index134 < $$l; pug_index134++) {
        var option = $$obj[pug_index134];
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
    for (var pug_index134 in $$obj) {
      $$l++;
      var option = $$obj[pug_index134];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index137 = 0, $$l = $$obj.length; pug_index137 < $$l; pug_index137++) {
        var release = $$obj[pug_index137];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index137 in $$obj) {
      $$l++;
      var release = $$obj[pug_index137];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index138 = 0, $$l = $$obj.length; pug_index138 < $$l; pug_index138++) {
        var v = $$obj[pug_index138];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index138 in $$obj) {
      $$l++;
      var v = $$obj[pug_index138];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index139 = 0, $$l = $$obj.length; pug_index139 < $$l; pug_index139++) {
        var v = $$obj[pug_index139];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index139 in $$obj) {
      $$l++;
      var v = $$obj[pug_index139];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index140 = 0, $$l = $$obj.length; pug_index140 < $$l; pug_index140++) {
        var v = $$obj[pug_index140];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index140 in $$obj) {
      $$l++;
      var v = $$obj[pug_index140];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index141 = 0, $$l = $$obj.length; pug_index141 < $$l; pug_index141++) {
        var other = $$obj[pug_index141];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index141 in $$obj) {
      $$l++;
      var other = $$obj[pug_index141];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index142 = 0, $$l = $$obj.length; pug_index142 < $$l; pug_index142++) {
        var v = $$obj[pug_index142];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index142 in $$obj) {
      $$l++;
      var v = $$obj[pug_index142];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index143 = 0, $$l = $$obj.length; pug_index143 < $$l; pug_index143++) {
        var other = $$obj[pug_index143];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index143 in $$obj) {
      $$l++;
      var other = $$obj[pug_index143];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
      for (var pug_index146 = 0, $$l = $$obj.length; pug_index146 < $$l; pug_index146++) {
        var option = $$obj[pug_index146];
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
    for (var pug_index146 in $$obj) {
      $$l++;
      var option = $$obj[pug_index146];
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
















































































































pug_mixins["commit-selection"] = pug_interp = function(id, label, commits, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (commits && commits.length) {
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var commit = $$obj[index];
if (index === 0) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", !selected||commit.id===commitId, true, false)) + "\u003ELatest\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", commit.message, true, false)) + "\u003E\u003C\u002Foptgroup\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===selected, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatCommitDescription(commit.message)) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
}
};
pug_mixins["release-selection"] = pug_interp = function(id, label, releases, selected, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["select"].call({
block: function(){
// iterate releases
;(function(){
  var $$obj = releases;
  if ('number' == typeof $$obj.length) {
      for (var pug_index149 = 0, $$l = $$obj.length; pug_index149 < $$l; pug_index149++) {
        var release = $$obj[pug_index149];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index149 in $$obj) {
      $$l++;
      var release = $$obj[pug_index149];
pug_html = pug_html + "\u003Coption" + (pug_attr("value", release.id, true, false)+pug_attr("selected", release.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = release.version) ? "" : pug_interp)) + "\u003C\u002Foption\u003E";
    }
  }
}).call(this);

}
}, id, label, null, null, inline);
};






pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["long-text"] = pug_interp = function(value, length){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!length) {
length = 250;
}
if (value && value.length > length) {
const short = value.substring(0, Math.floor(length - length / 5)) + '... '
pug_mixins["toggleable-text"](short, value, true);
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
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
pug_mixins["content"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commitId', 'Version', releases, commitId, true);
}
else {
pug_mixins["commit-selection"]('commitId', 'Commit', commits, commitId, true);
}
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else
if (dataset.notFound) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003ENot found\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
if (!isPublic) {
var currentCommit = commits.find(function(c) { return c.id === dataset.commitId; }) || commits[0];
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to ";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_mixins["version-info"]();
}
pug_html = pug_html + "\u003C\u002Fh4\u003E\u003Cdiv class=\"pull-left\" data-compare=\"added\"\u003E ";
pug_mixins["compare-icon"]('added');
pug_html = pug_html + "Additions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"changed\"\u003E";
pug_mixins["compare-icon"]('changed');
pug_html = pug_html + "Changes: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\" data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "Deletions: \u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["version-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (isPublic) {
var version = releases.find(function(r) { return r.id === compareTo.commitId; }).version;
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = version) ? "" : pug_interp)) + "'";
}
else {
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'";
}
};
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var selectFrom = releases || commits
var isFirst = selectFrom.length === 1 || selectFrom[selectFrom.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (selectFrom.length > 2 || (selectFrom.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset, compareTo);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ch3\u003E\u003Csmall\u003E\u003Cspan data-path=\"description\"\u003E";
pug_mixins["field"]('description', true);
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fsmall\u003E\u003C\u002Fh3\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
if (!isPublic) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
};
pug_mixins["meta"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"model-right-content content-box\"\u003E\u003Cdiv class=\"meta-info\"\u003E";
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-field-block"]('version');
pug_mixins["labeled-field-block"]('lastChange', formatDate, '-');
}
pug_mixins["labeled-field-block"]('id');
if (!dataset.deleted && !dataset.notFound) {
pug_mixins["labeled-array-field-block"].call({
attributes: {"class": "badge"}
}, 'tags');
}
if (!dataset.deleted && !dataset.notFound) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (renderEmpty || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path.replace('.', '-');
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path.replace('.', '-'), true, false)+" role=\"tabpanel\"") + "\u003E";
if (renderEmpty || hasAtLeastOne(dataset, compareTo, path)) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["compare-icon"] = pug_interp = function(value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value === 'added') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-plus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'changed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-exclamation-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
else
if (value === 'removed') {
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"class": "comparison-indicator glyphicon glyphicon-minus-sign"},attributes]), false)) + "\u003E\u003C\u002Fspan\u003E";
}
};
pug_mixins["compare-value"] = pug_interp = function(value, value2, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value || value === 0) {
if ($.isArray(value)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated original-value\"\u003E";
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index150 = 0, $$l = $$obj.length; pug_index150 < $$l; pug_index150++) {
        var v = $$obj[pug_index150];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index150 in $$obj) {
      $$l++;
      var v = $$obj[pug_index150];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && (value2 || value2 === 0))) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
if ((value2 || value2 === 0) && value != value2) {
if ($.isArray(value2)) {
pug_html = pug_html + "\u003Cspan class=\"comma-separated comparison-value\"\u003E";
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index151 = 0, $$l = $$obj.length; pug_index151 < $$l; pug_index151++) {
        var v = $$obj[pug_index151];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index151 in $$obj) {
      $$l++;
      var v = $$obj[pug_index151];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E" + (pug_escape(null == (pug_interp = value2) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["field"] = pug_interp = function(path, collapseLongtext){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["sub-field"].call({
attributes: attributes
}, dataset, compareTo, path, null, collapseLongText);
};
pug_mixins["sub-field"] = pug_interp = function(ref, ref2, path, alternativePath, collapseLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (collapseLongText && !changed) {
pug_mixins["long-text"](value);
}
else {
pug_mixins["compare-value"](value, value2);
}
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};






pug_mixins["sub-field-block"] = pug_interp = function(ref, ref2, path, alternativePath, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path, alternativePath);
var value2 = getValue(ref2, path, alternativePath); 
var changed = ref && ref2 ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["compare-value"](value, value2, defaultLabel);
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-field-block"] = pug_interp = function(path, formatter, defaultLabel, cropLongText){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
var changed = compare(value, value2);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv" + (pug_attr("data-path", path, true, false)+pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (changed || !cropLongText) {
pug_mixins["compare-value"](value, value2, defaultLabel);
}
else {
pug_mixins["long-text"](value || defaultLabel);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["labeled-boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["boolean-field-block"](path);
};
pug_mixins["labeled-ref-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
pug_mixins["ref-block"](path, '-');
};
pug_mixins["labeled-array-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length)) {
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003Cdiv" + (pug_attr("data-path", path, true, false)) + "\u003E";
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index152 = 0, $$l = $$obj.length; pug_index152 < $$l; pug_index152++) {
        var v = $$obj[pug_index152];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index152 in $$obj) {
      $$l++;
      var v = $$obj[pug_index152];
var other = value2 ? findValue(null, v, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index153 = 0, $$l = $$obj.length; pug_index153 < $$l; pug_index153++) {
        var other = $$obj[pug_index153];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index153 in $$obj) {
      $$l++;
      var other = $$obj[pug_index153];
var v = findValue(null, other, value);
if (!v) {
pug_html = pug_html + "\u003Cspan data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_html = pug_html + "\u003Cspan" + (pug_attrs(attributes, false)) + "\u003E" + (pug_escape(null == (pug_interp = other) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fspan\u003E";
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fstrong\u003E";
}
};
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var added = compareTo && (compareTo.deleted || compareTo.notFound);
var changed = compareTo ? added ? 'added' : value != value2 ? 'changed' : null : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed && !added) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["field-row-frame"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attrs(pug_merge([{"data-path": pug_escape(path)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["field-row-value"] = pug_interp = function(value, value2){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compare(value, value2);
pug_mixins["compare-icon"](changed);
if (changed) {
pug_mixins["compare-value"](value, value2);
}
else {
pug_mixins["long-text"](value);
}
};
pug_mixins["field-row"] = pug_interp = function(path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path, null, formatter);
var value2 = getValue(compareTo, path, null, formatter);
if (value || value2 || reviewMode) {
var changed = compare(value, value2);
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](value, value2);
block && block();
},
attributes: {"data-compare": pug_escape(changed)}
}, path);
}
};
pug_mixins["boolean-field-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["boolean-field"](path);
}
}, path);
};
pug_mixins["array-field-row"] = pug_interp = function(value, value2, path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index154 = 0, $$l = $$obj.length; pug_index154 < $$l; pug_index154++) {
        var v = $$obj[pug_index154];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index154 in $$obj) {
      $$l++;
      var v = $$obj[pug_index154];
var other = value2 ? findValue(type, v, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", compareTo&&!other?'added':null, true, false)+pug_attr("data-path", path + '[' + getSpecificId(v) + ']', true, false)) + "\u003E";
if (!other) {
pug_mixins["compare-icon"]('added');
}
pug_mixins["ref"](v, null);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
if (value2) {
// iterate value2
;(function(){
  var $$obj = value2;
  if ('number' == typeof $$obj.length) {
      for (var pug_index155 = 0, $$l = $$obj.length; pug_index155 < $$l; pug_index155++) {
        var other = $$obj[pug_index155];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index155 in $$obj) {
      $$l++;
      var other = $$obj[pug_index155];
var v = findValue(type, other, value);
if (!v) {
pug_html = pug_html + "\u003Cdiv data-compare=\"removed\"\u003E";
pug_mixins["compare-icon"]('removed');
pug_mixins["ref"](null, other);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
    }
  }
}).call(this);

}
}
}, path);
}
};
























pug_mixins["ref"] = pug_interp = function(ref, ref2, defaultLabel, noCompareIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = ref2 && compare(ref, ref2);
var changedName = ref2 && !changed && compare(getValue(ref, 'name'), getValue(ref2, 'name'));
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changedName==='changed'?'changed':null, true, false)) + "\u003E";
if (changedName==='changed' && !noCompareIcon) {
pug_mixins["compare-icon"](changedName);
}
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
if (ref) {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref), true, false)+pug_attr("aria-label", 'Model icon of ' + ref.type, true, false)) + "\u002F\u003E";
var query = commitId ? '?commitId=' + commitId : ''
var path = '';
if (ref.category) {
if ($.isArray(ref.category)) {
// iterate ref.category
;(function(){
  var $$obj = ref.category;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var category = $$obj[index];
if (index > 0) {
path += '/';
}
path += category;
    }
  }
}).call(this);

}
else {
path = ref.category;
}
path += '/' + ref.name
}
else {
path = ref.name;
}
if (ref.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
else
if (defaultLabel && !(compareTo && ref2)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
if (changed || changedName) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIcon(ref2), true, false)+pug_attr("aria-label", 'Model icon of ' + ref2.type, true, false)) + "\u002F\u003E";
var query = comparisonCommitId ? '?commitId=' + comparisonCommitId : ''
var path = ref2.category ? ref2.category + '/' + ref2.name : ref2.name
if (ref2.isInRepo) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["ref-row"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if (value || value === 0 || value2 || value2 === 0 || reviewMode) {
var changed = compare(value, value2)
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel(path)) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
}
};










pug_mixins["ref-block"] = pug_interp = function(path, defaultLabel){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path); 
var changed = compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)+pug_attr("data-path", path, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](value, value2, defaultLabel);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["category-field"] = pug_interp = function(parent, otherParent, inTable, categoryType){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = ((parent ? parent.category : null) || '').split('/')
var value2 = ((otherParent ? otherParent.category : null) || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && parent && otherParent ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(categoryType ? categoryType : dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth, commitId, !getValue(parent, 'categoryIsInRepo'));
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth, comparisonCommitId, !getValue(otherParent, 'categoryIsInRepo'));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["uncertainty-cell"] = pug_interp = function(ref1, ref2, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var u1 = ref1 ? ref1.uncertainty : null; 
var u2 = ref2 ? ref2.uncertainty : null; 
var changed = ref1 && ref2 && compareTo ? compareUncertainty(u1, u2) : null;
pug_html = pug_html + "\u003Ctd" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
if (u1) {
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E";
pug_mixins["uncertainty"](u1, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
if (changed && u2) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E";
pug_mixins["uncertainty"](u2, formatter);
pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Ftd\u003E";
};
pug_mixins["uncertainty"] = pug_interp = function(uncertainty, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (uncertainty) {
if (uncertainty.distributionType === 'LOG_NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Lognormal distribution\u003Cbr\u002F\u003EGeom. mean: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomMean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EGeom. SD: ";
pug_mixins["formatted-number-span"](uncertainty, 'geomSd', formatter);
}
else
if (uncertainty.distributionType === 'NORMAL_DISTRIBUTION') {
pug_html = pug_html + "Normal distribution \u003Cbr\u002F\u003EMean: ";
pug_mixins["formatted-number-span"](uncertainty, 'mean', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003ESD: ";
pug_mixins["formatted-number-span"](uncertainty, 'sd', formatter);
}
else
if (uncertainty.distributionType === 'TRIANGLE_DISTRIBUTION') {
pug_html = pug_html + "Triangle distribution\u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMode: ";
pug_mixins["formatted-number-span"](uncertainty, 'mode', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
else
if (uncertainty.distributionType === 'UNIFORM_DISTRIBUTION') {
pug_html = pug_html + "Uniform distribution \u003Cbr\u002F\u003EMin: ";
pug_mixins["formatted-number-span"](uncertainty, 'minimum', formatter);
pug_html = pug_html + "\u003Cbr\u002F\u003EMax: ";
pug_mixins["formatted-number-span"](uncertainty, 'maximum', formatter);
}
}
};
pug_mixins["formatted-number-span"] = pug_interp = function(ref, path, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("title", getValue(ref, path), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = getValue(ref, path, null, formatter)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-field"] = pug_interp = function(ref, ref2, path, system, system2, defaultLabel, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(ref, path);
var value2 = getValue(ref2, path); 
var changed = (!inTable || (ref && ref2)) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attrs(pug_merge([{"data-compare": pug_escape(changed)},attributes]), false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["dq-sub-field"](value, system, 'original');
if (changed || (!value && value2)) {
pug_mixins["dq-sub-field"](value2, system2, 'comparison');
}
else
if (!value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = defaultLabel) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["dq-sub-field"] = pug_interp = function(entry, system, type){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (entry) {
if (system) {
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)+" href=\"#\" data-action=\"show-data-quality\""+pug_attr("data-entry", entry, true, false)+pug_attr("data-scheme", system.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + "\u003Cspan" + (pug_attr("class", pug_classes([type+'-value'], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = entry) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}
};
pug_mixins["aspect-table"] = pug_interp = function(aspects, pathPrefix){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.aspect')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel(pathPrefix + '.value')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate aspects
;(function(){
  var $$obj = aspects;
  if ('number' == typeof $$obj.length) {
      for (var pug_index157 = 0, $$l = $$obj.length; pug_index157 < $$l; pug_index157++) {
        var values = $$obj[pug_index157];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index157 in $$obj) {
      $$l++;
      var values = $$obj[pug_index157];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E ";
pug_mixins["compare-icon"](changed);
if (pathPrefix === 'processDocumentation.flowCompleteness') {
if ((values[0] && values[0].aspect === 'Product model') || (values[1] && values[1].aspect) === 'Product model') {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('Flow', false, null, 'PRODUCT_FLOW'), true, false)+" aria-label=\"Model icon of Flow\"") + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + getIconByType('IMPACT_CATEGORY'), true, false)+" aria-label=\"Model icon of ImpactCategory\"") + "\u002F\u003E";
}
}
pug_mixins["sub-field"](values[0], values[1], 'aspect');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](values[0], values[1], 'value');
pug_html = pug_html + "\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["compliance-tables"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var doc = dataset ? dataset.processDocumentation : null;
var otherDoc = compareTo ? compareTo.processDocumentation : null;
var declarations = getArrayValues(doc, otherDoc, 'COMPLIANCE_DECLARATION', 'complianceDeclarations');
// iterate declarations
;(function(){
  var $$obj = declarations;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var values = $$obj[index];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Cp\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.complianceDeclaration')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003C\u002Fp\u003E\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["sub-field-ref-row"](values[0], values[1], 'processDocumentation.complianceDeclarations.system');
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.complianceDeclarations.comment');
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
var aspects = getArrayValues(values[0], values[1], 'ASPECT', 'aspects')
pug_mixins["aspect-table"](aspects, 'processDocumentation.complianceDeclarations.aspects');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (index !== declarations.length - 1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var values = $$obj[index];
var changed = compareTo && !values[1] ? 'added' : (compareTo && !values[0] ? 'removed' : null);
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Cp\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processDocumentation.complianceDeclaration')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003C\u002Fp\u003E\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["sub-field-ref-row"](values[0], values[1], 'processDocumentation.complianceDeclarations.system');
pug_mixins["sub-field-row"](values[0], values[1], 'processDocumentation.complianceDeclarations.comment');
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
var aspects = getArrayValues(values[0], values[1], 'ASPECT', 'aspects')
pug_mixins["aspect-table"](aspects, 'processDocumentation.complianceDeclarations.aspects');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (index !== declarations.length - 1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
    }
  }
}).call(this);

};
var doc = dataset.processDocumentation;
var otherDoc = getValue(compareTo, 'processDocumentation');
pug_mixins["menubar"]();
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"model-left-content\"\u003E";
pug_mixins["header"]();
pug_mixins["content"].call({
block: function(){
pug_html = pug_html + "\u003Cul class=\"nav nav-tabs\" role=\"tablist\"\u003E";
pug_mixins["nav-tab"]('exchanges', true);
pug_mixins["nav-tab"]('processDocumentation', false, true);
pug_mixins["nav-tab"]('allocationFactors');
pug_mixins["nav-tab"]('socialAspects');
pug_mixins["nav-tab"]('parameters');
pug_mixins["nav-tab"]('processDocumentation.flowCompleteness', false);
pug_mixins["nav-tab"]('processDocumentation.reviews', false);
pug_mixins["nav-tab"]('processDocumentation.complianceDeclarations', false);
pug_html = pug_html + "\u003C\u002Ful\u003E\u003Cdiv class=\"tab-content\"\u003E";
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ca class=\"pull-right toggle-control switch-to-list\" href=\"#\"\u003ESwitch to list view\u003C\u002Fa\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"exchange-tables toggleable\"\u003E";
pug_mixins["exchange-table"]('exchanges', true);
pug_mixins["exchange-table"]('exchanges', false);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ca class=\"pull-right toggle-control switch-to-table\" href=\"#\"\u003ESwitch to table view\u003C\u002Fa\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"exchange-list toggleable\"\u003E";
pug_mixins["exchange-list"](exchangeMap, otherExchangeMap);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
}, 'exchanges', true);
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["field-row"]('processDocumentation.inventoryMethodDescription');
pug_mixins["field-row"]('processDocumentation.modelingConstantsDescription');
pug_mixins["field-row"]('processDocumentation.completenessDescription');
pug_mixins["field-row"]('processDocumentation.dataSelectionDescription');
pug_mixins["field-row"]('processDocumentation.dataTreatmentDescription');
pug_mixins["field-row"]('processDocumentation.samplingDescription');
pug_mixins["field-row"]('processDocumentation.dataCollectionDescription');
pug_mixins["field-row"]('processDocumentation.useAdvice');
pug_mixins["field-row"]('processDocumentation.reviewDetails');
pug_mixins["ref-row"]('processDocumentation.reviewer');
pug_mixins["field-row"]('processDocumentation.projectDescription');
pug_mixins["field-row"]('processDocumentation.intendedApplication');
pug_mixins["ref-row"]('processDocumentation.dataSetOwner');
pug_mixins["ref-row"]('processDocumentation.dataGenerator');
pug_mixins["ref-row"]('processDocumentation.dataDocumentor');
pug_mixins["ref-row"]('processDocumentation.publication');
pug_mixins["field-row"]('processDocumentation.creationDate', formatDate);
pug_mixins["field-row"]('processDocumentation.restrictionsDescription');
pug_mixins["boolean-field-row"]('processDocumentation.isCopyrightProtected');
var getSourceId = function(source) {return source.id};
var sources = getValue(doc, 'sources');
var otherSources = getValue(otherDoc, 'sources');
pug_mixins["array-field-row"](sources, otherSources, 'processDocumentation.sources', 'SOURCE', getSourceId);
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
}, 'processDocumentation', false, true);
pug_mixins["nav-tab-pane"].call({
block: function(){
var aspects = getArrayValues(doc, otherDoc, 'ASPECT', 'flowCompleteness');
pug_mixins["aspect-table"](aspects, 'processDocumentation.flowCompleteness');
}
}, 'processDocumentation.flowCompleteness');
pug_mixins["nav-tab-pane"].call({
block: function(){
if (hasAtLeastOne(dataset, compareTo, 'nonCausalAllocationFactors')) {
pug_mixins["non-causal-allocation-factor-table"]();
}
if (hasAtLeastOne(dataset, compareTo, 'causalAllocationFactors')) {
pug_mixins["causal-allocation-factor-table"]();
}
}
}, 'allocationFactors');
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["social-aspect-table"]();
}
}, 'socialAspects');
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["parameter-table"](true);
pug_mixins["parameter-table"](false);
}
}, 'parameters');
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["review-tables"]();
}
}, 'processDocumentation.reviews');
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["compliance-tables"]();
}
}, 'processDocumentation.complianceDeclarations');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
});
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
pug_mixins["meta"].call({
block: function(){
pug_html = pug_html + "\u003Chr\u002F\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('isQuantitativeReference')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
var flow = null;
var otherFlow = null;
var eIndex = -1;
if (dataset.exchanges) {
// iterate dataset.exchanges
;(function(){
  var $$obj = dataset.exchanges;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var exchange = $$obj[index];
if (exchange.isQuantitativeReference) {
flow = exchange.flow;
eIndex = index;
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var exchange = $$obj[index];
if (exchange.isQuantitativeReference) {
flow = exchange.flow;
eIndex = index;
}
    }
  }
}).call(this);

}
if (compareTo && compareTo.exchanges) {
// iterate compareTo.exchanges
;(function(){
  var $$obj = compareTo.exchanges;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var otherExchange = $$obj[index];
if (otherExchange.isQuantitativeReference) {
otherFlow = otherExchange.flow;
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var otherExchange = $$obj[index];
if (otherExchange.isQuantitativeReference) {
otherFlow = otherExchange.flow;
}
    }
  }
}).call(this);

}
var qRefChanged = compareTo ? compare(flow, otherFlow) : null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", qRefChanged, true, false)) + "\u003E";
pug_mixins["compare-icon"](qRefChanged);
pug_mixins["ref"](flow, otherFlow, '-');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
pug_mixins["labeled-ref-block"]('location');
var typeFormat = function(value) {return value==='LCI_RESULT'?'System process':'Unit process'};
pug_mixins["labeled-field-block"]('processType', typeFormat);
pug_mixins["labeled-boolean-field-block"]('isInfrastructureProcess');
if (hasOneOf(doc, otherDoc, ['validFrom', 'validUntil']) || reviewMode) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["labeled-field-block"]('processDocumentation.validFrom', formatDate, '-');
pug_mixins["labeled-field-block"]('processDocumentation.validUntil', formatDate, '-');
}
if (hasOneOf(doc, otherDoc, ['timeDescription', 'geographyDescription', 'technologyDescription']) || reviewMode) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["labeled-field-block"]('processDocumentation.timeDescription', null, '-', true);
pug_mixins["labeled-field-block"]('processDocumentation.geographyDescription', null, '-', true);
pug_mixins["labeled-field-block"]('processDocumentation.technologyDescription', null, '-', true);
}
if (hasOneOf(dataset, compareTo, ['dqSystem', 'dqEntry', 'exchangeDqSystem', 'socialDqSystem']) || reviewMode) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["labeled-ref-block"]('dqSystem');
pug_mixins["labeled-ref-block"]('exchangeDqSystem');
pug_mixins["labeled-ref-block"]('socialDqSystem');
var dqChanged = compareTo ? compare(dataset.dqEntry, compareTo.dqEntry) : null;
pug_html = pug_html + "\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('dqEntry')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv data-path=\"dqEntry\"\u003E";
var otherSystem = compareTo ? compareTo.dqSystem : null;
pug_mixins["dq-field"](dataset, compareTo, 'dqEntry', dataset.dqSystem, otherSystem, '-');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
}
});
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"$" in locals_for_with?locals_for_with.$:typeof $!=="undefined"?$:undefined,"Math" in locals_for_with?locals_for_with.Math:typeof Math!=="undefined"?Math:undefined,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"activityUnit" in locals_for_with?locals_for_with.activityUnit:typeof activityUnit!=="undefined"?activityUnit:undefined,"activityVariable" in locals_for_with?locals_for_with.activityVariable:typeof activityVariable!=="undefined"?activityVariable:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"collapseLongText" in locals_for_with?locals_for_with.collapseLongText:typeof collapseLongText!=="undefined"?collapseLongText:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"compare" in locals_for_with?locals_for_with.compare:typeof compare!=="undefined"?compare:undefined,"compareTo" in locals_for_with?locals_for_with.compareTo:typeof compareTo!=="undefined"?compareTo:undefined,"compareUncertainty" in locals_for_with?locals_for_with.compareUncertainty:typeof compareUncertainty!=="undefined"?compareUncertainty:undefined,"comparisonCommitId" in locals_for_with?locals_for_with.comparisonCommitId:typeof comparisonCommitId!=="undefined"?comparisonCommitId:undefined,"dataset" in locals_for_with?locals_for_with.dataset:typeof dataset!=="undefined"?dataset:undefined,"exchangeDqSystem" in locals_for_with?locals_for_with.exchangeDqSystem:typeof exchangeDqSystem!=="undefined"?exchangeDqSystem:undefined,"exchangeMap" in locals_for_with?locals_for_with.exchangeMap:typeof exchangeMap!=="undefined"?exchangeMap:undefined,"findValue" in locals_for_with?locals_for_with.findValue:typeof findValue!=="undefined"?findValue:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"formatRelative" in locals_for_with?locals_for_with.formatRelative:typeof formatRelative!=="undefined"?formatRelative:undefined,"formatScientific" in locals_for_with?locals_for_with.formatScientific:typeof formatScientific!=="undefined"?formatScientific:undefined,"getArrayValues" in locals_for_with?locals_for_with.getArrayValues:typeof getArrayValues!=="undefined"?getArrayValues:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getIconByType" in locals_for_with?locals_for_with.getIconByType:typeof getIconByType!=="undefined"?getIconByType:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getTypeAsEnum" in locals_for_with?locals_for_with.getTypeAsEnum:typeof getTypeAsEnum!=="undefined"?getTypeAsEnum:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"getValue" in locals_for_with?locals_for_with.getValue:typeof getValue!=="undefined"?getValue:undefined,"hasAtLeastOne" in locals_for_with?locals_for_with.hasAtLeastOne:typeof hasAtLeastOne!=="undefined"?hasAtLeastOne:undefined,"hasOneOf" in locals_for_with?locals_for_with.hasOneOf:typeof hasOneOf!=="undefined"?hasOneOf:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"otherExchangeMap" in locals_for_with?locals_for_with.otherExchangeMap:typeof otherExchangeMap!=="undefined"?otherExchangeMap:undefined,"otherValue" in locals_for_with?locals_for_with.otherValue:typeof otherValue!=="undefined"?otherValue:undefined,"releases" in locals_for_with?locals_for_with.releases:typeof releases!=="undefined"?releases:undefined,"reviewMode" in locals_for_with?locals_for_with.reviewMode:typeof reviewMode!=="undefined"?reviewMode:undefined,"socialDqSystem" in locals_for_with?locals_for_with.socialDqSystem:typeof socialDqSystem!=="undefined"?socialDqSystem:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });