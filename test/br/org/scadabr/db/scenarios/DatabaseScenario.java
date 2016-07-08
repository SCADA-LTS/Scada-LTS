package br.org.scadabr.db.scenarios;

import com.serotonin.mango.db.DatabaseAccess;

public interface DatabaseScenario {

	public void setupScenario(DatabaseAccess database);

}
