package org.scada_lts.dao.migration.postgresql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_2__PostgresSetViewSizeProperties extends BaseJavaMigration {

	private static final Logger LOG = LoggerFactory.getLogger(V1_2__PostgresSetViewSizeProperties.class);

	@Override
	public void migrate(Context context) throws Exception {

		final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();


		final String setViewSizeProperties = ""
				+ "alter table mangoviews " +
				"ADD COLUMN width INTEGER,"+
				"ADD COLUMN height INTEGER;";

		try {
			jdbcTemplate.execute(setViewSizeProperties);
		} catch (Exception e) {
			LOG.warn("Column width/height already exists, skipping.");
		}
	}

}
