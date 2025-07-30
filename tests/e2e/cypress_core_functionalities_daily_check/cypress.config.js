const { defineConfig } = require("cypress");
const { downloadFile } = require("cypress-downloadfile/lib/addPlugin");
const { verifyDownloadTasks } = require("cy-verify-downloads");

module.exports = defineConfig({
  video: false,
  trashAssetsBeforeRuns: true,
  stopOnExit: true,

  reporter: "cypress-mochawesome-reporter",
  reporterOptions: {
    reportDir: "cypress/reports",
    html: true,
    json: true,
    reportFilename: "[status]_[datetime]-[name]-report",
    timestamp: "longDate",
    reportPageTitle: "Cypress Inline Reporter",
    embeddedScreenshots: true,
    inlineAssets: true,
  },

  downloadsFolder: "cypress/downloads",

  e2e: {
    baseUrl: "http://localhost:8080/lca-collaboration/", // ✅ Correct location for baseUrl in v10+

    defaultCommandTimeout: 12000,

    setupNodeEvents(on, config) {
      require("cypress-mochawesome-reporter/plugin")(on);
      on("task", { downloadFile });
      on("task", verifyDownloadTasks);
      console.log(config); // Debug: see the full config
    },

    env: {
      credentials: {
        username: "administrator",
        password: "admin",
      },
      testGroup: {
        name: "binquantestgroup",
      },
      testTeam: {
        teamname: "binquantestteamname",
        name: "binquantestteam",
      },
      testUser: {
        username: "binquantestusername",
        name: "abinquantestname",
        email: "binquan.wang@dsfederal.com",
      },
      testRepository: {
        name: "testadministratorrepo",
        group: "administrator",
        label: "labeladministrator",
        confirmation: "administrator/testadministratorrepo",
      },
      downloadFiles: [
        "Argonne_National_Lab-By_Product_Hydrogen.zip",
        "National_Energy_Technology_Lab-Coal_extraction.zip"
      ]
    }
  }
});
