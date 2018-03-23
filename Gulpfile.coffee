del = require 'del'
fs = require 'fs'
runSequence = require 'run-sequence'
gulp = require 'gulp'
concat = require 'gulp-concat'
clean = require 'gulp-clean'
cssConcat = require 'gulp-concat-css'
insert = require 'gulp-insert'
minifyCss = require 'gulp-minify-css'
rjs = require 'gulp-requirejs'
stylus = require 'gulp-stylus'
uglify = require 'gulp-uglify' 
nib = require 'nib'
stream = require 'stream'
params = require('yargs').argv
$ = require('gulp-load-plugins')()

unless params.contextPath
	params.contextPath = '/'

timestamp = new Date().getTime()

collect = (directory, finished) ->
	all = []
	files = []
	for file in fs.readdirSync(directory)
		files.push directory + '/' + file
	while files.length isnt 0
		file = files.pop() 
		if fs.statSync(file).isDirectory()
			for f in fs.readdirSync(file)
				files.push file + '/' + f
		else
			all.push file
	return all

gulp.task 'default', [], (callback) ->
	runSequence 'clear', 'jadeIndex', 'jadeViews', 'stylus', 'cssBuild', 'fontBuild', 'collectDependencies', callback

gulp.task 'build', [], (callback) ->
	runSequence 'default', 'copySprites', 'modifyIndexHtml', 'modifyLoginHtml', 'modifyImprintHtml', 'modfiyCustomPublicHtml', 'copyCustomImages', 'copyJQueryForLogin', 'jsBuild', callback

gulp.task 'clear', () ->
	gulp.src(['./src/main/webapp/js/templates', './src/main/webapp/index.html', './src/main/webapp/login.html', './src/main/webapp/imprint.html'], {read: false})
		.pipe clean()
		
gulp.task 'jadeIndex', () ->
	gulp.src('./src/main/jade/*.jade')
		.pipe($.jade 
			locals: {}
		)
		.pipe gulp.dest './src/main/webapp/'

gulp.task 'jadeViews', () ->
	gulp.src('./src/main/jade/views/**/*.jade')
		.pipe($.jade 
			client: true
		)
		.pipe($.wrapAmd
			deps: ['jadeRuntime']
			params: ['jade']
		)
		.pipe gulp.dest './src/main/webapp/js/templates/views'

gulp.task 'stylus', () ->
	gulp.src('./src/main/stylus/**/*.styl')
		.pipe(stylus
			use: [nib()]
		)
		.pipe(concat 'main.css')
		.pipe gulp.dest './src/main/webapp/css/'

gulp.task 'cssBuild', () ->
	gulp.src(['./src/main/webapp/css/styles.css', './custom/styles.css'])
		.pipe(cssConcat("styles#{timestamp}.css"))
		.pipe(minifyCss({keepSpecialComments: false}))
		.pipe gulp.dest './target/require-build/css'

gulp.task 'fontBuild', () ->
	gulp.src(['./src/main/webapp/css/fonts/**/*.*', './custom/fonts/**/*.*'])
		.pipe gulp.dest './target/require-build/css/fonts'

gulp.task 'copySprites', () ->
	gulp.src(['./src/main/webapp/css/libs/*.png', './src/main/webapp/css/libs/*.gif'])
		.pipe gulp.dest './target/require-build/css/libs'

gulp.task 'collectDependencies', () ->
	# (most) views and templates are loaded dynamically, App.coffee references DynamicDependencies so the build includes the files
	fs.writeFileSync 'src/main/webapp/js/app/DynamicDependencies.coffee'
	gulp.src('./src/main/webapp/js/app/DynamicDependencies.coffee')
		.pipe(insert.transform (contents) ->
			content = 'define(['
			for file, index in collect('./src/main/webapp/js/views')
				if index isnt 0
					content += ', '
				content += "'" + file.replace('./src/main/webapp/js/', 'cs!').replace('.coffee', '') + "'"
			for file, index in collect('./src/main/webapp/js/templates')
				content += ", '" + file.replace('./src/main/webapp/js/', '').replace('.js', '') + "'"
			content += '], () -> )'
			return content
		)
		.pipe gulp.dest './src/main/webapp/js/app'

gulp.task 'modifyIndexHtml', () ->
	# replace styles.css and main.js with timestamp filename
	path = if fs.existsSync('./custom/index.html') then './custom/index.html' else './src/main/webapp/index.html'
	gulp.src(path)
		.pipe(insert.transform (contents) ->
			content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"')
			content = content.replace(' data-main="js/main"', '')
			content = content.replace('src="js/libs/require.js"', 'src="js/main' + timestamp + '.js"')
			content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>')
			return content
		)
		.pipe gulp.dest './target/require-build'

gulp.task 'modifyLoginHtml', () ->
	# replace styles-login.css with timestamp filename
	path = if fs.existsSync('./custom/login.html') then './custom/login.html' else './src/main/webapp/login.html'
	gulp.src(path)
		.pipe(insert.transform (contents) ->
			content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"')
			content = content.replace('js/libs/jquery', 'js/jquery')
			content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>')
			return content
		)
		.pipe gulp.dest './target/require-build'

gulp.task 'modifyImprintHtml', () ->
	# replace styles-login.css with timestamp filename
	path = if fs.existsSync('./custom/imprint.html') then './custom/imprint.html' else './src/main/webapp/imprint.html'
	gulp.src(path)
		.pipe(insert.transform (contents) ->
			content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"')
			content = content.replace('js/libs/jquery', 'js/jquery')
			content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>')
			return content
		)
		.pipe gulp.dest './target/require-build'

gulp.task 'modfiyCustomPublicHtml', () ->
	# replace styles-login.css with timestamp filename
	gulp.src('./custom/index_public.html')
		.pipe(insert.transform (contents) ->
			content = contents.replace('href="css/styles.css"', 'href="css/styles' + timestamp + '.css"')
			content = content.replace('js/libs/jquery', 'js/jquery')
			content = content.replace('<base href="/"/>', '<base href="' + params.contextPath + '"/>')
			return content
		)
		.pipe gulp.dest './target/require-build'

gulp.task 'copyCustomImages', () ->
	gulp.src('./custom/images')
		.pipe gulp.dest './target/require-build/images'

gulp.task 'copyJQueryForLogin', () ->
	gulp.src('./src/main/webapp/js/libs/jquery.js')
		.pipe gulp.dest './target/require-build/js'

gulp.task 'jsBuild', () ->
	rjs(
		baseUrl: 'src/main/webapp/js'
		mainConfigFile: 'src/main/webapp/js/main.js'
		out: "main#{timestamp}.js"
		name: 'main'
		findNestedDependencies: false
		include: ['requireLib']
		stubModules: ['cs', 'coffee-script']
		insertRequire: ['main']
	)
	.pipe(uglify
		output: 
			ascii_only: true
	)
	.pipe gulp.dest './target/require-build/js'