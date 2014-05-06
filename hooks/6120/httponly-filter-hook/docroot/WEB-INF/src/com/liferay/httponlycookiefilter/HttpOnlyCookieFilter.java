package com.liferay.httponlycookiefilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

public class HttpOnlyCookieFilter implements Filter {

	private boolean setHttpOnlyFlag = false;
	private boolean setSecureFlag = false;

	private class ResponseWrapper extends HttpServletResponseWrapper {

		public ResponseWrapper(HttpServletResponse response) {

			super(response);
		}

		@Override
		public void addCookie(Cookie cookie) {

			cookie.setHttpOnly(setHttpOnlyFlag);
			cookie.setSecure(setSecureFlag);
			super.addCookie(cookie);

		}
	}

	public void destroy() {
	}

	public void doFilter(
		ServletRequest request, ServletResponse response,
		FilterChain filterChain)
		throws IOException, ServletException {

		ResponseWrapper wrappedResponse =
			new ResponseWrapper((HttpServletResponse) response);

		filterChain.doFilter(request, wrappedResponse);
	}

	public void init(FilterConfig filterConfig) {
		if ("true".equals(filterConfig.getInitParameter("httponly"))) {
			setHttpOnlyFlag = true;
		}

		if ("true".equals(filterConfig.getInitParameter("secure"))) {
			setSecureFlag = true;
		}
	}
}
