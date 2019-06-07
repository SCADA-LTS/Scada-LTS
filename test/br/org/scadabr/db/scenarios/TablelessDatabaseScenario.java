package br.org.scadabr.db.scenarios;

import static org.junit.Assert.*;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.db.ScriptRunner;

public class TablelessDatabaseScenario extends AbstractDatabaseScenario
		implements RequireInitializationAfterSetup {

	@Override
	public void setupScenario(DatabaseAccess database) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			fail("MySQL driver not found");
		}
		try {
			Connection con = DriverManager.getConnection(Common
					.getEnvironmentProfile().getString("db.url"), Common
					.getEnvironmentProfile().getString("db.username"), Common
					.getEnvironmentProfile().getString("db.password"));

			ScriptRunner sr = new ScriptRunner(con, false, false);
			Reader reader = getScriptCommands("scripts/drop-mysql.sql");
			sr.runScript(reader);
		} catch (Exception e) {
			throw new ShouldNeverHappenException(e);
		}
	}
}
