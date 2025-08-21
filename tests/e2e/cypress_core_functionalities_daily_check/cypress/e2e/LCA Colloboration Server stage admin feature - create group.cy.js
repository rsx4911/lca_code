describe('Stage admin feature', () => {
  it('Should be able to create groups', () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // Log the error to the console
      console.error("Error:", err);
      // Prevent the error from failing the test
      return false;
    });

    // Get variables from Cypress environment
    const { username, password } = Cypress.env('credentials');
    const groupName = Cypress.env('testGroup').name;

    // Visit the base URL from cypress.config.js
    cy.visit('/');

    cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(5) > a > span').click();
    cy.url().should('contain', '/');

    // Log in
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login > button').click();
    cy.findAllByText("Latest activities").should("be.visible");

    // cy.get('body > nav > div > div.menu-left > ul > li.active > a > span').click()

    // Go to group dashboard
    cy.visit('/dashboard/groups');
    // cy.url().should('contain', '/administration/overview');

    // Create a group
    cy.get('#main > div > div > div > div.header-box > div > span > button').click();
    cy.url().should('contain', '/group/new');
    cy.get('#name').type(groupName);
    cy.get('#group-form > button').click();
    cy.url().should('contain', `/groups/${groupName}`);
    cy.findAllByText(groupName).should("be.visible");

    // Delete the group
    cy.get('#main > div > div > div > div > div:nth-child(1) > div:nth-child(1) > button').click();
    cy.get('#confirmation-phrase').type(groupName);
    cy.get('#btn-confirm-delete').click();
    cy.url().should('contain', '/dashboard/groups');
  });
});
