define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (active, items, name, repository, username) {if (items && items.length) {
pug_html = pug_html + "\u003Cdiv class=\"menu-left\"\u003E\u003Cdiv class=\"menu-bar menu-active-bar\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"menu-bar menu-hover-bar\"\u003E\u003C\u002Fdiv\u003E\u003Cul\u003E";
if (repository && !repository.groupIsUserNamespace) {
pug_html = pug_html + "\u003Cli class=\"back\"\u003E\u003Ca" + (pug_attr("href", 'groups/' + repository.group, true, false)) + "\u003E\u003Cimg class=\"nav-icon mobile-only\" src=\"images\u002Fback.png\" aria-label=\"Back logo\"\u002F\u003E\u003Cspan class=\"nav-label\"\u003EBack to group\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"separate\"\u003E\u003C\u002Fli\u003E";
}
var first = true;
// iterate items
;(function(){
  var $$obj = items;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var item = $$obj[pug_index0];
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([item.id===active||(!active&&first)?'active':null], [true]), false, false)+pug_attr("data-nav-id", item.id, true, false)) + "\u003E\u003Ca" + (pug_attr("href", item.href, true, false)) + "\u003E \u003Cimg" + (" class=\"nav-icon\""+pug_attr("src", item.imageSrc, true, false)+pug_attr("aria-label", item.label + ' icon', true, false)) + "\u002F\u003E\u003Cspan class=\"nav-label\"\u003E" + (pug_escape(null == (pug_interp = item.label) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
first = false;
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var item = $$obj[pug_index0];
pug_html = pug_html + "\u003Cli" + (pug_attr("class", pug_classes([item.id===active||(!active&&first)?'active':null], [true]), false, false)+pug_attr("data-nav-id", item.id, true, false)) + "\u003E\u003Ca" + (pug_attr("href", item.href, true, false)) + "\u003E \u003Cimg" + (" class=\"nav-icon\""+pug_attr("src", item.imageSrc, true, false)+pug_attr("aria-label", item.label + ' icon', true, false)) + "\u002F\u003E\u003Cspan class=\"nav-label\"\u003E" + (pug_escape(null == (pug_interp = item.label) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fli\u003E";
first = false;
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv" + (" class=\"profile-link\""+pug_attr("title", name, true, false)) + "\u003E\u003Ca href=\"user\u002Fprofile\"\u003E\u003Cimg" + (" class=\"avatar\""+pug_attr("src", 'ws/user/avatar/' + username, true, false)+" aria-label=\"User avatar\"") + "\u002F\u003E\u003Cspan class=\"username\"\u003E" + (pug_escape(null == (pug_interp = name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";}.call(this,"active" in locals_for_with?locals_for_with.active:typeof active!=="undefined"?active:undefined,"items" in locals_for_with?locals_for_with.items:typeof items!=="undefined"?items:undefined,"name" in locals_for_with?locals_for_with.name:typeof name!=="undefined"?name:undefined,"repository" in locals_for_with?locals_for_with.repository:typeof repository!=="undefined"?repository:undefined,"username" in locals_for_with?locals_for_with.username:typeof username!=="undefined"?username:undefined));;return pug_html;} return template; });