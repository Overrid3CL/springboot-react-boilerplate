package cl.overrid3.boilerplate.support;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;

/**
 * JWKS local y JWT firmados en el test. No se llama a WorkOS.
 */
public final class TestJwts {

	private static final RSAKey KEY = generateKey();

	private static final HttpServer SERVER = startServer();

	private TestJwts() {
	}

	public static String jwksUri() {
		return "http://127.0.0.1:" + SERVER.getAddress().getPort() + "/jwks";
	}

	public static String token(String subject, String organizationId) {
		return token(subject, organizationId, "admin", true);
	}

	public static String token(String subject, String organizationId, String role, boolean includeAudience) {
		try {
			JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
					.issuer("https://api.workos.com")
					.subject(subject)
					.claim("client_id", "client_test")
					.claim("org_id", organizationId)
					.claim("role", role)
					.claim("roles", List.of(role))
					.claim("permissions", List.of("notes:read", "notes:write"))
					.issueTime(Date.from(Instant.now().minusSeconds(5)))
					.expirationTime(Date.from(Instant.now().plusSeconds(300)));
			if (includeAudience) {
				claims.audience("client_test");
			}
			SignedJWT jwt = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KEY.getKeyID()).type(JOSEObjectType.JWT).build(),
					claims.build());
			jwt.sign(new RSASSASigner(KEY));
			return jwt.serialize();
		}
		catch (JOSEException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private static RSAKey generateKey() {
		try {
			return new RSAKeyGenerator(2048).keyID("test-key").generate();
		}
		catch (JOSEException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static HttpServer startServer() {
		try {
			String jwks = new JWKSet(KEY.toPublicJWK()).toString();
			byte[] body = jwks.getBytes(StandardCharsets.UTF_8);
			HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
			server.createContext("/jwks", exchange -> {
				exchange.getResponseHeaders().set("Content-Type", "application/json");
				exchange.sendResponseHeaders(200, body.length);
				try (OutputStream output = exchange.getResponseBody()) {
					output.write(body);
				}
			});
			server.start();
			return server;
		}
		catch (IOException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

}
