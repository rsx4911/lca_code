define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canEdit, group, showGroupMembers, showRepositoryMembers) {pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E ";
if (canEdit) {
pug_html = pug_html + "\u003Cdiv class=\"input-group input-group-lg\"\u003E\u003Cinput class=\"form-control\" id=\"filter\" type=\"text\" placeholder=\"Filter by name\"\u002F\u003E\u003Cspan class=\"input-group-btn\"\u003E\u003Cbutton class=\"btn btn-success\" data-action=\"add-members\"\u003E \u003Cspan class=\"glyphicon glyphicon-plus\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EAdd members\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"form-group\"\u003E\u003Cinput class=\"form-control input-lg\" id=\"filter\" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"\u002F\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
if (showRepositoryMembers) {
pug_html = pug_html + "\u003Cdiv class=\"subheader-box\" data-type=\"repository-members\"\u003ERepository members (\u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E)\u003C\u002Fdiv\u003E\u003Cdiv class=\"subcontent-box\" id=\"repository-members\"\u003E\u003C\u002Fdiv\u003E";
}
if (showGroupMembers) {
if (showRepositoryMembers) {
pug_html = pug_html + "\u003Cbr\u002F\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"subheader-box\" data-type=\"group-members\"\u003EGroup members (\u003Cspan class=\"count\"\u003E0\u003C\u002Fspan\u003E)";
if (showRepositoryMembers && group && canEdit) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-xs btn-default pull-right\""+pug_attr("data-route", 'groups/' + group + '/members', true, false)) + "\u003E \u003Cspan class=\"glyphicon glyphicon-pencil\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EEdit group members\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"subcontent-box\" id=\"group-members\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"canEdit" in locals_for_with?locals_for_with.canEdit:typeof canEdit!=="undefined"?canEdit:undefined,"group" in locals_for_with?locals_for_with.group:typeof group!=="undefined"?group:undefined,"showGroupMembers" in locals_for_with?locals_for_with.showGroupMembers:typeof showGroupMembers!=="undefined"?showGroupMembers:undefined,"showRepositoryMembers" in locals_for_with?locals_for_with.showRepositoryMembers:typeof showRepositoryMembers!=="undefined"?showRepositoryMembers:undefined));;return pug_html;} return template; });