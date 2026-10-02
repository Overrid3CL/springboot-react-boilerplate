package cl.overrid3.boilerplate.identity.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Escribe un Problem Detail (RFC 9457) sin depender del tipo concreto de Jackson.
 */
final class SecurityProblemWriter {

	private SecurityProblemWriter() {
	}

	static void write(HttpServletResponse response, HttpStatus status, String title, String detail) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		String body = """
				{"type":"about:blank","title":"%s","status":%d,"detail":"%s"}
				""".formatted(escape(title), status.value(), escape(detail)).trim();
		response.getWriter().write(body);
	}

	private static String escape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

}
