define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canApprove, canComment, canEdit, comment, currentUser, formatDate, getLabel, getRoleLabel, isRoleAvailable, roles) {pug_mixins["user-info"] = pug_interp = function(id, name, type){
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
pug_mixins["comment-entry"](comment, roles, !roles);}.call(this,"canApprove" in locals_for_with?locals_for_with.canApprove:typeof canApprove!=="undefined"?canApprove:undefined,"canComment" in locals_for_with?locals_for_with.canComment:typeof canComment!=="undefined"?canComment:undefined,"canEdit" in locals_for_with?locals_for_with.canEdit:typeof canEdit!=="undefined"?canEdit:undefined,"comment" in locals_for_with?locals_for_with.comment:typeof comment!=="undefined"?comment:undefined,"currentUser" in locals_for_with?locals_for_with.currentUser:typeof currentUser!=="undefined"?currentUser:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getRoleLabel" in locals_for_with?locals_for_with.getRoleLabel:typeof getRoleLabel!=="undefined"?getRoleLabel:undefined,"isRoleAvailable" in locals_for_with?locals_for_with.isRoleAvailable:typeof isRoleAvailable!=="undefined"?isRoleAvailable:undefined,"roles" in locals_for_with?locals_for_with.roles:typeof roles!=="undefined"?roles:undefined));;return pug_html;} return template; });