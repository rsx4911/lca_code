define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (announcementMessage, groups, homeText, homeTitle, isAdmin, isDataManager, isHomepageEnabled, isSearchEnabled, isUserManager, licenseAgreementText, maintenanceMessage, maintenanceModeActive, modelTypesOrder, repositories, repositoriesOrder, teams, users) {pug_mixins["box"] = pug_interp = function(label, plural, count){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var lcLabel = label.toLowerCase()	
var lcPlural = plural.toLowerCase()	
pug_html = pug_html + ("\u003Cdiv class=\"header-box\"\u003E\u003Ch4\u003E" + (pug_escape(null == (pug_interp = plural) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E\u003Cdiv class=\"count\"\u003E" + (pug_escape(null == (pug_interp = count) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E\u003Chr\u002F\u003E\u003Cbutton" + (" class=\"btn btn-lg btn-block btn-success\""+pug_attr("data-action", 'create-' + lcLabel, true, false)) + "\u003ECreate " + (pug_escape(null == (pug_interp = lcLabel) ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"list-box\"\u003E\u003Clegend\u003E" + (pug_escape(null == (pug_interp = plural) ? "" : pug_interp)));
if (label === 'Repository' && isSearchEnabled) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-xs btn-default pull-right\" data-action=\"reindex-repositories\"\u003EReindex\u003C\u002Fbutton\u003E\u003Cbutton class=\"btn btn-xs btn-default pull-right\" data-action=\"clear-index\"\u003EClear index\u003C\u002Fbutton\u003E\u003Cdiv id=\"indexing-status\"\u003E\u003C\u002Fdiv\u003E";
}
else
if (label === 'User') {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-xs btn-default pull-right\" data-action=\"copy-users-to-clipboard\"\u003ECopy to clipboard\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003Cinput" + (" class=\"form-control input-sm filter pull-right\""+pug_attr("id", lcLabel + '-filter', true, false)+" type=\"text\" placeholder=\"filter\""+pug_attr("aria-label", 'Filter + ' + label, true, false)) + "\u002F\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Flegend\u003E\u003Cdiv" + (pug_attr("id", lcPlural, true, false)) + "\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["editable-text"] = pug_interp = function(action, label, value, defaultValue){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + ": \u003C\u002Fstrong\u003E";
if (value) {
pug_html = pug_html + (pug_escape(null == (pug_interp = value) ? "" : pug_interp));
}
else {
pug_html = pug_html + "\u003Ci\u003E" + (pug_escape(null == (pug_interp = defaultValue) ? "" : pug_interp)) + "\u003C\u002Fi\u003E";
}
pug_html = pug_html + " \u003Ca" + (" href=\"#\""+pug_attr("data-action", action, true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-edit\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["ordered-list"] = pug_interp = function(title, elements){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"ordered-list\"\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + ": \u003C\u002Fstrong\u003E\u003Col\u003E";
// iterate elements
;(function(){
  var $$obj = elements;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var element = $$obj[pug_index0];
pug_html = pug_html + "\u003Cli" + (pug_attr("data-id", element.id, true, false)+pug_attr("data-hidden", element.hidden, true, false)) + "\u003E\u003Cspan class=\"list-label\"\u003E" + (pug_escape(null == (pug_interp = element.label) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Cspan class=\"glyphicon glyphicon-upload\"\u003E\u003C\u002Fspan\u003E \u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E \u003Cspan" + (pug_attr("class", pug_classes(["glyphicon",'glyphicon-eye-' + (element.hidden ? 'close' : 'open')], [false,true]), false, false)+" data-action=\"change-visibility\"") + "\u003E\u003C\u002Fspan\u003E\u003C\u002Fli\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var element = $$obj[pug_index0];
pug_html = pug_html + "\u003Cli" + (pug_attr("data-id", element.id, true, false)+pug_attr("data-hidden", element.hidden, true, false)) + "\u003E\u003Cspan class=\"list-label\"\u003E" + (pug_escape(null == (pug_interp = element.label) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E \u003Cspan class=\"glyphicon glyphicon-upload\"\u003E\u003C\u002Fspan\u003E \u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E \u003Cspan" + (pug_attr("class", pug_classes(["glyphicon",'glyphicon-eye-' + (element.hidden ? 'close' : 'open')], [false,true]), false, false)+" data-action=\"change-visibility\"") + "\u003E\u003C\u002Fspan\u003E\u003C\u002Fli\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fol\u003E\u003C\u002Fdiv\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"content-box overview\"\u003E\u003Cdiv class=\"two-columns\"\u003E";
if (isDataManager) {
pug_html = pug_html + "\u003Cdiv\u003E";
pug_mixins["box"]('Repository', 'Repositories', repositories);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (isUserManager) {
pug_html = pug_html + "\u003Cdiv\u003E";
pug_mixins["box"]('User', 'Users', users);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (isDataManager) {
pug_html = pug_html + "\u003Cdiv\u003E";
pug_mixins["box"]('Group', 'Groups', groups);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
if (isUserManager) {
pug_html = pug_html + "\u003Cdiv\u003E";
pug_mixins["box"]('Team', 'Teams', teams);
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
if (isAdmin) {
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003EServer info\u003Cbutton" + (pug_attr("class", pug_classes(["btn","btn-lg","pull-right",maintenanceModeActive?'btn-success':'btn-warning'], [false,false,false,true]), false, false)+" data-action=\"toggle-maintenance-mode\"") + "\u003E";
if (maintenanceModeActive) {
pug_html = pug_html + "Deactivate maintenance mode";
}
else {
pug_html = pug_html + "Activate maintenance mode";
}
pug_html = pug_html + "\u003C\u002Fbutton\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv\u003E \u003Cstrong\u003ERelease version: \u003C\u002Fstrong\u003E2.2.0\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003ECommit id: \u003C\u002Fstrong\u003Ee15bfc59\u003C\u002Fdiv\u003E\u003Cdiv\u003E\u003Cstrong\u003EBuild date: \u003C\u002Fstrong\u003E9/17/2024, 1:12:44 PM\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EBrowse configuration\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cdiv class=\"col-md-6\"\u003E\u003Cdiv class=\"model-types-order\"\u003E";
pug_mixins["ordered-list"]('Model type order', modelTypesOrder);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"col-md-6\"\u003E\u003Cdiv class=\"repositories-order\"\u003E";
pug_mixins["ordered-list"]('Public repository order', repositoriesOrder);
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EMessages & Texts\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
pug_mixins["editable-text"].call({
block: function(){
pug_html = pug_html + " ";
}
}, 'set-maintenance-message', 'Maintenance message', maintenanceMessage, 'Not set');
pug_mixins["editable-text"].call({
block: function(){
pug_html = pug_html + " ";
}
}, 'set-announcement', 'Announcement text', announcementMessage, 'No announcement set');
pug_mixins["editable-text"].call({
block: function(){
pug_html = pug_html + " ";
}
}, 'set-license-agreement-text', 'License agreement text', licenseAgreementText, 'No license agreement set');
if (isHomepageEnabled) {
pug_html = pug_html + "\u003Chr\u002F\u003E";
pug_mixins["editable-text"].call({
block: function(){
pug_html = pug_html + " ";
}
}, 'set-home-title', 'Home title', homeTitle, 'Not set');
pug_mixins["editable-text"].call({
block: function(){
pug_html = pug_html + " ";
}
}, 'set-home-text', 'Home welcome text', homeText, 'Not set');
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}}.call(this,"announcementMessage" in locals_for_with?locals_for_with.announcementMessage:typeof announcementMessage!=="undefined"?announcementMessage:undefined,"groups" in locals_for_with?locals_for_with.groups:typeof groups!=="undefined"?groups:undefined,"homeText" in locals_for_with?locals_for_with.homeText:typeof homeText!=="undefined"?homeText:undefined,"homeTitle" in locals_for_with?locals_for_with.homeTitle:typeof homeTitle!=="undefined"?homeTitle:undefined,"isAdmin" in locals_for_with?locals_for_with.isAdmin:typeof isAdmin!=="undefined"?isAdmin:undefined,"isDataManager" in locals_for_with?locals_for_with.isDataManager:typeof isDataManager!=="undefined"?isDataManager:undefined,"isHomepageEnabled" in locals_for_with?locals_for_with.isHomepageEnabled:typeof isHomepageEnabled!=="undefined"?isHomepageEnabled:undefined,"isSearchEnabled" in locals_for_with?locals_for_with.isSearchEnabled:typeof isSearchEnabled!=="undefined"?isSearchEnabled:undefined,"isUserManager" in locals_for_with?locals_for_with.isUserManager:typeof isUserManager!=="undefined"?isUserManager:undefined,"licenseAgreementText" in locals_for_with?locals_for_with.licenseAgreementText:typeof licenseAgreementText!=="undefined"?licenseAgreementText:undefined,"maintenanceMessage" in locals_for_with?locals_for_with.maintenanceMessage:typeof maintenanceMessage!=="undefined"?maintenanceMessage:undefined,"maintenanceModeActive" in locals_for_with?locals_for_with.maintenanceModeActive:typeof maintenanceModeActive!=="undefined"?maintenanceModeActive:undefined,"modelTypesOrder" in locals_for_with?locals_for_with.modelTypesOrder:typeof modelTypesOrder!=="undefined"?modelTypesOrder:undefined,"repositories" in locals_for_with?locals_for_with.repositories:typeof repositories!=="undefined"?repositories:undefined,"repositoriesOrder" in locals_for_with?locals_for_with.repositoriesOrder:typeof repositoriesOrder!=="undefined"?repositoriesOrder:undefined,"teams" in locals_for_with?locals_for_with.teams:typeof teams!=="undefined"?teams:undefined,"users" in locals_for_with?locals_for_with.users:typeof users!=="undefined"?users:undefined));;return pug_html;} return template; });