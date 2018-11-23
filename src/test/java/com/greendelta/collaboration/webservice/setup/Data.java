package com.greendelta.collaboration.webservice.setup;

import org.openlca.util.Dirs;

import com.greendelta.collaboration.installer.Database;
import com.greendelta.collaboration.installer.LibraryDir;
import com.greendelta.collaboration.installer.RepositoryDir;
import com.greendelta.collaboration.installer.Search;

public class Data {

	public static void init(Setup setup) throws Exception {
		Database.init(setup);
		RepositoryDir.init(setup.repoDir);
		LibraryDir.init(setup.libDir);
		Search.init(setup.searchCluster, setup.searchHost, setup.searchPort, setup.searchIndex);
		setup.tomcatDir.mkdirs();
	}
	
	public static void clear(Setup setup) throws Exception {
		Dirs.delete(setup.repoDir.toPath());
		Dirs.delete(setup.libDir.toPath());
		Dirs.delete(setup.dbDir.toPath());
		Dirs.delete(setup.tomcatDir.toPath());
	}

}
