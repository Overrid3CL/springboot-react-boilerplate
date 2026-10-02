package cl.overrid3.boilerplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import cl.overrid3.boilerplate.support.TestJwts;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class ApiSecurityIT {

	private static final Path OPENAPI_SNAPSHOT = Path.of("..", "frontend", "openapi", "openapi.json");

	private final HttpClient http = HttpClient.newHttpClient();

	@LocalServerPort
	private int port;

	@DynamicPropertySource
	static void workOs(DynamicPropertyRegistry registry) {
		registry.add("app.workos.jwks-uri", TestJwts::jwksUri);
	}

	@Test
	void rechazaLaApiSinToken() throws Exception {
		HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/notes")).GET().build());

		assertThat(response.statusCode()).isEqualTo(401);
		assertThat(response.headers().firstValue("content-type").orElse("")).contains("application/problem+json");
		assertThat(response.body()).contains("No autorizado");
	}

	@Test
	void aceptaUnJwtValido() throws Exception {
		HttpResponse<String> response = send(
				authorized(HttpRequest.newBuilder(uri("/api/notes")).GET(), "user_empty", "org_empty"));

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).isEqualTo("[]");
	}

	@Test
	void unaOrganizacionNoLeeDatosDeOtra() throws Exception {
		HttpResponse<String> created = send(authorized(HttpRequest.newBuilder(uri("/api/notes"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"Secreto\",\"body\":\"solo org A\"}")),
				"user_a", "org_a"));
		assertThat(created.statusCode()).isEqualTo(201);
		String noteId = JsonPath.read(created.body(), "$.id");

		HttpResponse<String> foreignList = send(authorized(HttpRequest.newBuilder(uri("/api/notes")).GET(), "user_b", "org_b"));
		assertThat(foreignList.statusCode()).isEqualTo(200);
		assertThat(foreignList.body()).isEqualTo("[]");

		HttpResponse<String> foreignGet = send(
				authorized(HttpRequest.newBuilder(uri("/api/notes/" + noteId)).GET(), "user_b", "org_b"));
		assertThat(foreignGet.statusCode()).isEqualTo(404);

		HttpResponse<String> ownerGet = send(
				authorized(HttpRequest.newBuilder(uri("/api/notes/" + noteId)).GET(), "user_a", "org_a"));
		assertThat(ownerGet.statusCode()).isEqualTo(200);
		assertThat(JsonPath.read(ownerGet.body(), "$.title").toString()).isEqualTo("Secreto");
	}

	@Test
	void publicaLaConfiguracionSinToken() throws Exception {
		HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/config")).GET().build());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(JsonPath.read(response.body(), "$.clientId").toString()).isEqualTo("client_test");
	}

	@Test
	void elContratoOpenApiEstaAlDia() throws Exception {
		HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/v3/api-docs")).GET().build());
		assertThat(response.statusCode()).isEqualTo(200);

		ObjectMapper mapper = JsonMapper.builder().build();
		JsonNode actual = normalize(mapper.readTree(response.body()));
		String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual) + "\n";

		if (Boolean.getBoolean("openapi.export")) {
			Files.createDirectories(OPENAPI_SNAPSHOT.getParent());
			Files.writeString(OPENAPI_SNAPSHOT, pretty);
			return;
		}

		assertThat(Files.exists(OPENAPI_SNAPSHOT))
				.as("Falta frontend/openapi/openapi.json. Ejecuta ./gradlew exportOpenApi")
				.isTrue();
		JsonNode expected = mapper.readTree(Files.readString(OPENAPI_SNAPSHOT));
		assertThat(actual).isEqualTo(expected);
	}

	private HttpRequest authorized(HttpRequest.Builder builder, String subject, String organizationId) {
		return builder.header("Authorization", "Bearer " + TestJwts.token(subject, organizationId)).build();
	}

	private HttpResponse<String> send(HttpRequest request) throws Exception {
		return http.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private URI uri(String path) {
		return URI.create("http://127.0.0.1:" + port + path);
	}

	private static JsonNode normalize(JsonNode root) {
		if (root instanceof ObjectNode object) {
			object.remove("servers");
		}
		return root;
	}

}
