const { defineConfig } = require("cypress");
const {downloadFile} = require('cypress-downloadfile/lib/addPlugin')
const { verifyDownloadTasks } = require('cy-verify-downloads');

module.exports = defineConfig({
  // setupNodeEvents can be defined in either
  // the e2e or component configuration
  video: false,
  trashAssetsBeforeRuns: true,
  stopOnExit: true,
  // reporter: "mochawesome",
  reporter: "cypress-mochawesome-reporter",
  // reporter: "reporters/custom.js",
  reporterOptions: {
    // reportDir: "reporters/",
    // overwrite: false,
    reportDir: "cypress/reports",
    html: true,
    json: false,
    reportFilename: "[status]_[datetime]-[name]-report",
    timestamp: "longDate",
    reportPageTitle: "Cypress Inline Reporter",
    embeddedScreenshots: true,
    inlineAssets: true, //Adds the asserts inline
  },
  e2e: {
    defaultCommandTimeout: 12000,
    setupNodeEvents(on, config) {
      require("cypress-mochawesome-reporter/plugin")(on);
      on("task", { downloadFile });
      on("task", verifyDownloadTasks);
      console.log(config); // see everything in here!
    },
    env: {
      baseUrl: "https://www.lcacommons.gov/",
      aethinaTumida: "Aethina tumida",
    },
  },
  // Specify downloadsFolder here
  downloadsFolder: "cypress/downloads",
  
  setupNodeEvents(on, config) {
    require("cypress-mochawesome-reporter/plugin")(on);
    on("task", { downloadFile });
    on("task", verifyDownloadTasks);
    console.log(config); // see everything in here!
  },
});