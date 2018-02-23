package com.socioseer.restapp.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socioseer.common.dto.Response;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class ApplicationFilter implements Filter {

	@Value("${socio.seer.auth.header}")
	private String authHeader;

	@Value("${socio.seer.secret.key}")
	private String secretKey;

	private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public void destroy() {

	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		try {
			validateSecretToken(httpRequest.getHeader(authHeader));
		} catch (Exception e) {
			writeErrorToResponse(e, response);
			return;
		}
		filterChain.doFilter(httpRequest, response);
	}

	private void validateSecretToken(String secretKey) {

		if (StringUtils.isEmpty(secretKey)) {
			log.info("No secret token found in header, hence cannot process request.");
			throw new IllegalArgumentException(
					"No secret token found in header, hence cannot process request.");
		}

		if (!this.secretKey.equals(secretKey)) {
			log.info("Secret key in header does not matches to configured secret key.");
			throw new IllegalArgumentException(
					"Secret key in header does not matches to configured secret key.");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	private void writeErrorToResponse(Exception e, ServletResponse servletResponse)
			throws IOException {

		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(),
				e.getMessage(), null);

		try (PrintWriter writer = servletResponse.getWriter()) {
			writer.write(OBJECT_MAPPER.writeValueAsString(response));
			writer.flush();
		}

	}

}
