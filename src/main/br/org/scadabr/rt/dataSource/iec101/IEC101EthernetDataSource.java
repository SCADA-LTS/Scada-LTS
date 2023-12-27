package br.org.scadabr.rt.dataSource.iec101;

import br.org.scadabr.protocol.iec101.session.IECConfig;
import br.org.scadabr.vo.dataSource.iec101.IEC101EthernetDataSourceVO;

public class IEC101EthernetDataSource extends IEC101DataSource {

	private final IEC101EthernetDataSourceVO configuration;

	public IEC101EthernetDataSource(IEC101EthernetDataSourceVO configuration) {
		super(configuration);
		this.configuration = configuration;
	}

	@Override
	public void initialize() {
		IECConfig config = new IECConfig(configuration.getHost(), configuration
				.getPort());
		config.setLinkAddress(configuration.getLinkLayerAddress());
		config.setLinkAddressSize((byte) configuration
				.getLinkLayerAddressSize());
		config
				.setObjectAddressSize((byte) configuration
						.getObjectAddressSize());
		config.setAsduAddressSize((byte) configuration.getAsduAddressSize());
		config.setCotSize((byte) configuration.getCotSize());

		IEC101Master iec101Master = new IEC101Master();
		iec101Master.setConfiguration(config);
		super.initialize(iec101Master);
	}

}
