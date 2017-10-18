package com.greendelta.collaboration.webservice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.model.index.IndexAction;

@Path("public/cached")
public class CachedResource {

	private final static Logger log = LogManager.getLogger(CachedResource.class);
	private final static Map<String, BufferedImage> imageCache = new HashMap<>();

	@GET
	@Path("overlay/{modelType}/{overlayType}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getOverlayedImage(
			@PathParam("modelType") ModelType modelType,
			@PathParam("overlayType") IndexAction overlayType,
			@QueryParam("category") @DefaultValue("false") boolean category,
			@Context HttpServletRequest request) {
		if (overlayType == IndexAction.UPDATE || overlayType == null)
			return Respond.badRequest();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedImage image = getModelImage(request, modelType, category);
			BufferedImage overlay = getOverlayImage(request, overlayType);
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);
			ImageIO.write(combined, "PNG", out);
			return Respond.ok(out.toByteArray());
		} catch (IOException e) {
			log.error("Error loading overlay image", e);
			return Respond.notFound();
		}
	}

	private BufferedImage getModelImage(HttpServletRequest request, ModelType type, boolean category)
			throws IOException {
		String subPath = "/model/small/";
		if (category) {
			subPath += "category/";
		}
		subPath += type.name().toLowerCase() + ".png";
		if (imageCache.containsKey(subPath))
			return imageCache.get(subPath);
		String path = getImageBaseUrl(request) + subPath;
		BufferedImage image = ImageIO.read(new URL(path).openStream());
		imageCache.put(subPath, image);
		return image;
	}

	private BufferedImage getOverlayImage(HttpServletRequest request, IndexAction action) throws IOException {
		String subPath = "/model/" + action.name().toLowerCase() + ".png";
		if (imageCache.containsKey(subPath))
			return imageCache.get(subPath);
		String path = getImageBaseUrl(request) + subPath;
		BufferedImage image = ImageIO.read(new URL(path).openStream());
		imageCache.put(subPath, image);
		return image;
	}

	private String getImageBaseUrl(HttpServletRequest request) {
		String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
		if (Strings.notEmpty(request.getContextPath())) {
			path += request.getContextPath() + "/";
		}
		return path + "images";
	}

}
