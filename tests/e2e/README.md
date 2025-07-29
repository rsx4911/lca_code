# LCA Commons Core Admin Features Cypress Check - Stage Environment

This repository subfolder contains the source code of the Cypress testing scripts for the LCA Commons Core Admin Functionalities. 

The purpose for this Cypress UI Testing is to ensure it loads with the expected UI, and mimic the user behavior to ensure the core functionalities are working as expected.

## First time set up
To run a Cypress project, you will need the following prerequisites:

Node.js: Cypress is a Node.js library, so you will need to install Node.js on your machine. You can download Node.js from the official website: https://nodejs.org/en/download/
(The project is developed using node v18.9.0 )

Cypress: Once you have Node.js installed, you can install Cypress by running the following command in your terminal after cloning the project:

#### Setting the project

```bash
cd app (LCA Commons stage server) or ~\nal-lca-repo-application\tests\e2e\cypress_core_functionalities_daily_check (local)
```

Run NPM to install node modules
```bash
npm install
```

To run the Cypress Testing only
```bash
npm run cypress:run 
```

To run the Cypress Testing with report
```bash
npx cypress run --reporter mochawesome
```

To manually run the Cypress Testing
```bash
npx cypress open
```

Cypress Testing report location
```bash
~\nal-lca-repo-application\tests\e2e\cypress_core_functionalities_daily_check\report
```
