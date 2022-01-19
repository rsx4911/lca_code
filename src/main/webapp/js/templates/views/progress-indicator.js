define(function(require,exports,module){ function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (message) {pug_html = pug_html + "\u003Cdiv class=\"progress-indicator\"\u003E\u003Cdiv class=\"backdrop\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"loader\"\u003E\u003Cimg src=\"images\u002Floader.gif\" aria-label=\"Loader image\"\u002F\u003E";
if (message) {
pug_html = pug_html + "\u003Cspan\u003E" + (null == (pug_interp = message) ? "" : pug_interp) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"message" in locals_for_with?locals_for_with.message:typeof message!=="undefined"?message:undefined));;return pug_html;} return template; });