package com.greendelta.collaboration.webservice.setup;

public class TestServer {

	private final Setup setup;
	private final Elasticsearch elastic = new Elasticsearch();
	private final Tomcat tomcat = new Tomcat();

	public TestServer(Setup setup) {
		this.setup = setup;
	}

	public void start() throws Exception {
		System.out.println("Starting elastic at port " + setup.searchPort);
		elastic.start(setup);
		System.out.println("Initializing data");
		Data.init(setup);	
		System.out.println("Starting tomcat at port " + setup.tomcatPort);
		tomcat.start(setup);
	}

	public void stop() throws Exception {
		System.out.println("Stopping tomcat");
		try {
			tomcat.stop();
		} catch (Exception e) {
			System.out.println("Error stopping tomcat: " + e.getMessage());
		}
		System.out.println("Clearing data");
		try {
			Data.clear(setup);
		} catch (Exception e) {
			System.out.println("Error clearing data: " + e.getMessage());
		}
		System.out.println("Stopping elastic");
		try {
			elastic.stop();
		} catch (Exception e) {
			System.out.println("Error stopping elastic: " + e.getMessage());
		}
		System.out.println("Done");
	}

}
