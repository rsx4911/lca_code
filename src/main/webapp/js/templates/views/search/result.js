define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_attrs(t,r){var a="";for(var s in t)if(pug_has_own_property.call(t,s)){var u=t[s];if("class"===s){u=pug_classes(u),a=pug_attr(s,u,!1,r)+a;continue}"style"===s&&(u=pug_style(u)),a+=pug_attr(s,u,!1,r)}return a}
function pug_classes(s,r){return Array.isArray(s)?pug_classes_array(s,r):s&&"object"==typeof s?pug_classes_object(s):s||""}
function pug_classes_array(r,a){for(var s,e="",u="",c=Array.isArray(a),g=0;g<r.length;g++)(s=pug_classes(r[g]))&&(c&&a[g]&&(s=pug_escape(s)),e=e+u+s,u=" ");return e}
function pug_classes_object(r){var a="",n="";for(var o in r)o&&r[o]&&pug_has_own_property.call(r,o)&&(a=a+n+o,n=" ");return a}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_has_own_property=Object.prototype.hasOwnProperty;
var pug_match_html=/["&<>]/;
function pug_merge(e,r){if(1===arguments.length){for(var t=e[0],g=1;g<e.length;g++)t=pug_merge(t,e[g]);return t}for(var l in r)if("class"===l){var n=e[l]||[];e[l]=(Array.isArray(n)?n:[n]).concat(r[l]||[])}else if("style"===l){var n=pug_style(e[l]);n=n&&";"!==n[n.length-1]?n+";":n;var a=pug_style(r[l]);a=a&&";"!==a[a.length-1]?a+";":a,e[l]=n+a}else e[l]=r[l];return e}
function pug_style(r){if(!r)return"";if("object"==typeof r){var t="";for(var e in r)pug_has_own_property.call(r,e)&&(t=t+e+":"+r[e]+";");return t}return r+""}function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;



































































pug_mixins["link"] = pug_interp = function(dataset, repo, title, defaultLink, highlight){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var url = repo.path + '/dataset/' + dataset.type + '/' + dataset.refId;
if (repo.commitId) {
url += '?commitId=' + repo.commitId;
}
pug_html = pug_html + "\u003Ca" + (pug_attr("class", pug_classes([defaultLink?'default-link':null], [true]), false, false)+pug_attr("href", url, true, false)) + "\u003E\u003Cspan" + (pug_attr("class", pug_classes([highlight?'result-text':null], [true]), false, false)) + "\u003E" + (pug_escape(null == (pug_interp = title) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E\u003C\u002Fa\u003E";
};
pug_mixins["repositories"] = pug_interp = function(dataset, versionIndex){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var version = dataset.versions[versionIndex];
var versions = dataset.versions.length;
pug_html = pug_html + "\u003Cdiv class=\"result-repositories\"\u003Efound in " + (pug_escape(null == (pug_interp = version.repos.length === 1 ? 'repository' : 'repositories') ? "" : pug_interp)) + " \u003Cspan class=\"comma-separated\"\u003E";
// iterate version.repos
;(function(){
  var $$obj = version.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index1 = 0, $$l = $$obj.length; pug_index1 < $$l; pug_index1++) {
        var repo = $$obj[pug_index1];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index1 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index1];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
if (versions > 1) {
pug_html = pug_html + "\u003Cdiv\u003E" + (pug_escape(null == (pug_interp = versions - 1) ? "" : pug_interp)) + " other " + (pug_escape(null == (pug_interp = versions > 2 ? 'versions' : 'version') ? "" : pug_interp)) + " found in  \u003Cspan class=\"comma-separated\"\u003E";
// iterate dataset.versions
;(function(){
  var $$obj = dataset.versions;
  if ('number' == typeof $$obj.length) {
      for (var index = 0, $$l = $$obj.length; index < $$l; index++) {
        var v = $$obj[index];
if (index !== versionIndex) {
// iterate v.repos
;(function(){
  var $$obj = v.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index3 = 0, $$l = $$obj.length; pug_index3 < $$l; pug_index3++) {
        var repo = $$obj[pug_index3];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index3 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index3];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

}
      }
  } else {
    var $$l = 0;
    for (var index in $$obj) {
      $$l++;
      var v = $$obj[index];
if (index !== versionIndex) {
// iterate v.repos
;(function(){
  var $$obj = v.repos;
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var repo = $$obj[pug_index4];
pug_mixins["link"](dataset, repo, repo.path, true);
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index4];
pug_mixins["link"](dataset, repo, repo.path, true);
    }
  }
}).call(this);

}
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fspan\u003E\u003C\u002Fdiv\u003E";
}
};
pug_mixins["validity"] = pug_interp = function(version){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (version.validFromYear || version.validUntilYear) {
pug_html = pug_html + "\u003Cdiv\u003E\u003Cimg src=\"images\u002Fcalendar.png\" aria-label=\"Calendar icon\"\u002F\u003E";
if (version.validFromYear && version.validUntilYear && version.validFromYear != version.validUntilYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validFromYear) ? "" : pug_interp)) + " - " + (pug_escape(null == (pug_interp = version.validUntilYear) ? "" : pug_interp)));
}
else
if (version.validFromYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validFromYear) ? "" : pug_interp)));
}
else
if (version.validUntilYear) {
pug_html = pug_html + (" " + (pug_escape(null == (pug_interp = version.validUntilYear) ? "" : pug_interp)));
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["location"] = pug_interp = function(version){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (version.location) {
pug_html = pug_html + "\u003Cdiv\u003E \u003Cimg src=\"images\u002Fmodel\u002Fsmall\u002Flocation.png\" aria-label=\"Location icon\"\u002F\u003E " + (pug_escape(null == (pug_interp = version.location) ? "" : pug_interp)) + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["tags"] = pug_interp = function(version, repo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
var repoTags = repo.tags || [] 
var versionTags = version.tags || []
if (repoTags.length || versionTags.length) {
pug_html = pug_html + "\u003Cdiv class=\"result-tags\"\u003E";
// iterate repoTags
;(function(){
  var $$obj = repoTags;
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var tag = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan class=\"badge badge-primary\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var tag = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan class=\"badge badge-primary\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

// iterate versionTags
;(function(){
  var $$obj = versionTags;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var tag = $$obj[pug_index6];
pug_html = pug_html + "\u003Cspan class=\"badge\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index6 in $$obj) {
      $$l++;
      var tag = $$obj[pug_index6];
pug_html = pug_html + "\u003Cspan class=\"badge\"\u003E" + (pug_escape(null == (pug_interp = tag) ? "" : pug_interp)) + "\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
};
pug_mixins["actions"] = pug_interp = function(dataset, version, repo){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Cdiv class=\"dropdown btn-group\"\u003E\u003Cbutton class=\"btn btn-success btn-sm dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\"\u003E\u003Cspan class=\"glyphicon glyphicon-download\"\u003E\u003C\u002Fspan\u003E\u003Cspan class=\"text\"\u003EDownload\u003C\u002Fspan\u003E\u003Cspan class=\"caret\"\u003E\u003C\u002Fspan\u003E\u003C\u002Fbutton\u003E";
var repoName = repo.path.split('/')[1]
pug_html = pug_html + "\u003Cul class=\"dropdown-menu\"\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json\" data-datatype=\"dataset\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-type", dataset.type, true, false)+pug_attr("data-ref-id", dataset.refId, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003EDataset as JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json1\" data-datatype=\"dataset\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-type", dataset.type, true, false)+pug_attr("data-ref-id", dataset.refId, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003EDataset as JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli class=\"divider\" role=\"separator\"\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json\" data-datatype=\"repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003ERepository as JSON-LD (openLCA 2.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003Cli\u003E\u003Ca" + (" href=\"#\" data-format=\"json1\" data-datatype=\"repository\""+pug_attr("data-group", repo.group, true, false)+pug_attr("data-repo", repoName, true, false)+pug_attr("data-commit-id", repo.commitId, true, false)) + "\u003ERepository as JSON-LD (openLCA 1.x)\u003C\u002Fa\u003E\u003C\u002Fli\u003E\u003C\u002Ful\u003E\u003C\u002Fdiv\u003E";
};;return pug_html;} return template; });