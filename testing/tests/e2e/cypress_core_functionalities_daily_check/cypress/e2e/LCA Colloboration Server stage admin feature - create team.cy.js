describe('Stage admin feature', () => {
    it('Should be able to create repositories', () => {
      cy.on("uncaught:exception", (err, runnable) => {
        // Log the error to the console
        console.error("Error:", err);

        // Return false to prevent the error from
        // failing the test
        return false;
      });
      // Visit the website
      cy.visit("https://lca-stage.nal.usda.gov/lca-collaboration/");

       cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(5) > a > span').click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/login');
       cy.get('#username').type('binquanw');
       cy.get('#password').type('TCl!KPjZV07JE4V0');
       cy.get('#login > button').click();
       cy.findAllByText("Latest activities").should("be.visible");
       cy.get('#user-menu > div:nth-child(2) > div > div.default-menu > a:nth-child(2) > span').click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/administration/overview');
       cy.get('#main > div > div > div > div.content-box.overview > div.two-columns > div:nth-child(4) > div.header-box > button').click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/administration/team/new');
       cy.get('#teamname').type('binquantestteamname');
       cy.get('#name').type('binquantestteam');
       cy.get('#team-form > button').click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/administration/overview');
       cy.findAllByText("binquantestteam").should("be.visible").click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/administration/team/profile/binquantestteamname');
       cy.get('#main > div > div > div > div > div.team-info > div:nth-child(1) > button').click();
       cy.get('#confirmation-phrase').type('binquantestteamname');
       cy.get('#btn-confirm-delete').click();
       cy.url().should('contain', 'https://lca-stage.nal.usda.gov/lca-collaboration/administration/overview');
    });
  });
  

