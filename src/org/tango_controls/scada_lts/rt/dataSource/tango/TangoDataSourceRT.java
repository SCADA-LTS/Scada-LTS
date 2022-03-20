package org.tango_controls.scada_lts.rt.dataSource.tango;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.web.i18n.LocalizableMessage;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.CallBack;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoApi.events.EventData;
import fr.esrf.TangoDs.TangoConst;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tango_controls.scada_lts.vo.dataSource.tango.TangoDataSourceVO;
import org.tango_controls.scada_lts.vo.dataSource.tango.TangoPointLocatorVO;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GP Orcullo
 */

public class TangoDataSourceRT extends EventDataSource implements Runnable {
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int POINT_READ_EXCEPTION_EVENT = 2;
    public static final int POINT_WRITE_EXCEPTION_EVENT = 3;

    private static final Log LOG = LogFactory.getLog(TangoDataSourceRT.class);
    private final TangoDataSourceVO vo;
    private DeviceProxy deviceProxy;
    private EventCallBack ecb;
    private final Map<String, DataPointRT> attributes = new ConcurrentHashMap<>();
    private final Map<String, TangoAttributeInfo> attrInfo = new ConcurrentHashMap<>();

    public TangoDataSourceRT(TangoDataSourceVO vo) {
        super(vo);
        this.vo = vo;
    }

    @Override
    public void initialize() {
        super.initialize();
        LOG.debug("INITIALIZE TangoDataSourceRT");

        String devName = String.format("tango://%s:%d/%s", vo.getHostName(), vo.getPort(), vo.getDeviceID());
        try {
            deviceProxy = new DeviceProxy(devName);
            LOG.debug(String.format("Connected to device: %s", devName));

            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        } catch (DevFailed e) {
            LOG.error(String.format("Failed to connect to device: %s", devName));
            deviceProxy = null;
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.initializationError", e.getMessage()));
        }
    }

    @Override
    public void terminate() {
        LOG.debug("TERMINATE TangoDataSourceRT");
        super.terminate();

        if (ecb != null) {
            unsubscribeAll();
        }
    }

    @Override
    public void beginPolling() {
        if (deviceProxy != null) {
            LOG.debug("BEGIN-POLLING TangoDataSourceRT");
            new Thread(this, "TANGO data source").start();
        }
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        super.addDataPoint(dataPoint);
        LOG.debug("ADD-DATA-POINT TangoDataSourceRT");

        TangoPointLocatorVO locator = ((TangoPointLocatorRT) dataPoint.getPointLocator()).getVO();

        String attr = locator.getAttribute();
        attributes.put(attr, dataPoint);

        if (ecb != null) {
            subscribe(attr);
        }
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        super.removeDataPoint(dataPoint);
        LOG.debug("REMOVE-DATA-POINT TangoDataSourceRT");

        TangoPointLocatorVO locator = ((TangoPointLocatorRT) dataPoint.getPointLocator()).getVO();

        String attr = locator.getAttribute();
        if ((attributes.remove(attr) != null) && (ecb != null)) {
            unsubscribe(attr);
        }
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        if (ecb == null)
            return;

        TangoPointLocatorVO locator = ((TangoPointLocatorRT) dataPoint.getPointLocator()).getVO();

        String attr = locator.getAttribute();
        DeviceAttribute devAttr = new DeviceAttribute(attr);

        if (attrInfo.get(attr).readOnly == AttrWriteType._READ) {
            String err = String.format("Attribute: %s is not writable!", attr);

            LOG.error(err);
            raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.tango.setPointValueErr", err));
            return;
        }

        switch (locator.getDataTypeId()) {
            case DataTypes.BINARY:
                devAttr.insert(valueTime.getBooleanValue());
                break;
            case DataTypes.MULTISTATE:
                int iv = valueTime.getIntegerValue();
                switch (attrInfo.get(attr).dataType) {
                    case TangoConst.Tango_DEV_INT:
                        devAttr.insert(iv);
                        break;
                    case TangoConst.Tango_DEV_SHORT:
                        devAttr.insert((short) iv);
                        break;
                    case TangoConst.Tango_DEV_LONG:
                        devAttr.insert((long) iv);
                        break;
                    default:
                        throw new ShouldNeverHappenException("Unsupported Tango Type: "
                                + TangoConst.Tango_CmdArgTypeName[attrInfo.get(attr).dataType]);
                }
                break;
            case DataTypes.NUMERIC:
                double dv = valueTime.getDoubleValue();
                switch (attrInfo.get(attr).dataType) {
                    case TangoConst.Tango_DEV_DOUBLE:
                        devAttr.insert(dv);
                        break;
                    case TangoConst.Tango_DEV_FLOAT:
                        devAttr.insert((float) dv);
                        break;
                    default:
                        throw new ShouldNeverHappenException("Unsupported Tango Type: "
                                + TangoConst.Tango_CmdArgTypeName[attrInfo.get(attr).dataType]);
                }
                break;
            case DataTypes.ALPHANUMERIC:
                devAttr.insert(valueTime.getStringValue());
                break;
            default:
                throw new ShouldNeverHappenException("Unknown point type: "
                        + DataTypes.getDataTypeMessage(locator.getDataTypeId()));
        }

        try {
            deviceProxy.write_attribute_asynch(devAttr);
            dataPoint.setPointValue(valueTime, source);

            LOG.debug(String.format("setPointValue TangoDataSourceRT: %s", attr));
        } catch (DevFailed e) {
            String err = String.format("%s:%s", e.getLocalizedMessage(), attr);

            LOG.error(String.format("Failed to set attribute: %s", err));
            raiseEvent(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.tango.setPointValueErr", err));
        }
    }

    public void run() {
        if (deviceProxy != null) {
            LOG.debug("RUN TangoDataSourceRT");
            ecb = new EventCallBack();

            attributes.keySet().forEach(this::subscribe);
        }
    }

    private void subscribe(String attr) {
        if (! attrInfo.containsKey(attr)) {
            LOG.debug(String.format("Subscribe TangoDataSourceRT: %s", attr));
            try {
                TangoAttributeInfo ai = new TangoAttributeInfo();

                ai.dataType = deviceProxy.get_attribute_info(attr).data_type;
                ai.readOnly = deviceProxy.get_attribute_info(attr).writable.value();
                ai.eventID = deviceProxy.subscribe_event(attr, TangoConst.CHANGE_EVENT,
                        ecb, new String[]{}, true);

                attrInfo.put(attr, ai);
            } catch (DevFailed e) {
                String err = String.format("%s:%s", e.getLocalizedMessage(), attr);

                LOG.error(String.format("Failed to subscribe to attribute: %s", err));
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                        new LocalizableMessage("event.tango.subscribeErr", err));
            }
        }
    }

    private void unsubscribe(String attr) {
        TangoAttributeInfo evt = attrInfo.remove(attr);
        if (evt != null) {
            LOG.debug(String.format("Unsubscribe TangoDataSourceRT: %s", attr));
            try {
                deviceProxy.unsubscribe_event(evt.eventID);
            } catch (DevFailed e) {
                String err = String.format("%s:%s", e.getLocalizedMessage(), attr);

                LOG.error(String.format("Failed to un-subscribe to attribute: %s", err));
                raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                        new LocalizableMessage("event.tango.unSubscribeErr", err));
            }
        }
    }

    private void unsubscribeAll() {
        if (! attrInfo.isEmpty()) {
            attrInfo.forEach((k, v) -> {
                try {
                    LOG.debug(String.format("Unsubscribe TangoDataSourceRT: %s", k));
                    deviceProxy.unsubscribe_event(v.eventID);
                } catch (DevFailed e) {
                    String err = String.format("%s:%s", e.getLocalizedMessage(), k);

                    LOG.error(String.format("Failed to un-subscribe to attribute: %s", err));
                    raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                            new LocalizableMessage("event.tango.unSubscribeErr", err));
                }
            });
        }
    }

    private static class TangoAttributeInfo {
        public int readOnly;
        public int dataType;
        public int eventID;
    }

    private class EventCallBack extends CallBack {

        public void push_event(EventData event) {
            LOG.debug("PUSH-EVENT TangoDataSourceRT");
            if (event.err) {
                String err = Arrays.toString(event.errors);

                LOG.error(String.format("PUSH-EVENT TangoDataSourceRT: Event error: %s", err));
                raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                        new LocalizableMessage("event.tango.pushEventErr", err));
            } else {
                DeviceAttribute attr_value = event.attr_value;
                if (attr_value.hasFailed()) {
                    String err = Arrays.toString(attr_value.getErrStack());

                    LOG.error(String.format("PUSH-EVENT TangoDataSourceRT: Event error: %s", err));
                    raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                            new LocalizableMessage("event.tango.pushEventErr", err));
                } else {
                    try {
                        String name = attr_value.getName();
                        int tangoType = attr_value.getType();

                        DataPointRT dp = attributes.get(name);
                        if (dp != null) {
                            int dataTypeId = dp.getVO().getPointLocator().getDataTypeId();
                            MangoValue val;
                            switch (dataTypeId) {
                                case DataTypes.BINARY:
                                    val = new BinaryValue(attr_value.extractBoolean());
                                    break;
                                case DataTypes.MULTISTATE:
                                    switch(tangoType) {
                                        case TangoConst.Tango_DEV_SHORT:
                                            val = new MultistateValue(attr_value.extractShort());
                                            break;
                                        case TangoConst.Tango_DEV_INT:
                                        case TangoConst.Tango_DEV_LONG:
                                            val = new MultistateValue(attr_value.extractLong());
                                            break;
                                        default:
                                            throw new ShouldNeverHappenException("Unsupported Tango Type: "
                                                    + TangoConst.Tango_CmdArgTypeName[tangoType]);
                                    }
                                    break;
                                case DataTypes.NUMERIC:
                                    switch(tangoType) {
                                        case TangoConst.Tango_DEV_DOUBLE:
                                            val = new NumericValue(attr_value.extractDouble());
                                            break;
                                        case TangoConst.Tango_DEV_FLOAT:
                                            val = new NumericValue(attr_value.extractFloat());
                                            break;
                                        default:
                                            throw new ShouldNeverHappenException("Unsupported Tango Type: "
                                                    + TangoConst.Tango_CmdArgTypeName[tangoType]);
                                    }
                                    break;
                                case DataTypes.ALPHANUMERIC:
                                    val = new AlphanumericValue(attr_value.extractString());
                                    break;
                                default:
                                    throw new ShouldNeverHappenException("Unknown point type: "
                                        + DataTypes.getDataTypeMessage(dataTypeId));
                            }

                            dp.updatePointValue(new PointValueTime(val, attr_value.getTime()));
                        }
                    } catch(DevFailed e) {
                        String err = e.getMessage();

                        LOG.error(String.format("PUSH-EVENT TangoDataSourceRT: Event error: %s", err));
                        raiseEvent(POINT_READ_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                                new LocalizableMessage("event.tango.pushEventErr", err));
                    }
                }
            }
        }
    }
}