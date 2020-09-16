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
var child_process = require('child_process');
var params = require('yargs').argv;

if (!params.contextPath) {
  params.contextPath = '/';
}

if (!params.customDir) {
  params.customDir = './custom';
}

var getCustomHtmlFiles = function(excludePublicIndex) {
  try {
    var files = fs.readdirSync(params.customDir)
    var result = files.filter(function(file) { return file.indexOf('.html') !== -1 && (!excludePublicIndex || file !== 'index_public.html'); });
    return result;
  } catch (e) {
    return []
  }
}

var getPomVersion = function() {
  try {
    var pom = fs.readFileSync('pom.xml', 'utf8')
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

var collect = function(directory, finished) {
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
      './src/main/webapp/js/templates',
      './src/main/webapp/js/libs',
      './src/main/webapp/css/libs',
      './src/main/webapp/css/fonts',
      './src/main/webapp/index.html',
      './src/main/webapp/login.html',
      './src/main/webapp/imprint.html'
    ], { read: false, allowEmpty: true })
    .pipe(clean());
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
      'node_modules/pace-js-amd-fix/pace.js',
      'node_modules/qrcode/build/qrcode.js',
      'node_modules/requirejs/require.js',
      'node_modules/underscore/underscore.js',
      'external-libs/coffee-script.js',
      'external-libs/cs.js',
      'external-libs/d3.layout.cloud.js'
    ])
    .pipe(gulp.dest('./src/main/webapp/js/libs'));
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
    .pipe(gulp.dest('./src/main/webapp/css/libs'));
});

gulp.task('copyFontModules', function() {
  return gulp.src([
      'node_modules/bootstrap/dist/fonts/*',
      'node_modules/font-awesome/fonts/*',
    ])
    .pipe(gulp.dest('./src/main/webapp/css/fonts'));
});

gulp.task('pugIndex', function() {
  return gulp.src('./src/main/pug/*.pug')
    .pipe(pug({ locals: {} }))
    .pipe(gulp.dest('./src/main/webapp/'));
});

gulp.task('pugViews', function() {
  return gulp.src('./src/main/pug/views/**/*.pug')
    .pipe(pug({ client: true, compileDebug: false }))
    .pipe(insert.transform(function (contents) {
      return 'define(function(require,exports,module){ ' + contents + ' return template; });'
    }))
    .pipe(gulp.dest('./src/main/webapp/js/templates/views'));
});

gulp.task('stylus', function() {
  return gulp.src('./src/main/stylus/**/*.styl')
    .pipe(stylus())
    .pipe(concat('main.css'))
    .pipe(gulp.dest('./src/main/webapp/css/'));
});

gulp.task('internalCssBuild', function() {
  return gulp.src('./src/main/webapp/css/styles.css')
    .pipe(cssConcat('internal-styles' + timestamp + '.css'))
    .pipe(gulp.dest('./target/css-build'));
});

gulp.task('customCssBuild', function() {
  return gulp.src(params.customDir + '/styles.css', { allowEmpty: true })
    .pipe(cssConcat('custom-styles' + timestamp + '.css', { rebaseUrls: false, allowEmpty: true }))
    .pipe(gulp.dest('./target/css-build'));
});

gulp.task('cssBuild', function() {
  return gulp.src([
      './target/css-build/internal-styles' + timestamp + '.css',
      './target/css-build/custom-styles' + timestamp + '.css'
    ], {allowEmpty: true})
    .pipe(cssConcat('styles' + timestamp + '.css', { rebaseUrls: false, allowEmpty: true }))
    .pipe(minifyCss({ keepSpecialComments: false, allowEmpty: true }))
    .pipe(gulp.dest('./target/require-build/css'));
});

gulp.task('fontBuild', function() {
  return gulp.src([
      './src/main/webapp/css/fonts/**/*.*',
      params.customDir + '/fonts/**/*.*'
    ])
    .pipe(gulp.dest('./target/require-build/css/fonts'));
});

gulp.task('copySprites', function() {
  return gulp.src([
      './src/main/webapp/css/libs/*.png',
      './src/main/webapp/css/libs/*.gif'
    ])
    .pipe(gulp.dest('./target/require-build/css/libs'));
});

gulp.task('collectDependencies', function() {
  // (most) views and templates are loaded dynamically, App.coffee references DynamicDependencies so the build includes the files
  fs.writeFileSync('src/main/webapp/js/app/DynamicDependencies.coffee');
  return gulp.src('./src/main/webapp/js/app/DynamicDependencies.coffee')
    .pipe(insert.transform(function(contents) {
      var content = 'define([';
      var views = collect('./src/main/webapp/js/views');
      for (var i = 0; i < views.length; i++) {
        if (i !== 0) {
          content += ', ';
        }
        content += "'" + views[i].replace('./src/main/webapp/js/', 'cs!').replace('.coffee', '') + "'";
      }
      var templates = collect('./src/main/webapp/js/templates');
      for (var j = 0; j < templates.length; j++) {
        content += ", '" + templates[j].replace('./src/main/webapp/js/', '').replace('.js', '') + "'";
      }
      content += '], () -> )';
      return content;
    }))
    .pipe(gulp.dest('./src/main/webapp/js/app'));
});

gulp.task('setBuildInfo', function() {
  return gulp.src('./src/main/webapp/js/templates/views/admin/overview.js')
    .pipe(insert.transform(function(contents) {
      return contents.replace('{{releaseVersion}}', getPomVersion())
        .replace('{{commitId}}', getCommitVersion())
        .replace('{{buildDate}}', new Date(timestamp).toLocaleString());
    }))
    .pipe(gulp.dest('./src/main/webapp/js/templates/views/admin'));
});

gulp.task('setCustomPublicResources', function() {
  return gulp.src('./src/main/java/com/greendelta/collaboration/platform/guice/ShiroModule.java')
    .pipe(insert.transform(function(contents) {
      var member = 'public static final String[] CUSTOM_PUBLIC_RESOURCES = {'
      var resources = member;
      var customFiles = getCustomHtmlFiles(true);
      for (var i = 0; i < customFiles.length; i++) {
        resources += i === 0 ? ' ' : ', ';
        resources += '"/' + customFiles[i].substring(0, customFiles[i].lastIndexOf('.html')) + '"';
        resources += i === customFiles.length - 1 ? ' ' : '';
      }
      resources += '};'
      var result = contents.substring(0, contents.indexOf(member));
      return result + resources + contents.substring(contents.indexOf('\n', contents.indexOf(member)));
    }))
    .pipe(gulp.dest('./src/main/java/com/greendelta/collaboration/platform/guice'));
});

gulp.task('modifyIndexHtml', function() {
  // replace styles.css and main.js with timestamp filename
  var path = fs.existsSync(params.customDir + '/index.html') ? params.customDir + '/index.html' : './src/main/webapp/index.html';
  return gulp.src(path)
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"');
      content = content.replace(' data-main="js/main"', '');
      content = content.replace('src="js/libs/require.js"', 'src="js/main' + timestamp + '.js"');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('./target/require-build'));
});

gulp.task('modifyOtherHtml', function() {
  // replace styles-login.css with timestamp filename
  return gulp.src(['./src/main/webapp/login.html', './src/main/webapp/imprint.html', './src/main/webapp/maintenance.html'])
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"');
      content = content.replace('js/libs/jquery', 'js/jquery');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('./target/require-build'));
});
gulp.task('modifyCustomHtmlPages', function() {
  // replace styles-login.css with timestamp filename
  return gulp.src(params.customDir + '/*.html')
    .pipe(insert.transform(function(contents) {
      var content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"');
      content = content.replace(' data-main="js/main"', '');
      content = content.replace('src="js/libs/require.js"', 'src="js/main' + timestamp + '.js"');
      content = content.replace('js/libs/jquery', 'js/jquery');
      content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>');
      return content;
    }))
    .pipe(gulp.dest('./target/require-build'));
});
gulp.task('modifyCustomPublicHtml', function() {
    return  gulp.src('./target/require-build/index_public.html')
		.pipe(gulpif(params.appserver == 'stage',htmlreplace( {
			'google':{
				src: null,
				tpl: ''
			 }
		})))
		.pipe(gulp.dest('./target/require-build'));
});
gulp.task('copyCustomImages', function() {
  return gulp.src(params.customDir + '/images/**/*.*')
    .pipe(gulp.dest('./target/require-build/images'));
});

gulp.task('copyJQueryForLogin', function() {
  return gulp.src('./src/main/webapp/js/libs/jquery.js')
    .pipe(gulp.dest('./target/require-build/js'));
});

gulp.task('jsBuild', function() {
  return gulp.src('src/main/webapp/js/main.js')
    .pipe(requirejsOptimize({
        baseUrl: 'src/main/webapp/js',
        mainConfigFile: 'src/main/webapp/js/main.js',
        out: 'main' + timestamp + '.js',
        name: 'main',
        findNestedDependencies: false,
        include: ['requireLib'],
        stubModules: ['cs', 'coffee-script'],
        insertRequire: ['main']
    }))
    .pipe(gulp.dest('./target/require-build/js'));
});

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
  'internalCssBuild',
  'customCssBuild',
  'cssBuild',
  'fontBuild',
  'collectDependencies',
  'setBuildInfo',
  'setCustomPublicResources'
));

gulp.task('build', gulp.series(
  'default',
  'copySprites',
  'modifyIndexHtml',
  'modifyOtherHtml',
  'modifyCustomHtmlPages',
  'modifyCustomPublicHtml',
  'copyCustomImages',
  'copyJQueryForLogin',
  'jsBuild'
));

