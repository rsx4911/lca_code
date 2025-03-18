package com.greendelta.collaboration.service;

import java.util.UUID;

import org.openlca.util.Strings;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.ServerSetting;

@Service
public class AnnouncementService {

	private final SettingsService settings;

	public AnnouncementService(SettingsService settings) {
		this.settings = settings;
	}

	public void announce(String message) {
		settings.set(ServerSetting.ANNOUNCEMENT_ID, UUID.randomUUID().toString());
		settings.set(ServerSetting.ANNOUNCEMENT_MESSAGE, message);
	}

	public void clear() {
		settings.set(ServerSetting.ANNOUNCEMENT_ID, null);
		settings.set(ServerSetting.ANNOUNCEMENT_MESSAGE, null);
	}

	public Announcement getAnnouncement() {
		String id = settings.get(ServerSetting.ANNOUNCEMENT_ID);
		String message = settings.get(ServerSetting.ANNOUNCEMENT_MESSAGE);
		if (Strings.nullOrEmpty(id) || Strings.nullOrEmpty(message))
			return null;
		return new Announcement(id, message);
	}

	public record Announcement(String id, String message) {

	}

}
