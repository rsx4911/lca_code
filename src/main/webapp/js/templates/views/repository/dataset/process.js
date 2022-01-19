define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function ($, Math, Object, activityUnit, activityVariable, baseUrl, collapseLongText, commitId, commits, compare, compareTo, compareUncertainty, comparisonCommitId, dataset, exchangeDqSystem, exchangeMap, findValue, formatCommitDescription, formatDate, formatRelative, formatScientific, getArrayValues, getIcon, getLabel, getTypeAsEnum, getTypeLabel, getValue, hasAtLeastOne, isPublic, otherExchangeMap, otherValue, reviewMode, socialDqSystem, standalone, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var v = $$obj[pug_index4];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var v = $$obj[pug_index4];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var v = $$obj[pug_index6];
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
    for (var pug_index6 in $$obj) {
      $$l++;
      var v = $$obj[pug_index6];
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
      for (var pug_index7 = 0, $$l = $$obj.length; pug_index7 < $$l; pug_index7++) {
        var other = $$obj[pug_index7];
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
    for (var pug_index7 in $$obj) {
      $$l++;
      var other = $$obj[pug_index7];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
if (map.referenceProduct || (otherMap && otherMap.referenceProduct)) {
pug_mixins["exchange-sublist"]('Reference product', map ? [map.referenceProduct] : null, otherMap ? [otherMap.referenceProduct] : null);
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
      for (var pug_index9 = 0, $$l = $$obj.length; pug_index9 < $$l; pug_index9++) {
        var key = $$obj[pug_index9];
cats1.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index9 in $$obj) {
      $$l++;
      var key = $$obj[pug_index9];
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
      for (var pug_index10 = 0, $$l = $$obj.length; pug_index10 < $$l; pug_index10++) {
        var key = $$obj[pug_index10];
if ($.inArray(key, cats1) === -1) {
cats1.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index10 in $$obj) {
      $$l++;
      var key = $$obj[pug_index10];
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
      for (var pug_index11 = 0, $$l = $$obj.length; pug_index11 < $$l; pug_index11++) {
        var cat1 = $$obj[pug_index11];
var cats2 = [];
if (map && map[path] && map[path][cat1]) {
// iterate Object.keys(map[path][cat1])
;(function(){
  var $$obj = Object.keys(map[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index12 = 0, $$l = $$obj.length; pug_index12 < $$l; pug_index12++) {
        var key = $$obj[pug_index12];
cats2.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index12 in $$obj) {
      $$l++;
      var key = $$obj[pug_index12];
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
      for (var pug_index13 = 0, $$l = $$obj.length; pug_index13 < $$l; pug_index13++) {
        var key = $$obj[pug_index13];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index13 in $$obj) {
      $$l++;
      var key = $$obj[pug_index13];
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
      for (var pug_index14 = 0, $$l = $$obj.length; pug_index14 < $$l; pug_index14++) {
        var cat2 = $$obj[pug_index14];
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
    for (var pug_index14 in $$obj) {
      $$l++;
      var cat2 = $$obj[pug_index14];
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
    for (var pug_index11 in $$obj) {
      $$l++;
      var cat1 = $$obj[pug_index11];
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
var title = amount ? amount + (otherAmount && otherAmount != amount ? ' / ' + otherAmount : '') : (otherAmount ? otherAmount : '');
var amountChanged = compare(amount, otherAmount);
var unitChanged = compare(unit, otherUnit) || compare(getValue(unit, 'name'), getValue(otherUnit, 'name'));
var flowChanged = compare(flow, otherFlow) || compare(getValue(flow, 'name'), getValue(otherFlow, 'name'));
var changed = amountChanged || unitChanged || flowChanged;
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([index>=10?'toggleable':null], [true]), false, false)+pug_attr("style", pug_style(index>=10?'display:none':null), true, false)) + "\u003E\u003Ctd\u003E\u003Cspan" + (" class=\"same-size-font\""+pug_attr("title", title, true, false)+pug_attr("data-compare", amountChanged, true, false)) + "\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + ((exchange?exchange.input:other.input)?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + ((exchange?exchange.input:other.input)?'input':'output'), true, false)) + "\u002F\u003E";
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
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-compare", flowChanged, true, false)) + "\u003E";
pug_mixins["ref"](flow, otherFlow, null, true);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
      for (var pug_index20 = 0, $$l = $$obj.length; pug_index20 < $$l; pug_index20++) {
        var option = $$obj[pug_index20];
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
    for (var pug_index20 in $$obj) {
      $$l++;
      var option = $$obj[pug_index20];
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index23 = 0, $$l = $$obj.length; pug_index23 < $$l; pug_index23++) {
        var v = $$obj[pug_index23];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index23 in $$obj) {
      $$l++;
      var v = $$obj[pug_index23];
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
      for (var pug_index24 = 0, $$l = $$obj.length; pug_index24 < $$l; pug_index24++) {
        var v = $$obj[pug_index24];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index24 in $$obj) {
      $$l++;
      var v = $$obj[pug_index24];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index25 = 0, $$l = $$obj.length; pug_index25 < $$l; pug_index25++) {
        var v = $$obj[pug_index25];
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
    for (var pug_index25 in $$obj) {
      $$l++;
      var v = $$obj[pug_index25];
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
      for (var pug_index26 = 0, $$l = $$obj.length; pug_index26 < $$l; pug_index26++) {
        var other = $$obj[pug_index26];
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
    for (var pug_index26 in $$obj) {
      $$l++;
      var other = $$obj[pug_index26];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["exchange-table"] = pug_interp = function(field, input){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var count = 0;
var exchanges = getValue(dataset, field);
var otherExchanges = compareTo ? getValue(compareTo, field) : null;
// iterate exchanges
;(function(){
  var $$obj = exchanges;
  if ('number' == typeof $$obj.length) {
      for (var pug_index28 = 0, $$l = $$obj.length; pug_index28 < $$l; pug_index28++) {
        var exchange = $$obj[pug_index28];
if (exchange.input === input) {
count++;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index28 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index28];
if (exchange.input === input) {
count++;
}
    }
  }
}).call(this);

if (count > 0) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ctable" + (pug_attr("class", pug_classes(["table",input?'inputs':'outputs'], [false,true]), false, false)) + "\u003E";
var show = {costs: false, uncertainty: false, dqEntry: false, defaultProvider: false, avoidedProduct: false, description: false};
if (exchanges) {
// iterate exchanges
;(function(){
  var $$obj = exchanges;
  if ('number' == typeof $$obj.length) {
      for (var pug_index29 = 0, $$l = $$obj.length; pug_index29 < $$l; pug_index29++) {
        var exchange = $$obj[pug_index29];
if (exchange.input === input) {
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.avoidedProduct = show.avoidedProduct || exchange.avoidedProduct;
show.description = show.description || exchange.description;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index29 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index29];
if (exchange.input === input) {
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.avoidedProduct = show.avoidedProduct || exchange.avoidedProduct;
show.description = show.description || exchange.description;
}
    }
  }
}).call(this);

}
if (otherExchanges) {
// iterate otherExchanges
;(function(){
  var $$obj = otherExchanges;
  if ('number' == typeof $$obj.length) {
      for (var pug_index30 = 0, $$l = $$obj.length; pug_index30 < $$l; pug_index30++) {
        var exchange = $$obj[pug_index30];
if (exchange.input === input) {
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.avoidedProduct = show.avoidedProduct || exchange.avoidedProduct;
show.description = show.description || exchange.description;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index30 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index30];
if (exchange.input === input) {
show.costs = show.costs || exchange.costValue || exchange.currency;
show.dqEntry = show.dqEntry || exchange.dqEntry;
show.uncertainty = show.uncertainty || exchange.uncertainty;
show.defaultProvider = show.defaultProvider || exchange.defaultProvider;
show.avoidedProduct = show.avoidedProduct || exchange.avoidedProduct;
show.description = show.description || exchange.description;
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.flow')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.flow.category')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('exchanges.amount')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.costs?getLabel('exchanges.costs'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.uncertainty?getLabel('exchanges.uncertainty'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.dqEntry?getLabel('exchanges.dqEntry'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.defaultProvider?getLabel('exchanges.defaultProvider'):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.avoidedProduct?(input?getLabel('exchanges.avoidedWaste'):getLabel('exchanges.avoidedProduct')):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = show.description?(getLabel('exchanges.description')):'') ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
count = 0;
// iterate getArrayValues(dataset, compareTo, 'EXCHANGE', field)
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'EXCHANGE', field);
  if ('number' == typeof $$obj.length) {
      for (var pug_index31 = 0, $$l = $$obj.length; pug_index31 < $$l; pug_index31++) {
        var values = $$obj[pug_index31];
if ((values[0] ? values[0].input : values[1].input) === input) {
pug_mixins["exchange-row"](values[0], values[1], count, input, show);
count++;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index31 in $$obj) {
      $$l++;
      var values = $$obj[pug_index31];
if ((values[0] ? values[0].input : values[1].input) === input) {
pug_mixins["exchange-row"](values[0], values[1], count, input, show);
count++;
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
if (count > 10 && !compareTo) {
pug_html = pug_html + "\u003Ca class=\"toggle-control\" href=\"#\"\u003EShow " + (pug_escape(null == (pug_interp = count - 10) ? "" : pug_interp)) + " more\u003C\u002Fa\u003E\u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\"\u003EShow less\u003C\u002Fa\u003E";
if (input) {
pug_html = pug_html + "\u003Cp\u003E&nbsp;\u003C\u002Fp\u003E";
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["exchange-row"] = pug_interp = function(exchange, other, count, input, show){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var clazz = getValue(exchange, 'quantitativeReference') ? 'reference' : '';
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
if (category) {
category.categoryType = flow.type;
}
var otherCategory = getValue(otherFlow, 'category');
if (otherCategory) {
otherCategory.categoryType = otherFlow.type;
}
var changed = compareTo && !other ? 'added' : (compareTo && !exchange ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([clazz?clazz:null], [true]), false, false)+pug_attr("data-compare", changed, true, false)+pug_attr("style", pug_style(style), true, false)) + "\u003E\u003Ctd\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + (input?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + (input?'input':'output'), true, false)) + "\u002F\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["category-field"](category, otherCategory, true);
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
if (show.avoidedProduct) {
pug_mixins["boolean-sub-field"](exchange, other, 'avoidedProduct');
}
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](exchange, other, 'description', null, null, false, 75);
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", exchange?'exchanges[' + exchange.internalId +']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
      for (var pug_index33 = 0, $$l = $$obj.length; pug_index33 < $$l; pug_index33++) {
        var option = $$obj[pug_index33];
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
    for (var pug_index33 in $$obj) {
      $$l++;
      var option = $$obj[pug_index33];
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index36 = 0, $$l = $$obj.length; pug_index36 < $$l; pug_index36++) {
        var v = $$obj[pug_index36];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index36 in $$obj) {
      $$l++;
      var v = $$obj[pug_index36];
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
      for (var pug_index37 = 0, $$l = $$obj.length; pug_index37 < $$l; pug_index37++) {
        var v = $$obj[pug_index37];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index37 in $$obj) {
      $$l++;
      var v = $$obj[pug_index37];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index38 = 0, $$l = $$obj.length; pug_index38 < $$l; pug_index38++) {
        var v = $$obj[pug_index38];
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
    for (var pug_index38 in $$obj) {
      $$l++;
      var v = $$obj[pug_index38];
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
      for (var pug_index39 = 0, $$l = $$obj.length; pug_index39 < $$l; pug_index39++) {
        var other = $$obj[pug_index39];
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
    for (var pug_index39 in $$obj) {
      $$l++;
      var other = $$obj[pug_index39];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["non-causal-allocation-factor-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ch4\u003EPhysical & Economic allocation\u003C\u002Fh4\u003E\u003Ctable class=\"table non-causal-allocation-factors\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.product')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.physical')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.economic')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'nonCausalAllocationFactors')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'ALLOCATION_FACTOR', 'nonCausalAllocationFactors');
  if ('number' == typeof $$obj.length) {
      for (var pug_index41 = 0, $$l = $$obj.length; pug_index41 < $$l; pug_index41++) {
        var values = $$obj[pug_index41];
pug_mixins["non-causal-allocation-factor-row"](values[0], values[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index41 in $$obj) {
      $$l++;
      var values = $$obj[pug_index41];
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
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
      for (var pug_index43 = 0, $$l = $$obj.length; pug_index43 < $$l; pug_index43++) {
        var option = $$obj[pug_index43];
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
    for (var pug_index43 in $$obj) {
      $$l++;
      var option = $$obj[pug_index43];
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index46 = 0, $$l = $$obj.length; pug_index46 < $$l; pug_index46++) {
        var v = $$obj[pug_index46];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index46 in $$obj) {
      $$l++;
      var v = $$obj[pug_index46];
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
      for (var pug_index47 = 0, $$l = $$obj.length; pug_index47 < $$l; pug_index47++) {
        var v = $$obj[pug_index47];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index47 in $$obj) {
      $$l++;
      var v = $$obj[pug_index47];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index48 = 0, $$l = $$obj.length; pug_index48 < $$l; pug_index48++) {
        var v = $$obj[pug_index48];
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
    for (var pug_index48 in $$obj) {
      $$l++;
      var v = $$obj[pug_index48];
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
      for (var pug_index49 = 0, $$l = $$obj.length; pug_index49 < $$l; pug_index49++) {
        var other = $$obj[pug_index49];
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
    for (var pug_index49 in $$obj) {
      $$l++;
      var other = $$obj[pug_index49];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["causal-allocation-factor-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ch4\u003ECausal allocation\u003C\u002Fh4\u003E\u003Ctable class=\"table causal-allocation-factors\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('allocationFactors.product')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E";
var factor = dataset ? dataset.causalAllocationFactors[0] : null;
var otherFactor = compareTo && compareTo.causalAllocationFactors ? compareTo.causalAllocationFactors[0]: null;
var products = getArrayValues(factor, otherFactor, 'PRODUCT', 'products');
// iterate products
;(function(){
  var $$obj = products;
  if ('number' == typeof $$obj.length) {
      for (var pug_index51 = 0, $$l = $$obj.length; pug_index51 < $$l; pug_index51++) {
        var values = $$obj[pug_index51];
var flow = getValue(values[0], 'flow');
var otherFlow = getValue(values[1], 'flow');
pug_html = pug_html + "\u003Cth\u003E";
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Fth\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index51 in $$obj) {
      $$l++;
      var values = $$obj[pug_index51];
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
      for (var pug_index52 = 0, $$l = $$obj.length; pug_index52 < $$l; pug_index52++) {
        var values = $$obj[pug_index52];
if (isValid(values[0]) || isValid(values[1])) {
pug_mixins["causal-allocation-factor-row"](values[0], values[1], products);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index52 in $$obj) {
      $$l++;
      var values = $$obj[pug_index52];
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
var isInput = exchange ? exchange.input : otherExchange && otherExchange.input;
pug_html = pug_html + "\u003Ctd\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/' + (isInput?'input':'output') + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + (isInput?'input':'output'), true, false)) + "\u002F\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Ftd\u003E";
// iterate products
;(function(){
  var $$obj = products;
  if ('number' == typeof $$obj.length) {
      for (var pug_index53 = 0, $$l = $$obj.length; pug_index53 < $$l; pug_index53++) {
        var values = $$obj[pug_index53];
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
    for (var pug_index53 in $$obj) {
      $$l++;
      var values = $$obj[pug_index53];
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
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
      for (var pug_index55 = 0, $$l = $$obj.length; pug_index55 < $$l; pug_index55++) {
        var option = $$obj[pug_index55];
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
    for (var pug_index55 in $$obj) {
      $$l++;
      var option = $$obj[pug_index55];
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index58 = 0, $$l = $$obj.length; pug_index58 < $$l; pug_index58++) {
        var v = $$obj[pug_index58];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index58 in $$obj) {
      $$l++;
      var v = $$obj[pug_index58];
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
      for (var pug_index59 = 0, $$l = $$obj.length; pug_index59 < $$l; pug_index59++) {
        var v = $$obj[pug_index59];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index59 in $$obj) {
      $$l++;
      var v = $$obj[pug_index59];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index60 = 0, $$l = $$obj.length; pug_index60 < $$l; pug_index60++) {
        var v = $$obj[pug_index60];
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
    for (var pug_index60 in $$obj) {
      $$l++;
      var v = $$obj[pug_index60];
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
      for (var pug_index61 = 0, $$l = $$obj.length; pug_index61 < $$l; pug_index61++) {
        var other = $$obj[pug_index61];
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
    for (var pug_index61 in $$obj) {
      $$l++;
      var other = $$obj[pug_index61];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["social-aspect-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table social-aspects\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.socialIndicator.name')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.rawValue')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.riskLevel')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.acitivityVariable')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.quality')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.comment')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('socialAspects.source')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'SOCIAL_ASPECT', 'socialAspects')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'SOCIAL_ASPECT', 'socialAspects');
  if ('number' == typeof $$obj.length) {
      for (var pug_index63 = 0, $$l = $$obj.length; pug_index63 < $$l; pug_index63++) {
        var values = $$obj[pug_index63];
pug_mixins["social-aspect-row"](values[0], values[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index63 in $$obj) {
      $$l++;
      var values = $$obj[pug_index63];
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
pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
if (!baseUrl || (index === paths.length - 1 && !linkLast)) {
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
pug_html = pug_html + ("\u003Cspan" + (pug_attrs(pug_merge([{"class": "user-info"},attributes]), false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cimg" + (" class=\"avatar avatar-small\""+pug_attr("src", 'ws/' + lcType + '/avatar/' + id, true, false)+pug_attr("aria-label", ucType + ' avatar', true, false)) + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-left\"\u003E\u003Cspan class=\"user-info-content\"\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)));
if (type) {
pug_html = pug_html + " (" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)) + ")";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fspan\u003E";
};
pug_mixins["menubar"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!isPublic) {
if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
var currentCommit = null;
pug_mixins["select"].call({
block: function(){
// iterate commits
;(function(){
  var $$obj = commits;
  if ('number' == typeof $$obj.length) {
      for (var i = 0, $$l = $$obj.length; i < $$l; i++) {
        var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
      }
  } else {
    var $$l = 0;
    for (var i in $$obj) {
      $$l++;
      var commit = $$obj[i];
if (commit.id === dataset.commitId) {
currentCommit = commit;
}
pug_html = pug_html + "\u003Coption" + (pug_attr("class", pug_classes([commit.modelHasChanged?'highlight':null], [true]), false, false)+pug_attr("value", commit.id, true, false)+pug_attr("selected", commit.id===commitId, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = i===0?'Latest':commit.id) ? "" : pug_interp)) + "\u003C\u002Foption\u003E\u003Coptgroup" + (" class=\"additional-info\""+pug_attr("label", '&nbsp; &nbsp;' + formatCommitDescription(commit.message), false, false)) + "\u003E\u003C\u002Foptgroup\u003E";
    }
  }
}).call(this);

}
}, 'commitId', 'Commit', null, null, true);
if (dataset.deleted) {
pug_html = pug_html + "\u003Cdiv class=\"badge pull-right deleted\"\u003EDeleted\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"btn-group-vertical pull-right\" role=\"group\"\u003E";
pug_mixins["download-dropdown"]();
pug_mixins["comparison-dropdown"]();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_html = pug_html + (" on " + (pug_escape(null == (pug_interp = formatDate(currentCommit.timestamp)) ? "" : pug_interp)));
},
attributes: {"class": "concealed","data-path": "null"}
}, currentCommit.user, currentCommit.userDisplayName);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
}
if (compareTo) {
pug_html = pug_html + "\u003Cdiv class=\"header-box comparison-statistics\"\u003E\u003Ch4\u003EDifferences ";
if (!standalone) {
pug_html = pug_html + "to\u003Cbr\u002F\u003E\u003Csmall\u003E";
if (compareTo.id !== dataset.id) {
pug_html = pug_html + "'" + (pug_escape(null == (pug_interp = compareTo.name) ? "" : pug_interp)) + "' ";
}
pug_html = pug_html + "version '" + (pug_escape(null == (pug_interp = compareTo.commitId) ? "" : pug_interp)) + "'\u003C\u002Fsmall\u003E";
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
pug_mixins["download-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var dsType = dataset ? dataset.type : compareTo ? compareTo.type : null;
if (getTypeAsEnum(dsType) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
if (!dsType || $.inArray(getTypeAsEnum(dsType), ['ACTOR', 'SOURCE', 'UNIT_GROUP', 'FLOW_PROPERTY', 'FLOW', 'PROCESS', 'IMPACT_METHOD']) !== -1) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-format=\"ilcd\"\u003Eas ILCD\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["comparison-dropdown"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-transfer\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECompare to\u003C\u002Fspan\u003E\u003Cspan\u003E \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E";
var isFirst = commits[commits.length - 1].id === commitId;
if (!isFirst) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"previous\"\u003Eprevious version\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E";
}
if (commits.length > 2 || (commits.length === 2 && isFirst)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-version\"\u003Eother version...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
if (dataset && getTypeAsEnum(dataset.type) !== 'IMPACT_CATEGORY') {
pug_html = pug_html + "\u003Cli\u003E\u003Ca href=\"#\" data-compare-to=\"other-dataset\"\u003Eother data set...\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["header"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["title",isPublic?'header-box':null], [false,true]), false, false)) + "\u003E\u003Cimg" + (" class=\"pull-right model-icon\""+pug_attr("src", 'images/model/large/' + getIcon(dataset), true, false)+pug_attr("aria-label", 'Model icon of ' + dataset.type, true, false)) + "\u002F\u003E\u003Cdiv class=\"info-header\"\u003E\u003Ch3 data-path=\"name\"\u003E";
pug_mixins["field"]('name');
pug_html = pug_html + "\u003C\u002Fh3\u003E\u003Cdiv class=\"category\"\u003E";
pug_mixins["category-field"](dataset.category, getValue(compareTo, 'category'));
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
if (isPublic && !standalone) {
pug_mixins["download-dropdown"]();
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('version')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.version?dataset.version:'-') ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('lastChange')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E";
if (dataset.lastChange) {
pug_html = pug_html + (pug_escape(null == (pug_interp = formatDate(dataset.lastChange)) ? "" : pug_interp));
}
else {
pug_html = pug_html + "-";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('id')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.id) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};




pug_mixins["nav-tab"] = pug_interp = function(path, active, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!path || reviewMode || hasAtLeastOne(dataset, compareTo, path)) {
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([active?'active':null], [true]), false, false)+" role=\"presentation\"") + "\u003E";
var id = path || label.replace(' ', '-').toLowerCase();
pug_html = pug_html + "\u003Ca" + (pug_attr("href", ('#' + id), true, false)+pug_attr("aria-controls", id, true, false)+" role=\"tab\" data-toggle=\"tab\""+pug_attr("data-path", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = path?getLabel(path):label) ? "" : pug_interp)) + " \u003Cspan class=\"badge change-count\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
};
pug_mixins["nav-tab-pane"] = pug_interp = function(path, active, renderEmpty){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["tab-pane",active?'active':null], [false,true]), false, false)+pug_attr("id", path, true, false)+" role=\"tabpanel\"") + "\u003E";
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
      for (var pug_index68 = 0, $$l = $$obj.length; pug_index68 < $$l; pug_index68++) {
        var v = $$obj[pug_index68];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index68 in $$obj) {
      $$l++;
      var v = $$obj[pug_index68];
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
pug_mixins["long-text"](value);
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
pug_mixins["boolean-field"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["boolean-field-block"] = pug_interp = function(path){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cdiv" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
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
pug_mixins["array-field-row"] = pug_interp = function(path, type, getSpecificId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = getValue(dataset, path);
var value2 = getValue(compareTo, path);
if ((value && value.length) || (value2 && value2.length) || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
if (value) {
// iterate value
;(function(){
  var $$obj = value;
  if ('number' == typeof $$obj.length) {
      for (var pug_index70 = 0, $$l = $$obj.length; pug_index70 < $$l; pug_index70++) {
        var v = $$obj[pug_index70];
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
    for (var pug_index70 in $$obj) {
      $$l++;
      var v = $$obj[pug_index70];
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
      for (var pug_index71 = 0, $$l = $$obj.length; pug_index71 < $$l; pug_index71++) {
        var other = $$obj[pug_index71];
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
    for (var pug_index71 in $$obj) {
      $$l++;
      var other = $$obj[pug_index71];
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref.type) + '/' + ref.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
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
pug_html = pug_html + "\u003Ca" + (pug_attr("href", baseUrl + '/' + getTypeAsEnum(ref2.type) + '/' + ref2.id + query, true, false)+pug_attr("title", path, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = ref2.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
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
pug_mixins["category-field"] = pug_interp = function(category, category2, inTable){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var value = (category || '').split('/')
var value2 = (category2 || '').split('/')
var depth = inTable ? 2 : null;
var categoryCompare = ((value && value2) || !inTable) && compareTo ? compare(value, value2) : null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", categoryCompare, true, false)) + "\u003E";
pug_mixins["compare-icon"](categoryCompare);
if (value) {
var typeLabel = getTypeLabel(getTypeAsEnum(dataset.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "original-value","data-path": pug_escape(!inTable?'category':null)}
}, value, url, true, depth);
if (value2) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
}
if (value2 && (!value || categoryCompare)) {
var typeLabel = getTypeLabel(getTypeAsEnum(compareTo.type));
var url = baseUrl + 's' + '/' + typeLabel;
pug_mixins["category-breadcrumb"].call({
attributes: {"class": "comparison-value","style": pug_escape(pug_style(value&&!inTable?'margin-left:19px':null))}
}, value2, url, true, depth);
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
var changed = compareTo&&value!=value2?'changed':null;
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-compare", changed, true, false)) + "\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + "\u003Cspan class=\"original-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
if (changed) {
pug_html = pug_html + "\u003Cspan class=\"comparison-value\"\u003E\u003Cimg" + (" class=\"icon-small\""+pug_attr("src", 'images/check_' + (value2?'true':'false') + '.png', true, false)+pug_attr("aria-label", value?'Checked icon':'Unchecked icon', true, false)) + "\u002F\u003E\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["parameter-table"] = pug_interp = function(input){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var parameters = getValue(dataset, 'parameters');
var isEmpty = true;
var putPath = true;
if (parameters) {
// iterate parameters
;(function(){
  var $$obj = parameters;
  if ('number' == typeof $$obj.length) {
      for (var pug_index73 = 0, $$l = $$obj.length; pug_index73 < $$l; pug_index73++) {
        var parameter = $$obj[pug_index73];
if (parameter.inputParameter && !input) {
putPath = false;
}
if (parameter.inputParameter === input) {
isEmpty = false;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index73 in $$obj) {
      $$l++;
      var parameter = $$obj[pug_index73];
if (parameter.inputParameter && !input) {
putPath = false;
}
if (parameter.inputParameter === input) {
isEmpty = false;
}
    }
  }
}).call(this);

}
if (!isEmpty) {
var filter = function(param) {return param.inputParameter !==input;};
pug_html = pug_html + "\u003Ch4\u003E" + (pug_escape(null == (pug_interp = input?'Input parameters':'Dependant parameters') ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Ctable class=\"table table-parameters\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E";
if (hasAtLeastOne(dataset, compareTo, 'parameters', 'description', filter) || hasAtLeastOne(dataset, compareTo, 'parameters', 'uncertainty', filter)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameters.name') + '/' + getLabel('parameters.value')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E\u003Cth\u003E";
if (hasAtLeastOne(dataset, compareTo, 'parameters', 'description', filter)) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameters.description')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E";
if (input) {
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
      for (var pug_index74 = 0, $$l = $$obj.length; pug_index74 < $$l; pug_index74++) {
        var values = $$obj[pug_index74];
if ((values[0] ? values[0].inputParameter : values[1].inputParameter) === input) {
pug_mixins["parameter-row"](values[0], values[1], input);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index74 in $$obj) {
      $$l++;
      var values = $$obj[pug_index74];
if ((values[0] ? values[0].inputParameter : values[1].inputParameter) === input) {
pug_mixins["parameter-row"](values[0], values[1], input);
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
};
pug_mixins["parameter-row"] = pug_interp = function(parameter, other, input){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !other ? 'added' : (compareTo && !parameter ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_html = pug_html + (pug_escape(null == (pug_interp = parameter?parameter.name:other.name) ? "" : pug_interp));
if (!input) {
pug_html = pug_html + " = ";
pug_mixins["sub-field"](parameter, other, 'formula');
}
pug_html = pug_html + " = ";
pug_mixins["sub-field"](parameter, other, 'value');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"](parameter, other, 'description');
if (input) {
pug_mixins["uncertainty-cell"](parameter, other);
}
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", parameter?'parameters[' + parameter.name + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
var doc = dataset.processDocumentation;
var doc2 = getValue(compareTo, 'processDocumentation');
pug_mixins["menubar"]();
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"model-left-content\"\u003E";
pug_mixins["header"]();
pug_html = pug_html + "\u003Cdiv class=\"content\"\u003E\u003Cul class=\"nav nav-tabs\" role=\"tablist\"\u003E";
pug_mixins["nav-tab"]('exchanges', true);
pug_mixins["nav-tab"](null, false, 'Documentation');
pug_mixins["nav-tab"]('allocationFactors');
pug_mixins["nav-tab"]('socialAspects');
pug_mixins["nav-tab"]('parameters');
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
pug_mixins["field-row"]('processDocumentation.intendedApplication');
pug_mixins["ref-row"]('processDocumentation.dataSetOwner');
pug_mixins["ref-row"]('processDocumentation.dataGenerator');
pug_mixins["ref-row"]('processDocumentation.dataDocumentor');
pug_mixins["ref-row"]('processDocumentation.publication');
pug_mixins["ref-row"]('processDocumentation.reviewer');
pug_mixins["field-row"]('processDocumentation.reviewDetails');
pug_mixins["field-row"]('processDocumentation.restrictionsDescription');
pug_mixins["field-row"]('processDocumentation.projectDescription');
pug_mixins["field-row"]('processDocumentation.creationDate', formatDate);
pug_mixins["boolean-field-row"]('processDocumentation.copyright');
pug_mixins["field-row"]('processDocumentation.inventoryMethodDescription');
pug_mixins["field-row"]('processDocumentation.modelingConstantsDescription');
pug_mixins["field-row"]('processDocumentation.completenessDescription');
pug_mixins["field-row"]('processDocumentation.dataSelectionDescription');
pug_mixins["field-row"]('processDocumentation.dataTreatmentDescription');
pug_mixins["field-row"]('processDocumentation.samplingDescription');
pug_mixins["field-row"]('processDocumentation.dataCollectionDescription');
var getSourceId = function(source) {return source.id};
pug_mixins["array-field-row"]('processDocumentation.sources', 'SOURCE', getSourceId);
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
}, 'documentation', false, true);
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
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
pug_mixins["meta"].call({
block: function(){
pug_html = pug_html + "\u003Chr\u002F\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('quantitativeReference')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E";
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
if (exchange.quantitativeReference) {
flow = exchange.flow;
eIndex = index;
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var exchange = $$obj[index];
if (exchange.quantitativeReference) {
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
if (otherExchange.quantitativeReference) {
otherFlow = otherExchange.flow;
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var otherExchange = $$obj[index];
if (otherExchange.quantitativeReference) {
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
pug_mixins["labeled-boolean-field-block"]('infrastructureProcess');
if ((doc && (doc.validFrom || doc.validUntil)) || reviewMode) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["labeled-field-block"]('processDocumentation.validFrom', formatDate, '-');
pug_mixins["labeled-field-block"]('processDocumentation.validUntil', formatDate, '-');
}
if ((doc && (doc.timeDescription || doc.geographyDescription || doc.technologyDescription)) || reviewMode) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["labeled-field-block"]('processDocumentation.timeDescription', null, '-', true);
pug_mixins["labeled-field-block"]('processDocumentation.geographyDescription', null, '-', true);
pug_mixins["labeled-field-block"]('processDocumentation.technologyDescription', null, '-', true);
}
if (dataset.dqSystem || dataset.dqEntry || dataset.exchangeDqSystem || dataset.socialDqSystem || reviewMode) {
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
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"$" in locals_for_with?locals_for_with.$:typeof $!=="undefined"?$:undefined,"Math" in locals_for_with?locals_for_with.Math:typeof Math!=="undefined"?Math:undefined,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"activityUnit" in locals_for_with?locals_for_with.activityUnit:typeof activityUnit!=="undefined"?activityUnit:undefined,"activityVariable" in locals_for_with?locals_for_with.activityVariable:typeof activityVariable!=="undefined"?activityVariable:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"collapseLongText" in locals_for_with?locals_for_with.collapseLongText:typeof collapseLongText!=="undefined"?collapseLongText:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"compare" in locals_for_with?locals_for_with.compare:typeof compare!=="undefined"?compare:undefined,"compareTo" in locals_for_with?locals_for_with.compareTo:typeof compareTo!=="undefined"?compareTo:undefined,"compareUncertainty" in locals_for_with?locals_for_with.compareUncertainty:typeof compareUncertainty!=="undefined"?compareUncertainty:undefined,"comparisonCommitId" in locals_for_with?locals_for_with.comparisonCommitId:typeof comparisonCommitId!=="undefined"?comparisonCommitId:undefined,"dataset" in locals_for_with?locals_for_with.dataset:typeof dataset!=="undefined"?dataset:undefined,"exchangeDqSystem" in locals_for_with?locals_for_with.exchangeDqSystem:typeof exchangeDqSystem!=="undefined"?exchangeDqSystem:undefined,"exchangeMap" in locals_for_with?locals_for_with.exchangeMap:typeof exchangeMap!=="undefined"?exchangeMap:undefined,"findValue" in locals_for_with?locals_for_with.findValue:typeof findValue!=="undefined"?findValue:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"formatRelative" in locals_for_with?locals_for_with.formatRelative:typeof formatRelative!=="undefined"?formatRelative:undefined,"formatScientific" in locals_for_with?locals_for_with.formatScientific:typeof formatScientific!=="undefined"?formatScientific:undefined,"getArrayValues" in locals_for_with?locals_for_with.getArrayValues:typeof getArrayValues!=="undefined"?getArrayValues:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getTypeAsEnum" in locals_for_with?locals_for_with.getTypeAsEnum:typeof getTypeAsEnum!=="undefined"?getTypeAsEnum:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"getValue" in locals_for_with?locals_for_with.getValue:typeof getValue!=="undefined"?getValue:undefined,"hasAtLeastOne" in locals_for_with?locals_for_with.hasAtLeastOne:typeof hasAtLeastOne!=="undefined"?hasAtLeastOne:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"otherExchangeMap" in locals_for_with?locals_for_with.otherExchangeMap:typeof otherExchangeMap!=="undefined"?otherExchangeMap:undefined,"otherValue" in locals_for_with?locals_for_with.otherValue:typeof otherValue!=="undefined"?otherValue:undefined,"reviewMode" in locals_for_with?locals_for_with.reviewMode:typeof reviewMode!=="undefined"?reviewMode:undefined,"socialDqSystem" in locals_for_with?locals_for_with.socialDqSystem:typeof socialDqSystem!=="undefined"?socialDqSystem:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });