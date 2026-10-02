package cl.overrid3.boilerplate;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	@Test
	void verificaLaEstructuraModular() {
		ApplicationModules.of(BoilerplateApplication.class).verify();
	}

}
