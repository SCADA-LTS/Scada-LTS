package br.org.scadabr.db.configuration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import br.org.scadabr.db.AbstractWebContentDependentTest;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationDBTest extends AbstractWebContentDependentTest {

	@Test
	public void useOracle11gDB_shouldReplaceEnvPropertiesWithOracle11gVersion()
			throws IOException {
		ConfigurationDB.useOracle11gDB();

		assertTrue(FileUtils.contentEquals(oracle11gEnvProperties, new File(
				classesFolder, "env.properties.oracle11g")));
	}

	@Test
	public void useMySQL_shouldReplaceEnvPropertiesWithMySQLVersion()
			throws IOException {
		ConfigurationDB.useMysqlDB();

		assertTrue(FileUtils.contentEquals(mySqlEnvProperties, new File(
				classesFolder, "env.properties.mysql")));
	}

	@Test
	public void useDerby_shouldReplaceEnvPropertiesWithDerbyVersion()
			throws IOException {
		ConfigurationDB.useDerbyDB();

		assertTrue(FileUtils.contentEquals(derbyEnvProperties, new File(
				classesFolder, "env.properties.derby")));
	}
}
