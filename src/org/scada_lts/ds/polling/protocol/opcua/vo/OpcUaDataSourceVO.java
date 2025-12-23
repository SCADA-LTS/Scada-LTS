package org.scada_lts.ds.polling.protocol.opcua.vo;

import com.serotonin.json.*;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.ds.DataSourceUpdatable;
import org.scada_lts.ds.polling.PollingDataSourceRT;
import org.scada_lts.ds.polling.protocol.opcua.client.IOpcUaService;
import org.scada_lts.ds.polling.protocol.opcua.security.OpcUaMessageSecurityType;
import org.scada_lts.ds.polling.protocol.opcua.security.OpcUaSecurityPolicyType;
import org.scada_lts.utils.security.KeyStoreData;
import org.scada_lts.utils.security.KeyStoreType;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@JsonRemoteEntity
public class OpcUaDataSourceVO extends DataSourceVO<OpcUaDataSourceVO>
		implements DataSourceUpdatable<OpcUaDataSourceVO>, KeyStoreData {

	public static final Type TYPE = Type.OPC_UA;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				PollingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
		eventTypes.add(createEventType(
				PollingDataSourceRT.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				PollingDataSourceRT.POINT_READ_ALL_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointReadAll")));
		eventTypes.add(createEventType(
				PollingDataSourceRT.POINT_WRITE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointWrite")));
		eventTypes.add(createEventType(
				PollingDataSourceRT.POINT_UPDATE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointUpdate")));
		eventTypes.add(createEventType(
				PollingDataSourceRT.UPDATE_TIME_EXCEEDED_UPDATE_PERIOD_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.updateTimeExceededUpdatePeriod")));
	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(PollingDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(PollingDataSourceRT.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
		EVENT_CODES.addElement(PollingDataSourceRT.POINT_WRITE_EXCEPTION_EVENT,
				"POINT_WRITE_EXCEPTION");
		EVENT_CODES.addElement(PollingDataSourceRT.POINT_READ_ALL_EXCEPTION_EVENT,
				"POINT_READ_ALL_EXCEPTION");
		EVENT_CODES.addElement(PollingDataSourceRT.POINT_UPDATE_EXCEPTION_EVENT,
				"POINT_UPDATE_EXCEPTION");
		EVENT_CODES.addElement(PollingDataSourceRT.UPDATE_TIME_EXCEEDED_UPDATE_PERIOD_EXCEPTION_EVENT,
				"UPDATE_EXECUTED_LONGER_UPDATE_PERIOD_EXCEPTION");

	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new PollingDataSourceRT(this, IOpcUaService.newService(this));
	}

	@Override
	public PointLocatorVO createPointLocator() {
		return new OpcUaPointLocatorVO();
	}

	@Override
	public LocalizableMessage getConnectionDescription() {
		return new LocalizableMessage("common.default", this.serverName);
	}

	@Override
	public ExportCodes getEventCodes() {
		return EVENT_CODES;
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	private int updatePeriodType = Common.TimePeriods.SECONDS;
	@JsonRemoteProperty
	private int updatePeriods = 1;
	@JsonRemoteProperty
	private boolean quantize;

	@JsonRemoteProperty
	private int serverPort = 4840;
	@JsonRemoteProperty
	private String serverHost = "localhost";
	@JsonRemoteProperty
	private String serverName = "";
	@JsonRemoteProperty
	private String serverPath = "";

	/*
    @ConfigurationParameter("username")
    @Description("A username to authenticate to the OPCUA server with.")

     */
	@JsonRemoteProperty
	private String user;

	/*
    @ConfigurationParameter("password")
    @Description("A password to authenticate to the OPCUA server with.")

     */
	@JsonRemoteProperty
	private String password;

	/*
    @ConfigurationParameter("security-policy")
    @StringDefaultValue("NONE")
    @Description("The security policy applied to communication channel between driver and OPC UA server.\n" +
        "Default value assumes. Possible options are `NONE`, `Basic128Rsa15`, `Basic256`, `Basic256Sha256`, `Aes128_Sha256_RsaOaep`, `Aes256_Sha256_RsaPss`.")

     */
	private OpcUaSecurityPolicyType securityPolicy = OpcUaSecurityPolicyType.NONE;

	/*
    @ConfigurationParameter("message-security")
    @StringDefaultValue("SIGN_ENCRYPT")
    @Description("The security policy applied to messages exchanged after handshake phase.\n" +
        "Possible options are `NONE`, `SIGN`, `SIGN_ENCRYPT`.\n" +
        "This option is effective only when `securityPolicy` turns encryption (anything beyond `NONE`).")

     */
	private OpcUaMessageSecurityType messageSecurity = OpcUaMessageSecurityType.NONE;

	/*
    @ConfigurationParameter("key-store-file")
    @Description("The Keystore file used to lookup client certificate and its private key.")

     */
	@JsonRemoteProperty
	private String keyStoreFile = Common.getHomeDir() + File.separator + "security" + File.separator + "keystore.jks";

	/*
    @ConfigurationParameter("key-store-type")
    @StringDefaultValue("pkcs12")
    @Description("Keystore type used to access keystore and private key, defaults to PKCS (for Java 11+).\n" +
        "Possible values are between others `jks`, `pkcs11`, `dks`, `jceks`.")

     */
	private KeyStoreType keyStoreType = KeyStoreType.PKCS12;

	/*
    @ConfigurationParameter("key-store-password")
    @Description("Java keystore password used to access keystore and private key.")

     */
	@JsonRemoteProperty
	private String keyStorePassword;

	/*
    @ConfigurationParameter("channel-lifetime")
    @LongDefaultValue(3600000)
    @Description("Time for which negotiated secure channel, its keys and session remains open. Value in milliseconds, by default 60 minutes.")

     */
	@JsonRemoteProperty
	private long channelLifetime = 3600000; //3600000;

	/*
    @ConfigurationParameter("session-timeout")
    @LongDefaultValue(120000)
    @Description("Expiry time for opened secure session, value in milliseconds. Defaults to 2 minutes.")

     */
	@JsonRemoteProperty
	private long sessionTimeout = 120000;

	/*
    @ConfigurationParameter("negotiation-timeout")
    @LongDefaultValue(60000)
    @Description("Timeout for all negotiation steps prior acceptance of application level operations - this timeout applies to open secure channel, create session and close calls. Defaults to 60 seconds.")

     */
	@JsonRemoteProperty
	private long negotiationTimeout = 6000;

	/*
    @ConfigurationParameter("request-timeout")
    @LongDefaultValue(30000)
    @Description("Timeout for read/write/subscribe calls. Value in milliseconds.")

     */
	@JsonRemoteProperty
	private long requestTimeout = 3000;

	/*
    @ConfigurationParameter("max-message-size")
    @IntDefaultValue(2097152)
    @Description("Maximum size of complete message.")

     */
	@JsonRemoteProperty
	private int maxMessageSize = 2097152;

	/*
    @ConfigurationParameter("default-timeout")
    @IntDefaultValue(1000)
    @Description("Timeout after which a connection will be treated as disconnected.")

     */
	@JsonRemoteProperty
	private long defaultTimeout = 2000;

	@JsonRemoteProperty
	private int creationMode;

	public int getCreationMode() {
		return creationMode;
	}

	public void setCreationMode(int creationMode) {
		this.creationMode = creationMode;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public OpcUaSecurityPolicyType getSecurityPolicy() {
		return securityPolicy;
	}

	public void setSecurityPolicy(OpcUaSecurityPolicyType securityPolicy) {
		this.securityPolicy = securityPolicy;
	}

	public OpcUaMessageSecurityType getMessageSecurity() {
		return messageSecurity;
	}

	public void setMessageSecurity(OpcUaMessageSecurityType messageSecurity) {
		this.messageSecurity = messageSecurity;
	}

	@Override
	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	@Override
	public KeyStoreType getKeyStoreType() {
		return keyStoreType;
	}

	public void setKeyStoreType(KeyStoreType keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public long getChannelLifetime() {
		return channelLifetime;
	}

	public void setChannelLifetime(long channelLifetime) {
		this.channelLifetime = channelLifetime;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public long getNegotiationTimeout() {
		return negotiationTimeout;
	}

	public void setNegotiationTimeout(long negotiationTimeout) {
		this.negotiationTimeout = negotiationTimeout;
	}

	public long getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(long requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public long getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public int getUpdatePeriodType() {
		return updatePeriodType;
	}

	public void setUpdatePeriodType(int updatePeriodType) {
		this.updatePeriodType = updatePeriodType;
	}

	public int getUpdatePeriods() {
		return updatePeriods;
	}

	public void setUpdatePeriods(int updatePeriods) {
		this.updatePeriods = updatePeriods;
	}

	@Override
	public boolean isQuantize() {
		return quantize;
	}

	public void setQuantize(boolean quantize) {
		this.quantize = quantize;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public String getServerAddress() {
		return MessageFormat.format("opc.tcp://{0}:{1}{2}", serverHost, String.valueOf(serverPort), (StringUtils.isEmpty(serverPath) ? "" : "/" + serverPath));
	}

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (StringUtils.isEmpty(serverHost))
			response.addContextualMessage("serverHost", "validate.required");
		if (StringUtils.isEmpty(serverName))
			response.addContextualMessage("serverName", "validate.required");
		if (updatePeriods <= 0)
			response.addContextualMessage("updatePeriods",
					"validate.greaterThanZero");
		if (StringUtils.isEmpty(user) && !StringUtils.isEmpty(password))
			response.addContextualMessage("user", "validate.required");
		if (!StringUtils.isEmpty(user) && StringUtils.isEmpty(password))
			response.addContextualMessage("password", "validate.required");

		if(messageSecurity != OpcUaMessageSecurityType.NONE) {
			if(keyStoreType == null) {
				response.addContextualMessage("keyStoreType", "validate.required");
			}
			if(StringUtils.isEmpty(keyStoreFile)) {
				response.addContextualMessage("keyStoreFile", "validate.required");
			}
			if(StringUtils.isEmpty(keyStorePassword)) {
				response.addContextualMessage("keyStorePassword", "validate.required");
			}
		}
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod",
				updatePeriodType, updatePeriods);
		AuditEventType.addPropertyMessage(list, "dsEdit.quantize", quantize);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.user", user);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.serverHost", serverHost);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.serverPort", serverPort);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.serverPath", serverPath);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.serverName", serverName);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.securityPolicy", securityPolicy);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.messageSecurity", messageSecurity);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.keyStoreFile", keyStoreFile);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.keyStoreType", keyStoreType);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.channelLifetime", channelLifetime);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.sessionTimeout", sessionTimeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.negotiationTimeout", negotiationTimeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.requestTimeout", requestTimeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.maxMessageSize", maxMessageSize);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.defaultTimeout", defaultTimeout);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.creationMode", creationMode);

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, OpcUaDataSourceVO from) {
		AuditEventType.maybeAddPeriodChangeMessage(list,
				"dsEdit.updatePeriod", from.getUpdatePeriodType(),
				from.getUpdatePeriods(), updatePeriodType, updatePeriods);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.quantize",
				from.isQuantize(), quantize);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.user",
				from.getUser(), user);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.serverHost",
				from.getServerHost(), serverHost);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.serverPort",
				from.getServerPort(), serverPort);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.serverPath",
				from.getServerPath(), serverPath);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.serverName",
				from.getServerName(), serverName);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.securityPolicy",
				from.getSecurityPolicy(), securityPolicy);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.messageSecurity",
				from.getMessageSecurity(), messageSecurity);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.keyStoreFile",
				from.getKeyStoreFile(), keyStoreFile);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.keyStoreType",
				from.getKeyStoreType(), keyStoreType);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.channelLifetime",
				from.getChannelLifetime(), channelLifetime);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.sessionTimeout",
				from.getSessionTimeout(), sessionTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.negotiationTimeout",
				from.getNegotiationTimeout(), negotiationTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.requestTimeout",
				from.getRequestTimeout(), requestTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.maxMessageSize",
				from.getMaxMessageSize(), maxMessageSize);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.defaultTimeout",
				from.getDefaultTimeout(), defaultTimeout);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.creationMode",
				from.getCreationMode(), creationMode);

	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 1;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);

		out.writeInt(this.updatePeriodType);
		out.writeInt(this.updatePeriods);

		SerializationHelper.writeSafeUTF(out, this.serverHost);
		SerializationHelper.writeSafeUTF(out, this.user);
		SerializationHelper.writeSafeUTF(out, this.password);
		SerializationHelper.writeSafeUTF(out, this.serverName);

		SerializationHelper.writeSafeUTF(out, this.keyStorePassword);

		out.writeInt(this.serverPort);
		out.writeInt(this.maxMessageSize);

		out.writeLong(this.defaultTimeout);
		out.writeLong(this.channelLifetime);
		out.writeLong(this.negotiationTimeout);
		out.writeLong(this.sessionTimeout);
		out.writeLong(this.requestTimeout);

		out.writeObject(this.keyStoreType);
		out.writeObject(this.messageSecurity);
		out.writeObject(this.securityPolicy);

		SerializationHelper.writeSafeUTF(out, keyStoreFile);
		SerializationHelper.writeSafeUTF(out, serverPath);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();
		if (ver == 1) {
			updatePeriodType = in.readInt();
			updatePeriods = in.readInt();

			serverHost = SerializationHelper.readSafeUTF(in);
			user = SerializationHelper.readSafeUTF(in);
			password = SerializationHelper.readSafeUTF(in);
			serverName = SerializationHelper.readSafeUTF(in);
			keyStorePassword = SerializationHelper.readSafeUTF(in);

			serverPort = in.readInt();
			maxMessageSize = in.readInt();

			defaultTimeout = in.readLong();
			channelLifetime = in.readLong();
			negotiationTimeout = in.readLong();
			sessionTimeout = in.readLong();
			requestTimeout = in.readLong();

			try {
				keyStoreType = (KeyStoreType) in.readObject();
			} catch (Exception e) {
				keyStoreType = KeyStoreType.DEFAULT;
			}

			try {
				messageSecurity = (OpcUaMessageSecurityType) in.readObject();
			} catch (Exception e) {
				messageSecurity = OpcUaMessageSecurityType.DEFAULT;
			}

			try {
				securityPolicy = (OpcUaSecurityPolicyType) in.readObject();
			} catch (Exception e) {
				securityPolicy = OpcUaSecurityPolicyType.DEFAULT;
			}

			keyStoreFile = SerializationHelper.readSafeUTF(in);
			serverPath = SerializationHelper.readSafeUTF(in);
		}
	}

	@Override
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		super.jsonDeserialize(reader, json);
		Integer value = deserializeUpdatePeriodType(json);
		if (value != null)
			updatePeriodType = value;

		String keyStoreTypeJson = json.getString("keyStoreType");
		if(keyStoreTypeJson != null) {
			try {
				keyStoreType = KeyStoreType.valueOf(keyStoreTypeJson);
			} catch (Exception ex) {
				keyStoreType = KeyStoreType.DEFAULT;
			}
		}

		String messageSecurityJson = json.getString("messageSecurity");
		if(messageSecurityJson != null) {
			try {
				messageSecurity = OpcUaMessageSecurityType.valueOf(messageSecurityJson);
			} catch (Exception ex) {
				messageSecurity = OpcUaMessageSecurityType.DEFAULT;
			}
		}

		String securityPolicyJson = json.getString("securityPolicy");
		if(securityPolicyJson != null) {
			try {
				securityPolicy = OpcUaSecurityPolicyType.valueOf(securityPolicyJson);
			} catch (Exception ex) {
				securityPolicy = OpcUaSecurityPolicyType.DEFAULT;
			}
		}
	}

	@Override
	public void jsonSerialize(Map<String, Object> map) {
		super.jsonSerialize(map);
		serializeUpdatePeriodType(map, updatePeriodType);
		map.put("keyStoreType", keyStoreType.name());
		map.put("messageSecurity", messageSecurity.name());
		map.put("securityPolicy", securityPolicy.name());
	}


	@Override
	public DataSourceVO<OpcUaDataSourceVO> toDataSource() {
		return this;
	}
}
