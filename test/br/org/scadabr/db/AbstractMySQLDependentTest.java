package br.org.scadabr.db;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import br.org.scadabr.db.scenarios.DatabaseScenario;
import br.org.scadabr.db.scenarios.RequireInitializationAfterSetup;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.MySQLAccess;

public abstract class AbstractMySQLDependentTest extends
		AbstractWebContentDependentTest {

	protected MySQLAccess mysqlAccess;

	@Before
	public void createMySQLAccess() throws SQLException {
		mysqlAccess = new MySQLAccess();
		this.putAttributeInServletContext(Common.ContextKeys.DATABASE_ACCESS,
				mysqlAccess);
	}

	@After
	public void shutdownIfNecessary() {
		if (mysqlAccess.getDataSource() != null) {
			mysqlAccess.terminate();
		}
	}

	protected final void useScenario(DatabaseScenario scenario) {
		boolean requireInitializationAfterSetup = scenario instanceof RequireInitializationAfterSetup;
		if (!requireInitializationAfterSetup) {
			mysqlAccess.initialize(this.getServletContextStub());
		}
		scenario.setupScenario(mysqlAccess);

		if (requireInitializationAfterSetup) {
			mysqlAccess.initialize(this.getServletContextStub());
		}
	}

}