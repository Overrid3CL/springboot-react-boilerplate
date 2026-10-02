package cl.overrid3.boilerplate;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Sirve la SPA desde el jar. Las rutas del cliente que no son archivos caen en index.html.
 * /api y /actuator las resuelven los controladores, no este handler.
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/static/")
				.resourceChain(true)
				.addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {
						Resource requested = location.createRelative(resourcePath);
						if (requested.exists() && requested.isReadable()) {
							return requested;
						}
						if (resourcePath.startsWith("api/") || resourcePath.startsWith("actuator/")
								|| resourcePath.startsWith("v3/") || resourcePath.startsWith("swagger-ui")
								|| resourcePath.contains(".")) {
							return null;
						}
						Resource index = location.createRelative("index.html");
						if (index.exists() && index.isReadable()) {
							return index;
						}
						return null;
					}
				});
	}

}
