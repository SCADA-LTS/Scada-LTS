package br.org.scadabr.vo.dataSource.opcua;

import br.org.scadabr.KeyStoreType;
import br.org.scadabr.rt.dataSource.opc.OPCDataSource;
import br.org.scadabr.rt.dataSource.opcua.OpcUaDataSourceRT;
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@JsonRemoteEntity
public class OpcUaDataSourceVO<T extends OpcUaDataSourceVO<?>> extends
		DataSourceVO<T> {

	public static final Type TYPE = Type.OPC_UA;

	@Override
	protected void addEventTypes(List<EventTypeVO> eventTypes) {
		eventTypes.add(createEventType(
				OPCDataSource.POINT_READ_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.pointRead")));
		eventTypes.add(createEventType(
				OPCDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));
		eventTypes.add(createEventType(
				OPCDataSource.POINT_WRITE_EXCEPTION_EVENT,
				new LocalizableMessage("event.ds.dataSource")));

	}

	private static final ExportCodes EVENT_CODES = new ExportCodes();
	static {
		EVENT_CODES.addElement(OPCDataSource.DATA_SOURCE_EXCEPTION_EVENT,
				"DATA_SOURCE_EXCEPTION");
		EVENT_CODES.addElement(OPCDataSource.POINT_READ_EXCEPTION_EVENT,
				"POINT_READ_EXCEPTION");
		EVENT_CODES.addElement(OPCDataSource.POINT_WRITE_EXCEPTION_EVENT,
				"POINT_WRITE_EXCEPTION");

	}

	@Override
	public DataSourceRT createDataSourceRT() {
		return new OpcUaDataSourceRT(this);
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
    @ConfigurationParameter("discovery")
    @BooleanDefaultValue(true)
    @Description("Controls the feature of the discovery endpoint of an OPC UA server which every server\n" +
        "will propagate over an '<address>/discovery' endpoint. The most common issue here is that most servers are not correctly\n" +
        "configured and propagate the wrong external IP or URL address. If that is the case you can disable the discovery by\n" +
        "configuring it with a `false` value.\n" +
        "\n" +
        "The discovery phase is always conducted using `NONE` security policy.")

     */
	@JsonRemoteProperty
	private boolean discovery = false;

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
	private OpcUaMessageSecurityType messageSecurity = OpcUaMessageSecurityType.SIGN_ENCRYPT;

	/*
    @ConfigurationParameter("key-store-file")
    @Description("The Keystore file used to lookup client certificate and its private key.")

     */
	@JsonRemoteProperty
	private String keyStoreFile;

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
    @ConfigurationParameter("server-certificate-file")
    @Description("Filesystem location where server certificate is located, supported formats are `DER` and `PEM`.")

     */
	@JsonRemoteProperty
	private String serverCertificateFile;

	/*
    @ConfigurationParameter("trust-store-file")
    @Description("The trust store file used to verify server certificates and its chain.")

     */
	@JsonRemoteProperty
	private String trustStoreFile;

	/*
    @ConfigurationParameter("trust-store-type")
    @StringDefaultValue("pkcs12")
    @Description("Keystore type used to access keystore and private key, defaults to PKCS (for Java 11+).\n" +
        "Possible values are between others `jks`, `pkcs11`, `dks`, `jceks`.")

     */
	private KeyStoreType trustStoreType = KeyStoreType.PKCS12;

	/*
    @ConfigurationParameter("trust-store-password")
    @Description("Password used to open trust store.")

     */
	@JsonRemoteProperty
	private String trustStorePassword;

	/*
    @ConfigurationParameter("channel-lifetime")
    @LongDefaultValue(3600000)
    @Description("Time for which negotiated secure channel, its keys and session remains open. Value in milliseconds, by default 60 minutes.")

     */
	@JsonRemoteProperty
	private long channelLifetime = 3600000;

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
    @ConfigurationParameter("receive-buffer-size")
    @IntDefaultValue(65535)
    @Description("Maximum size of received TCP transport message chunk value in bytes.")

     */
	@JsonRemoteProperty
	private int receiveBufferSize = 65535;

	/*
    @ConfigurationParameter("send-buffer-size")
    @IntDefaultValue(65535)
    @Description("Maximum size of sent transport message chunk.")

     */
	@JsonRemoteProperty
	private int sendBufferSize = 65535;

	/*
    @ConfigurationParameter("max-message-size")
    @IntDefaultValue(2097152)
    @Description("Maximum size of complete message.")

     */
	@JsonRemoteProperty
	private int maxMessageSize = 2097152;

	/*
    @ConfigurationParameter("max-chunk-count")
    @IntDefaultValue(64)
    @Description("Maximum number of chunks for both sent and received messages.")

     */
	@JsonRemoteProperty
	private int maxChunkCount = 64;

	/*
	@ConfigurationParameter("keep-alive")
	@BooleanDefaultValue(false)
	@Description("Should keep-alive packets be sent?")

	 */
	@JsonRemoteProperty
	private boolean keepAlive = true;

	/*
    @ConfigurationParameter("no-delay")
    @BooleanDefaultValue(true)
    @Description("Should packets be sent instantly or should we give the OS some time to aggregate data.")

     */
	@JsonRemoteProperty
	private boolean noDelay = true;

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

	public boolean isDiscovery() {
		return discovery;
	}

	public void setDiscovery(boolean discovery) {
		this.discovery = discovery;
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

	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	public KeyStoreType getKeyStoreType() {
		return keyStoreType;
	}

	public void setKeyStoreType(KeyStoreType keyStoreType) {
		this.keyStoreType = keyStoreType;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public String getServerCertificateFile() {
		return serverCertificateFile;
	}

	public void setServerCertificateFile(String serverCertificateFile) {
		this.serverCertificateFile = serverCertificateFile;
	}

	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStoreFile = trustStoreFile;
	}

	public KeyStoreType getTrustStoreType() {
		return trustStoreType;
	}

	public void setTrustStoreType(KeyStoreType trustStoreType) {
		this.trustStoreType = trustStoreType;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
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

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public int getMaxChunkCount() {
		return maxChunkCount;
	}

	public void setMaxChunkCount(int maxChunkCount) {
		this.maxChunkCount = maxChunkCount;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isNoDelay() {
		return noDelay;
	}

	public void setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
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
		return MessageFormat.format("opcua:tcp://{0}:{1}{2}", serverHost, String.valueOf(serverPort), (StringUtils.isEmpty(serverPath) ? "" : "/" + serverPath));
	}

	public String getConnectionString() {

		String address = getServerAddress();
		String discoveryQuery = MessageFormat.format("?discovery={0}", isDiscovery());
		String query = "";

		/// Sec

		if (getSecurityPolicy() != null) {
			query += MessageFormat.format("&security-policy={0}", getSecurityPolicy().getCode());

			if(getSecurityPolicy() != OpcUaSecurityPolicyType.NONE) {

				if (!StringUtils.isEmpty(getUser())) {
					query += MessageFormat.format("&username={0}", getUser());
				}

				if (!StringUtils.isEmpty(getPassword())) {
					query += MessageFormat.format("&password={0}", getPassword());
				}

				if (getMessageSecurity() != null) {
					query += MessageFormat.format("&message-security={0}", getMessageSecurity().getCode());
				}

				if (!StringUtils.isEmpty(getServerCertificateFile())) {
					String serverCertFilePath = URLEncoder.encode(
							Paths.get(getServerCertificateFile()).toString(),
							StandardCharsets.UTF_8
					);
					query += MessageFormat.format("&server-certificate-file={0}", serverCertFilePath);
				}

				if (!StringUtils.isEmpty(getKeyStoreFile())) {
					String keyStoreFilePath = URLEncoder.encode(
							Paths.get(getKeyStoreFile()).toString(),
							StandardCharsets.UTF_8
					);
					query += MessageFormat.format("&key-store-file={0}" +
							"&key-store-type={1}" +
							"&key-store-password={2}", keyStoreFilePath, getKeyStoreType().getDescription(), getKeyStorePassword());
				}

				if (!StringUtils.isEmpty(getTrustStoreFile())) {
					String trustStoreFilePath = URLEncoder.encode(
							Paths.get(getTrustStoreFile()).toString(),
							StandardCharsets.UTF_8
					);
					query += MessageFormat.format("&trust-store-file={0}" +
							"&trust-store-type={1}" +
							"&trust-store-password={2}", trustStoreFilePath, getTrustStoreType().getDescription(), getTrustStorePassword());
				}
			}
		}
		///

		if(getMaxChunkCount() > 0) {
			query += MessageFormat.format("&encoding.max-chunk-count={0}", String.valueOf(getMaxChunkCount()));
		}

		if(getMaxMessageSize() > 0) {
			query += MessageFormat.format("&encoding.max-message-size={0}", String.valueOf(getMaxMessageSize()));
		}

		if(getSendBufferSize() > 0) {
			query += MessageFormat.format("&encoding.send-buffer-size={0}", String.valueOf(getSendBufferSize()));
		}

		if(getReceiveBufferSize() > 0) {
			query += MessageFormat.format("&encoding.receive-buffer-size={0}", String.valueOf(getReceiveBufferSize()));
		}

		///

		if(getChannelLifetime() > 0) {
			query += MessageFormat.format("&channel-lifetime={0}", String.valueOf(getChannelLifetime()));
		}

		if(getNegotiationTimeout() > 0) {
			query += MessageFormat.format("&negotiation-timeout={0}", String.valueOf(getNegotiationTimeout()));
		}

		if(getRequestTimeout() > 0) {
			query += MessageFormat.format("&request-timeout={0}", String.valueOf(getRequestTimeout()));
		}

		if(getSessionTimeout() > 0) {
			query += MessageFormat.format("&session-timeout={0}", String.valueOf(getSessionTimeout()));
		}

		///

		query += MessageFormat.format("&tcp.keep-alive={0}"
				+ "&tcp.no-delay={1}", isKeepAlive(), isNoDelay());

		if(getDefaultTimeout() > 0) {
			query += MessageFormat.format("&tcp.default-timeout={0}", String.valueOf(getDefaultTimeout()));
		}

		return address + discoveryQuery + query;
	}

	@Override
	public void validate(DwrResponseI18n response) {
		super.validate(response);
		if (StringUtils.isEmpty(serverHost))
			response.addContextualMessage("host", "validate.required");
		if (StringUtils.isEmpty(user))
			response.addContextualMessage("user", "validate.required");
		if (StringUtils.isEmpty(password))
			response.addContextualMessage("password", "validate.required");
		if (StringUtils.isEmpty(serverName))
			response.addContextualMessage("server", "validate.required");
		if (updatePeriods <= 0)
			response.addContextualMessage("updatePeriods",
					"validate.greaterThanZero");
	}

	@Override
	protected void addPropertiesImpl(List<LocalizableMessage> list) {
		AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod",
				updatePeriodType, updatePeriods);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.host", serverHost);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.user", user);
		AuditEventType
				.addPropertyMessage(list, "dsEdit.opcua.password", password);
		AuditEventType.addPropertyMessage(list, "dsEdit.opcua.server", serverName);

	}

	@Override
	protected void addPropertyChangesImpl(List<LocalizableMessage> list, T from) {
		AuditEventType.maybeAddPeriodChangeMessage(list,
				"dsEdit.updatePeriod", from.getUpdatePeriodType(),
				from.getUpdatePeriods(), updatePeriodType, updatePeriods);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.host",
				from.getServerHost(), serverHost);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.user",
				from.getUser(), user);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"dsEdit.opcua.password", from.getPassword(), password);
		AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.opcua.server",
				from.getServerName(), serverName);

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

		out.writeInt(updatePeriodType);
		out.writeInt(updatePeriods);

		SerializationHelper.writeSafeUTF(out, serverHost);
		SerializationHelper.writeSafeUTF(out, user);
		SerializationHelper.writeSafeUTF(out, password);
		SerializationHelper.writeSafeUTF(out, serverName);

		SerializationHelper.writeSafeUTF(out, keyStorePassword);
		SerializationHelper.writeSafeUTF(out, trustStorePassword);

		out.writeInt(maxChunkCount);
		out.writeInt(serverPort);
		out.writeInt(this.maxMessageSize);
		out.writeInt(this.sendBufferSize);
		out.writeInt(this.receiveBufferSize);

		out.writeLong(this.defaultTimeout);
		out.writeLong(this.channelLifetime);
		out.writeLong(this.negotiationTimeout);
		out.writeLong(this.sessionTimeout);
		out.writeLong(this.requestTimeout);

		out.writeBoolean(this.noDelay);
		out.writeBoolean(this.keepAlive);
		out.writeBoolean(this.discovery);

		out.writeObject(this.keyStoreType);
		out.writeObject(this.messageSecurity);
		out.writeObject(this.securityPolicy);
		out.writeObject(this.trustStoreType);

		SerializationHelper.writeSafeUTF(out, keyStoreFile);
		SerializationHelper.writeSafeUTF(out, trustStoreFile);
		SerializationHelper.writeSafeUTF(out, serverCertificateFile);
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
			trustStorePassword = SerializationHelper.readSafeUTF(in);

			maxChunkCount = in.readInt();
			serverPort = in.readInt();
			maxMessageSize = in.readInt();
			sendBufferSize = in.readInt();
			receiveBufferSize = in.readInt();

			defaultTimeout = in.readLong();
			channelLifetime = in.readLong();
			negotiationTimeout = in.readLong();
			sessionTimeout = in.readLong();
			requestTimeout = in.readLong();

			noDelay = in.readBoolean();
			keepAlive = in.readBoolean();
			discovery = in.readBoolean();

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

			try {
				trustStoreType = (KeyStoreType) in.readObject();
			} catch (Exception e) {
				trustStoreType = KeyStoreType.DEFAULT;
			}

			keyStoreFile = SerializationHelper.readSafeUTF(in);
			trustStoreFile = SerializationHelper.readSafeUTF(in);
			serverCertificateFile = SerializationHelper.readSafeUTF(in);
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

		String trustStoreTypeJson = json.getString("trustStoreType");
		if(trustStoreTypeJson != null) {
			try {
				trustStoreType = KeyStoreType.valueOf(trustStoreTypeJson);
			} catch (Exception ex) {
				trustStoreType = KeyStoreType.DEFAULT;
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
		map.put("trustStoreType", trustStoreType.name());
	}

}
