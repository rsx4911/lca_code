define(function(require,exports,module){ function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canCreateGroups) {pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E";
if (canCreateGroups) {
pug_html = pug_html + "\u003Cdiv class=\"input-group input-group-lg\"\u003E\u003Cinput class=\"form-control\" id=\"filter\" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"\u002F\u003E\u003Cspan class=\"input-group-btn\"\u003E\u003Cbutton class=\"btn btn-success\" data-action=\"create-group\"\u003E\u003Cspan class=\"glyphicon glyphicon-plus\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003ECreate new group\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"form-group\"\u003E\u003Cinput class=\"form-control input-lg\" id=\"filter\" type=\"text\" placeholder=\"Filter by name\" aria-label=\"Filter by name\"\u002F\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\" id=\"groups\"\u003E\u003C\u002Fdiv\u003E";}.call(this,"canCreateGroups" in locals_for_with?locals_for_with.canCreateGroups:typeof canCreateGroups!=="undefined"?canCreateGroups:undefined));;return pug_html;} return template; });