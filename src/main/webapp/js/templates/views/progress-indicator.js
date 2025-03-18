define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Array, message) {pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["progress-indicator",!message?'transparent':null], [false,true]), false, false)) + "\u003E\u003Cdiv class=\"backdrop\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"loader\"\u003E\u003Cimg src=\"images\u002Floader.gif\" aria-label=\"Loader image\"\u002F\u003E";
if (Array.isArray(message)) {
pug_html = pug_html + "\u003Cspan\u003E";
// iterate message
;(function(){
  var $$obj = message;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var m = $$obj[pug_index0];
pug_html = pug_html + (pug_escape(null == (pug_interp = m) ? "" : pug_interp)) + "\u003Cbr\u002F\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var m = $$obj[pug_index0];
pug_html = pug_html + (pug_escape(null == (pug_interp = m) ? "" : pug_interp)) + "\u003Cbr\u002F\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E";
}
else {
pug_html = pug_html + "\u003Cspan\u003E" + (pug_escape(null == (pug_interp = message) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"Array" in locals_for_with?locals_for_with.Array:typeof Array!=="undefined"?Array:undefined,"message" in locals_for_with?locals_for_with.message:typeof message!=="undefined"?message:undefined));;return pug_html;} return template; });