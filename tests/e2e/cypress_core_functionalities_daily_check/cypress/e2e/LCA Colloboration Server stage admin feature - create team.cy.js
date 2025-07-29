describe('Stage admin feature', () => {
  it('Should be able to create team', () => {
    cy.on("uncaught:exception", (err, runnable) => {
      // Log the error to the console
      console.error("Error:", err);
      // Prevent the error from failing the test
      return false;
    });

    // Pull variables from Cypress environment
    const { username, password } = Cypress.env('credentials');
    const { teamname, name } = Cypress.env('testTeam');

    // Visit the base collaboration site (baseUrl from cypress.config.js)
    cy.visit('/');

    // Navigate to login
    cy.get('#block-bootstrap-lca-main-menu > ul > li:nth-child(7) > a > span').click();
    cy.url().should('contain', '/login');

    // Log in
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('#login > button').click();
    cy.findAllByText("Latest activities").should("be.visible");

    // Navigate to Teams admin area
    cy.get('#user-menu > div:nth-child(2) > div > div.default-menu > a:nth-child(4) > span').click();
    cy.url().should('contain', '/administration/overview');
    cy.get('#main > div > div > div > div.content-box.overview > div.two-columns > div:nth-child(4) > div.header-box > button').click();
    cy.url().should('contain', '/administration/team/new');

    // Fill out team creation form
    cy.get('#teamname').type(teamname);
    cy.get('#name').type(name);
    cy.get('#team-form > button').click();

    // Verify creation and go to team profile
    cy.url().should('contain', '/administration/overview');
    cy.findAllByText(name).should("be.visible").click();
    cy.url().should('contain', `/administration/team/profile/${teamname}`);

    // Delete the created team
    cy.get('#main > div > div > div > div > div.team-info > div:nth-child(1) > button').click();
    cy.get('#confirmation-phrase').type(teamname);
    cy.get('#btn-confirm-delete').click();
    cy.url().should('contain', '/administration/overview');
  });
});
