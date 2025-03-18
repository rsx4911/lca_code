define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, inline, team, window) {pug_mixins["static"] = pug_interp = function(id, label, value, href){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var clazz = null;
if (inline) {
clazz = 'form-inline';
}
else {
clazz = 'form-group';
}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([clazz], [true]), false, false)+pug_attr("id", id + '-group', true, false)) + "\u003E";
if (label) {
pug_html = pug_html + "\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Flabel\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"form-value\"\u003E";
if (href) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", href, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = value) ? "" : pug_interp)) + "\u003C\u002Fa\u003E";
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
}
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["input"] = pug_interp = function(id, label, type, inline, required, hide){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var hidden = type === 'hidden'
var clazz = hidden ? 'hidden ' : ''
if (inline) {
clazz += 'form-inline';
}
else {
clazz += 'form-group';
}
if (hide) {
clazz += ' hidden'
}
if (hidden || !type) {
type = 'text'
}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([clazz], [true]), false, false)+pug_attr("id", id + '-group', true, false)) + "\u003E";
if (label) {
pug_html = pug_html + ("\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp));
if (required) {
pug_html = pug_html + "\u003Csup\u003E*\u003C\u002Fsup\u003E";
}
pug_html = pug_html + "\u003C\u002Flabel\u003E";
}
if (type === 'integer') {
pug_html = pug_html + "\u003Cinput" + (pug_attrs(pug_merge([{"class": "form-control","id": pug_escape(id),"name": pug_escape(id),"type": "number","min": 0,"step": 1},attributes]), false)) + "\u002F\u003E";
}
else {
pug_html = pug_html + "\u003Cinput" + (pug_attrs(pug_merge([{"class": "form-control","id": pug_escape(id),"name": pug_escape(id),"type": pug_escape(type)},attributes]), false)) + "\u002F\u003E";
}
block && block();
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};




pug_mixins["select"] = pug_interp = function(id, label, options, defaultSelection, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([inline?'form-inline':'form-group'], [true]), false, false)+pug_attr("id", id + '-group', true, false)+pug_attr("style", pug_style(inline?'display:inline-block':null), true, false)) + "\u003E";
if (label) {
pug_html = pug_html + "\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Flabel\u003E";
}
pug_html = pug_html + "\u003Cselect" + (pug_attrs(pug_merge([{"class": "form-control","id": pug_escape(id),"name": pug_escape(id)},attributes]), false)) + "\u003E";
if (options) {
pug_mixins["options"](options, defaultSelection);
}
else {
block && block();
}
pug_html = pug_html + "\u003C\u002Fselect\u003E";
if (options) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["optgroup"] = pug_interp = function(label, options, defaultSelection, groupId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Coptgroup" + (pug_attr("label", label, false, false)) + "\u003E";
pug_mixins["options"](options, defaultSelection, groupId);
pug_html = pug_html + "\u003C\u002Foptgroup\u003E";
};
pug_mixins["options"] = pug_interp = function(options, defaultSelection, groupId){
var block = (this && this.block), attributes = (this && this.attributes) || {};
// iterate options
;(function(){
  var $$obj = options;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var option = $$obj[pug_index0];
var value = option;
var label = option;
var disabled = false;
if (Object.prototype.toString.call(option) === '[object Array]') {
value = option[0];
label = option[1];
if (option.length === 3 && option[2]) {
disabled = true;
}
}
if (defaultSelection === value) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("selected", true, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("disabled", disabled, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var option = $$obj[pug_index0];
var value = option;
var label = option;
var disabled = false;
if (Object.prototype.toString.call(option) === '[object Array]') {
value = option[0];
label = option[1];
if (option.length === 3 && option[2]) {
disabled = true;
}
}
if (defaultSelection === value) {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("selected", true, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
else {
pug_html = pug_html + "\u003Coption" + (pug_attr("value", value, true, false)+pug_attr("disabled", disabled, true, false)+pug_attr("data-group-id", groupId, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Foption\u003E";
}
    }
  }
}).call(this);

};


























































































































































pug_mixins["avatar-box"] = pug_interp = function(type, id, canWrite){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003EAvatar\u003Cimg" + (" class=\"pull-right avatar\""+pug_attr("src", 'ws/' + type + '/avatar/' + id, true, false)+" aria-label=\"Avatar\"") + "\u002F\u003E\u003C\u002Fdiv\u003E";
if (canWrite) {
pug_html = pug_html + "\u003Cdiv class=\"content-box\" id=\"avatar-form\"\u003E\u003Cdiv class=\"alert\" id=\"avatar-general-message-group\" role=\"alert\"\u003E\u003Cspan class=\"message\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cform\u003E";
pug_mixins["input"].call({
attributes: {"aria-label": "Avatar file input"}
}, 'avatar', null, 'file');
if (!window.FileReader) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" type=\"submit\" data-action=\"save-avatar\"\u003ESave\u003C\u002Fbutton\u003E";
}
else {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" type=\"submit\" data-action=\"reset-avatar\"\u003EClear\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fform\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["user-info"] = pug_interp = function(id, name, type){
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
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes([team.id?'two-columns':null], [true]), false, false)) + "\u003E\u003Cdiv class=\"team-info\"\u003E\u003Cdiv class=\"header-box\"\u003ETeam";
if (team.id) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-danger pull-right\" data-action=\"delete-team\"\u003EDelete\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cform class=\"content-box\" id=\"team-form\"\u003E\u003Cdiv class=\"alert\" id=\"team-form-general-message-group\" role=\"alert\"\u003E\u003Cspan class=\"message\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
if (!(team.id)) {
pug_mixins["input"]('teamname', 'Teamname*');
}
else {
pug_mixins["static"]('teamname', 'Teamname', team.teamname);
}
pug_mixins["input"]('name', 'Name*');
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"save-team\"\u003E";
if (team.id) {
pug_html = pug_html + "Save";
}
else {
pug_html = pug_html + "Create";
}
pug_html = pug_html + "\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fform\u003E";
if (team.id && team.users.length) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003EActive until\u003C\u002Fdiv\u003E\u003Cform class=\"content-box\" id=\"active-form\"\u003E";
pug_mixins["input"].call({
attributes: {"class": "input-sm"}
}, 'active-until', 'Active until (unlimited if empty) ', 'date', true);
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"set-active-until\"\u003ESet for all team users\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fform\u003E";
}
if (team.id) {
pug_mixins["avatar-box"]('team', team.teamname, true);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
if (team.id) {
pug_html = pug_html + "\u003Cdiv class=\"team-members\"\u003E\u003Cdiv class=\"header-box\"\u003EMembers (" + (pug_escape(null == (pug_interp = team.users.length) ? "" : pug_interp)) + ")\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"add-members\"\u003E\u003Cspan class=\"glyphicon glyphicon-plus\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EAdd members\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
// iterate team.users
;(function(){
  var $$obj = team.users;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var user = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv class=\"team-member\"\u003E\u003Ca" + (pug_attr("href", 'administration/user/profile/' + user.username, true, false)) + "\u003E";
pug_mixins["user-info"](user.username, user.name);
pug_html = pug_html + "\u003C\u002Fa\u003E\u003Cbutton" + (" class=\"btn btn-xs btn-danger pull-right\""+" data-action=\"remove-member\""+pug_attr("data-username", user.username, true, false)+pug_attr("aria-label", 'Remove member ' + user.name, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var user = $$obj[pug_index4];
pug_html = pug_html + "\u003Cdiv class=\"team-member\"\u003E\u003Ca" + (pug_attr("href", 'administration/user/profile/' + user.username, true, false)) + "\u003E";
pug_mixins["user-info"](user.username, user.name);
pug_html = pug_html + "\u003C\u002Fa\u003E\u003Cbutton" + (" class=\"btn btn-xs btn-danger pull-right\""+" data-action=\"remove-member\""+pug_attr("data-username", user.username, true, false)+pug_attr("aria-label", 'Remove member ' + user.name, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"inline" in locals_for_with?locals_for_with.inline:typeof inline!=="undefined"?inline:undefined,"team" in locals_for_with?locals_for_with.team:typeof team!=="undefined"?team:undefined,"window" in locals_for_with?locals_for_with.window:typeof window!=="undefined"?window:undefined));;return pug_html;} return template; });