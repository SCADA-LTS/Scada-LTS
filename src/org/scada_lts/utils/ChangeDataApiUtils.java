package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValueErrorsDTO;
import org.scada_lts.web.mvc.api.components.cmp.model.SetValuePointDTO;

import static com.serotonin.mango.vo.permission.Permissions.hasDataPointSetPermission;

public final class ChangeDataApiUtils {

    private static final Log LOG = LogFactory.getLog(ChangeDataApiUtils.class);

    private ChangeDataApiUtils() {}

    public static SetValueErrorsDTO checkIfValuesCanBeSet(User user, SetValuePointDTO[] setValuePointDTOS) {
        SetValueErrorsDTO setValueErrorsDTO = new SetValueErrorsDTO();
        DataPointService dataPointService = new DataPointService();

        for (SetValuePointDTO sv: setValuePointDTOS) {
            StringBuilder msg = new StringBuilder();
            DataPointVO point = dataPointService.getDataPointByXid(sv.getXid());
            if (!hasDataPointSetPermission(user, point))
                msg.append("No permissions for point;");
            if (!point.getPointLocator().isSettable())
                msg.append("Point is not settable;");
            if (!point.isEnabled())
                msg.append("Point is not enabled;");
            if (!isDatasourceEnabled(point))
                msg.append("Datasource is not enabled;");
            if (!isValueValid(point, sv.getValue()))
                msg.append("Value is invalid for this datapoint type;");
            if (!msg.toString().equals(""))
                setValueErrorsDTO.getErrors().add(new SetValueErrorsDTO.PointError(point.getXid(), point.getName(), msg.toString()));
        }
        return setValueErrorsDTO;
    }

    private static boolean isDatasourceEnabled(DataPointVO point) {
        DataSourceService dataSourceService = new DataSourceService();
        return dataSourceService.getDataSource(point.getDataSourceId()).isEnabled();
    }

    private static boolean isValueValid(DataPointVO point, String value) {
        try {
            MangoValue.stringToValue(value, point.getPointLocator().getDataTypeId());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
