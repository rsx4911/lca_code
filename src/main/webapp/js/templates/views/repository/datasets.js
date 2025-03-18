define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, baseUrl, categoryPath, commitId, commits, formatCommitDescription, formatDate, info, inline, isPublic, releases, value) {pug_mixins["category-breadcrumb"] = pug_interp = function(paths, baseUrl, linkLast, depth, commitId, disabled){
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
pug_mixins["static"] = pug_interp = function(id, label, value, href){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var clazz = null;
if (inline) {
clazz = 'form-inline';
}
else {
clazz = 'form-group';
}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([clazz], [true]), false, false)+pug_attr("id", id + '-group', true, false)) + "\u003E";
if (label) {
pug_html = pug_html + "\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Flabel\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"form-value\"\u003E";
if (href) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", href, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
}
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
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
pug_mixins["toggleable"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan class=\"toggleable\"\u003E\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["toggleable-text"] = pug_interp = function(short, long, showLongInTitle){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cspan" + (" class=\"toggleable\""+pug_attr("title", showLongInTitle ? value : null, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = short) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" aria-label=\"Show more\"\u003EShow more\u003C\u002Fa\u003E\u003Cspan class=\"toggleable\" style=\"display:none\"\u003E" + (pug_escape(null == (pug_interp = long) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\" aria-label=\"Show less\"\u003EShow less\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};













pug_mixins["static-setting"] = pug_interp = function(field, label, formatter){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (info[field]) {
var value = formatter ? formatter(info[field]) : info[field];
pug_mixins["static"](field, label, value);
}
};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["header-box",isPublic?'only-box':null], [false,true]), false, false)) + "\u003E\u003Cdiv class=\"pull-left\"\u003E";
if (isPublic) {
pug_mixins["release-selection"]('commit', 'Version', releases, commitId, true);
}
else
if (!isPublic) {
pug_mixins["commit-selection"]('commit', 'Commit', commits, commitId, true);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"pull-right\"\u003E\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload \u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\"\u003Eas JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\"\u003Eas JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
var type = null;
if (categoryPath && categoryPath.length) {
var type = categoryPath[0]
}
pug_html = pug_html + "\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json\" data-action=\"select-data\"\u003Eas JSON-LD (openLCA 2.x) (Subselect)...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca href=\"#\" data-format=\"json1\" data-action=\"select-data\"\u003Eas JSON-LD (openLCA 1.x) (Subselect)...\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
if (info && (info.description || info.version || info.lastChange || info.typeOfData || info.sourceInfo || info.contactInfo || info.changeLog || info.projectInfo || info.projectFunding || info.appropriateUse || info.dqAssessment)) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E\u003Cdiv class=\"repository-description\"\u003E";
if (info.description) {
pug_html = pug_html + "\u003Cp\u003E" + (pug_escape(null == (pug_interp = info.description) ? "" : pug_interp)) + "\u003C\u002Fp\u003E";
}
pug_mixins["toggleable"].call({
block: function(){
if (!isPublic) {
pug_mixins["static-setting"]('version', 'Version');
}
pug_mixins["static-setting"]('lastChange', 'Last change', formatDate);
pug_mixins["static-setting"]('typeOfData', 'Type of data');
pug_mixins["static-setting"]('sourceInfo', 'Source information');
pug_mixins["static-setting"]('contactInfo', 'Contact information');
pug_mixins["static-setting"]('changeLog', 'Change log');
pug_mixins["static-setting"]('projectInfo', 'Project information');
pug_mixins["static-setting"]('projectFunding', 'Project funding');
pug_mixins["static-setting"]('appropriateUse', 'Appropriate use');
pug_mixins["static-setting"]('dqAssessment', 'Data quality assessment');
pug_mixins["static-setting"]('citation', 'Citation');
}
});
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
if (categoryPath) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E\u003Cdiv class=\"form-group\"\u003E\u003Cinput class=\"form-control input-lg\" id=\"filter\" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"\u002F\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003E";
pug_mixins["category-breadcrumb"](categoryPath.split('/'), baseUrl, false, false, commitId);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003Ctable class=\"table table-browse\"\u003E";
if (!isPublic) {
pug_html = pug_html + "\u003Cthead\u003E\u003Ctr\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003Cth\u003EName\u003C\u002Fth\u003E\u003Cth\u003ELast change\u003C\u002Fth\u003E\u003Cth\u003ECommit\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Fthead\u003E";
}
pug_html = pug_html + "\u003Ctbody\u003E\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E\u003Cdiv class=\"no-content-message\" style=\"display:none\"\u003ENo data sets found\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"baseUrl" in locals_for_with?locals_for_with.baseUrl:typeof baseUrl!=="undefined"?baseUrl:undefined,"categoryPath" in locals_for_with?locals_for_with.categoryPath:typeof categoryPath!=="undefined"?categoryPath:undefined,"commitId" in locals_for_with?locals_for_with.commitId:typeof commitId!=="undefined"?commitId:undefined,"commits" in locals_for_with?locals_for_with.commits:typeof commits!=="undefined"?commits:undefined,"formatCommitDescription" in locals_for_with?locals_for_with.formatCommitDescription:typeof formatCommitDescription!=="undefined"?formatCommitDescription:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"info" in locals_for_with?locals_for_with.info:typeof info!=="undefined"?info:undefined,"inline" in locals_for_with?locals_for_with.inline:typeof inline!=="undefined"?inline:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"releases" in locals_for_with?locals_for_with.releases:typeof releases!=="undefined"?releases:undefined,"value" in locals_for_with?locals_for_with.value:typeof value!=="undefined"?value:undefined));;return pug_html;} return template; });