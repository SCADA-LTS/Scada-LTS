package br.org.scadabr.db;

import static java.io.File.*;
import static org.apache.commons.io.FileUtils.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.serotonin.mango.Common;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractWebContentDependentTest extends
		AbstractServletContextDependentTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File envProperties;
	protected File oracle11gEnvProperties;
	protected File mySqlEnvProperties;
	protected File derbyEnvProperties;
	protected File classesFolder;

	@Before
	public void setupServletContextStubBehavior() {
		when(getServletContextStub().getRealPath(anyString())).thenReturn(
				folder.getRoot().getAbsolutePath());
	}

	@Before
	public void setupFolder() throws IOException {
		classesFolder = new File(folder.getRoot(), "WEB-INF" + separator
				+ "classes");
		classesFolder.mkdirs();

		envProperties = getResourceAsFile("resources/env.properties");
		oracle11gEnvProperties = getResourceAsFile("resources/env.properties.oracle11g");
		mySqlEnvProperties = getResourceAsFile("resources/env.properties.mysql");
		derbyEnvProperties = getResourceAsFile("resources/env.properties.derby");

		copyFileToDirectory(envProperties, classesFolder);
		copyFileToDirectory(oracle11gEnvProperties, classesFolder);
		copyFileToDirectory(mySqlEnvProperties, classesFolder);
		copyFileToDirectory(derbyEnvProperties, classesFolder);
	}

	@Before
	public void useTestEnvironment() {
		Common.changeEnvironmentProfile("br/org/scadabr/db/resources/env");
	}

	@Before
	public void mockServletGetResourceAsStream() {
		when(getServletContextStub().getResourceAsStream(anyString()))
				.thenAnswer(new GetResourceAsStreamStub());

	}

	protected File getResourceAsFile(String resource) {
		return toFile(AbstractWebContentDependentTest.class
				.getResource(resource));
	}

	private class GetResourceAsStreamStub implements Answer<InputStream> {
		@Override
		public InputStream answer(InvocationOnMock invocation) throws Throwable {
			String resourceName = (String) invocation.getArguments()[0];

			final File resourceInTemporaryFolder = new File(folder.getRoot(),
					resourceName);
			final File resourceInWebRoot = new File("WebContent", resourceName);

			if (resourceInTemporaryFolder.exists()) {
				return new FileInputStream(resourceInTemporaryFolder);
			} else if (resourceInWebRoot.exists()) {
				return new FileInputStream(resourceInWebRoot);
			}
			return null;
		}
	}

}
