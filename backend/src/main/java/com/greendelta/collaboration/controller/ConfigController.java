package com.greendelta.collaboration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.util.Routes;

@RestController
@RequestMapping("ws/public/config")
public class ConfigController {

	@GetMapping("userRoutes")
	public String[] getUserRoutes() {
		return Routes.getUserRoutes();
	}

}
