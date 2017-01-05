package websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.inject.Inject;
import com.greendelta.collaboration.service.UpgradeService;

@ServerEndpoint(value = "/sockets/admin/upgrade", configurator = WebsocketConfigurator.class)
public class UpgradeEndpoint extends ProgressWebsocket {

	private final UpgradeService upgradeService;

	@Inject
	public UpgradeEndpoint(UpgradeService upgradeService) {
		this.upgradeService = upgradeService;
	}

	protected void run(Session session) {
		if (!upgradeService.upgradeAvailable()) {
			send(session, "Another update is already running", 0d);
			close(session);
			return;
		}
		upgradeService.upgrade(getMonitor(session));
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		super.onOpen(session, config);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		super.onMessage(message, session);
	}

	@OnClose
	public void onClose(Session session) {
		super.onClose(session);
	}

}
