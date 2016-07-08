package br.org.scadabr.db.scenarios;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ConnectionCallbackVoid;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.db.ScriptRunner;

public class DatalessDatabaseScenario extends AbstractDatabaseScenario {

	@Override
	public void setupScenario(DatabaseAccess database) {
		try {
			database.doInConnection(new ConnectionCallbackVoid() {

				@Override
				public void doInConnection(Connection conn) throws SQLException {
					ScriptRunner sr = new ScriptRunner(conn, false, false);
					Reader reader = getScriptCommands("scripts/resetDatabase-mysql.sql");
					try {
						sr.runScript(reader);
					} catch (IOException e) {
						fail("Error reading script");
					}
				}
			});
		} catch (Exception e) {
			throw new ShouldNeverHappenException(e);
		}
	}
}
