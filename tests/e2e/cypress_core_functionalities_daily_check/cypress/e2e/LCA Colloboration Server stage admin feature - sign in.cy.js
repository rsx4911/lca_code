describe('Stage admin feature', () => {
  it('Should be able to signin', () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // Log the error to the console
      console.error("Error:", err);
      // Prevent the error from failing the test
      return false;
    });

    // Get credentials from environment config
    const { username, password } = Cypress.env('credentials');

    // Visit the base URL (from cypress.config.js)
    cy.visit('/');

    // Go to login page
    cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(5) > a > span').click();
    cy.url().should('contain', '/login');

    // Perform login
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login > button').click();

    // Verify successful login
    cy.findAllByText("Latest activities").should("be.visible");
  });
});
