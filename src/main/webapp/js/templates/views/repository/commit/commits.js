define(function(require,exports,module){ function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canCreateChangeLog, standalone) {if (!standalone) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (canCreateChangeLog) {
pug_html = pug_html + "\u003Cdiv class=\"input-group input-group-lg\"\u003E\u003Cinput class=\"form-control input-lg\" id=\"filter\" type=\"text\" placeholder=\"Filter by message\" aria-label=\"Filter by message\"\u002F\u003E\u003Cdiv class=\"input-group-btn\"\u003E\u003Cbutton class=\"btn btn-success\" data-action=\"download-changelog\"\u003EDownload changelog\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"form-group\"\u003E\u003Cinput class=\"form-control input-lg\" id=\"filter\" type=\"text\" placeholder=\"Filter by message\" aria-label=\"Filter by message\"\u002F\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"content-box\"\u003E\u003C\u002Fdiv\u003E";}.call(this,"canCreateChangeLog" in locals_for_with?locals_for_with.canCreateChangeLog:typeof canCreateChangeLog!=="undefined"?canCreateChangeLog:undefined,"standalone" in locals_for_with?locals_for_with.standalone:typeof standalone!=="undefined"?standalone:undefined));;return pug_html;} return template; });