package com.greendelta.collaboration.webservice.setup;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;

import com.greendelta.collaboration.installer.Input;

public class Setup extends Input {

	public String esVersion = "6.2.0";
	public File tomcatDir;
	public int tomcatPort;
	public String repository = "test/test";

	public Setup() throws IOException {
		File tmp = Files.createTempDirectory("collab-test-server-").toFile();
		this.dbDir = new File(tmp, "database");
		this.libDir = new File(tmp, "libraries");
		this.repoDir = new File(tmp, "repositories");
		this.tomcatDir = new File(tmp, "tomcat");
		this.tomcatPort = getAvailablePort(8080);
		this.searchCluster = "es-test";
		this.searchHost = "localhost";
		this.searchIndex = "collab-test";
		this.searchPort = getAvailablePort(9200);
		while (!isPortAvailable(this.searchPort + 100)) {
			this.searchPort = getAvailablePort(this.searchPort + 1);
		}
		this.adminEmail = "doesnt@matter.com";
		this.adminPassword = "irrelevant";
	}

	public String getBaseUrl() {
		return "http://localhost:" + this.tomcatPort + "/ws";
	}

	public String getUrl(String part) {
		return getBaseUrl() + "/" + part;
	}

	private int getAvailablePort(int port) {
		while (!isPortAvailable(port))
			port++;
		return port;
	}

	private boolean isPortAvailable(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (ds != null) {
				ds.close();
			}
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
