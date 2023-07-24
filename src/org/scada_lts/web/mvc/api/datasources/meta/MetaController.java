package org.scada_lts.web.mvc.api.datasources.meta;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataSource.meta.DataPointStateException;
import com.serotonin.mango.rt.dataSource.meta.ResultTypeException;
import com.serotonin.mango.rt.dataSource.meta.ScriptExecutor;
import com.serotonin.mango.vo.User;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.serotonin.mango.util.LoggingScriptUtils.infoErrorExecutionScript;
import static org.scada_lts.utils.ValidationUtils.checkIfNonAdminThenUnauthorized;

@Controller
@RequestMapping(value = "/api/datapoint/meta")
public class MetaController {

    private static final Log LOG = LogFactory.getLog(MetaController.class);

    @PostMapping(value = "/test")
    public ResponseEntity<Map<Object, Object>> testMeta(@RequestBody MetaPointLocatorJson data, HttpServletRequest request) {

        checkIfNonAdminThenUnauthorized(request);
        Map<Object, Object> response = new HashMap<>();
        validateScript(data.getScript(), data.getContext(), data.getDataTypeId(), response, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void validateScript(String script, List<IntValuePair> context, int dataTypeId, Map<Object, Object> response, HttpServletRequest request) {
        ScriptExecutor executor = new ScriptExecutor();
        try {
            Map<String, IDataPoint> convertedContext = executor
                    .convertContext(context);
            PointValueTime pvt = executor.execute(script, convertedContext,
                    System.currentTimeMillis(), dataTypeId, -1);
            if(pvt.getTime() == -1) {
                if (script == "") {
                    response.put("success", false);
                    response.put("message", LocalizableMessage.getMessage(Common.getBundle(request), "dsEdit.meta.test.failure", pvt.getValue()));
                }
                else {
                    response.put("success", true);
                    response.put("message", LocalizableMessage.getMessage(Common.getBundle(request), "dsEdit.meta.test.success", pvt.getValue()));
                }
                response.put("result", pvt.getStringValue());

            } else {
                if (script == "") {
                    response.put("success", false);
                    response.put("message", LocalizableMessage.getMessage(Common.getBundle(request), "dsEdit.meta.test.failure", pvt.getValue(), DateFunctions.getTime(pvt.getTime())));
                }
                else {
                    response.put("success", true);
                    response.put("message", LocalizableMessage.getMessage(Common.getBundle(request), "dsEdit.meta.test.successTs", pvt.getValue(), DateFunctions.getTime(pvt.getTime())));
                }
                response.put("result", pvt.getStringValue());

            }
        } catch (DataPointStateException | ResultTypeException e) {
            response.put("success", false);
            response.put("result", "failed");
            response.put("message", e.getLocalizableMessage() != null ? e.getLocalizableMessage().getLocalizedMessage(Common.getBundle(request)) : e.getMessage());
            LOG.warn(infoErrorExecutionScript(e, "validateScript: " + script));
        } catch (Exception e) {
            response.put("success", false);
            response.put("result", "failed");
            response.put("message", LocalizableMessage.getMessage(Common.getBundle(request), "dsEdit.meta.test.scriptError", e.getMessage()));
            LOG.warn(infoErrorExecutionScript(e, "validateScript: " + script));
        }
    }
}
