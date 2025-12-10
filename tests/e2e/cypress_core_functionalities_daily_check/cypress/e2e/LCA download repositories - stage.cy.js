const downloadFiles = Cypress.env('downloadFiles');

describe('Inspect the repository download function1', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(1) .glyphicon').click();
    cy.verifyDownload(downloadFiles[0], { timeout: 120000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[0]}`);
  });
});

describe('Inspect the repository download function2', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(2) .glyphicon').click();
    cy.verifyDownload(downloadFiles[1], { timeout: 1200000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[1]}`);
  });
});
/*
describe('Inspect the repository download function3', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(3) .glyphicon').click();
    cy.verifyDownload(downloadFiles[2], { timeout: 25000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[2]}`);
  });
});

describe('Inspect the repository download function4', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(4) .glyphicon').click();
    cy.verifyDownload(downloadFiles[3], { timeout: 1200000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[3]}`);
  });
});

describe('Inspect the repository download function5', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(5) .glyphicon').click();
    cy.verifyDownload(downloadFiles[4], { timeout: 1200000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[4]}`);
  });
});

describe('Inspect the repository download function6', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('/');
    cy.url().should('contain', 'lca-collaboration');
    cy.get('.pinned-repository:nth-child(6) .glyphicon').click();
    cy.verifyDownload(downloadFiles[5], { timeout: 1250000, interval: 600 });
    cy.log(`✅ Confirmed download: ${downloadFiles[5]}`);
  });
});


describe('Inspect the repository download function7', () => {
  it('Should be able to download files without timeout issues', () => {
    cy.visit('https://www.lcacommons.gov/');
    cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
    cy.url().should('contain', '/lca-collaboration');
    // Further .click() actions omitted
  });
});
*/
