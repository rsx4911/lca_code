package com.greendelta.collaboration.webservice;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

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
			@Context HttpServletRequest request) {
		return getOverlayedImage(request, modelType, overlayType, null, false);
	}
	
	@GET
	@Path("overlay/CATEGORY/{modelType}/{overlayType}")
	public Response getOverlayedCategoryImage(
			@PathParam("modelType") ModelType modelType,
			@PathParam("overlayType") IndexAction overlayType,
			@Context HttpServletRequest request) {
		return getOverlayedImage(request, modelType, overlayType, null, true);
	}

	@GET
	@Path("overlay/FLOW/{flowType}/{overlayType}")
	public Response getOverlayedFlowImage(@PathParam("flowType") FlowType flowType,
			@PathParam("overlayType") IndexAction overlayType,
			@Context HttpServletRequest request) {
		return getOverlayedImage(request, ModelType.FLOW, overlayType, flowType.name(), false);
	}

	@GET
	@Path("overlay/PROCESS/{processType}/{overlayType}")
	public Response getOverlayedProcessImage(
			@PathParam("processType") ProcessType processType,
			@PathParam("overlayType") IndexAction overlayType,
			@Context HttpServletRequest request) {
		return getOverlayedImage(request, ModelType.PROCESS, overlayType, processType.name(), false);
	}

	private Response getOverlayedImage(HttpServletRequest request, ModelType modelType, IndexAction overlayType,
			String subType, boolean category) {
		if (overlayType == IndexAction.UPDATE || overlayType == null)
			return Respond.badRequest();
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedImage image = getModelImage(request, modelType.name(), mapSubType(modelType, subType), category);
			BufferedImage overlay = getOverlayImage(request, overlayType);
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);
			ImageIO.write(combined, "PNG", out);
			return Respond.status(Status.OK, out.toByteArray(), getCacheControl());
		} catch (IOException e) {
			log.error("Error loading overlay image", e);
			return Respond.notFound();
		}
	}

	private CacheControl getCacheControl() {
		CacheControl cacheControl = new CacheControl();
		cacheControl.setPrivate(true);
		cacheControl.setMaxAge(31536000);
		return cacheControl;
	}

	private String mapSubType(ModelType type, String subType) {
		if (subType == null)
			return null;
		if (type == ModelType.PROCESS && ProcessType.valueOf(subType) == ProcessType.LCI_RESULT)
			return "system";
		if (type == ModelType.FLOW)
			switch (FlowType.valueOf(subType)) {
			case ELEMENTARY_FLOW:
				return "elementary";
			case PRODUCT_FLOW:
				return "product";
			case WASTE_FLOW:
				return "waste";
			}
		return null;
	}

	private BufferedImage getModelImage(HttpServletRequest request, String type, String subType, boolean category)
			throws IOException {
		String path = "/images/model/small/";
		if (category) {
			path += "category/";
		}
		path += type.toLowerCase();
		if (subType != null) {
			path += "_" + subType.toLowerCase();
		}
		path += ".png";
		return getImage(request, path);
	}

	private BufferedImage getOverlayImage(HttpServletRequest request, IndexAction action) throws IOException {
		String path = "/images/model/" + action.name().toLowerCase() + ".png";
		return getImage(request, path);
	}

	private BufferedImage getImage(HttpServletRequest request, String path) throws IOException {
		if (imageCache.containsKey(path))
			return imageCache.get(path);
		BufferedImage image = ImageIO.read(request.getServletContext().getResourceAsStream(path));
		imageCache.put(path, image);
		return image;
	}

}
