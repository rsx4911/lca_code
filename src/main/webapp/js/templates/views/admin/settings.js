define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object) {






















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
pug_mixins["checkbox"] = pug_interp = function(id, label, type, inline){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (!type) {
type = 'success'
}
var clazz = 'abc-checkbox-' + type
if (inline) {
clazz += ' checkbox-inline'
}
pug_html = pug_html + "\u003Cdiv" + (pug_attr("class", pug_classes(["abc-checkbox",clazz], [false,true]), false, false)) + "\u003E\u003Cinput" + (pug_attrs(pug_merge([{"id": pug_escape(id),"name": pug_escape(id),"type": "checkbox"},attributes]), false)) + "\u002F\u003E\u003Clabel" + (pug_attr("for", id, true, false)) + "\u003E";
if (label) {
pug_html = pug_html + (null == (pug_interp = label) ? "" : pug_interp);
}
else {
block && block();
}
pug_html = pug_html + "\u003C\u002Flabel\u003E";
if (label) {
block && block();
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};




































































































































pug_html = pug_html + "\u003Cdiv id=\"settings-form\"\u003E\u003Cdiv class=\"header-box\"\u003EBasic settings\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('SERVER_SETTING__SERVER_NAME', 'Server name');
pug_mixins["input"]('SERVER_SETTING__SERVER_URL', 'Base url (e.g. used in email links)');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('SERVER_SETTING__REPOSITORY_PATH', 'Repositories root directory');
pug_mixins["input"]('SERVER_SETTING__LIBRARY_PATH', 'Libraries directory');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EEnabled features\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"alert alert-warning\" id=\"search-note\"\u003EYou need to reindex all repositories, for the search\u002Fusage to show correct results.\u003C\u002Fdiv\u003E";
pug_mixins["checkbox"]('SERVER_SETTING__SEARCH_ENABLED', 'Search</strong>: Users can search for data sets within repositories they have access to');
pug_html = pug_html + "\u003Cdiv class=\"indented-setting\"\u003E";
pug_mixins["checkbox"]('SERVER_SETTING__SEARCH_LINKS_ENABLED', 'Flow usage</strong>: Enables analysis and display of flow usage');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
pug_mixins["checkbox"]('SERVER_SETTING__COMMENTS_ENABLED', 'Comments: Reviewers are enabled to make comments on data set fields');
pug_mixins["checkbox"]('SERVER_SETTING__TASKS_ENABLED', 'Tasks: Editors can create review tasks');
pug_mixins["checkbox"]('SERVER_SETTING__MESSAGING_ENABLED', 'Messaging: Users can write messages to each other');
pug_mixins["checkbox"]('SERVER_SETTING__RELEASES_ENABLED', 'Releases: Commits of a repository can be released to the public');
pug_mixins["checkbox"]('SERVER_SETTING__HOMEPAGE_ENABLED', 'Homepage</strong>: Show a landing page instead of the default search page or login for anonymous users');
pug_mixins["checkbox"]('SERVER_SETTING__NOTIFICATIONS_ENABLED', 'Notifications</strong>: Users can receive notifications');
pug_mixins["checkbox"]('SERVER_SETTING__USER_REGISTRATION_ENABLED', 'User registration</strong>: Users can create accounts themselves');
pug_mixins["checkbox"]('SERVER_SETTING__USER_REGISTRATION_APPROVAL_ENABLED', 'User registration approval</strong>: Self signed-up users will be inactive until an admin activates them');
pug_mixins["checkbox"]('SERVER_SETTING__CHANGE_LOG_ENABLED', 'Change log</strong>: Users can create change logs from commit histories (local nodejs server for server side rendering must be installed)');
pug_mixins["checkbox"]('SERVER_SETTING__DASHBOARD_ACTIVITIES_ENABLED', 'Dashboard activities</strong>: Shows latest activities on the dashboard');
pug_mixins["checkbox"]('SERVER_SETTING__REPOSITORY_ACTIVITIES_ENABLED', 'Repository activities</strong>: Shows latest activities in each repository');
pug_mixins["checkbox"]('SERVER_SETTING__REPOSITORY_TAGS_ENABLED', 'Repository tags: Enables owners of a repository to tag the repository which add additional filtering in the search');
pug_mixins["checkbox"]('SERVER_SETTING__DATASET_TAGS_ENABLED', 'Data set tags: Enables a search filter for tags on data sets');
pug_html = pug_html + "\u003Cdiv class=\"indented-setting\"\u003E";
pug_mixins["checkbox"]('SERVER_SETTING__DATASET_TAGS_ON_DASHBOARD_ENABLED', 'Data set tags (Dashboard): Adds a data set tag cloud menu entry to the dashboard');
pug_mixins["checkbox"]('SERVER_SETTING__DATASET_TAGS_ON_GROUPS_ENABLED', 'Data set tags (Group): Adds a data set tag cloud menu entry to each groups menu');
pug_mixins["checkbox"]('SERVER_SETTING__DATASET_TAGS_ON_REPOSITORIES_ENABLED', 'Data set tags (Repository): Adds a data set tag cloud menu entry to each repositories menu');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box search-settings\"\u003EOpensearch settings\u003Cbutton class=\"btn btn-primary pull-right\" data-action=\"test-search\"\u003ERun test search\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box search-settings\"\u003E\u003Cdiv class=\"three-columns\"\u003E";
pug_mixins["input"]('SEARCH_SETTING__SCHEMA', 'Schema');
pug_mixins["input"]('SEARCH_SETTING__HOST', 'Url');
pug_mixins["input"]('SEARCH_SETTING__PORT', 'Port');
pug_mixins["input"]('SEARCH_INDEX__PRIVATE', 'Search index');
pug_mixins["input"]('SEARCH_INDEX__PUBLIC', 'Public search index');
pug_mixins["input"]('SEARCH_INDEX__IO_DATA', 'Flow  index');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EMail configuration \u003Cbutton class=\"btn btn-primary pull-right\" data-action=\"test-mail\"\u003ESend test mail\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('MAIL_SETTING__HOST', 'Host');
pug_html = pug_html + "\u003Cdiv class=\"three-columns\"\u003E";
pug_mixins["select"]('MAIL_SETTING__PROTO', 'Protocol', ['smtp', 'smtps']);
pug_mixins["input"]('MAIL_SETTING__PORT', 'Port', 'integer');
pug_html = pug_html + "\u003Cdiv\u003E";
pug_mixins["checkbox"]('MAIL_SETTING__SSL', 'SSL');
pug_mixins["checkbox"]('MAIL_SETTING__TLS', 'TLS');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"].call({
attributes: {"autocomplete": "new-password"}
}, 'MAIL_SETTING__USER', 'Username');
pug_mixins["input"].call({
attributes: {"autocomplete": "new-password"}
}, 'MAIL_SETTING__PASS', 'Password', 'password');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('MAIL_SETTING__DEFAULT_FROM', 'Default from');
pug_mixins["input"]('MAIL_SETTING__DEFAULT_REPLY_TO', 'Default reply to');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EImprint\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__COMPANY', 'Company');
pug_mixins["input"]('IMPRINT_SETTING__CEO', 'Ceo');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__STREET', 'Street');
pug_mixins["input"]('IMPRINT_SETTING__ZIP_CODE', 'Zip code');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__CITY', 'City');
pug_mixins["input"]('IMPRINT_SETTING__COUNTRY', 'Country');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__PHONE', 'Phone');
pug_mixins["input"]('IMPRINT_SETTING__FAX', 'Fax');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__EMAIL', 'Email');
pug_mixins["input"]('IMPRINT_SETTING__WEBSITE', 'Website');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"two-columns\"\u003E";
pug_mixins["input"]('IMPRINT_SETTING__REGISTRATION', 'Registration');
pug_mixins["input"]('IMPRINT_SETTING__VAT', 'Vat');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EGLAD service settings\u003Cbutton class=\"btn btn-primary pull-right\" data-action=\"test-glad\"\u003ECheck GLAD url\u003C\u002Fbutton\u003E\u003Cbutton class=\"btn btn-danger pull-right\" data-action=\"clear-glad\"\u003EDelete all data from GLAD\u003C\u002Fbutton\u003E\u003Cbutton class=\"btn btn-default pull-right\" data-action=\"list-glad\"\u003EList repositories on GLAD\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"three-columns\"\u003E";
pug_mixins["input"]('SERVER_SETTING__GLAD_URL', 'Service url');
pug_mixins["input"]('SERVER_SETTING__GLAD_API_KEY', 'Api-key');
pug_mixins["input"]('SERVER_SETTING__GLAD_DATAPROVIDER', 'Dataprovider name');
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined));;return pug_html;} return template; });