define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function ($, Math, Object, baseUrl, collapseLongText, commitId, commits, compare, compareTo, compareUncertainty, comparisonCommitId, dataset, exchangeDqSystem, exchangeMap, formatCommitDescription, formatDate, formatScientific, getArrayValues, getIcon, getLabel, getTypeAsEnum, getTypeLabel, getValue, hasAtLeastOne, isPublic, otherExchangeMap, otherValue, reviewMode, standalone, targetUnit, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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
pug_mixins["maximize-icon"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cli class=\"maximize-content\"\u003E\u003Ca href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-fullscreen\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
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
pug_mixins["product-system-parameter-redef-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('parameterRedefs.context')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('parameterRedefs.name')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('parameterRedefs.value')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E";
var showUncertainties = hasAtLeastOne(dataset, compareTo, 'parameterRedefs', 'uncertainty');
if (showUncertainties) {
pug_html = pug_html + (pug_escape(null == (pug_interp = getLabel('parameterRedefs.uncertainty')) ? "" : pug_interp));
}
pug_html = pug_html + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
// iterate getArrayValues(dataset, compareTo, 'PARAMETER_REDEF', 'parameterRedefs')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'PARAMETER_REDEF', 'parameterRedefs');
  if ('number' == typeof $$obj.length) {
      for (var pug_index9 = 0, $$l = $$obj.length; pug_index9 < $$l; pug_index9++) {
        var values = $$obj[pug_index9];
pug_mixins["product-system-parameter-redef-row"](values[0], values[1]);
      }
  } else {
    var $$l = 0;
    for (var pug_index9 in $$obj) {
      $$l++;
      var values = $$obj[pug_index9];
pug_mixins["product-system-parameter-redef-row"](values[0], values[1]);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["product-system-parameter-redef-row"] = pug_interp = function(parameter, other){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !other ? 'added' : (compareTo && !parameter ? 'removed' : null);
pug_html = pug_html + "\u003Ctr" + (pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
var parameterContext = getValue(parameter, 'context');
var otherContext = getValue(other, 'context');
if (parameterContext || otherContext) {
pug_mixins["ref"](parameterContext, otherContext);
}
else {
pug_html = pug_html + "\u003Cspan\u003EGlobal\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-cell"].call({
block: function(){
pug_html = pug_html + " ";
}
}, parameter, other, 'name');
pug_mixins["sub-field-cell"].call({
block: function(){
pug_html = pug_html + " ";
}
}, parameter, other, 'value');
pug_mixins["uncertainty-cell"].call({
block: function(){
pug_html = pug_html + " ";
}
}, parameter, other);
var context = parameter && parameter.context ? parameter.context.id : 'global';
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", parameter?'parameterRedefs[' + context + '-' + parameter.name + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
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
      for (var pug_index11 = 0, $$l = $$obj.length; pug_index11 < $$l; pug_index11++) {
        var option = $$obj[pug_index11];
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
    for (var pug_index11 in $$obj) {
      $$l++;
      var option = $$obj[pug_index11];
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
pug_mixins["maximize-icon"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cli class=\"maximize-content\"\u003E\u003Ca href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-fullscreen\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
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
      for (var pug_index14 = 0, $$l = $$obj.length; pug_index14 < $$l; pug_index14++) {
        var v = $$obj[pug_index14];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index14 in $$obj) {
      $$l++;
      var v = $$obj[pug_index14];
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
      for (var pug_index15 = 0, $$l = $$obj.length; pug_index15 < $$l; pug_index15++) {
        var v = $$obj[pug_index15];
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = v) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index15 in $$obj) {
      $$l++;
      var v = $$obj[pug_index15];
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
      for (var pug_index19 = 0, $$l = $$obj.length; pug_index19 < $$l; pug_index19++) {
        var exchange = $$obj[pug_index19];
if (exchange.input === input) {
count++;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index19 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index19];
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
      for (var pug_index20 = 0, $$l = $$obj.length; pug_index20 < $$l; pug_index20++) {
        var exchange = $$obj[pug_index20];
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
    for (var pug_index20 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index20];
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
      for (var pug_index21 = 0, $$l = $$obj.length; pug_index21 < $$l; pug_index21++) {
        var exchange = $$obj[pug_index21];
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
    for (var pug_index21 in $$obj) {
      $$l++;
      var exchange = $$obj[pug_index21];
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
      for (var pug_index22 = 0, $$l = $$obj.length; pug_index22 < $$l; pug_index22++) {
        var values = $$obj[pug_index22];
if ((values[0] ? values[0].input : values[1].input) === input) {
pug_mixins["exchange-row"](values[0], values[1], count, input, show);
count++;
}
      }
  } else {
    var $$l = 0;
    for (var pug_index22 in $$obj) {
      $$l++;
      var values = $$obj[pug_index22];
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
      for (var pug_index24 = 0, $$l = $$obj.length; pug_index24 < $$l; pug_index24++) {
        var option = $$obj[pug_index24];
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
    for (var pug_index24 in $$obj) {
      $$l++;
      var option = $$obj[pug_index24];
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
pug_mixins["maximize-icon"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cli class=\"maximize-content\"\u003E\u003Ca href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-fullscreen\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
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
      for (var pug_index32 = 0, $$l = $$obj.length; pug_index32 < $$l; pug_index32++) {
        var key = $$obj[pug_index32];
cats1.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index32 in $$obj) {
      $$l++;
      var key = $$obj[pug_index32];
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
      for (var pug_index33 = 0, $$l = $$obj.length; pug_index33 < $$l; pug_index33++) {
        var key = $$obj[pug_index33];
if ($.inArray(key, cats1) === -1) {
cats1.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index33 in $$obj) {
      $$l++;
      var key = $$obj[pug_index33];
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
      for (var pug_index34 = 0, $$l = $$obj.length; pug_index34 < $$l; pug_index34++) {
        var cat1 = $$obj[pug_index34];
var cats2 = [];
if (map && map[path] && map[path][cat1]) {
// iterate Object.keys(map[path][cat1])
;(function(){
  var $$obj = Object.keys(map[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index35 = 0, $$l = $$obj.length; pug_index35 < $$l; pug_index35++) {
        var key = $$obj[pug_index35];
cats2.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index35 in $$obj) {
      $$l++;
      var key = $$obj[pug_index35];
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
      for (var pug_index36 = 0, $$l = $$obj.length; pug_index36 < $$l; pug_index36++) {
        var key = $$obj[pug_index36];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index36 in $$obj) {
      $$l++;
      var key = $$obj[pug_index36];
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
      for (var pug_index37 = 0, $$l = $$obj.length; pug_index37 < $$l; pug_index37++) {
        var cat2 = $$obj[pug_index37];
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
    for (var pug_index37 in $$obj) {
      $$l++;
      var cat2 = $$obj[pug_index37];
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
    for (var pug_index34 in $$obj) {
      $$l++;
      var cat1 = $$obj[pug_index34];
var cats2 = [];
if (map && map[path] && map[path][cat1]) {
// iterate Object.keys(map[path][cat1])
;(function(){
  var $$obj = Object.keys(map[path][cat1]);
  if ('number' == typeof $$obj.length) {
      for (var pug_index38 = 0, $$l = $$obj.length; pug_index38 < $$l; pug_index38++) {
        var key = $$obj[pug_index38];
cats2.push(key);
      }
  } else {
    var $$l = 0;
    for (var pug_index38 in $$obj) {
      $$l++;
      var key = $$obj[pug_index38];
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
      for (var pug_index39 = 0, $$l = $$obj.length; pug_index39 < $$l; pug_index39++) {
        var key = $$obj[pug_index39];
if ($.inArray(key, cats2) === -1) {
cats2.push(key);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index39 in $$obj) {
      $$l++;
      var key = $$obj[pug_index39];
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
      for (var pug_index40 = 0, $$l = $$obj.length; pug_index40 < $$l; pug_index40++) {
        var cat2 = $$obj[pug_index40];
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
    for (var pug_index40 in $$obj) {
      $$l++;
      var cat2 = $$obj[pug_index40];
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
pug_mixins["target-amount-row"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = getLabel('targetAmount')) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["field"]('targetAmount');
if (dataset.targetUnit || getValue(compareTo, targetUnit) || reviewMode) {
pug_html = pug_html + " ";
pug_mixins["field"]('targetUnit.name');
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["menubar"]();
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"model-left-content\"\u003E";
pug_mixins["header"]();
pug_html = pug_html + "\u003Cdiv class=\"content\"\u003E";
var hasLinks = hasAtLeastOne(dataset, null, 'processLinks');
pug_html = pug_html + "\u003Cul class=\"nav nav-tabs\" role=\"tablist\"\u003E";
pug_mixins["nav-tab"](null, true, 'Overview');
pug_mixins["nav-tab"]('inventory');
if (dataset.inventory && dataset.inventory.length) {
pug_mixins["nav-tab"](null, false, 'Impacts');
}
if (hasLinks && !standalone) {
pug_mixins["nav-tab"](null, false, 'Supply chain');
pug_mixins["nav-tab"](null, false, 'Graph');
}
pug_mixins["nav-tab"]('parameterRedefs');
if (!standalone) {
pug_mixins["maximize-icon"]();
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003Cdiv class=\"tab-content\"\u003E";
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
pug_mixins["ref-row"]('referenceProcess');
if (dataset.referenceExchange && dataset.referenceExchange.flow) {
pug_mixins["ref-row"]('referenceExchange.flow');
}
else {
var name = dataset.referenceExchange ? dataset.referenceExchange.name : null;
var otherName = compareTo && compareTo.referenceExchange ? compareTo.referenceExchange.name : null;
if (name || otherName || reviewMode) {
pug_mixins["field-row-frame"].call({
block: function(){
pug_mixins["field-row-value"](name, otherName);
}
}, 'referenceExchange.name');
}
}
pug_mixins["ref-row"]('targetFlowProperty');
pug_mixins["target-amount-row"]();
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
}, 'overview', true, true);
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ca class=\"pull-right toggle-control switch-to-list\" href=\"#\"\u003ESwitch to list view\u003C\u002Fa\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"exchange-tables toggleable\"\u003E";
pug_mixins["exchange-table"]('inventory', true);
pug_mixins["exchange-table"]('inventory', false);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Ca class=\"pull-right toggle-control switch-to-table\" href=\"#\"\u003ESwitch to table view\u003C\u002Fa\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"exchange-list toggleable\"\u003E";
pug_mixins["exchange-list"](exchangeMap, otherExchangeMap);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
}, 'inventory');
if (dataset.inventory && dataset.inventory.length) {
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Clabel\u003EImpact method:\u003C\u002Flabel\u003E \u003Cspan class=\"impact-method\"\u003ENo selection\u003C\u002Fspan\u003E \u003Cbutton class=\"btn btn-sm btn-default select-method\"\u003ESelect...\u003C\u002Fbutton\u003E\u003Ctable class=\"table impact-result-table no-sorting\" style=\"display:none;\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003EImpact category\u003C\u002Fth\u003E\u003Cth\u003EResult\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
}, 'impacts', false, true);
}
if (!standalone) {
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Cdiv id=\"process-tree-container\"\u003E\u003C\u002Fdiv\u003E";
}
}, 'supply-chain', false, hasLinks);
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ciframe src=\"graph\u002Fgraph.html\" width=\"100%\" height=\"500px\"\u003E\u003C\u002Fiframe\u003E";
}
}, 'graph', false, hasLinks);
}
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["product-system-parameter-redef-table"]();
}
}, 'parameterRedefs');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
pug_mixins["meta"].call({
block: function(){
pug_html = pug_html + "\u003Chr\u002F\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processes')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.processes?dataset.processes.length:0) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = getLabel('processLinks')) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = dataset.processLinks?dataset.processLinks.length:0) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
}
});
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"$" in locals_for_with?locals_for_with.$:typeof $!=="undefined"?$:undefined,"Math" in locals_for_with?locals_for_with.Math:typeof Math!=="undefined"?Math:undefined,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"collapseLongText" in locals_for_with?locals_for_with.collapseLongText:typeof collapseLongText!=="undefined"?collapseLongText:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"compare" in locals_for_with?locals_for_with.compare:typeof compare!=="undefined"?compare:undefined,"compareTo" in locals_for_with?locals_for_with.compareTo:typeof compareTo!=="undefined"?compareTo:undefined,"compareUncertainty" in locals_for_with?locals_for_with.compareUncertainty:typeof compareUncertainty!=="undefined"?compareUncertainty:undefined,"comparisonCommitId" in locals_for_with?locals_for_with.comparisonCommitId:typeof comparisonCommitId!=="undefined"?comparisonCommitId:undefined,"dataset" in locals_for_with?locals_for_with.dataset:typeof dataset!=="undefined"?dataset:undefined,"exchangeDqSystem" in locals_for_with?locals_for_with.exchangeDqSystem:typeof exchangeDqSystem!=="undefined"?exchangeDqSystem:undefined,"exchangeMap" in locals_for_with?locals_for_with.exchangeMap:typeof exchangeMap!=="undefined"?exchangeMap:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"formatScientific" in locals_for_with?locals_for_with.formatScientific:typeof formatScientific!=="undefined"?formatScientific:undefined,"getArrayValues" in locals_for_with?locals_for_with.getArrayValues:typeof getArrayValues!=="undefined"?getArrayValues:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getTypeAsEnum" in locals_for_with?locals_for_with.getTypeAsEnum:typeof getTypeAsEnum!=="undefined"?getTypeAsEnum:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"getValue" in locals_for_with?locals_for_with.getValue:typeof getValue!=="undefined"?getValue:undefined,"hasAtLeastOne" in locals_for_with?locals_for_with.hasAtLeastOne:typeof hasAtLeastOne!=="undefined"?hasAtLeastOne:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"otherExchangeMap" in locals_for_with?locals_for_with.otherExchangeMap:typeof otherExchangeMap!=="undefined"?otherExchangeMap:undefined,"otherValue" in locals_for_with?locals_for_with.otherValue:typeof otherValue!=="undefined"?otherValue:undefined,"reviewMode" in locals_for_with?locals_for_with.reviewMode:typeof reviewMode!=="undefined"?reviewMode:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"targetUnit" in locals_for_with?locals_for_with.targetUnit:typeof targetUnit!=="undefined"?targetUnit:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });