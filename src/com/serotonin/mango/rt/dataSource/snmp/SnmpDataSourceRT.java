package com.serotonin.mango.rt.dataSource.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;

import com.serotonin.web.i18n.LocalizableMessage;

import static com.serotonin.mango.rt.dataSource.DataPointUnreliableUtils.*;

/**
 * @author Matthew Lohbihler
 *
 */
public class SnmpDataSourceRT extends PollingDataSource {

    enum MessageType{
        oidError,
        unknownOid,
        undefined

    }
    class SnmpResponses {
        PDU request = null;
        PDU response = null;
        long responseTime;

        SnmpResponses(){}

        public void setRequest(PDU request){

            this.request = request;
        }
        public PDU getRequest(){

            return this.request;
        }
        public PDU getResponseByGet() {

            try {
                startTime();
                response = getResponse(true);
                finishTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        public PDU getResponseBySet(){
            PDU response = null;
            try {
                startTime();
                response = getResponse(false);
                finishTime();
            } catch (Exception ex) {
                // TODO add error handling
                response = null;
            }
            return response;
        }
        private PDU getResponse(boolean setOrGet) throws IOException {

            return setOrGet?snmp.get(request, target).getResponse():snmp.set(request, target).getResponse();

        }
        private void startTime(){

            responseTime = System.currentTimeMillis();

        }
        private void finishTime(){

            responseTime = System.currentTimeMillis() - responseTime;
            log.debug("Snmp request/response time: " + responseTime);

        }
    }

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int PDU_EXCEPTION_EVENT = 2;
    public static final int TRAP_NOT_HANDLED_EVENT = 3;

    private final Log log = LogFactory.getLog(SnmpDataSourceRT.class);

    private final SnmpDataSourceVO vo;
    private final Version version;
    private String address;
    private Target target;
    private Snmp snmp;
    private int counterEmptyResponsesOrResponsesWithError;
    private boolean deviceDidNotRespondDespiteTheCounterOfRetries = Boolean.FALSE;
    private SnmpResponses snmpRequests;

    public Snmp getSnmp() {
        return snmp;
    }

    public SnmpDataSourceRT(SnmpDataSourceVO vo) {
        super(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
        this.vo = vo;
        version = Version.getVersion(vo.getSnmpVersion(), vo.getCommunity(),
                vo.getSecurityName(), vo.getAuthProtocol(),
                vo.getAuthPassphrase(), vo.getPrivProtocol(),
                vo.getPrivPassphrase(), vo.getSecurityLevel(),
                vo.getContextName());
        snmpRequests = new SnmpResponses();
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
                              SetPointSource source) {
        PDU request = version.createPDU();
        SnmpPointLocatorRT locator = dataPoint.getPointLocator();
        request.add(new VariableBinding(getOid(dataPoint), locator
                .valueToVariable(valueTime.getValue())));
        snmpRequests.setRequest(request);
        PDU response = snmpRequests.getResponseBySet();

        LocalizableMessage message = validatePdu(response);
        if (message != null) {
            setUnreliableDataPoint(dataPoint);
            raiseEvent(PDU_EXCEPTION_EVENT, valueTime.getTime(), false, message);
        } else {
            dataPoint.setPointValue(valueTime, source);
            resetUnreliableDataPoint(dataPoint);
        }
    }
    public void setDeviceDidNotRespondDespiteTheCounterOfRetries(boolean deviceDidNotRespondDespiteTheCounterOfRetries) {
        this.deviceDidNotRespondDespiteTheCounterOfRetries = deviceDidNotRespondDespiteTheCounterOfRetries;
        log.info("Device did not respond despite the counter of retries.");
    }
    public void createSnmpAndStartListening(){
        try {
            initializeComponents();

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
    public boolean isSnmpConnectionIsAlive(){
        if (target.getRetries() == counterEmptyResponsesOrResponsesWithError) {
            setDeviceDidNotRespondDespiteTheCounterOfRetries(Boolean.TRUE);
            return Boolean.FALSE;
        }
        else
            return Boolean.TRUE;

    }

    @Override
    protected void doPoll(long time) {
        try {
            doPollImpl(time);
        } catch (Exception e) {
            raiseEvent(PDU_EXCEPTION_EVENT, time, true,
                    DataSourceRT.getExceptionMessage(e));
        }
    }

    private void doPollImpl(long time) throws IOException {
        snmpRequests.setRequest(version.createPDU());
        PDU request = snmpRequests.getRequest();
        PDU response = null;
        request.setType(PDU.GET);

        VariableBinding vb;

        // Make a copy of the oids so that we can check if we got everything we
        // asked for, and
        // only what we asked for.
        List<DataPointRT> requestPoints = new ArrayList<DataPointRT>();

        // Add OID to send in the PDU.
        for (DataPointRT dp : dataPoints) {
            if (!getLocatorVO(dp).isTrapOnly()) {
                request.add(new VariableBinding(getOid(dp)));
                requestPoints.add(dp);
            }
        }

        ResponseEvent event = snmp.send(request, target);
        if(event != null) {
            response = event.getResponse();
            LocalizableMessage message = validateResponseAndValidateStateOfConnection(response);
            if (time == -1) {
                if (!isSnmpConnectionIsAlive()) {
                    snmp.close();
                }
            } else {
                if (!isSnmpConnectionIsAlive()) {
                    setUnreliableDataPoints(dataPoints);
//                    Common.ctx.getRuntimeManager().stopDataSourceAndDontJoinTermination(vo.getId());
                } else if (message != null) {
                    setUnreliableDataPoints(dataPoints);
                    raiseEvent(PDU_EXCEPTION_EVENT, time, true, message);
                }
            }
            if (response.getErrorStatus() == PDU.noError) {
                DataPointRT dp;
                for (int i = 0; i < response.size(); i++) {
                    vb = response.get(i);
                    dp = setDataPoint(vb,requestPoints);
                    if (dp != null) {
                        requestPoints.remove(dp);
                        updatePoint(dp, vb.getVariable(), time);
                    }
                }
                resetUnreliableDataPoints(dataPoints);
            } else {
                setUnreliableDataPoints(dataPoints);
                raiseEvent(PDU_EXCEPTION_EVENT, time, true, validatePdu(response));

            }
        }

    }
    private DataPointRT setDataPoint(VariableBinding vb, List<DataPointRT> requestPoints){
        for (DataPointRT requestPoint : requestPoints) {
            if (getOid(requestPoint).equals(vb.getOid())) {
                return  requestPoint;
            }
        }
        return null;
    }
    public LocalizableMessage validateResponseAndValidateStateOfConnection(PDU response){
        LocalizableMessage message = validatePdu(response);

        increaseCounterIfErrorExistOrNoResponseAppear(response);

        return message;
    }

    /**
     * if messageType is different that value 0 or 1
     * then LocalizableMessage is created and is gived to raiseEvent method.
     * Then result of this is true,otherwise (messageType == -1) is false.
     *
     * @param messageType
     * @param vb
     * @param dp
     * @param time
     * @return boolean
     */
    private boolean logEventsDependsOnMessageType(MessageType messageType, VariableBinding vb, DataPointRT dp, long time){
        LocalizableMessage message=null;
        switch(messageType) {
            case oidError:
                message=new LocalizableMessage(
                        "event.snmp.oidError",
                        address, getOid(dp), vb.getVariable());
                break;
            case unknownOid:
                message=new LocalizableMessage(
                        "event.snmp.unknownOid",
                        vb.getOid(), address);
                break;
        }

        if(messageType!=MessageType.undefined) {
            setUnreliableDataPoint(dp);
            raiseEvent(PDU_EXCEPTION_EVENT, time, true, message);
        } else {
            resetUnreliableDataPoint(dp);
        }

        return messageType!=MessageType.undefined;
    }
    private LocalizableMessage validatePdu(PDU pdu) {
        if (pdu == null)
            return new LocalizableMessage("event.snmp.noResponse");

        if (pdu.getErrorIndex() != 0)
            return new LocalizableMessage("event.snmp.pduOidError", pdu.get(
                    pdu.getErrorIndex() - 1).getOid(), pdu.getErrorStatusText());

        if (pdu.getErrorStatus() != 0)
            return new LocalizableMessage("event.snmp.pduErrorStatus",
                    pdu.getErrorStatus(), pdu.getErrorStatusText());

        return null;
    }

    private void increaseCounterIfErrorExistOrNoResponseAppear(PDU pdu) {
        if ((pdu == null) || (pdu.getErrorIndex() != 0) || (pdu.getErrorStatus() != 0)) {
            ++counterEmptyResponsesOrResponsesWithError;
            log.info("Counter Empty Responses Or Responses With Error: "+counterEmptyResponsesOrResponsesWithError);
        }
    }



    private OID getOid(DataPointRT dp) {
        return ((SnmpPointLocatorRT) dp.getPointLocator()).getOid();
    }

    private SnmpPointLocatorVO getLocatorVO(DataPointRT dp) {
        return ((SnmpPointLocatorRT) dp.getPointLocator()).getVO();
    }

    int getTrapPort() {
        return vo.getTrapPort();
    }

    boolean isTrapEnabled() {
        return vo.isTrapEnabled();
    }

    String getLocalAddress() {
        return vo.getLocalAddress();
    }

    String getAddress() {
        return address;
    }

    Version getVersion() { return this.version; }

    void receivedTrap(PDU trap) {
        long time = System.currentTimeMillis();
        VariableBinding vb;

        // Take a look at the response.
        LocalizableMessage message = validatePdu(trap);
        if (message != null) {
            setUnreliableDataPoints(dataPoints);
            raiseEvent(PDU_EXCEPTION_EVENT, time, false, message);
        } else {
            resetUnreliableDataPoints(dataPoints);
            synchronized (pointListChangeLock) {
                updateChangedPoints();

                for (int i = 0; i < trap.getVariableBindings().size(); i++) {
                    vb = trap.get(i);
                    boolean found = false;

                    // Find the command for this binding.
                    for (DataPointRT dp : dataPoints) {
                        if (getOid(dp).equals(vb.getOid())) {
                            updatePoint(dp, vb.getVariable(), time);
                            found = true;
                        }
                    }

                    if (!found) {
                        setUnreliableDataPoints(dataPoints);
                        raiseEvent(TRAP_NOT_HANDLED_EVENT, time, false, new LocalizableMessage("event.snmp.trapNotHandled", vb));
                        log.warn("Trap not handled: " + vb);
                    } else {
                        resetUnreliableDataPoints(dataPoints);
                    }
                }
            }
        }
    }

    private void updatePoint(DataPointRT dp, Variable variable, long time) {
        SnmpPointLocatorRT locator = dp.getPointLocator();
        dp.updatePointValue(new PointValueTime(locator
                .variableToValue(variable), time));
    }

    //
    // /
    // / Lifecycle
    // /
    //
    @Override
    public void initialize() {
        try {
            initializeComponents();
            counterEmptyResponsesOrResponsesWithError=0;
            log.info("Counter Empty Responses Or Responses With Error is set 0.");

            SnmpTrapRouter.addDataSource(this);

            // Deactivate any existing event.
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
                    System.currentTimeMillis());
            resetUnreliableDataPoints(dataPoints);
        } catch (Exception e) {
            setUnreliableDataPoints(dataPoints);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
            log.debug("Error while initializing data source", e);
            return;
        }

        super.initialize();
    }
    private void initializeComponents() throws IOException {
        snmp = new Snmp(new DefaultUdpTransportMapping());

        OctetString localEngineId = new OctetString(MPv3.createLocalEngineID()).substring(0,9);
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
        SecurityModels.getInstance().addSecurityModel(usm);

        version.addUser(snmp);
        target = version.getTarget(vo.getHost(), vo.getPort(), vo.getRetries(), vo.getTimeout());

        snmp.listen();

    }
    @Override
    public void terminate() {
        super.terminate();

        SnmpTrapRouter.removeDataSource(this);

        try {
            if (snmp != null)
                snmp.close();
        } catch (IOException e) {
            throw new ShouldNeverHappenException(e);
        }
    }
}