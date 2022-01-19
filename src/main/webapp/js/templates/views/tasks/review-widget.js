define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (closed, references, review) {pug_mixins["reference"] = pug_interp = function(review, reference, view){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv" + (pug_attrs(pug_merge([{"class": "overflow-ellipsis"},attributes]), false)) + "\u003E\u003Cinput" + (" type=\"checkbox\""+pug_attr("id", view + reference.id, true, false)+pug_attr("data-id", reference.id, true, false)+" data-action=\"mark-as-reviewed\""+pug_attr("checked", !!reference.reviewer, true, false)+pug_attr("disabled", closed, true, false)+pug_attr("aria-label", 'Mark ' + reference.name + ' as reviewed', true, false)) + "\u002F\u003E\u003Ca" + (pug_attr("href", review.repositoryPath + '/dataset/' + reference.type + '/' + reference.refId + '?commitId=' + reference.commitId, true, false)+pug_attr("title", reference.name, true, false)) + "\u003E\u003Cimg" + (" class=\"model-icon-small\""+pug_attr("src", 'images/model/small/' + reference.type.toLowerCase() + '.png', true, false)+pug_attr("aria-label", 'Icon of ' + reference.type, true, false)) + "\u002F\u003E\u003Cstrong\u003E" + (pug_escape(null == (pug_interp = reference.name) ? "" : pug_interp)) + "\u003C\u002Fstrong\u003E\u003C\u002Fa\u003E\u003C\u002Fdiv\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003E\u003Cspan" + (" class=\"review-title\""+pug_attr("title", review.name, true, false)) + "\u003E" + (pug_escape(null == (pug_interp = review.name) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003Cspan class=\"glyphicon glyphicon-info-sign\" data-action=\"open-review\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"glyphicon glyphicon-filter\" data-action=\"filter-checked\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"glyphicon glyphicon-resize-full\" data-action=\"toggle-size\"\u003E\u003C\u002Fspan\u003E\u003Cspan aria-hidden=\"true\" data-action=\"close-widget\"\u003E&times;\u003C\u002Fspan\u003E\u003Cspan class=\"sr-only\"\u003EClose\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
// iterate references
;(function(){
  var $$obj = references;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var list = $$obj[pug_index0];
// iterate list
;(function(){
  var $$obj = list;
  if ('number' == typeof $$obj.length) {
      for (var pug_index1 = 0, $$l = $$obj.length; pug_index1 < $$l; pug_index1++) {
        var reference = $$obj[pug_index1];
pug_mixins["reference"].call({
attributes: {"class": "review-reference"}
}, review, reference, 'review-widget');
      }
  } else {
    var $$l = 0;
    for (var pug_index1 in $$obj) {
      $$l++;
      var reference = $$obj[pug_index1];
pug_mixins["reference"].call({
attributes: {"class": "review-reference"}
}, review, reference, 'review-widget');
    }
  }
}).call(this);

pug_html = pug_html + "\u003Cp\u003E \u003C\u002Fp\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var list = $$obj[pug_index0];
// iterate list
;(function(){
  var $$obj = list;
  if ('number' == typeof $$obj.length) {
      for (var pug_index2 = 0, $$l = $$obj.length; pug_index2 < $$l; pug_index2++) {
        var reference = $$obj[pug_index2];
pug_mixins["reference"].call({
attributes: {"class": "review-reference"}
}, review, reference, 'review-widget');
      }
  } else {
    var $$l = 0;
    for (var pug_index2 in $$obj) {
      $$l++;
      var reference = $$obj[pug_index2];
pug_mixins["reference"].call({
attributes: {"class": "review-reference"}
}, review, reference, 'review-widget');
    }
  }
}).call(this);

pug_html = pug_html + "\u003Cp\u003E \u003C\u002Fp\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"closed" in locals_for_with?locals_for_with.closed:typeof closed!=="undefined"?closed:undefined,"references" in locals_for_with?locals_for_with.references:typeof references!=="undefined"?references:undefined,"review" in locals_for_with?locals_for_with.review:typeof review!=="undefined"?review:undefined));;return pug_html;} return template; });