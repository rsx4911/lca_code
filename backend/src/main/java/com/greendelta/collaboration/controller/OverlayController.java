package com.greendelta.collaboration.controller;

import java.awt.RenderingHints;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greendelta.collaboration.controller.util.Response;

@RestController
@RequestMapping("ws/public/overlay")
public class OverlayController {

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImageWithOverlay(
			@RequestParam(required = true) String image,
			@RequestParam(required = true) String overlay) {
		try {
			var icon = getResource(image);
			var overlayIcon = getResource(overlay);
			if (icon == null || !icon.exists() || overlayIcon == null || !overlayIcon.exists())
				throw Response.notFound();
			return getImageWithOverlay(icon, overlayIcon);
		} catch (IOException e) {
			throw Response.error("Error overlaying image");
		}
	}

	private byte[] getImageWithOverlay(Resource imageRes, Resource overlayRes) throws IOException {
		try (var imageStream = imageRes.getInputStream();
				var overlayStream = overlayRes.getInputStream()) {
			var image = ImageIO.read(imageStream);
			var overlay = ImageIO.read(overlayStream);
			var g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);
			g.dispose();
			var out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			return out.toByteArray();
		}
	}

	private Resource getResource(String path) {
		var r = resourceLoader.getResource("classpath:static/" + path);
		if (r != null && r.exists())
			return r;
		return resourceLoader.getResource("/" + path);
	}
	
}
