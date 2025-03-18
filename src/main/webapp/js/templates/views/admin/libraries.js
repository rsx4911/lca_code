define(function(require,exports,module){ function pug_attr(t,e,n,r){if(!1===e||null==e||!e&&("class"===t||"style"===t))return"";if(!0===e)return" "+(r?t:t+'="'+t+'"');var f=typeof e;return"object"!==f&&"function"!==f||"function"!=typeof e.toJSON||(e=e.toJSON()),"string"==typeof e||(e=JSON.stringify(e),n||-1===e.indexOf('"'))?(n&&(e=pug_escape(e))," "+t+'="'+e+'"'):" "+t+"='"+e.replace(/'/g,"&#39;")+"'"}
function pug_escape(e){var a=""+e,t=pug_match_html.exec(a);if(!t)return e;var r,c,n,s="";for(r=t.index,c=0;r<a.length;r++){switch(a.charCodeAt(r)){case 34:n="&quot;";break;case 38:n="&amp;";break;case 60:n="&lt;";break;case 62:n="&gt;";break;default:continue}c!==r&&(s+=a.substring(c,r)),c=r+1,s+=n}return c!==r?s+a.substring(c,r):s}
var pug_match_html=/["&<>]/;function template(locals) {var pug_html = "", pug_mixins = {}, pug_interp;;var locals_for_with = (locals || {});(function (Object, groups, missingLibraries) {pug_mixins["access-group"] = pug_interp = function(libraries, label, access){
var block = (this && this.block), attributes = (this && this.attributes) || {};
if (libraries && libraries.length) {
if (label) {
pug_html = pug_html + "\u003Ch4\u003E" + (pug_escape(null == (pug_interp = label) ? "" : pug_interp)) + "\u003C\u002Fh4\u003E";
}
pug_html = pug_html + "\u003Ctable class=\"table table-bordered\"\u003E\u003Ctheader\u003E\u003Ctr\u003E\u003Cth\u003ELibrary\u003C\u002Fth\u003E\u003Cth\u003EDescription\u003C\u002Fth\u003E\u003Cth\u003ELinked in repositories\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Ftheader\u003E\u003Ctbody\u003E";
// iterate libraries
;(function(){
  var $$obj = libraries;
  if ('number' == typeof $$obj.length) {
      for (var pug_index0 = 0, $$l = $$obj.length; pug_index0 < $$l; pug_index0++) {
        var library = $$obj[pug_index0];
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E\u003Ca" + (pug_attr("href", 'ws/libraries/' + library.name, true, false)+" target=\"_blank\"") + "\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = library.description||'-') ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
if (!library.linkedIn || !library.linkedIn.length) {
pug_html = pug_html + "-";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"comma-separated\"\u003E";
// iterate library.linkedIn
;(function(){
  var $$obj = library.linkedIn;
  if ('number' == typeof $$obj.length) {
      for (var pug_index1 = 0, $$l = $$obj.length; pug_index1 < $$l; pug_index1++) {
        var repo = $$obj[pug_index1];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index1 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index1];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" data-action=\"delete\""+pug_attr("data-name", library.name, true, false)+pug_attr("data-access", access, true, false)) + "\u003EDelete\u003C\u002Fbutton\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index0 in $$obj) {
      $$l++;
      var library = $$obj[pug_index0];
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E\u003Ca" + (pug_attr("href", 'ws/libraries/' + library.name, true, false)+" target=\"_blank\"") + "\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = library.description||'-') ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd\u003E";
if (!library.linkedIn || !library.linkedIn.length) {
pug_html = pug_html + "-";
}
else {
pug_html = pug_html + "\u003Cdiv class=\"comma-separated\"\u003E";
// iterate library.linkedIn
;(function(){
  var $$obj = library.linkedIn;
  if ('number' == typeof $$obj.length) {
      for (var pug_index2 = 0, $$l = $$obj.length; pug_index2 < $$l; pug_index2++) {
        var repo = $$obj[pug_index2];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index2 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index2];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E";
}
pug_html = pug_html + "\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cbutton" + (" class=\"btn btn-xs btn-danger\""+" data-action=\"delete\""+pug_attr("data-name", library.name, true, false)+pug_attr("data-access", access, true, false)) + "\u003EDelete\u003C\u002Fbutton\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
}
};
pug_mixins["missing-libraries"] = pug_interp = function(missingLibraries){
var block = (this && this.block), attributes = (this && this.attributes) || {};
pug_html = pug_html + "\u003Ctable class=\"table table-bordered\"\u003E\u003Ctheader\u003E\u003Ctr\u003E\u003Cth\u003ELibrary\u003C\u002Fth\u003E\u003Cth\u003ELinked in repositories\u003C\u002Fth\u003E\u003Cth\u003E\u003C\u002Fth\u003E\u003C\u002Ftr\u003E\u003C\u002Ftheader\u003E\u003Ctbody\u003E";
// iterate missingLibraries
;(function(){
  var $$obj = missingLibraries;
  if ('number' == typeof $$obj.length) {
      for (var pug_index3 = 0, $$l = $$obj.length; pug_index3 < $$l; pug_index3++) {
        var library = $$obj[pug_index3];
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cdiv class=\"comma-separated\"\u003E";
// iterate (library.linkedIn || [])
;(function(){
  var $$obj = (library.linkedIn || []);
  if ('number' == typeof $$obj.length) {
      for (var pug_index4 = 0, $$l = $$obj.length; pug_index4 < $$l; pug_index4++) {
        var repo = $$obj[pug_index4];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index4 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index4];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cbutton class=\"btn btn-xs btn-success\" data-action=\"add\"\u003EAdd\u003C\u002Fbutton\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index3 in $$obj) {
      $$l++;
      var library = $$obj[pug_index3];
pug_html = pug_html + "\u003Ctr\u003E\u003Ctd\u003E" + (pug_escape(null == (pug_interp = library.name) ? "" : pug_interp)) + "\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cdiv class=\"comma-separated\"\u003E";
// iterate (library.linkedIn || [])
;(function(){
  var $$obj = (library.linkedIn || []);
  if ('number' == typeof $$obj.length) {
      for (var pug_index5 = 0, $$l = $$obj.length; pug_index5 < $$l; pug_index5++) {
        var repo = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
      }
  } else {
    var $$l = 0;
    for (var pug_index5 in $$obj) {
      $$l++;
      var repo = $$obj[pug_index5];
pug_html = pug_html + "\u003Cspan\u003E\u003Ca" + (" class=\"follow\""+pug_attr("href", '/' + repo + '/datasets/Libraries', true, false)) + "\u003E" + (pug_escape(null == (pug_interp = repo) ? "" : pug_interp)) + "\u003C\u002Fa\u003E\u003C\u002Fspan\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003C\u002Ftd\u003E\u003Ctd\u003E\u003Cbutton class=\"btn btn-xs btn-success\" data-action=\"add\"\u003EAdd\u003C\u002Fbutton\u003E\u003C\u002Ftd\u003E\u003C\u002Ftr\u003E";
    }
  }
}).call(this);

pug_html = pug_html + "\u003C\u002Ftbody\u003E\u003C\u002Ftable\u003E";
};
pug_html = pug_html + "\u003Cdiv class=\"header-box\"\u003ELibraries\u003Cbutton class=\"btn btn-success pull-right\" data-action=\"add\"\u003EAdd library\u003C\u002Fbutton\u003E\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E";
if (!groups || !Object.keys(groups).length) {
pug_html = pug_html + "\u003Cem\u003ENo libraries uploaded\u003C\u002Fem\u003E";
}
else {
var accesses = [{ type: 'PUBLIC', label: 'Public libraries' }, { type:'USER', label: 'Private libraries' }, { type: 'MEMBER', label: 'Member libraries' }];
// iterate accesses
;(function(){
  var $$obj = accesses;
  if ('number' == typeof $$obj.length) {
      for (var pug_index6 = 0, $$l = $$obj.length; pug_index6 < $$l; pug_index6++) {
        var access = $$obj[pug_index6];
pug_mixins["access-group"](groups[access.type], access.label, access.type);
      }
  } else {
    var $$l = 0;
    for (var pug_index6 in $$obj) {
      $$l++;
      var access = $$obj[pug_index6];
pug_mixins["access-group"](groups[access.type], access.label, access.type);
    }
  }
}).call(this);

// iterate Object.keys(groups)
;(function(){
  var $$obj = Object.keys(groups);
  if ('number' == typeof $$obj.length) {
      for (var pug_index7 = 0, $$l = $$obj.length; pug_index7 < $$l; pug_index7++) {
        var access = $$obj[pug_index7];
if (!accesses.find(function (a) { return a.type === access})) {
var label = 'Libraries of team ' + access;
pug_mixins["access-group"](groups[access], label, access);
}
      }
  } else {
    var $$l = 0;
    for (var pug_index7 in $$obj) {
      $$l++;
      var access = $$obj[pug_index7];
if (!accesses.find(function (a) { return a.type === access})) {
var label = 'Libraries of team ' + access;
pug_mixins["access-group"](groups[access], label, access);
}
    }
  }
}).call(this);

}
pug_html = pug_html + "\u003C\u002Fdiv\u003E\u003Cdiv class=\"header-box\"\u003EMissing libraries\u003Cdiv class=\"clear\"\u003E\u003C\u002Fdiv\u003E\u003C\u002Fdiv\u003E\u003Cdiv class=\"content-box\"\u003E\u003Cp\u003EThe following libraries are linked in repositories, but are not present on the server. For users to be able to work with these repositories, you will need to share the required libraries with them manually - or add them here.\u003C\u002Fp\u003E";
if (!missingLibraries || !missingLibraries.length) {
pug_html = pug_html + "\u003Cem\u003ENo missing libraries found\u003C\u002Fem\u003E";
}
else {
pug_mixins["missing-libraries"](missingLibraries);
}
pug_html = pug_html + "\u003C\u002Fdiv\u003E";}.call(this,"Object" in locals_for_with?locals_for_with.Object:typeof Object!=="undefined"?Object:undefined,"groups" in locals_for_with?locals_for_with.groups:typeof groups!=="undefined"?groups:undefined,"missingLibraries" in locals_for_with?locals_for_with.missingLibraries:typeof missingLibraries!=="undefined"?missingLibraries:undefined));;return pug_html;} return template; });