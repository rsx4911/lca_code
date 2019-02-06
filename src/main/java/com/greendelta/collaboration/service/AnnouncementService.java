package com.greendelta.collaboration.service;

import java.util.UUID;

import org.openlca.util.Strings;

import com.google.inject.Inject;
import com.greendelta.collaboration.model.Setting.Key;

public class AnnouncementService {

	private final SettingsService settingsService;
	
	@Inject
	public AnnouncementService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}
	
	public void announce(String message) {
		settingsService.set(Key.ANNOUNCEMENT_ID, UUID.randomUUID().toString());
		settingsService.set(Key.ANNOUNCEMENT_MESSAGE, message);
	}

	public void clear() {
		settingsService.set(Key.ANNOUNCEMENT_ID, null);
		settingsService.set(Key.ANNOUNCEMENT_MESSAGE, null);		
	}
	
	public Announcement getAnnouncement() {
		String id = settingsService.get(Key.ANNOUNCEMENT_ID);
		String message = settingsService.get(Key.ANNOUNCEMENT_MESSAGE);
		if (Strings.nullOrEmpty(id) || Strings.nullOrEmpty(message))
			return null;
		Announcement announcement = new Announcement();
		announcement.id = id;
		announcement.message = message;
		return announcement;
	}
	
	public class Announcement {
		
		public String id;
		public String message;
		
	}
	
}
