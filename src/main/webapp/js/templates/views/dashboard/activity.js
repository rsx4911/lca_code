define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (assignedTo, field, formatDate, formatTime, id, label, message, reply, repositoryPath, showRepositoryPath, taskType, timestamp, type, user, userDisplayName) {pug_mixins["commit-info-entry"] = pug_interp = function(value, label, clazz, first){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (value) {
if (!first) {
pug_html = pug_html + ", ";
}
pug_html = pug_html + ("\u003Cspan" + (pug_attr("class", pug_classes(["commit-info",clazz], [false,true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E " + (pug_escape(null == (pug_interp = value === 1 ? label : label + 's') ? "" : pug_interp)));
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
pug_mixins["type-text"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cspan" + (pug_attr("data-activity", type, true, false)) + "\u003E ";
if (type === 'COMMIT') {
pug_html = pug_html + "committed data";
}
else
if (type === 'COMMENT') {
if (reply) {
pug_html = pug_html + "replied";
}
else {
pug_html = pug_html + "commented";
}
}
else
if (type === 'TASK_STARTED') {
pug_html = pug_html + "started a task";
}
else
if (type === 'TASK_ASSIGNED') {
pug_html = pug_html + ("assigned a task to " + (pug_escape(null == (pug_interp = assignedTo) ? "" : pug_interp)));
}
else
if (type === 'TASK_COMPLETED') {
pug_html = pug_html + "completed a task";
}
else
if (type === 'TASK_CANCELED') {
pug_html = pug_html + "canceled a task";
}
else
if (type === 'TASK_ASSIGNMENT_COMPLETED') {
pug_html = pug_html + "completed a task assignment";
if (assignedTo) {
pug_html = pug_html + (" for " + (pug_escape(null == (pug_interp = assignedTo) ? "" : pug_interp)));
}
}
else
if (type === 'TASK_ASSIGNMENT_CANCELED') {
pug_html = pug_html + "canceled a task assignment";
if (assignedTo) {
pug_html = pug_html + (" for " + (pug_escape(null == (pug_interp = assignedTo) ? "" : pug_interp)));
}
}
pug_html = pug_html + "\u003C\u002Fspan\u003E";
};
pug_mixins["link"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var href = '';
var text = '';
if (type === 'COMMIT') {
href = repositoryPath + '/commit/' + id;
text = 'Go to commit';
}
else
if (type === 'COMMENT') {
href = repositoryPath + '/dataset/' + field.modelType + '/' + field.refId + '?commitId=' + field.commitId + '&commentPath=' + (field.path || 'null') ;
text = 'Go to comment';
}
else
if (type.indexOf('TASK_') === 0) {
href = 'tasks/' + taskType.toLowerCase() + '/' + id;
text = 'Go to task';
}
pug_html = pug_html + "\u003Ca" + (" class=\"pull-right default-link\""+pug_attr("href", href, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = text) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"list-entry activity\"\u003E";
pug_mixins["user-info"].call({
block: function(){
pug_mixins["type-text"]();
if (showRepositoryPath) {
pug_html = pug_html + " on \u003Ca" + (" class=\"default-link\""+pug_attr("href", repositoryPath, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
if (timestamp) {
pug_html = pug_html + "\u003Cspan\u003E on " + (pug_escape(null == (pug_interp = formatDate(timestamp)) ? "" : pug_interp)) + " at " + (pug_escape(null == (pug_interp = formatTime(timestamp)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003Cbr\u002F\u003E";
if (type === 'COMMIT') {
pug_html = pug_html + "\u003Cdiv" + (" class=\"commit-info-container\""+pug_attr("data-commit-id", id, true, false)) + "\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cspan class=\"message\"\u003E " + (pug_escape(null == (pug_interp = message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
}, user, userDisplayName);
pug_mixins["link"]();
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"assignedTo" in locals_for_with?locals_for_with.assignedTo:typeof assignedTo!=="undefined"?assignedTo:undefined,"field" in locals_for_with?locals_for_with.field:typeof field!=="undefined"?field:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"formatTime" in locals_for_with?locals_for_with.formatTime:typeof formatTime!=="undefined"?formatTime:undefined,"id" in locals_for_with?locals_for_with.id:typeof id!=="undefined"?id:undefined,"label" in locals_for_with?locals_for_with.label:typeof label!=="undefined"?label:undefined,"message" in locals_for_with?locals_for_with.message:typeof message!=="undefined"?message:undefined,"reply" in locals_for_with?locals_for_with.reply:typeof reply!=="undefined"?reply:undefined,"repositoryPath" in locals_for_with?locals_for_with.repositoryPath:typeof repositoryPath!=="undefined"?repositoryPath:undefined,"showRepositoryPath" in locals_for_with?locals_for_with.showRepositoryPath:typeof showRepositoryPath!=="undefined"?showRepositoryPath:undefined,"taskType" in locals_for_with?locals_for_with.taskType:typeof taskType!=="undefined"?taskType:undefined,"timestamp" in locals_for_with?locals_for_with.timestamp:typeof timestamp!=="undefined"?timestamp:undefined,"type" in locals_for_with?locals_for_with.type:typeof type!=="undefined"?type:undefined,"user" in locals_for_with?locals_for_with.user:typeof user!=="undefined"?user:undefined,"userDisplayName" in locals_for_with?locals_for_with.userDisplayName:typeof userDisplayName!=="undefined"?userDisplayName:undefined));;return pug_html;} return template; });