define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (activeTasks, debugMode, isAdmin, isDataManager, isLibraryManager, isPublic, isUserManager, reviewMode, settings, unreadMessages, websocketSupported) {pug_mixins["menu-items"] = pug_interp = function(searchIcon){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (searchIcon && settings['SEARCH_AVAILABLE']) {
pug_html = pug_html + "\u003Ca class=\"search-link-icon\" href=\"search\"\u003E\u003Cspan class=\"glyphicon glyphicon-search\"\u003E \u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
pug_html = pug_html + "\u003Ca href=\"\"\u003E\u003Cspan class=\"glyphicon glyphicon-home\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Dashboard\" aria-label=\"Dashboard\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
if (isDataManager || isUserManager || isLibraryManager) {
pug_html = pug_html + "\u003Ca href=\"administration\"\u003E\u003Cspan class=\"glyphicon glyphicon-wrench\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Admin area\" aria-label=\"Admin area\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
if (settings['TASKS_ENABLED'] === true) {
pug_html = pug_html + "\u003Ca class=\"no-deco tasks\" href=\"tasks\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Tasks\" aria-label=\"Tasks\"\u003E\u003Cspan class=\"task-icon glyphicon glyphicon-inbox\"\u003E\u003C\u002Fspan\u003E\u003Cspan" + (" class=\"task-count\""+pug_attr("data-count", activeTasks, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = activeTasks) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
if (websocketSupported && settings['MESSAGING_ENABLED'] === true) {
pug_html = pug_html + "\u003Ca class=\"no-deco messages\" href=\"messages\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Inbox\" aria-label=\"Inbox\"\u003E\u003Cspan" + (pug_attr("class", pug_classes(["message-icon","glyphicon","glyphicon-comment",unreadMessages?'new-messages':null], [false,false,false,true]), false, false)) + "\u003E\u003C\u002Fspan\u003E\u003Cspan" + (pug_attr("class", pug_classes(["message-count",unreadMessages?null:'hidden'], [false,true]), false, false)+pug_attr("data-count", unreadMessages, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = unreadMessages) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
if (settings['COMMENTS_ENABLED'] === true) {
pug_html = pug_html + "\u003Ca class=\"toggle-review\" href=\"#\"\u003E\u003Cspan" + (" class=\"glyphicon glyphicon-pencil\""+" data-toggle=\"tooltip\" data-placement=\"bottom\""+pug_attr("title", 'Review mode ' + (reviewMode?'on':'off'), true, false)+pug_attr("aria-label", 'Review mode ' + (reviewMode?'on':'off'), true, false)+pug_attr("data-active", reviewMode, true, false)) + "\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
if (isAdmin) {
pug_html = pug_html + "\u003Ca class=\"toggle-debug\" href=\"#\"\u003E\u003Cspan" + (pug_attr("class", pug_classes(["glyphicon",debugMode?'glyphicon-eye-open':'glyphicon-eye-close'], [false,true]), false, false)+" data-toggle=\"tooltip\" data-placement=\"bottom\""+pug_attr("aria-label", 'Debugging ' + (debugMode?'on':'off'), true, false)+pug_attr("title", 'Debugging ' + (debugMode?'on':'off'), true, false)) + "\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
pug_html = pug_html + "\u003Ca class=\"logout\" href=\"#\"\u003E\u003Cspan class=\"glyphicon glyphicon-log-out\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Logout\" aria-label=\"Logout\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
};
if (isPublic) {
pug_html = pug_html + "\u003Cdiv class=\"pull-right responsive-menu dropdown\"\u003E\u003Ca class=\"login\" href=\"login\"\u003E\u003Cspan class=\"glyphicon glyphicon-log-in\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"Login\" aria-label=\"Login\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"pull-right responsive-menu dropdown\"\u003E\u003Ca href=\"#\" data-toggle=\"dropdown\" aria-label=\"Settings\"\u003E\u003Cspan class=\"glyphicon glyphicon-cog\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003Cdiv class=\"dropdown-menu\"\u003E";
pug_mixins["menu-items"](true);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"default-menu\"\u003E";
pug_mixins["menu-items"](false);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}}.call(this,"activeTasks" in locals_for_with?locals_for_with.activeTasks:typeof activeTasks!=="undefined"?activeTasks:undefined,"debugMode" in locals_for_with?locals_for_with.debugMode:typeof debugMode!=="undefined"?debugMode:undefined,"isAdmin" in locals_for_with?locals_for_with.isAdmin:typeof isAdmin!=="undefined"?isAdmin:undefined,"isDataManager" in locals_for_with?locals_for_with.isDataManager:typeof isDataManager!=="undefined"?isDataManager:undefined,"isLibraryManager" in locals_for_with?locals_for_with.isLibraryManager:typeof isLibraryManager!=="undefined"?isLibraryManager:undefined,"isPublic" in locals_for_with?locals_for_with.isPublic:typeof isPublic!=="undefined"?isPublic:undefined,"isUserManager" in locals_for_with?locals_for_with.isUserManager:typeof isUserManager!=="undefined"?isUserManager:undefined,"reviewMode" in locals_for_with?locals_for_with.reviewMode:typeof reviewMode!=="undefined"?reviewMode:undefined,"settings" in locals_for_with?locals_for_with.settings:typeof settings!=="undefined"?settings:undefined,"unreadMessages" in locals_for_with?locals_for_with.unreadMessages:typeof unreadMessages!=="undefined"?unreadMessages:undefined,"websocketSupported" in locals_for_with?locals_for_with.websocketSupported:typeof websocketSupported!=="undefined"?websocketSupported:undefined));;return pug_html;} return template; });