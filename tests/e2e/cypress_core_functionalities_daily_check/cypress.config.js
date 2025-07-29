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
    json: false,
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
        username: "binquanw",
        password: "SHN98dUh@kPH5bk4",
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
        name: "test",
        group: "binquan wang",
        label: "labeltest",
        confirmation: "binquanw/test",
      },
      downloadFiles: [
        "National_Renewable_Energy_Laboratory-USLCI_Database_Public.zip",
        "Federal_LCA_Commons-US_electricity_baseline.zip",
        "NIST-construction_materials.zip",
        "US_Forest_Service_Forest_Products_Lab-Woody_biomass.zip",
        "US_Environmental_Protection_Agency-USEEIO_v2.zip",
        "Federal_LCA_Commons-elementary_flow_list.zip"
      ]
    }
  }
});
