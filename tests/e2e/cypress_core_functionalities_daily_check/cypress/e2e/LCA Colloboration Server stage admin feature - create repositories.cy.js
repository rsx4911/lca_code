describe('Stage admin feature', () => {
  it('Should be able to create repositories', () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // Log the error to the console
      console.error("Error:", err);
      // Prevent the error from failing the test
      return false;
    });

    // Pull test data from Cypress environment
    const { username, password } = Cypress.env('credentials');
    const { name, group, label, confirmation } = Cypress.env('testRepository');

    // Visit the base URL (from cypress.config.js -> e2e.baseUrl)
    cy.visit('/');

    // Navigate to login
    cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(5) > a > span').click();
    cy.url().should('contain', '/login');

    // Log in
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login > button').click();
    cy.findAllByText("Latest activities").should("be.visible");

    // Go to Repositories Dashboard
    cy.get('#user-menu > div:nth-child(2) > div > div.default-menu > a:nth-child(2) > span').click();
    cy.url().should('contain', '/dashboard/repositories');

    // Start creating a repository
    cy.get('#main > div > div > div > div.header-box > div > span > button').click();
    cy.get('#main > div > div > div > div.header-box > div > span > ul > li:nth-child(1)').click();
    cy.url().should('contain', '/repository/new');

    // Fill out the repository form
    cy.get('#name').type(name);
    cy.get('#group').type(group);
    cy.get('#repository-form > button').click();

    // Validate the repository was created
    cy.url().should('contain', `/${username}/${name}`);
    cy.get('#label').type(label);

    // Delete the repository
    cy.get('#main > div > div > div > div:nth-child(1) > div > div > button.btn.btn-lg.btn-danger.pull-right').click();
    cy.get('#confirmation-phrase').type(confirmation);
    cy.get('#btn-confirm-delete').click();
    cy.url().should('contain', '/dashboard/repositories');
  });
});
