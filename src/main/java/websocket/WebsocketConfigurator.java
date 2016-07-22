package websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import org.apache.shiro.SecurityUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class WebsocketConfigurator extends Configurator {

	@Inject
	private static Injector injector;

	public <T> T getEndpointInstance(Class<T> endpointClass) {
		return injector.getInstance(endpointClass);
	}

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		config.getUserProperties().put("subject", SecurityUtils.getSubject());
	}

}