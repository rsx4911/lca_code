define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (libraries) {pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E\u003Cdiv class=\"btn btn-success pull-right\" data-action=\"add\"\u003EAdd library\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
// iterate libraries
;(function(){
  var $$obj = libraries;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var library = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv class=\"entry\"\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + " (" + (pug_escape(null == (pug_interp = library.count) ? "" : pug_interp)) + " data set ids)\u003Cdiv" + (" class=\"btn btn-default btn-xs pull-right\""+" data-action=\"show\""+pug_attr("data-library", library.name, true, false)) + "\u003EShow\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"btn btn-warning btn-xs pull-right\""+" data-action=\"replace\""+pug_attr("data-library", library.name, true, false)) + "\u003EReplace\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"btn btn-danger btn-xs pull-right\""+" data-action=\"delete\""+pug_attr("data-library", library.name, true, false)) + "\u003EDelete\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var library = $$obj[pug_index0];
pug_html = pug_html + "\u003Cdiv class=\"entry\"\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + " (" + (pug_escape(null == (pug_interp = library.count) ? "" : pug_interp)) + " data set ids)\u003Cdiv" + (" class=\"btn btn-default btn-xs pull-right\""+" data-action=\"show\""+pug_attr("data-library", library.name, true, false)) + "\u003EShow\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"btn btn-warning btn-xs pull-right\""+" data-action=\"replace\""+pug_attr("data-library", library.name, true, false)) + "\u003EReplace\u003C\u002Fdiv\u003E\u003Cdiv" + (" class=\"btn btn-danger btn-xs pull-right\""+" data-action=\"delete\""+pug_attr("data-library", library.name, true, false)) + "\u003EDelete\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"libraries" in locals_for_with?locals_for_with.libraries:typeof libraries!=="undefined"?libraries:undefined));;return pug_html;} return template; });