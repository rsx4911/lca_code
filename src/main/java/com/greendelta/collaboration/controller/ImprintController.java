package com.greendelta.collaboration.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.service.SettingsService;

@RestController
@RequestMapping("ws/public/imprint")
public class ImprintController {

	private final SettingsService settings;

	public ImprintController(SettingsService settings) {
		this.settings = settings;
	}

	@GetMapping
	public Map<String, Object> get() {
		return settings.imprint.toMap();
	}

}
