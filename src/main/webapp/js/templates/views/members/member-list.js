define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canEdit, data, group, onlyOneOwner, type) {pug_mixins["user-info"] = pug_interp = function(id, name, type){
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
// iterate data
;(function(){
  var $$obj = data;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var membership = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv class=\"member-row\"\u003E";
var memberType = membership.user ? 'User' : 'Team';
var lcType = memberType.toLowerCase();
var id = membership.user ? membership.user.username : membership.team.teamname;
var name = membership.user ? membership.user.name : membership.team.name;
var roleDescription = type === 'group-members' ? membership.role.descriptionForGroup : membership.role.descriptionForRepository;
pug_mixins["user-info"](id, name, memberType);
pug_html = pug_html + ("\u003Cspan" + (" class=\"pull-right role\""+pug_attr("title", roleDescription, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = membership.role.name) ? "" : pug_interp)));
if (canEdit && (membership.role.id !== 'OWNER' || !onlyOneOwner) && (!membership.user || group !== membership.user.username)) {
pug_html = pug_html + " \u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" title=\"Remove member\" data-action=\"remove-member\""+pug_attr("data-id", id, true, false)+pug_attr("data-type", lcType, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E \u003Cbutton" + (" class=\"btn btn-xs btn-warning\""+" title=\"Edit role\" data-action=\"set-role\""+pug_attr("data-id", id, true, false)+pug_attr("data-type", lcType, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-edit\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var membership = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv class=\"member-row\"\u003E";
var memberType = membership.user ? 'User' : 'Team';
var lcType = memberType.toLowerCase();
var id = membership.user ? membership.user.username : membership.team.teamname;
var name = membership.user ? membership.user.name : membership.team.name;
var roleDescription = type === 'group-members' ? membership.role.descriptionForGroup : membership.role.descriptionForRepository;
pug_mixins["user-info"](id, name, memberType);
pug_html = pug_html + ("\u003Cspan" + (" class=\"pull-right role\""+pug_attr("title", roleDescription, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = membership.role.name) ? "" : pug_interp)));
if (canEdit && (membership.role.id !== 'OWNER' || !onlyOneOwner) && (!membership.user || group !== membership.user.username)) {
pug_html = pug_html + " \u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" title=\"Remove member\" data-action=\"remove-member\""+pug_attr("data-id", id, true, false)+pug_attr("data-type", lcType, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E \u003Cbutton" + (" class=\"btn btn-xs btn-warning\""+" title=\"Edit role\" data-action=\"set-role\""+pug_attr("data-id", id, true, false)+pug_attr("data-type", lcType, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-edit\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fspan\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);
}.call(this,"canEdit" in locals_for_with?locals_for_with.canEdit:typeof canEdit!=="undefined"?canEdit:undefined,"data" in locals_for_with?locals_for_with.data:typeof data!=="undefined"?data:undefined,"group" in locals_for_with?locals_for_with.group:typeof group!=="undefined"?group:undefined,"onlyOneOwner" in locals_for_with?locals_for_with.onlyOneOwner:typeof onlyOneOwner!=="undefined"?onlyOneOwner:undefined,"type" in locals_for_with?locals_for_with.type:typeof type!=="undefined"?type:undefined));;return pug_html;} return template; });