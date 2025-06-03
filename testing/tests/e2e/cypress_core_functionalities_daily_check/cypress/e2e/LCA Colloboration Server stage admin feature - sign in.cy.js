describe('Stage admin feature', () => {
    it('Should be able to signin', () => {
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
    });
  });
  

