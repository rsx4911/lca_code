define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (canCreateTasks, tasks) {pug_mixins["task-box"] = pug_interp = function(label, tasks){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + ("\u003Cdiv class=\"content-container\"\u003E\u003Cdiv class=\"header-box\"\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)));
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
if (tasks.length) {
var lastRepo = '';
// iterate tasks
;(function(){
  var $$obj = tasks;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var task = $$obj[pug_index0];
if (lastRepo !== task.repositoryPath) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (pug_attr("href", task.repositoryPath, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = task.repositoryLabel||task.repositoryPath) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
lastRepo = task.repositoryPath
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"task-link default-link\""+pug_attr("href", 'tasks/' + task.type.toLowerCase() + '/' + task.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = task.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var task = $$obj[pug_index0];
if (lastRepo !== task.repositoryPath) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (pug_attr("href", task.repositoryPath, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = task.repositoryLabel||task.repositoryPath) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
lastRepo = task.repositoryPath
}
pug_html = pug_html + "\u003Cdiv\u003E\u003Ca" + (" class=\"task-link default-link\""+pug_attr("href", 'tasks/' + task.type.toLowerCase() + '/' + task.id, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = task.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
else {
pug_html = pug_html + "\u003Cdiv class=\"no-content-message\"\u003ENo tasks in this category\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["task-box"].call({
block: function(){
if (canCreateTasks) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-success btn-lg pull-right\" data-route=\"tasks\u002Freview\u002Fnew\"\u003ECreate review task\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
}
}, 'To do', tasks.todo);
pug_mixins["task-box"]('In progress', tasks.inProgress);
pug_mixins["task-box"]('Completed', tasks.completed);
pug_mixins["task-box"]('Canceled', tasks.canceled);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"canCreateTasks" in locals_for_with?locals_for_with.canCreateTasks:typeof canCreateTasks!=="undefined"?canCreateTasks:undefined,"tasks" in locals_for_with?locals_for_with.tasks:typeof tasks!=="undefined"?tasks:undefined));;return pug_html;} return template; });