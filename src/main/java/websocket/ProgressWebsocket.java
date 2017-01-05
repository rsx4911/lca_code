package websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.greendelta.collaboration.util.IProgressMonitor;

abstract class ProgressWebsocket {

	private static final Logger log = LoggerFactory.getLogger(ProgressWebsocket.class);
	private static final Map<String, ProgressMonitor> monitors = new HashMap<>();
	private static final Map<String, Subject> subjects = new HashMap<>();

	protected void onOpen(Session session, EndpointConfig config) {
		subjects.put(session.getId(), (Subject) config.getUserProperties().get("subject"));
	}

	protected void onMessage(String message, Session session) {
		switch (message) {
		case "start":
			new Thread(() -> run(session)).start();
			break;
		case "cancel":
			IProgressMonitor monitor = monitors.get(session.getId());
			if (monitor != null)
				monitor.cancel();
			break;
		}
	}

	protected abstract void run(Session session);

	protected void onClose(Session session) {
		monitors.remove(session.getId());
		subjects.remove(session.getId());
	}

	protected ProgressMonitor getMonitor(Session session) {
		if (monitors.containsKey(session.getId()))
			return monitors.get(session.getId());
		ProgressMonitor monitor = new ProgressMonitor(session);
		monitors.put(session.getId(), monitor);
		return monitor;
	}

	protected Subject getSubject(Session session) {
		return subjects.get(session.getId());
	}

	protected void send(Session session, String message, double progress) {
		if (!session.isOpen())
			return;
		Map<String, Object> data = new HashMap<>();
		data.put("message", message);
		data.put("progress", progress);
		String json = new Gson().toJson(data);
		session.getAsyncRemote().sendText(json);
	}

	protected void close(Session session) {
		if (!session.isOpen())
			return;
		try {
			session.close();
		} catch (IOException e) {
			log.error("Error closing session", e);
		}
	}

	class ProgressMonitor implements IProgressMonitor {

		private final Session session;
		private boolean canceled;
		private int progress;
		private int total;

		ProgressMonitor(Session session) {
			this.session = session;
		}

		@Override
		public void started(int total) {
			this.total = total;
		}

		@Override
		public void task(String name) {
			send(session, name, progress / (double) total);
		}

		@Override
		public void worked() {
			progress++;
		}

		@Override
		public boolean canceled() {
			return canceled;
		}

		@Override
		public void cancel() {
			canceled = true;
		}

		@Override
		public void done() {
			String message = "Done";
			if (canceled && progress < total)
				message = "Canceled";
			send(session, message, progress / (double) total);
			close(session);
		}

	}

}
