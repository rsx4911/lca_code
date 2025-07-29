describe('Stage admin feature', () => {
  it('Should be able to create user', () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // Log the error to the console
      console.error("Error:", err);
      // Prevent the error from failing the test
      return false;
    });

    // Extract environment variables
    const { username, password } = Cypress.env('credentials');
    const { username: newUsername, name, email } = Cypress.env('testUser');

    // Visit base URL
    cy.visit('/');

    // Go to login page
    cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(7) > a > span').click();
    cy.url().should('contain', '/login');

    // Perform login
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login > button').click();
    cy.findAllByText("Latest activities").should("be.visible");

    // Navigate to User Management
    cy.get('#user-menu > div:nth-child(2) > div > div.default-menu > a:nth-child(4) > span').click();
    cy.url().should('contain', '/administration/overview');

    // Click "Add User" button
    cy.get('#main > div > div > div > div.content-box.overview > div.two-columns > div:nth-child(2) > div.header-box > button').click();
    cy.url().should('contain', '/administration/user/new');

    // Fill in user form
    cy.get('#username').type(newUsername);
    cy.get('#name').type(name);
    cy.get('#email').type(email);
    cy.get('#user-form > button').click();

    // Validate creation and go to profile
    cy.url().should('contain', '/administration/overview');
    cy.findAllByText(name).should("be.visible").click();
    cy.url().should('contain', `/administration/user/profile/${newUsername}`);

    // Delete the created user
    cy.get('#main > div > div > div > div:nth-child(1) > button:nth-child(2)').click();
    cy.get('#confirmation-phrase').type(newUsername);
    cy.get('#btn-confirm-delete').click();
    cy.url().should('contain', '/administration/overview');
  });
});
