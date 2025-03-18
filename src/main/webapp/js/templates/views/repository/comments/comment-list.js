define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, canApprove, canComment, canEdit, currentUser, data, formatDate, getLabel, getRoleLabel, isRoleAvailable, resultInfo, values) {pug_mixins["user-info"] = pug_interp = function(id, name, type){
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
pug_mixins["comment-visibility"] = pug_interp = function(restrictedToRole, roles){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var title = 'Visible to everybody';
if (restrictedToRole) {
title = 'Only visible for users with role \'' + getRoleLabel(restrictedToRole) + '\' or higher';
}
pug_html = pug_html + "\u003Cdiv class=\"change-visibility dropdown\"\u003E\u003Ca href=\"#\" data-toggle=\"dropdown\"\u003E\u003Cspan" + (pug_attr("class", pug_classes(["comment-visibility","glyphicon",!restrictedToRole?'glyphicon-globe':'glyphicon-lock'], [false,false,true]), false, false)+pug_attr("title", title, true, false)) + "\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca href=\"#\" data-role=\"null\"\u003EEverybody\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"dropdown-submenu\"\u003E\u003Ca href=\"#\"\u003ESpecific role \u003Cdiv class=\"caret right\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fa\u003E\u003Cul class=\"dropdown-menu\"\u003E";
// iterate roles
;(function(){
  var $$obj = roles;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var role = $$obj[pug_index0];
if (role === restrictedToRole || isRoleAvailable(role)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca" + (" href=\"#\""+pug_attr("data-role", role.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = role.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var role = $$obj[pug_index0];
if (role === restrictedToRole || isRoleAvailable(role)) {
pug_html = pug_html + "\u003Cli\u003E\u003Ca" + (" href=\"#\""+pug_attr("data-role", role.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = role.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["comment-info"] = pug_interp = function(comment, roles, showDatasetInfo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var canChangeVisibility = canComment && (currentUser.admin || (comment.user && currentUser.username === comment.user.username));
pug_html = pug_html + "\u003Cspan class=\"user-extra-info\"\u003E commented on " + (pug_escape(null == (pug_interp = formatDate(comment.date)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (canChangeVisibility && roles) {
pug_html = pug_html + "&nbsp;";
pug_mixins["comment-visibility"](comment.restrictedToRole, roles);
}
if ((!comment.released && comment.user && comment.user.username === currentUser.username) || (comment.released && !comment.approved && canApprove)) {
pug_html = pug_html + " | \u003Ca class=\"default-link release\" href=\"#\"\u003E\u003Csmall\u003E" + (pug_escape(null == (pug_interp = !comment.released?'Release':'Approve') ? "" : pug_interp)) + "\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E";
}
if (comment.released && !comment.approved && !canApprove) {
pug_html = pug_html + " | \u003Ci\u003E\u003Csmall\u003EApproval pending\u003C\u002Fsmall\u003E\u003C\u002Fi\u003E";
}
if (!comment.replyTo && canComment && comment.released && !!comment.approved) {
pug_html = pug_html + " | \u003Ca class=\"default-link reply-to\" href=\"#\"\u003E\u003Csmall\u003EReply\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E";
}
if ((!comment.released || !comment.approved) && canEdit && comment.user && comment.user.username === currentUser.username) {
pug_html = pug_html + " | \u003Ca class=\"default-link edit\" href=\"#\"\u003E\u003Csmall\u003EEdit\u003C\u002Fsmall\u003E\u003C\u002Fa\u003E \u003Ca class=\"remove\" href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-remove\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
};
pug_mixins["comment-entry"] = pug_interp = function(comment, roles, showDatasetInfo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var classes = [];
if (comment.replyTo) {
classes.push('reply');
}
if (comment.released) {
if (comment.approved) {
classes.push('approved');
}
else {
classes.push('released');
}
}
var addClass = null;
if (classes.length) {
addClass = '';
// iterate classes
;(function(){
  var $$obj = classes;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var c = $$obj[index];
if (index > 0) {
addClass += ' '
}
addClass += c;
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var c = $$obj[index];
if (index > 0) {
addClass += ' '
}
addClass += c;
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["comment-entry",addClass], [false,true]), false, false)+pug_attr("data-comment-id", comment.id, true, false)) + "\u003E";
var username = comment.user ? comment.user.username : 'null'
var name = comment.user ? comment.user.name : 'Anonymous user'
pug_mixins["user-info"].call({
block: function(){
pug_mixins["comment-info"](comment, roles, false);
if (showDatasetInfo) {
var field = comment.field;
var label = getLabel(field);
var query = '?';
if (field.commitId) {
query += 'commitId=' + field.commitId + '&';
}
query += 'commentPath=' + (field.path || 'null');
pug_html = pug_html + "\u003Cdiv class=\"dataset-link\"\u003E\u003Ca" + (" class=\"default-link\""+pug_attr("href", comment.repositoryPath + '/dataset/' + field.modelType + '/' + field.refId + query, true, false)) + "\u003E \u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + field.modelType.toLowerCase() + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + field.modelType, true, false)) + "\u002F\u003E";
var path = comment.dsPath
if (path && path[0] === '/') {
path = path.substring(1);
}
pug_html = pug_html + ("\u003Cspan\u003E" + (pug_escape(null == (pug_interp = path) ? "" : pug_interp)));
if (label) {
pug_html = pug_html + (" - " + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)));
}
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["comment-text",!showDatasetInfo?'comment-layer':null], [false,true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = comment.text) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
if (comment.replyCount) {
pug_html = pug_html + "\u003Cdiv class=\"replies\"\u003E\u003Ca class=\"toggle-control\" href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-chevron-down\"\u003E\u003C\u002Fspan\u003E Show " + (pug_escape(null == (pug_interp = comment.replyCount) ? "" : pug_interp)) + " replies\u003C\u002Fa\u003E\u003Cdiv class=\"toggleable\" style=\"display:none\"\u003E\u003Cimg class=\"loader\" src=\"images\u002Floader.gif\"\u002F\u003E\u003C\u002Fdiv\u003E\u003Ca class=\"toggle-control\" href=\"#\" style=\"display:none\"\u003E \u003Cspan class=\"glyphicon glyphicon-chevron-up\"\u003E\u003C\u002Fspan\u003E Hide " + (pug_escape(null == (pug_interp = comment.replyCount) ? "" : pug_interp)) + " replies\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
}
}, username, name);
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
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
      for (var pug_index2 = 0, $$l = $$obj.length; pug_index2 < $$l; pug_index2++) {
        var option = $$obj[pug_index2];
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
    for (var pug_index2 in $$obj) {
      $$l++;
      var option = $$obj[pug_index2];
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
if (!data.length) {
pug_html = pug_html + "\u003Cdiv class=\"no-content-message\"\u003ENo comments found\t\u003C\u002Fdiv\u003E";
}
else {
// iterate data
;(function(){
  var $$obj = data;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var comment = $$obj[index];
pug_mixins["comment-entry"](comment, null, true);
if (index < data.length-1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var comment = $$obj[index];
pug_mixins["comment-entry"](comment, null, true);
if (index < data.length-1) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
}
    }
  }
}).call(this);

if (resultInfo) {
pug_mixins["paging"](resultInfo);
}
}}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"canApprove" in locals_for_with?locals_for_with.canApprove:typeof canApprove!=="undefined"?canApprove:undefined,"canComment" in locals_for_with?locals_for_with.canComment:typeof canComment!=="undefined"?canComment:undefined,"canEdit" in locals_for_with?locals_for_with.canEdit:typeof canEdit!=="undefined"?canEdit:undefined,"currentUser" in locals_for_with?locals_for_with.currentUser:typeof currentUser!=="undefined"?currentUser:undefined,"data" in locals_for_with?locals_for_with.data:typeof data!=="undefined"?data:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getRoleLabel" in locals_for_with?locals_for_with.getRoleLabel:typeof getRoleLabel!=="undefined"?getRoleLabel:undefined,"isRoleAvailable" in locals_for_with?locals_for_with.isRoleAvailable:typeof isRoleAvailable!=="undefined"?isRoleAvailable:undefined,"resultInfo" in locals_for_with?locals_for_with.resultInfo:typeof resultInfo!=="undefined"?resultInfo:undefined,"values" in locals_for_with?locals_for_with.values:typeof values!=="undefined"?values:undefined));;return pug_html;} return template; });