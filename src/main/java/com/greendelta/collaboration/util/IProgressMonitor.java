package com.greendelta.collaboration.util;

public interface IProgressMonitor {

	void started(int total);

	void task(String name);

	void worked();

	boolean canceled();

	void cancel();

	void done();

}
