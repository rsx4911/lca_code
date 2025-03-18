define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, activeAssignments, canceledAssignments, closed, completedAssignments, currentUser, formatDate, formatDateTime, hasAssignment, inline, references, repositories, review) {pug_mixins["static"] = pug_interp = function(id, label, value, href){
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
pug_mixins["hidden"] = pug_interp = function(id, value){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cinput" + (pug_attr("id", id, true, false)+pug_attr("name", id, true, false)+pug_attr("value", value, true, false)+" type=\"hidden\"") + "\u002F\u003E";
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



































































pug_mixins["textarea"] = pug_interp = function(id, label){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (" class=\"form-group\""+pug_attr("id", id + '-group', true, false)) + "\u003E";
if (label) {
pug_html = pug_html + "\u003Clabel" + (" class=\"control-label\""+pug_attr("for", id, true, false)) + "\u003E" + (null == (pug_interp = label) ? "" : pug_interp) + "\u003C\u002Flabel\u003E";
}
pug_html = pug_html + "\u003Ctextarea" + (pug_attrs(pug_merge([{"class": "form-control","id": pug_escape(id),"name": pug_escape(id)},attributes]), false)) + "\u003E";
block && block();
pug_html = pug_html + "\u003C\u002Ftextarea\u003E\u003C\u002Fdiv\u003E";
};













































































pug_mixins["reference"] = pug_interp = function(review, reference, view){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"class": "overflow-ellipsis"},attributes]), false)) + "\u003E\u003Cinput" + (" type=\"checkbox\""+pug_attr("id", view + reference.id, true, false)+pug_attr("data-id", reference.id, true, false)+" data-action=\"mark-as-reviewed\""+pug_attr("checked", !!reference.reviewer, true, false)+pug_attr("disabled", closed, true, false)+pug_attr("aria-label", 'Mark ' + reference.name + ' as reviewed', true, false)) + "\u002F\u003E\u003Ca" + (pug_attr("href", review.repositoryPath + '/dataset/' + reference.type + '/' + reference.refId + '?commitId=' + reference.commitId, true, false)+pug_attr("title", reference.name, true, false)) + "\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + reference.type.toLowerCase() + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + reference.type, true, false)) + "\u002F\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = reference.name) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
var selectDatasetsHint = !review || !review.references || !review.references.length ? 'Please select data sets to be reviewed' : null;
pug_mixins["assignments"] = pug_interp = function(assignments, type, noContentMessage){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + ("\u003Cdiv class=\"content-container\"\u003E\u003Cdiv class=\"header-box\"\u003E" + (pug_escape(null == (pug_interp = type) ? "" : pug_interp)));
if (type === 'Active' && !closed) {
var isDisabled = !review.references || !review.references.length;
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-lg btn-success pull-right\""+" data-action=\"assign-task\""+pug_attr("disabled", !!selectDatasetsHint, true, false)+pug_attr("title", selectDatasetsHint, true, false)) + "\u003EAdd\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
if (!assignments || !assignments.length) {
pug_html = pug_html + "\u003Cdiv class=\"no-content-message\"\u003E" + (pug_escape(null == (pug_interp = noContentMessage) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
}
else {
// iterate assignments
;(function(){
  var $$obj = assignments;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var assignment = $$obj[pug_index4];
var user = assignment.assignedTo;
pug_html = pug_html + "\u003Cp\u003E ";
if (!assignment.endDate && type === 'Active' && !closed) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" title=\"Cancel assignment\""+pug_attr("data-username", user.username, true, false)+pug_attr("data-user-displayname", user.name, true, false)+" data-action=\"cancel-assignment\"") + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + " " + (pug_escape(null == (pug_interp = user.name) ? "" : pug_interp)) + " (\u003Cspan" + (pug_attr("title", formatDateTime(assignment.startDate), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatDate(assignment.startDate)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (assignment.endDate) {
pug_html = pug_html + " to \u003Cspan" + (pug_attr("title", formatDateTime(assignment.endDate), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatDate(assignment.endDate)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + ")\u003C\u002Fp\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var assignment = $$obj[pug_index4];
var user = assignment.assignedTo;
pug_html = pug_html + "\u003Cp\u003E ";
if (!assignment.endDate && type === 'Active' && !closed) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" title=\"Cancel assignment\""+pug_attr("data-username", user.username, true, false)+pug_attr("data-user-displayname", user.name, true, false)+" data-action=\"cancel-assignment\"") + "\u003E\u003Cspan class=\"glyphicon glyphicon-minus\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + " " + (pug_escape(null == (pug_interp = user.name) ? "" : pug_interp)) + " (\u003Cspan" + (pug_attr("title", formatDateTime(assignment.startDate), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatDate(assignment.startDate)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
if (assignment.endDate) {
pug_html = pug_html + " to \u003Cspan" + (pug_attr("title", formatDateTime(assignment.endDate), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = formatDate(assignment.endDate)) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
}
pug_html = pug_html + ")\u003C\u002Fp\u003E";
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E";
};
pug_mixins["review-info"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003EReview info";
if (review) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-default pull-right\""+" data-action=\"open-widget\""+pug_attr("disabled", !!selectDatasetsHint, true, false)+pug_attr("title", selectDatasetsHint, true, false)) + "\u003EOpen in task widget\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
if (!closed && (isInitiator || !review)) {
pug_html = pug_html + "\u003Cform id=\"review-form\"\u003E\u003Cdiv class=\"alert\" id=\"review-form-general-message-group\" role=\"alert\"\u003E\u003Cspan class=\"message\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
if (review) {
pug_mixins["hidden"]('id', review.id);
}
pug_mixins["hidden"]('type', 'REVIEW');
pug_mixins["input"]('name', 'Review Task Name');
if (!review) {
pug_mixins["select"]('repositoryPath', 'Repository', repositories);
}
else {
pug_mixins["static"]('repositoryPath', 'Repository', review.repositoryLabel, review.repositoryPath);
}
pug_mixins["textarea"].call({
attributes: {"rows": 5}
}, 'comment', 'Comment');
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"create-task\"\u003E" + (pug_escape(null == (pug_interp = review?'Save':'Create') ? "" : pug_interp)) + "\u003C\u002Fbutton\u003E";
if (review) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"complete-task\"\u003EComplete\u003C\u002Fbutton\u003E\u003Cbutton class=\"btn btn-lg btn-danger pull-right\" data-action=\"cancel-task\"\u003ECancel\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fform\u003E";
}
else {
var comment = review.comment || '-';
pug_mixins["static"]('name', 'Name', review.name);
pug_mixins["static"]('repositoryPath', 'Repository', review.repositoryLabel, review.repositoryPath);
pug_mixins["static"]('comment', 'Comment', comment);
if (hasAssignment && !closed) {
pug_html = pug_html + "\u003Cbutton" + (" class=\"btn btn-lg btn-success pull-right\""+" data-action=\"complete-assignment\""+pug_attr("data-username", currentUser, true, false)) + "\u003EComplete\u003C\u002Fbutton\u003E\u003Cbutton" + (" class=\"btn btn-lg btn-danger pull-right\""+" data-action=\"cancel-assignment\""+pug_attr("data-username", currentUser, true, false)) + "\u003ECancel\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E";
}
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
pug_mixins["data-set-selection"] = pug_interp = function(){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003EData sets to be reviewed";
if (isInitiator && !closed && (!review.assignments || !review.assignments.length)) {
pug_html = pug_html + "\u003Cbutton class=\"btn btn-lg btn-success pull-right\" data-action=\"select-references\"\u003ESelect\u003C\u002Fbutton\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box dataset-references\"\u003E";
if (references && references.length) {
// iterate references			
;(function(){
  var $$obj = references			;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var list = $$obj[pug_index5];
pug_html = pug_html + "\u003Cdiv class=\"three-columns no-margin-bottom\"\u003E";
// iterate list
;(function(){
  var $$obj = list;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var reference = $$obj[pug_index6];
pug_mixins["reference"](review, reference, 'manage-review');
      }
  } else {
    var $$l = 0;
    for (var pug_index6 in $$obj) {
      $$l++;
      var reference = $$obj[pug_index6];
pug_mixins["reference"](review, reference, 'manage-review');
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var list = $$obj[pug_index5];
pug_html = pug_html + "\u003Cdiv class=\"three-columns no-margin-bottom\"\u003E";
// iterate list
;(function(){
  var $$obj = list;
  if ('number' == typeof $$obj.length) {
      for (var pug_index7 = 0, $$l = $$obj.length; pug_index7 < $$l; pug_index7++) {
        var reference = $$obj[pug_index7];
pug_mixins["reference"](review, reference, 'manage-review');
      }
  } else {
    var $$l = 0;
    for (var pug_index7 in $$obj) {
      $$l++;
      var reference = $$obj[pug_index7];
pug_mixins["reference"](review, reference, 'manage-review');
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
    }
  }
}).call(this);

}
else {
pug_html = pug_html + "There are not data sets selected yet for this review.";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
};
var isInitiator = review && review.initiator.username === currentUser;
pug_mixins["review-info"]();
if (review) {
pug_mixins["data-set-selection"]();
}
if (isInitiator) {
pug_html = pug_html + "\u003Cdiv class=\"three-columns\"\u003E";
pug_mixins["assignments"](activeAssignments, 'Active', 'This task is currently not assigned to any user.');
pug_mixins["assignments"](completedAssignments, 'Completed', 'This task has no completed assignments yet.');
pug_mixins["assignments"](canceledAssignments, 'Canceled', 'This task has no canceled assignments.');
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"activeAssignments" in locals_for_with?locals_for_with.activeAssignments:typeof activeAssignments!=="undefined"?activeAssignments:undefined,"canceledAssignments" in locals_for_with?locals_for_with.canceledAssignments:typeof canceledAssignments!=="undefined"?canceledAssignments:undefined,"closed" in locals_for_with?locals_for_with.closed:typeof closed!=="undefined"?closed:undefined,"completedAssignments" in locals_for_with?locals_for_with.completedAssignments:typeof completedAssignments!=="undefined"?completedAssignments:undefined,"currentUser" in locals_for_with?locals_for_with.currentUser:typeof currentUser!=="undefined"?currentUser:undefined,"formatDate" in locals_for_with?locals_for_with.formatDate:typeof formatDate!=="undefined"?formatDate:undefined,"formatDateTime" in locals_for_with?locals_for_with.formatDateTime:typeof formatDateTime!=="undefined"?formatDateTime:undefined,"hasAssignment" in locals_for_with?locals_for_with.hasAssignment:typeof hasAssignment!=="undefined"?hasAssignment:undefined,"inline" in locals_for_with?locals_for_with.inline:typeof inline!=="undefined"?inline:undefined,"references" in locals_for_with?locals_for_with.references:typeof references!=="undefined"?references:undefined,"repositories" in locals_for_with?locals_for_with.repositories:typeof repositories!=="undefined"?repositories:undefined,"review" in locals_for_with?locals_for_with.review:typeof review!=="undefined"?review:undefined));;return pug_html;} return template; });