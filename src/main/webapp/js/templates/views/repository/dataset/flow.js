define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function ($, Math, Object, baseUrl, collapseLongText, commitId, commits, compare, compareTo, comparisonCommitId, dataset, formatCommitDescription, formatDate, getArrayValues, getIcon, getLabel, getSpecificTypeLabel, getTypeAsEnum, getTypeLabel, getValue, hasAtLeastOne, isPublic, reviewMode, standalone, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId){
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



































































pug_mixins["flow-property-table"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table flow-properties\"\u003E\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('flowProperties.name')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E" + (pug_escape(null == (pug_interp = getLabel('flowProperties.formula')) ? "" : pug_interp)) + "\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E\u003Ctbody\u003E";
var referenceFactor = null;
if (dataset.flowProperties) {
// iterate dataset.flowProperties
;(function(){
  var $$obj = dataset.flowProperties;
  if ('number' == typeof $$obj.length) {
      for (var pug_index9 = 0, $$l = $$obj.length; pug_index9 < $$l; pug_index9++) {
        var factor = $$obj[pug_index9];
if (factor.referenceFlowProperty) {
referenceFactor = factor
}
      }
  } else {
    var $$l = 0;
    for (var pug_index9 in $$obj) {
      $$l++;
      var factor = $$obj[pug_index9];
if (factor.referenceFlowProperty) {
referenceFactor = factor
}
    }
  }
}).call(this);

}
// iterate getArrayValues(dataset, compareTo, 'FLOW_PROPERTY_FACTOR', 'flowProperties')
;(function(){
  var $$obj = getArrayValues(dataset, compareTo, 'FLOW_PROPERTY_FACTOR', 'flowProperties');
  if ('number' == typeof $$obj.length) {
      for (var pug_index10 = 0, $$l = $$obj.length; pug_index10 < $$l; pug_index10++) {
        var values = $$obj[pug_index10];
pug_mixins["flow-property-row"](values[0], values[1], referenceFactor);
      }
  } else {
    var $$l = 0;
    for (var pug_index10 in $$obj) {
      $$l++;
      var values = $$obj[pug_index10];
pug_mixins["flow-property-row"](values[0], values[1], referenceFactor);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_mixins["flow-property-row"] = pug_interp = function(factor, other, referenceFactor){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var changed = compareTo && !other ? 'added' : (compareTo && !factor ? 'removed' : null);
var property = getValue(factor, 'flowProperty');
var otherProperty = getValue(other, 'flowProperty');
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([getValue(factor, 'referenceFlowProperty')?'reference':null], [true]), false, false)+pug_attr("data-compare", changed, true, false)) + "\u003E\u003Ctd\u003E";
pug_mixins["compare-icon"](changed);
pug_mixins["ref"](property, otherProperty);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E1 " + (pug_escape(null == (pug_interp = referenceFactor.referenceUnit) ? "" : pug_interp)) + " = ";
pug_mixins["sub-field"](factor, other, 'conversionFactor');
pug_html = pug_html + " ";
pug_mixins["sub-field"](factor, other, 'referenceUnit');
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd" + (pug_attr("data-path", property?'flowProperties[' + property.id + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};
pug_mixins["menubar"]();
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"model-left-content\"\u003E";
pug_mixins["header"]();
pug_html = pug_html + "\u003Cdiv class=\"content\"\u003E";
var outLabel = dataset.flowType==='ELEMENTARY_FLOW'?'Emitted by':'Produced by';
var outId = dataset.flowType==='ELEMENTARY_FLOW'?'emitted-by':'produced-by';
pug_html = pug_html + "\u003Cul class=\"nav nav-tabs\" role=\"tablist\"\u003E";
pug_mixins["nav-tab"](null, true, 'Flow info');
pug_mixins["nav-tab"]('flowProperties');
pug_mixins["nav-tab"](null, false, 'Used by');
pug_mixins["nav-tab"](null, false, outLabel);
pug_html = pug_html + "\u003C\u002Ful\u003E\u003Cdiv class=\"tab-content\"\u003E";
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Ctable class=\"table no-head\"\u003E\u003Ctbody\u003E";
var getFlowTypeLabel = function (flowType) { return getSpecificTypeLabel('FlowType', flowType); };
pug_mixins["field-row"]('flowType', getFlowTypeLabel);
pug_mixins["field-row"]('cas');
pug_mixins["field-row"]('formula');
pug_mixins["field-row"]('synonyms');
pug_mixins["ref-row"]('location');
pug_mixins["boolean-field-row"]('infrastructureFlow');
pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
}, 'flow-info', true, true);
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_mixins["flow-property-table"]();
}
}, 'flowProperties');
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Cdiv class=\"form-group\"\u003E\u003Cinput class=\"form-control\" id=\"used-by-filter\" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv id=\"used-by-data\"\u003E\u003C\u002Fdiv\u003E";
}
}, 'used-by', false, true);
pug_mixins["nav-tab-pane"].call({
block: function(){
pug_html = pug_html + "\u003Cdiv class=\"form-group\"\u003E\u003Cinput" + (" class=\"form-control\""+pug_attr("id", outId + '-filter', true, false)+" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"") + "\u002F\u003E\u003C\u002Fdiv\u003E\u003Cdiv" + (pug_attr("id", outId + '-data', true, false)) + "\u003E\u003C\u002Fdiv\u003E";
}
}, outId, false, true);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
pug_mixins["meta"]();
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"$" in locals_for_with?locals_for_with.$:typeof $!=="undefined"?$:undefined,"Math" in locals_for_with?locals_for_with.Math:typeof Math!=="undefined"?Math:undefined,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"collapseLongText" in locals_for_with?locals_for_with.collapseLongText:typeof collapseLongText!=="undefined"?collapseLongText:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"compare" in locals_for_with?locals_for_with.compare:typeof compare!=="undefined"?compare:undefined,"compareTo" in locals_for_with?locals_for_with.compareTo:typeof compareTo!=="undefined"?compareTo:undefined,"comparisonCommitId" in locals_for_with?locals_for_with.comparisonCommitId:typeof comparisonCommitId!=="undefined"?comparisonCommitId:undefined,"dataset" in locals_for_with?locals_for_with.dataset:typeof dataset!=="undefined"?dataset:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"getArrayValues" in locals_for_with?locals_for_with.getArrayValues:typeof getArrayValues!=="undefined"?getArrayValues:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getSpecificTypeLabel" in locals_for_with?locals_for_with.getSpecificTypeLabel:typeof getSpecificTypeLabel!=="undefined"?getSpecificTypeLabel:undefined,"getTypeAsEnum" in locals_for_with?locals_for_with.getTypeAsEnum:typeof getTypeAsEnum!=="undefined"?getTypeAsEnum:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"getValue" in locals_for_with?locals_for_with.getValue:typeof getValue!=="undefined"?getValue:undefined,"hasAtLeastOne" in locals_for_with?locals_for_with.hasAtLeastOne:typeof hasAtLeastOne!=="undefined"?hasAtLeastOne:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"reviewMode" in locals_for_with?locals_for_with.reviewMode:typeof reviewMode!=="undefined"?reviewMode:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });