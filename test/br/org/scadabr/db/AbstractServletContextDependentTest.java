package br.org.scadabr.db;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.serotonin.mango.Common;
import com.serotonin.mango.web.ContextWrapper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractServletContextDependentTest {

	@Mock
	private ServletContext servletContext;

	private Map<String, Object> attributes;

	@Before
	public void setupServletContextStub() {
		Common.ctx = new ContextWrapper(servletContext);
		this.attributes = new HashMap<String, Object>();

		when(servletContext.getAttribute(anyString())).then(
				new ServletContexteGetAttributeStub());
	}

	@After
	public void contextWrapperCleanUp() {
		Common.ctx = null;
	}

	protected ServletContext getServletContextStub() {
		return servletContext;
	}

	protected ContextWrapper getContextWrapper() {
		return Common.ctx;
	}

	protected void putAttributeInServletContext(String key, Object object) {
		attributes.put(key, object);
	}

	private class ServletContexteGetAttributeStub implements Answer<Object> {
		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			String key = (String) invocation.getArguments()[0];
			return attributes.get(key);
		}
	}

}