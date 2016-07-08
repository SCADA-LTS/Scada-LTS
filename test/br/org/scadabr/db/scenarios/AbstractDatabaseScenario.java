package br.org.scadabr.db.scenarios;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public abstract class AbstractDatabaseScenario implements DatabaseScenario {

	protected final Reader getScriptCommands(String name) {
		final Class<?> scriptRelativeClass = scriptLoadingReferenceClass();

		File file = toFile(scriptRelativeClass.getResource(name));
		if (file == null) {
			fail("Could not read resource: " + name);
		}
		try {
			return new FileReader(file);
		} catch (IOException e) {
			fail("Could not read resource: " + name);
			return null;
		}
	}

	protected Class<?> scriptLoadingReferenceClass() {
		return DatabaseScenario.class;
	}

}
