package br.org.scadabr.rt.dataSource.dnp3;

import java.util.Date;

import br.org.scadabr.vo.dataSource.dnp3.Dnp3IpDataSourceVO;

import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;

public class Dnp3IpDataSource extends Dnp3DataSource {

	private static final Log LOG = LogFactory.getLog(Dnp3IpDataSource.class);
	private final Dnp3IpDataSourceVO configuration;

	public Dnp3IpDataSource(Dnp3IpDataSourceVO configuration) {
		super(configuration);
		this.configuration = configuration;
	}

	@Override
	public void initialize() {
		// inicializa DnpMaster com os parametros IP.
		DNP3Master dnp3Master = new DNP3Master();
		try {
			dnp3Master.initEthernet(configuration.getSourceAddress(),
					configuration.getSlaveAddress(), configuration.getHost(),
					configuration.getPort(), configuration
							.getStaticPollPeriods());
			resetUnreliableDataPoints(dataPoints);
		} catch (Exception e) {
			setUnreliableDataPoints(dataPoints);
			LOG.error(e.getMessage(), e);
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, new Date().getTime(), true,
					new LocalizableMessage("event.exception2", configuration
							.getName(), e.getMessage()));
		}

		super.initialize(dnp3Master);
	}
}