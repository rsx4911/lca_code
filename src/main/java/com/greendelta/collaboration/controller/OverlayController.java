package com.greendelta.collaboration.controller;

import java.awt.RenderingHints;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
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
	private ServletContext context;

	@GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImageWithOverlay(
			@RequestParam(name = "image", required = true) String image,
			@RequestParam(name = "overlay", required = true) String overlay) {
		try {
			var icon = context.getResourceAsStream(image);
			var overlayIcon = context.getResourceAsStream(overlay);
			if (icon == null || overlayIcon == null)
				throw Response.notFound();
			return getImageWithOverlay(icon, overlayIcon);
		} catch (IOException e) {
			throw Response.error("Error overlaying image");
		}
	}

	private byte[] getImageWithOverlay(InputStream imageStream, InputStream overlayStream) throws IOException {
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
