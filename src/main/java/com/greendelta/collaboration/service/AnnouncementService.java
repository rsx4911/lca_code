package com.greendelta.collaboration.service;

import java.util.UUID;

import org.openlca.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greendelta.collaboration.model.settings.ServerSetting;

@Service
public class AnnouncementService {

	private final SettingsService settingsService;

	@Autowired
	public AnnouncementService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	public void announce(String message) {
		settingsService.set(ServerSetting.ANNOUNCEMENT_ID, UUID.randomUUID().toString());
		settingsService.set(ServerSetting.ANNOUNCEMENT_MESSAGE, message);
	}

	public void clear() {
		settingsService.set(ServerSetting.ANNOUNCEMENT_ID, null);
		settingsService.set(ServerSetting.ANNOUNCEMENT_MESSAGE, null);
	}

	public Announcement getAnnouncement() {
		String id = settingsService.get(ServerSetting.ANNOUNCEMENT_ID);
		String message = settingsService.get(ServerSetting.ANNOUNCEMENT_MESSAGE);
		if (Strings.nullOrEmpty(id) || Strings.nullOrEmpty(message))
			return null;
		return new Announcement(id, message);
	}

	public record Announcement(String id, String message) {

	}

}
