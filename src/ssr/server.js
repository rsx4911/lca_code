var puppeteer = require('puppeteer');
var url = require('url');
var http = require('http');

var parseParameters = function(req) {
    var url_parts = url.parse(req.url, true);
    var route = url_parts.pathname;
    var query = url_parts.query;
    var sessionid = query.sessionid;
    var params = Object.keys(query);
    for (var i = 0; i < params.length; i++) {
        if (params[i] === 'sessionid' || params[i] === 'scheme')
            continue;
        route += route.indexOf('?') !== -1 ? '&' : '?';
        route += params[i] + '=' + query[params[i]];
    }
    return { route: route, sessionid: sessionid, scheme: query.scheme }
};

var getContent = async function(browser, params) {
    var page = await browser.newPage();
    await page.setCookie({
        domain: 'localhost',
        name: 'JSESSIONID',
        value: params.sessionid
    });
    await page.goto(params.scheme + '://localhost' + params.route, { waitUntil: 'networkidle0' });
    return await page.content();
}

var replaceBaseHref = function(html) {
    var start = html.indexOf('<base href=');
    var end = html.indexOf('>', start);
    var before = html.substring(0, start + 11);
    var after = html.substring(end);
    return before + '"."' + after;
}

var removeScriptTags = function(html) {
    while (html.indexOf('<script') !== -1) {
        before = html.substring(0, html.indexOf('<script'));
        after = html.substring(html.indexOf('</script') + 9);
        html = before + after;
    }
    return html;
}

var addLibraryTags = function(html) {
    before = html.substring(0, html.indexOf('</body>'));
    after = html.substring(html.indexOf('</body>'));
    html = before;
    html += '<script src="js/jquery.js"></script>';
    html += '<script src="js/bootstrap.js"></script>';
    html += '<script>$(".toggle-control").on("click", function(event) { event.preventDefault ? event.preventDefault() : event.returnValue = false; var parent = $(event.target || event.srcElement || event.originalTarget).parent(); $("> .toggle-control, > .toggleable, > table > tbody > tr.toggleable", parent).toggle() })</script>';
    html += after;
    return html;
}

http.createServer(async function (req, res) {
    res.writeHead(200, {'Content-Type': 'text/html'});
    if (req.url.indexOf('standalone=true') === -1) {
        res.end('');
        return;
    }
    var params = parseParameters(req);
    if (!params.sessionid)
        return;
    var browser = await puppeteer.launch();
    var html = await getContent(browser, params);
    html = replaceBaseHref(html);
    html = removeScriptTags(html);
    html = addLibraryTags(html);
    await browser.close();
    res.end(html);
}).listen(3000);
