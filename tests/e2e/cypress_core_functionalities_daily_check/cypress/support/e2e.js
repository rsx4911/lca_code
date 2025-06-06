// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

// Verify Download 
// https://www.npmjs.com/package/cy-verify-downloads
Cypress.config("execTimeout", 30000);
require('cy-verify-downloads').addCustomCommand();
const { NodeModulesPolyfillPlugin } = require('@esbuild-plugins/node-modules-polyfill');
import "cypress-mochawesome-reporter/register";
Cypress.Commands.add("text", { prevSubject: "element" }, (subject, options) => {
  if (options === "innerText") {
    return subject[0].innerText;
  } else {
    return subject.text();
  }
});