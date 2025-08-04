package org.scada_lts.archiving;

import com.serotonin.mango.db.DatabaseAccess;

public class ArchiveQueryProviderFactory {

    private final DatabaseAccess databaseAccess;

    public ArchiveQueryProviderFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IArchiveQueryProvider newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        switch (databaseType) {
            case POSTGRES:
                return null; //TODO: CHANGE IT ON POSTGRES BRANCH + MAKE POSTGRES PROVIDER CLASS
            case MYSQL:
            default:
                return new MySQLArchiveQueryProvider();
        }
    }
}
