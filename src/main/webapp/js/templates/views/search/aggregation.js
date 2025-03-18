define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, aggregation, getAggregationUrl, getLabel, getSubCount, isSelected, isSelectedAggregationValue, label, multiSelect, showAll, totalCount) {



























































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




































































































































pug_mixins["aggregation-entry"] = pug_interp = function(aggregationName, entry, singleton, depth, parentLabel, previousCount){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cli\u003E";
var label = getLabel(aggregationName, entry.key, entry.label);
if (parentLabel && label.indexOf(parentLabel + '/') === 0) {
label = label.substring(parentLabel.length + 1);
}
var selected = isSelectedAggregationValue(aggregationName, entry.key)
if (!singleton && !selected) {
var id = aggregationName + '-' + entry.key;
if (multiSelect) {
var boxLabel = label + '(' + entry.count + ')';
pug_mixins["checkbox"].call({
attributes: {"data-value": pug_escape(entry.key),"data-aggregation": pug_escape(aggregationName)}
}, id, boxLabel, 'primary', true);
}
else {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", getAggregationUrl(aggregationName, entry.key), true, false)) + "\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + " (" + (pug_escape(null == (pug_interp = entry.count) ? "" : pug_interp)) + ")\u003C\u002Fa\u003E";
}
}
else {
pug_html = pug_html + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + " (" + (pug_escape(null == (pug_interp = entry.count) ? "" : pug_interp)) + ") ";
if (selected) {
pug_html = pug_html + "\u003Ca" + (pug_attr("href", getAggregationUrl(aggregationName, entry.key, true), true, false)) + "\u003E\u003Cspan class=\"glyphicon glyphicon-remove\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
}
}
previousCount += 1;
if (entry.subEntries) {
pug_html = pug_html + "\u003Cul\u003E";
// iterate entry.subEntries
;(function(){
  var $$obj = entry.subEntries;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var subEntry = $$obj[index];
if (showAll || index + previousCount < 10) {
var subAggregationName = entry.subAggregationName || aggregationName;
var subSingleton = entry.subEntries.length === 1 && singleton;
var subDepth = depth ? depth + 1 : 1;
pug_mixins["aggregation-entry"](subAggregationName, subEntry, subSingleton, subDepth, entry.key, previousCount);
}
previousCount += getSubCount(subEntry);
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var subEntry = $$obj[index];
if (showAll || index + previousCount < 10) {
var subAggregationName = entry.subAggregationName || aggregationName;
var subSingleton = entry.subEntries.length === 1 && singleton;
var subDepth = depth ? depth + 1 : 1;
pug_mixins["aggregation-entry"](subAggregationName, subEntry, subSingleton, subDepth, entry.key, previousCount);
}
previousCount += getSubCount(subEntry);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ful\u003E";
}
pug_html = pug_html + "\u003C\u002Fli\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"aggregation\"\u003E";
var singleton = aggregation.entries.length === 1;
pug_html = pug_html + "\u003Cdiv\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E ";
if (!singleton && !isSelected) {
pug_html = pug_html + "\u003Csmall\u003E\u003Ca class=\"multi-select\" href=\"#\"\u003E" + (pug_escape(null == (pug_interp = multiSelect ? 'apply' : 'multi-select') ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fsmall\u003E";
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cul\u003E";
if (totalCount <= 12) {
// iterate aggregation.entries
;(function(){
  var $$obj = aggregation.entries;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var entry = $$obj[index];
pug_mixins["aggregation-entry"](aggregation.name, entry, false, 0, null, -1);
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var entry = $$obj[index];
pug_mixins["aggregation-entry"](aggregation.name, entry, false, 0, null, -1);
    }
  }
}).call(this);

}
else {
pug_html = pug_html + "\u003Cdiv\u003E";
var previous = 0;
// iterate aggregation.entries
;(function(){
  var $$obj = aggregation.entries;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var entry = $$obj[index];
if (showAll || index + previous < 10) {
pug_mixins["aggregation-entry"](aggregation.name, entry, singleton, 0, null, previous);
previous += getSubCount(entry);
}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var entry = $$obj[index];
if (showAll || index + previous < 10) {
pug_mixins["aggregation-entry"](aggregation.name, entry, singleton, 0, null, previous);
previous += getSubCount(entry);
}
    }
  }
}).call(this);

pug_html = pug_html + "\u003Ca class=\"default-link toggle-show\" href=\"#\"\u003E" + (pug_escape(null == (pug_interp = showAll ? 'Show less' : 'Show ' + (totalCount - 10) + ' more') ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"aggregation" in locals_for_with?locals_for_with.aggregation:typeof aggregation!=="undefined"?aggregation:undefined,"getAggregationUrl" in locals_for_with?locals_for_with.getAggregationUrl:typeof getAggregationUrl!=="undefined"?getAggregationUrl:undefined,"getLabel" in locals_for_with?locals_for_with.getLabel:typeof getLabel!=="undefined"?getLabel:undefined,"getSubCount" in locals_for_with?locals_for_with.getSubCount:typeof getSubCount!=="undefined"?getSubCount:undefined,"isSelected" in locals_for_with?locals_for_with.isSelected:typeof isSelected!=="undefined"?isSelected:undefined,"isSelectedAggregationValue" in locals_for_with?locals_for_with.isSelectedAggregationValue:typeof isSelectedAggregationValue!=="undefined"?isSelectedAggregationValue:undefined,"label" in locals_for_with?locals_for_with.label:typeof label!=="undefined"?label:undefined,"multiSelect" in locals_for_with?locals_for_with.multiSelect:typeof multiSelect!=="undefined"?multiSelect:undefined,"showAll" in locals_for_with?locals_for_with.showAll:typeof showAll!=="undefined"?showAll:undefined,"totalCount" in locals_for_with?locals_for_with.totalCount:typeof totalCount!=="undefined"?totalCount:undefined));;return pug_html;} return template; });