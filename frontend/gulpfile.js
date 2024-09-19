var fs = require('fs');
var gulp = require('gulp');
var concat = require('gulp-concat');
var clean = require('gulp-clean');
var cssConcat = require('gulp-concat-css');
var insert = require('gulp-insert');
var minifyCss = require('gulp-clean-css');
var requirejsOptimize = require('gulp-requirejs-optimize');
var stylus = require('gulp-stylus');
var pug = require('gulp-pug');
var htmlreplace=require('gulp-html-replace');
var gulpif=require('gulp-if');
var zip = require('gulp-zip');
var child_process = require('child_process');
var params = require('yargs').argv;

if (!params.contextPath) {
  params.contextPath = '/';
}

if (!params.customDir) {
  params.customDir = 'custom';
}

var getPomVersion = function() {
  try {
    var pom = fs.readFileSync('../pom.xml', 'utf8')
    var version = pom.substring(pom.indexOf('<version>') + 9)
    return version.substring(0, version.indexOf('</version>'))
  } catch (e) {
    return 'Unknown'
  }
}

var getCommitVersion = function() {
  var cmd = 'git rev-parse --short HEAD'
  try {
    return (child_process.execSync(cmd) + '').trim()
  } catch (e) {
    return 'Unknown'
  }
}

var timestamp = new Date().getTime();

var collect = function(directory) {
  var all = [];
  var files = [];
  var fsFiles = fs.readdirSync(directory);
  for (var i = 0; i < fsFiles.length; i++) {
    files.push(directory + '/' + fsFiles[i]);
  }
  var file = null;
  var children = null;
  while (files.length !== 0) {
    file = files.pop();
    if (fs.statSync(file).isDirectory()) {
      children = fs.readdirSync(file);
      for (var j = 0; j < children.length; j++) {
        files.push(file + '/' + children[j]);
      }
    } else {
      all.push(file);
    }
  }
  return all;
};

gulp.task('clear', function() {
  return gulp.src([
      './src/webapp/js/templates',
      './src/webapp/js/libs',
      './src/webapp/css/libs',
      './src/webapp/css/fonts',
      './src/webapp/index.html',
      './src/webapp/login.html',
      './src/webapp/imprint.html',
      '../backend/src/main/webapp/*',
      '../backend/src/main/resources/ssr/*',
      '../backend/src/main/resources/static/*',
      '../backend/target/lca-collaboration-server*/*',
      '../backend/target/classes/static/*'
    ], { read: false, allowEmpty: true })
    .pipe(clean({ force: true }));
});

gulp.task('copyJsModules', function() {
  return gulp.src([
      'node_modules/backbone/backbone.js',
      'node_modules/bootstrap/dist/js/bootstrap.js',
      'node_modules/cropper/dist/cropper.js',
      'node_modules/d3/dist/d3.js',
      'node_modules/tablesorter/dist/js/jquery.tablesorter.js',
      'node_modules/jquery/dist/jquery.js',
      'node_modules/jstree/dist/jstree.js',
      'node_modules/leaflet/dist/leaflet.js',
      'node_modules/moment/moment.js',
      'node_modules/qrcode/build/qrcode.js',
      'node_modules/requirejs/require.js',
      'node_modules/underscore/underscore.js',
      'external-libs/coffee-script.js',
      'external-libs/cs.js',
      'external-libs/d3.layout.cloud.js',
      'external-libs/sockjs.js',
      'external-libs/stomp.js'
    ])
    .pipe(gulp.dest('./src/webapp/js/libs'));
});

gulp.task('copyCssModules', function() {
  return gulp.src([
      'node_modules/animate.css/animate.css',
      'node_modules/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css',
      'node_modules/bootstrap/dist/css/bootstrap.css',
      'node_modules/cropper/dist/cropper.css',
      'node_modules/font-awesome/css/font-awesome.css',
      'node_modules/jstree/dist/themes/default/style.css',
      'node_modules/jstree/dist/themes/default/throbber.gif',
      'node_modules/jstree/dist/themes/default/32px.png',
      'node_modules/jstree/dist/themes/default/40px.png',
      'node_modules/leaflet/dist/leaflet.css'
    ])
    .pipe(gulp.dest('./src/webapp/css/libs'));
});

gulp.task('copyFontModules', function() {
  return gulp.src([
      'node_modules/bootstrap/dist/fonts/*',
      'node_modules/font-awesome/fonts/*',
    ])
    .pipe(gulp.dest('./src/webapp/css/fonts'));
});

gulp.task('pugIndex', function() {
  return gulp.src('./src/pug/*.pug')
    .pipe(pug({ locals: {} }))
    .pipe(gulp.dest('./src/webapp'));
});

gulp.task('pugViews', function() {
  return gulp.src('./src/pug/views/**/*.pug')
    .pipe(pug({ client: true, compileDebug: false }))
    .pipe(insert.transform(function (contents) {
      return 'define(function(require,exports,module){ ' + contents + ' return template; });'
    }))
    .pipe(gulp.dest('./src/webapp/js/templates/views'));
});

gulp.task('stylus', function() {
  return gulp.src('./src/stylus/**/*.styl')
    .pipe(stylus())
    .pipe(concat('main.css'))
    .pipe(gulp.dest('./src/webapp/css'));
});

gulp.task('cssBuild', function() {
  return gulp.src(['./src/webapp/css/styles.css', params.customDir + '/styles.css'], { allowEmpty: true })
    .pipe(cssConcat('styles.css', { rebaseUrls: false, allowEmpty: true }))
    .pipe(minifyCss({ keepSpecialComments: false, allowEmpty: true }))
    .pipe(gulp.dest('../backend/src/main/resources/static/css'));
});

gulp.task('copyFonts', function() {
  return gulp.src([
      './src/webapp/css/fonts/**/*.*',
      params.customDir + '/fonts/**/*.*',
    ])
    .pipe(gulp.dest('../backend/src/main/resources/static/fonts'));
});

gulp.task('copySprites', function() {
  return gulp.src([
      './src/webapp/css/libs/*.png',
      './src/webapp/css/libs/*.gif',
    ])
    .pipe(gulp.dest('../backend/src/main/resources/static/css'));
});

gulp.task('copyImages', function() {
  return gulp.src([
      './src/webapp/images/**/*.*',
    ])
    .pipe(gulp.dest('../backend/src/main/resources/static/images'));
});

gulp.task('copyGraph', function() {
  return gulp.src([
      './src/webapp/graph/**/*.*',
    ])
    .pipe(gulp.dest('../backend/src/main/resources/static/graph'));
});

gulp.task('copyLicense', function() {
  return gulp.src(['../THIRD-PARTY.txt'])
    .pipe(gulp.dest('../backend/src/main/webapp'));	
});

gulp.task('collectDependencies', function() {
  // (most) views and templates are loaded dynamically, App.coffee references DynamicDependencies so the build includes the files
  fs.writeFileSync('./src/webapp/js/app/DynamicDependencies.coffee', '');
  return gulp.src('./src/webapp/js/app/DynamicDependencies.coffee')
    .pipe(insert.transform(function() {
      var content = 'define([';
      var views = collect('./src/webapp/js/views');
      for (var i = 0; i < views.length; i++) {
        if (i !== 0) {
          content += ', ';
        }
        content += "'" + views[i].replace('./src/webapp/js/', 'cs!').replace('.coffee', '') + "'";
      }
      var templates = collect('./src/webapp/js/templates');
      for (var j = 0; j < templates.length; j++) {
        content += ", '" + templates[j].replace('./src/webapp/js/', '').replace('.js', '') + "'";
      }
      content += '], () -> )';
      return content;
    }))
    .pipe(gulp.dest('./src/webapp/js/app'));
});

gulp.task('setBuildInfo', function() {
  return gulp.src('./src/webapp/js/templates/views/admin/overview.js')
    .pipe(insert.transform(function(contents) {
      return contents.replace('{{releaseVersion}}', getPomVersion())
        .replace('{{commitId}}', getCommitVersion())
        .replace('{{buildDate}}', new Date(timestamp).toLocaleString());
    }))
    .pipe(gulp.dest('./src/webapp/js/templates/views/admin'));
});

gulp.task('copyToBackend', function() {
  return gulp.src('./src/webapp/**/*.*')
    .pipe(gulp.dest('../backend/src/main/webapp'));
})

gulp.task('modifyIndexHtml', function() {
  var path = fs.existsSync(params.customDir + '/index.html') ? params.customDir + '/index.html' : './src/webapp/index.html';
  return gulp.src(path)
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles.css?timestamp=' + timestamp + '"');
      content = content.replace(' data-main="js/main"', '');
      content = content.replace('src="js/libs/require.js"', 'src="js/main.js?timestamp=' + timestamp + '"');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('../backend/src/main/resources/static'));
});

gulp.task('modifyOtherHtml', function() {
  return gulp.src(['./src/webapp/login.html', './src/webapp/imprint.html', './src/webapp/maintenance.html', './src/webapp/job.html'])
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles.css?timestamp=' + timestamp + '"');
      content = content.replace('js/libs/jquery', 'js/jquery');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('../backend/src/main/resources/static'));
});

gulp.task('modifyCustomHtmlPages', function() {
  return gulp.src(params.customDir + '/*.html')
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles.css?timestamp=' + timestamp + '"');
      content = content.replace(' data-main="js/main"', '');
      content = content.replace('src="js/libs/require.js"', 'src="js/main.js?timestamp=' + timestamp + '"');
      content = content.replace('js/libs/jquery', 'js/jquery');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('../backend/src/main/resources/static'));
});

gulp.task('modifyCustomPublicHtml', function() {
    return  gulp.src('../backend/src/main/resources/static/index_public.html')
		.pipe(gulpif(params.appserver == 'stage',htmlreplace( {
			'google':{
				src: null,
				tpl: ''
			 }
		})))
		.pipe(gulp.dest('../backend/src/main/resources/static'));
});

gulp.task('copyCustomImages', function() {
  return gulp.src(params.customDir + '/images/**/*.*')
    .pipe(gulp.dest('../backend/src/main/resources/static/images'));
});

gulp.task('copyJQueryForLogin', function() {
  return gulp.src('./src/webapp/js/libs/jquery.js')
    .pipe(gulp.dest('../backend/src/main/resources/static/js'));
});

gulp.task('copyRobots', function() {
  return gulp.src('./src/webapp/robots.txt')
    .pipe(gulp.dest('../backend/src/main/resources/static'));
});

gulp.task('jsBuild', function() {
  return gulp.src('src/webapp/js/main.js')
    .pipe(requirejsOptimize({
        baseUrl: 'src/webapp/js',
        mainConfigFile: 'src/webapp/js/main.js',
        out: 'main.js',
        name: 'main',
        findNestedDependencies: false,
        include: ['requireLib'],
        stubModules: ['cs', 'coffee-script'],
        insertRequire: ['main']
    }))
    .pipe(gulp.dest('../backend/src/main/resources/static/js'));
});

gulp.task('copySsrImages', function() {
  return gulp.src(['src/webapp/images/**/*.*'])
  .pipe(gulp.dest('../backend/src/main/resources/ssr/files/images'));
});

gulp.task('copySsrCss', function() {
  return gulp.src(['../backend/src/main/resources/static/css/styles.css'])
  .pipe(gulp.dest('../backend/src/main/resources/ssr/files/css'));
});

gulp.task('copySsrFonts', function() {
  return gulp.src(['../backend/src/main/resources/static/fonts/*'])
  .pipe(gulp.dest('../backend/src/main/resources/ssr/files/fonts'));
});

gulp.task('copySsrJs', function() {
  return gulp.src([
    'node_modules/bootstrap/dist/js/bootstrap.js',
    'node_modules/jquery/dist/jquery.js',
  ])
  .pipe(gulp.dest('../backend/src/main/resources/ssr/files/js'));
});

gulp.task('zipSsrPack', function() {
  return gulp.src('../backend/src/main/resources/ssr/files/**/*.*')
  .pipe(zip('resources.zip'))
  .pipe(gulp.dest('../backend/src/main/resources/ssr'))  
});

gulp.task('clearSsrFiles', function() {
  return gulp.src([
    '../backend/src/main/resources/ssr/files',
  ], { read: false, allowEmpty: true })
  .pipe(clean({ force: true }));
});

gulp.task('buildSsrPack', gulp.series(
    'copySsrImages',
    'copySsrCss',
    'copySsrFonts',
    'copySsrJs',
    'zipSsrPack',
    'clearSsrFiles'
));

gulp.task('copyFrontendModules', gulp.series(
  'copyJsModules',
  'copyCssModules',
  'copyFontModules'
));

gulp.task('default', gulp.series(
  'clear',
  'copyFrontendModules',
  'pugIndex',
  'pugViews',
  'stylus',
  'collectDependencies',
  'setBuildInfo'
));

gulp.task('dev', gulp.series(
  'default',
  'copyToBackend'
))

gulp.task('build', gulp.series(
  'default',
  'cssBuild',
  'copyFonts',
  'copySprites',
  'copyImages',
  'copyGraph',
  'modifyIndexHtml',
  'modifyOtherHtml',
  'modifyCustomHtmlPages',
  'modifyCustomPublicHtml',
  'copyCustomImages',
  'copyJQueryForLogin',
  'copyRobots',
  'copyLicense',
  'jsBuild',
  'buildSsrPack'
));

