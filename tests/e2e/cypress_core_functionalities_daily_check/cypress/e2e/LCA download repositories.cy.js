describe('Inspect the repository download function1', () => {
    it('Should be able to download files without timeout issues', () => {
      // Visit the website
      cy.visit("https://www.lcacommons.gov/");
      
      // Click on the link to navigate to the download section
      cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
      
      // Verify navigation to the download section
      cy.url().should('contain', 'https://www.lcacommons.gov/lca-collaboration/');
  
      // Click on the download button for the first repository
      cy.get('.pinned-repository:nth-child(1) .glyphicon').click();
  
      // Verify the download
      cy.verifyDownload('Federal_LCA_Commons-US_electricity_baseline.zip', { timeout: 120000, interval: 600 }).then(() => {
        cy.log('**Confirmed downloaded Federal_LCA_Commons-US_electricity_baseline ZIP**');
      });
    });
  });
  

describe('Inspect the repository download function2', () => {
    it('Should be able to download files without timeout issues', () => {
       
          cy.visit('https://www.lcacommons.gov/');
          cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
          cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(2) .glyphicon').click();
    //cy.wait(20000);
    cy.verifyDownload('Federal_LCA_Commons-elementary_flow_list.zip', { timeout: 1200000, interval: 600 }).then(() => {
        cy.log('**Confirmed downloaded Federal_LCA_Commons-elementary_flow_list ZIP**');
      });
    //cy.verifyDownload('Federal_LCA_Commons-elementary_flow_list.zip', { timeout: 60000, interval: 600 });
   // cy.log('**confirm downloaded Federal_LCA_Commons-elementary_flow_list ZIP**')    
});
});


describe('Inspect the repository download function3', () => {
    it('Should be able to download files without timeout issues', () => {
        cy.visit(
            "https://www.lcacommons.gov/"
          );
          cy.visit('https://www.lcacommons.gov/');
          cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
          cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(3) .glyphicon').click();
    cy.verifyDownload('Federal_LCA_Commons-Fed_Commons_core_database.zip', { timeout: 25000, interval: 600 });
    cy.log('**confirm downloaded Federal_LCA_Commons-Fed_Commons_core_database ZIP**')    
});
});
describe('Inspect the repository download function4', () => {
    it('Should be able to download files without timeout issues', () => {
        cy.visit(
            "https://www.lcacommons.gov/"
          );
          cy.visit('https://www.lcacommons.gov/');
          cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
          cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(4) .glyphicon').click();
    cy.verifyDownload('Federal_LCA_Commons-ReCiPe.zip', { timeout: 1200000, interval: 600 }).then(() => {
        cy.log('**Confirmed downloaded Federal_LCA_Commons-ReCiPe ZIP**');
      });
   // cy.verifyDownload('Federal_LCA_Commons-ReCiPe.zip', { timeout: 25000, interval: 600 });
   // cy.log('**confirm downloaded Federal_LCA_Commons-ReCiPe ZIP**')    
});
});
describe('Inspect the repository download function5', () => {
    it('Should be able to download files without timeout issues', () => {
        cy.visit('https://www.lcacommons.gov/');
        cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
        cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(5) .glyphicon').click();
    cy.verifyDownload('Federal_Highway_Administration-mtu_pavement.zip', { timeout: 1200000, interval: 600 }).then(() => {
        cy.log('**Confirmed downloaded Federal_Highway_Administration-mtu_pavement ZIP**');
      });
   // cy.verifyDownload('Federal_Highway_Administration-mtu_pavement.zip', { timeout: 25000, interval: 600 });
    //cy.log('**confirm downloaded Federal_Highway_Administration-mtu_pavement**')    
});
});
describe('Inspect the repository download function6', () => {
    it('Should be able to download files without timeout issues', () => {
        cy.visit('https://www.lcacommons.gov/');
        cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
        cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(6) .glyphicon').click();
    cy.verifyDownload('National_Renewable_Energy_Laboratory-USLCI_Database_Public.zip', { timeout: 1250000, interval: 600 });
    cy.log('**confirm downloaded National_Renewable_Energy_Laboratory-USLCI_Database_Public**')    
});
});
/*describe('Inspect the repository download function7', () => {
    it('Should be able to download files without timeout issues', () => {
        cy.visit('https://www.lcacommons.gov/');
        cy.get('#main-content > div > article > div > div > div > section.block.block-layout-builder.block-inline-blockhero.hero.clearfix > div.hero__callout.hero__callout--bg-transparent.hero__callout--left > div.field.field--name-hero-link.field--type-link.field--label-hidden.field--item > a').click();
        cy.url().should('contains', 'https://www.lcacommons.gov/lca-collaboration/');
    cy.get('.pinned-repository:nth-child(7) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(8) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(9) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(10) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(11) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(12) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(13) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(14) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(15) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(16) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(17) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(18) .glyphicon').click();
    cy.get('.pinned-repository:nth-child(19) .glyphicon').click();
    
});
  });
}); */
