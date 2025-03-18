define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function ($, Math, Object, baseUrl, collapseLongText, commitId, commits, compare, compareTo, compareUncertainty, comparisonCommitId, dataset, findValue, formatCommitDescription, formatScientific, getIcon, getLabel, getTypeAsEnum, getTypeLabel, getValue, isPublic, releases, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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





























































pug_mixins["impact-factor-row"] = pug_interp = function(category, otherCategory, factor, otherFactor, count){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var flow = getValue(factor, 'flow');
var otherFlow = getValue(otherFactor, 'flow');
var unit = getValue(factor, 'unit');
var otherUnit = getValue(otherFactor, 'unit');
var changed = compareTo && !otherFactor ? 'added' : (compareTo && !factor ? 'removed' : null);
var hide = count>=10 && !compareTo; 
pug_html = pug_html + "\u003Ctr" + (pug_attr("class", pug_classes([hide?'toggleable':null], [true]), false, false)+pug_attr("data-compare", changed, true, false)+pug_attr("style", pug_style(hide?'display:none;':null), true, false)) + "\u003E\u003Ctd\u003E";
if (category && otherCategory) {
pug_mixins["compare-icon"](changed);
}
pug_mixins["ref"](flow, otherFlow);
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
pug_mixins["category-field"](flow, otherFlow, true, 'Flow');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["sub-field-ref-cell"](factor, otherFactor, 'location');
pug_html = pug_html + "\u003Ctd\u003E ";
pug_mixins["sub-field"](factor, otherFactor, 'value', null, formatScientific);
pug_html = pug_html + " ";
pug_mixins["sub-field"](category, otherCategory, 'refUnit');
pug_html = pug_html + "\u002F";
pug_mixins["sub-field"](unit, otherUnit, 'name');
pug_html = pug_html + "\u003C\u002Ftd\u003E";
pug_mixins["uncertainty-cell"](factor, otherFactor, formatScientific);
pug_html = pug_html + "\u003Ctd" + (pug_attr("data-path", flow?'impactCategories[' + category.id + '].impactFactors[' + flow.id + ']':null, true, false)) + "\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
};}.call(this,"$" in locals_for_with?locals_for_with.$:typeof $!=="undefined"?$:undefined,"Math" in locals_for_with?locals_for_with.Math:typeof Math!=="undefined"?Math:undefined,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"collapseLongText" in locals_for_with?locals_for_with.collapseLongText:typeof collapseLongText!=="undefined"?collapseLongText:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"compare" in locals_for_with?locals_for_with.compare:typeof compare!=="undefined"?compare:undefined,"compareTo" in locals_for_with?locals_for_with.compareTo:typeof compareTo!=="undefined"?compareTo:undefined,"compareUncertainty" in locals_for_with?locals_for_with.compareUncertainty:typeof compareUncertainty!=="undefined"?compareUncertainty:undefined,"comparisonCommitId" in locals_for_with?locals_for_with.comparisonCommitId:typeof comparisonCommitId!=="undefined"?comparisonCommitId:undefined,"dataset" in locals_for_with?locals_for_with.dataset:typeof dataset!=="undefined"?dataset:undefined,"findValue" in locals_for_with?locals_for_with.findValue:typeof findValue!=="undefined"?findValue:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatScientific" in locals_for_with?locals_for_with.formatScientific:typeof formatScientific!=="undefined"?formatScientific:undefined,"getIcon" in locals_for_with?locals_for_with.getIcon:typeof getIcon!=="undefined"?getIcon:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getTypeAsEnum" in locals_for_with?locals_for_with.getTypeAsEnum:typeof getTypeAsEnum!=="undefined"?getTypeAsEnum:undefined,"getTypeLabel" in locals_for_with?locals_for_with.getTypeLabel:typeof getTypeLabel!=="undefined"?getTypeLabel:undefined,"getValue" in locals_for_with?locals_for_with.getValue:typeof getValue!=="undefined"?getValue:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"releases" in locals_for_with?locals_for_with.releases:typeof releases!=="undefined"?releases:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });