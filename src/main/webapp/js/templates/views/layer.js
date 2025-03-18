define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (actionButtons, body, buttons, dialogType, notCloseable, title) {pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["modal",dialogType?dialogType:null], [false,true]), false, false)) + "\u003E\u003Cdiv class=\"modal-dialog\"\u003E\u003Cdiv class=\"modal-content\"\u003E\u003Cdiv" + (pug_attr("class", pug_classes(["modal-header",!title?'empty':null], [false,true]), false, false)) + "\u003E";
if ((!notCloseable)) {
pug_html = pug_html + "\u003Cbutton class=\"close\" type=\"button\" data-dismiss=\"modal\"\u003E\u003Cspan aria-hidden=\"true\"\u003E&times;\u003C\u002Fspan\u003E\u003Cspan class=\"sr-only\"\u003EClose\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003Ch4 class=\"modal-title\"\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"modal-body clearfix\"\u003E" + (null == (pug_interp = body) ? "" : pug_interp) + "\u003C\u002Fdiv\u003E";
if (buttons || actionButtons) {
pug_html = pug_html + "\u003Cdiv class=\"modal-footer\"\u003E\u003Cdiv class=\"pull-left\"\u003E";
if (actionButtons) {
// iterate actionButtons
;(function(){
  var $$obj = actionButtons;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var button = $$obj[pug_index0];
pug_html = pug_html + "\u003Cbutton" + (pug_attr("class", pug_classes(["btn","pull-left",button.className?button.className:'btn-default'], [false,false,true]), false, false)+pug_attr("id", button.id, true, false)+" tabindex=\"50\" type=\"button\"") + "\u003E" + (pug_escape(null == (pug_interp = button.text) ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var button = $$obj[pug_index0];
pug_html = pug_html + "\u003Cbutton" + (pug_attr("class", pug_classes(["btn","pull-left",button.className?button.className:'btn-default'], [false,false,true]), false, false)+pug_attr("id", button.id, true, false)+" tabindex=\"50\" type=\"button\"") + "\u003E" + (pug_escape(null == (pug_interp = button.text) ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E";
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (buttons) {
// iterate buttons
;(function(){
  var $$obj = buttons;
  if ('number' == typeof $$obj.length) {
      for (var pug_index1 = 0, $$l = $$obj.length; pug_index1 < $$l; pug_index1++) {
        var button = $$obj[pug_index1];
pug_html = pug_html + "\u003Cbutton" + (pug_attr("class", pug_classes(["btn",button.className?button.className:'btn-default'], [false,true]), false, false)+pug_attr("id", button.id, true, false)+" tabindex=\"50\" type=\"button\""+pug_attr("data-dismiss", button.callback?null:'modal', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = button.text) ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index1 in $$obj) {
      $$l++;
      var button = $$obj[pug_index1];
pug_html = pug_html + "\u003Cbutton" + (pug_attr("class", pug_classes(["btn",button.className?button.className:'btn-default'], [false,true]), false, false)+pug_attr("id", button.id, true, false)+" tabindex=\"50\" type=\"button\""+pug_attr("data-dismiss", button.callback?null:'modal', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = button.text) ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E";
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"actionButtons" in locals_for_with?locals_for_with.actionButtons:typeof actionButtons!=="undefined"?actionButtons:undefined,"body" in locals_for_with?locals_for_with.body:typeof body!=="undefined"?body:undefined,"buttons" in locals_for_with?locals_for_with.buttons:typeof buttons!=="undefined"?buttons:undefined,"dialogType" in locals_for_with?locals_for_with.dialogType:typeof dialogType!=="undefined"?dialogType:undefined,"notCloseable" in locals_for_with?locals_for_with.notCloseable:typeof notCloseable!=="undefined"?notCloseable:undefined,"title" in locals_for_with?locals_for_with.title:typeof title!=="undefined"?title:undefined));;return pug_html;} return template; });