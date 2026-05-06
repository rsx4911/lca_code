/**
 * LCA Menu Sync
 * Version: 1.0.0
 *
 * Fetches the main navigation menu from the Drupal landing site's
 * built-in linkset endpoint and renders it on the Collaboration Server,
 * keeping both sites' menus in sync automatically.
 *
 * Drupal endpoint: /system/menu/main/linkset
 *
 * INSTALLATION:
 *   1. Add id="lca-main-menu" to the <ul> in index_public.html
 *   2. Add <script src="js/menu-sync.js"></script> before </body>
 *
 * DEPENDENCIES: None (vanilla JS). Renders Bootstrap 3 markup
 *   compatible with the existing navbar CSS.
 *
 * FALLBACK: If the fetch fails for any reason (network error, Drupal
 *   down, auth failure), the existing static menu HTML remains untouched.
 *
 * CORS: Not required in production — both sites are served under
 *   https://www.lcacommons.gov (same origin).
 */

(function () {
  'use strict';

  // =========================================================================
  // CONFIGURATION
  // =========================================================================
  var CONFIG = {
    // Drupal linkset endpoint (same-origin, no CORS needed)
    apiUrl: '/lca-collaboration/api/drupal-menu', //stage/prod Drupal Homepage URL - use dev path as an example since only dev's linkset is enabled

    // CSS selector for the <ul> that holds the navigation items.
    // The element MUST exist in index_public.html with this id.
    menuSelector: '#main-menu-shell',

    // Base URL prepended to relative menu-item hrefs so that links
    // on the Collaboration Server point back to the Drupal landing site.
    drupalBaseUrl: 'https://lca-drupal-stage.nal.usda.gov', //stage/prod Drupal Homepage URL - - use dev path as an example since only dev's linkset is enabled

    // OAuth2 — leave null unless David enables authentication on the
    // linkset endpoint.  When set, the script will request a Bearer
    // token via client_credentials grant before fetching the menu.
    tokenUrl: null,     // e.g. 'https://www.lcacommons.gov/oauth/token'
    clientId: null,     // e.g. 'collab-server'
    clientSecret: null  // e.g. 'secret-value'
  };

  // =========================================================================
  // INTERNALS — no configuration below this line
  // =========================================================================

  /** Cached OAuth2 access token (populated only when auth is configured). */
  var authToken = null;

  /**
   * Entry point — called once the DOM is ready.
   *
   * 1. Locate the menu container element.
   * 2. Optionally obtain an OAuth2 token.
   * 3. Fetch the linkset JSON from Drupal.
   * 4. Parse it into a tree and render Bootstrap-compatible HTML.
   *
   * If any step fails the catch handler fires and the original static
   * menu remains visible (graceful degradation).
   */
  function init() {
    var menuContainer = document.querySelector(CONFIG.menuSelector);
    if (!menuContainer) {
      console.warn('[menu-sync] Container not found:', CONFIG.menuSelector);
      return;
    }

    if (CONFIG.tokenUrl && CONFIG.clientId) {
      getAuthToken()
        .then(function () { return fetchMenu(); })
        .then(function (items) { renderMenu(menuContainer, items); })
        .catch(handleError);
    } else {
      fetchMenu()
        .then(function (items) { renderMenu(menuContainer, items); })
        .catch(handleError);
    }
  }

  // ---------------------------------------------------------------------------
  // OAuth2 (optional)
  // ---------------------------------------------------------------------------

  /**
   * Obtain a Bearer token using the OAuth2 client_credentials grant.
   *
   * The token is stored in the module-scoped `authToken` variable and
   * attached to the subsequent linkset request as an Authorization header.
   *
   * @returns {Promise<void>}
   */
  function getAuthToken() {
    return fetch(CONFIG.tokenUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body:
        'grant_type=client_credentials' +
        '&client_id=' + encodeURIComponent(CONFIG.clientId) +
        '&client_secret=' + encodeURIComponent(CONFIG.clientSecret)
    })
      .then(function (response) {
        if (!response.ok) throw new Error('Auth failed: HTTP ' + response.status);
        return response.json();
      })
      .then(function (data) {
        authToken = data.access_token;
      });
  }

  // ---------------------------------------------------------------------------
  // Fetch
  // ---------------------------------------------------------------------------

  /**
   * Fetch the linkset JSON and parse it into a menu tree.
   *
   * @returns {Promise<Array>} Nested menu items sorted by position.
   */
  function fetchMenu() {
    var headers = { Accept: 'application/json' };
    if (authToken) {
      headers['Authorization'] = 'Bearer ' + authToken;
    }

    return fetch(CONFIG.apiUrl, { headers: headers })
      .then(function (response) {
        if (!response.ok) throw new Error('Fetch failed: HTTP ' + response.status);
        return response.json();
      })
      .then(function (data) {
        return parseLinkset(data);
      });
  }

  // ---------------------------------------------------------------------------
  // Parse
  // ---------------------------------------------------------------------------

  /**
   * Convert the Drupal linkset response into a nested menu structure.
   *
   * Drupal's linkset format represents hierarchy via an array of position
   * strings on each item:
   *
   *   hierarchy: ["0"]      → top-level item at position 0
   *   hierarchy: ["2","0"]  → first child of the top-level item at position 2
   *   hierarchy: ["2","1"]  → second child of the top-level item at position 2
   *
   * This parser supports two levels (parent + children), which matches the
   * current Collaboration Server menu design.  Deeper levels are ignored.
   *
   * @param   {Object} data  Raw JSON from /system/menu/main/linkset
   * @returns {Array}        Sorted array of { title, href, position, children[] }
   */
  function parseLinkset(data) {
    var items = data.linkset[0].item;
    var menuTree = [];
    var topLevelMap = {};

    // First pass — collect top-level items (hierarchy length === 1)
    items.forEach(function (item) {
      if (item.hierarchy.length === 1) {
        var menuItem = {
          title: item.title,
          href: item.href,
          position: parseInt(item.hierarchy[0], 10),
          children: []
        };
        menuTree.push(menuItem);
        topLevelMap[item.hierarchy[0]] = menuItem;
      }
    });

    // Second pass — attach children to their parents (hierarchy length === 2)
    items.forEach(function (item) {
      if (item.hierarchy.length === 2) {
        var parent = topLevelMap[item.hierarchy[0]];
        if (parent) {
          parent.children.push({
            title: item.title,
            href: item.href,
            position: parseInt(item.hierarchy[1], 10)
          });
        }
      }
    });

    // Sort parents and children by their position index
    menuTree.sort(function (a, b) { return a.position - b.position; });
    menuTree.forEach(function (item) {
      if (item.children.length > 0) {
        item.children.sort(function (a, b) { return a.position - b.position; });
      }
    });

    return menuTree;
  }

  // ---------------------------------------------------------------------------
  // Render
  // ---------------------------------------------------------------------------

  /**
   * Replace the container's innerHTML with Bootstrap 3 navbar markup built
   * from the parsed menu tree.
   *
   * Generated classes match the existing hardcoded menu so that no CSS
   * changes are required:
   *   - <li class="expanded dropdown">   for parents with children
   *   - <a class="dropdown-toggle">      with data-toggle="dropdown"
   *   - <ul class="dropdown-menu">       for child lists
   *   - <li class="first"> / "last"      for first/last children
   *
   * A "Sign In" button is always appended as the final item.
   *
   * @param {HTMLElement} container  The <ul id="lca-main-menu"> element
   * @param {Array}       items     Sorted menu tree from parseLinkset()
   */
  function renderMenu(container, items) {
    var html = '';

    items.forEach(function (item, index) {
      var isFirst = index === 0;
      var isLast = index === items.length - 1;
      var hasChildren = item.children && item.children.length > 0;
      var href = resolveUrl(item.href);

      if (hasChildren) {
        // Parent with dropdown children
        var parentClasses = ['expanded', 'dropdown'];
        if (isFirst) parentClasses.unshift('first');
        html += '<li class="' + parentClasses.join(' ') + '">';
        html += '<a href="#" class="dropdown-toggle" data-toggle="dropdown">';
        html += escapeHtml(item.title) + ' <span class="caret"></span></a>';
        html += '<ul class="dropdown-menu">';

        item.children.forEach(function (child, ci) {
          var childHref = resolveUrl(child.href);
          var childClasses = [];
          if (ci === 0) childClasses.push('first');
          if (ci === item.children.length - 1) childClasses.push('last');

          html += '<li' + (childClasses.length ? ' class="' + childClasses.join(' ') + '"' : '') + '>';
          html += '<a href="' + childHref + '" target="_blank">';
          html += escapeHtml(child.title) + '</a></li>';
        });

        html += '</ul></li>';
      } else {
        // Leaf item — "Home" (href === "/") opens in same tab; others open
        // in a new tab so the user stays on the Collaboration Server.
        var target = (item.href === '/' || item.href === '') ? '_self' : '_blank';
        html += '<li' + (isFirst ? ' class="first"' : '') + '>';
        html += '<a href="' + href + '" target="' + target + '">';
        html += escapeHtml(item.title) + '</a></li>';
      }
    });

    // Append the Sign In button (always last)
    html += '<li class="last">';
    html += '<a class="login default-button" href="login"><span>Sign In</span></a>';
    html += '</li>';

    container.innerHTML = html;
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  /**
   * Resolve a Drupal path to a full URL using the configured base.
   *
   *   "/"                → "https://www.lcacommons.gov"
   *   "/about"           → "https://www.lcacommons.gov/about"
   *   "https://ext.com"  → "https://ext.com"  (unchanged)
   *   ""                 → "#"
   *
   * @param   {string} href  The href value from the linkset item
   * @returns {string}       Absolute URL safe for use in an <a> tag
   */
  function resolveUrl(href) {
    if (!href || href === '') return '#';
    if (href.indexOf('http://') === 0 || href.indexOf('https://') === 0) return href;
    if (href === '/') return CONFIG.drupalBaseUrl;
    if (href.charAt(0) === '/') return CONFIG.drupalBaseUrl + href;
    return CONFIG.drupalBaseUrl + '/' + href;
  }

  /**
   * HTML-escape a string to prevent XSS when inserting menu titles into
   * innerHTML.  Uses the browser's own text encoding via a temporary
   * DOM element.
   *
   * @param   {string} text  Untrusted text (menu item title from Drupal)
   * @returns {string}       Safe HTML string
   */
  function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Error handler — logs to console but takes no destructive action.
   * The static HTML menu that was in the page before this script ran
   * remains visible, so the site is never left without navigation.
   *
   * @param {Error} error
   */
  function handleError(error) {
    console.error('[menu-sync] Failed to sync menu:', error.message);
    // The existing static menu remains in place — no action needed.
  }

  // ---------------------------------------------------------------------------
  // Bootstrap
  // ---------------------------------------------------------------------------

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
