package com.greendelta.collaboration.platform.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import com.greendelta.collaboration.webservice.admin.AdminAreaResource;

public class RequestListener implements ServletRequestListener {
	
	private static RequestListener instance;
	public volatile Integer openRequest = 0;
	
	public static RequestListener getInstance() {
		if (instance == null) {
			instance = new RequestListener();
		}
		return instance;
	}
	
	private RequestListener() {
		// singleton
	}
	
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		if (!isRelevant(event))
			return;
		synchronized (openRequest) {
			openRequest++;
		}
	}

	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		if (!isRelevant(event))
			return; // ignore websockets
		synchronized (openRequest) {
			openRequest--;
		}
	}
	
	private boolean isRelevant(ServletRequestEvent event)  {
		if (!(event.getServletRequest() instanceof HttpServletRequest))
			return false; // ignore websockets
		String path = ((HttpServletRequest) event.getServletRequest()).getServletPath();
		if (!path.startsWith("/ws/"))
			return false;
		if (path.equals("/ws/" + AdminAreaResource.SERVER_INFO_PATH))
			return false;
		return true;
	}
	
}
