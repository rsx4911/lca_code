package com.greendelta.collaboration.platform.guice;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

@Singleton
public final class PersistFilter implements Filter {

	private final UnitOfWork unitOfWork;
	private final PersistService persistService;

	@Inject
	public PersistFilter(UnitOfWork unitOfWork, PersistService persistService) {
		this.unitOfWork = unitOfWork;
		this.persistService = persistService;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		persistService.start();
	}

	public void destroy() {
		persistService.stop();
	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		// TODO check if this can be solved differently
		// websockets will run outside of the servlet context so we can't ensure
		// that unit of work will not begin twice before end call, this way we
		// work around the internal check and only call begin if end was called
		// before.
		// Checking the current implementation of JpaPersistService it will not
		// lead to problems. One issue could be that begin is called, then a
		// second request comes in (does not call begin) but the first request
		// calls end before the second request is finished. This is not an issue
		// because when the entity manager is null it will call begin
		// automatically
		if (((Provider<EntityManager>) unitOfWork).get() == null)
			unitOfWork.begin();
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			unitOfWork.end();
		}
	}
}
