define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (repositories, showSearch, title, welcomeText) {pug_html = pug_html + "\u003Cdiv class=\"home-container\"\u003E\u003Ch3\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + "\u003C\u002Fh3\u003E\u003Cp\u003E" + (null == (pug_interp = welcomeText) ? "" : pug_interp) + "\u003C\u002Fp\u003E";
if (showSearch) {
pug_html = pug_html + "\u003Cform id=\"search-form\"\u003E\u003Cdiv class=\"input-group input-group-lg\"\u003E\u003Cinput class=\"form-control\" id=\"search\" type=\"text\" placeholder=\"Search across all public repositories\" aria-label=\"Search\"\u002F\u003E\u003Cspan class=\"input-group-btn\"\u003E\u003Cbutton class=\"btn btn-lg btn-primary\" data-action=\"search\"\u003ESearch\u003C\u002Fbutton\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003C\u002Fform\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv" + (pug_attr("class", pug_classes(["pinned-repositories",repositories.length>=3?'three-columns':repositories.length===2?'two-columns':null], [false,true]), false, false)) + "\u003E";
// iterate repositories
;(function(){
  var $$obj = repositories;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var repo = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv" + (" class=\"pinned-repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repo.name, true, false)) + "\u003E\u003Cdiv class=\"header-box\"\u003E\u003Ch4\u003E" + (pug_escape(null == (pug_interp = repo.label) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Cdiv class=\"dataset-count\"\u003E\u003Cspan class=\"dataset-count-container\"\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E data sets \u003C\u002Fspan\u003E\u003Cbutton" + (" class=\"btn btn-default btn-xs\""+" data-action=\"download\""+pug_attr("title", 'Download ' + repo.label + ' as JSON-LD', true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"repo-description\"\u003E";
if (repo.settings.description) {
pug_html = pug_html + "\u003Ch4\u003E\u003Csmall\u003E" + (pug_escape(null == (pug_interp = repo.settings.description) ? "" : pug_interp)) + "\u003C\u002Fsmall\u003E\u003C\u002Fh4\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003C\u002Fdiv\u003E\u003Cbutton class=\"btn btn-lg btn-block btn-success\" data-action=\"browse\"\u003EBrowse \u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv" + (" class=\"pinned-repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repo.name, true, false)) + "\u003E\u003Cdiv class=\"header-box\"\u003E\u003Ch4\u003E" + (pug_escape(null == (pug_interp = repo.label) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Cdiv class=\"dataset-count\"\u003E\u003Cspan class=\"dataset-count-container\"\u003E\u003Cimg src=\"images\u002Floader.gif\" width=\"16\" height=\"16\"\u002F\u003E data sets \u003C\u002Fspan\u003E\u003Cbutton" + (" class=\"btn btn-default btn-xs\""+" data-action=\"download\""+pug_attr("title", 'Download ' + repo.label + ' as JSON-LD', true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"repo-description\"\u003E";
if (repo.settings.description) {
pug_html = pug_html + "\u003Ch4\u003E\u003Csmall\u003E" + (pug_escape(null == (pug_interp = repo.settings.description) ? "" : pug_interp)) + "\u003C\u002Fsmall\u003E\u003C\u002Fh4\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003C\u002Fdiv\u003E\u003Cbutton class=\"btn btn-lg btn-block btn-success\" data-action=\"browse\"\u003EBrowse \u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"repositories" in locals_for_with?locals_for_with.repositories:typeof repositories!=="undefined"?repositories:undefined,"showSearch" in locals_for_with?locals_for_with.showSearch:typeof showSearch!=="undefined"?showSearch:undefined,"title" in locals_for_with?locals_for_with.title:typeof title!=="undefined"?title:undefined,"welcomeText" in locals_for_with?locals_for_with.welcomeText:typeof welcomeText!=="undefined"?welcomeText:undefined));;return pug_html;} return template; });