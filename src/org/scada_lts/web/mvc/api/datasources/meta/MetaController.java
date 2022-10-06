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

@Controller
@RequestMapping(value = "/api/datasources/meta")
public class MetaController {

    private static final Log LOG = LogFactory.getLog(MetaController.class);

    @PostMapping(value = "/testMeta")
    public ResponseEntity<Map<Object, Object>> testMeta(
            @RequestBody MetaPointLocatorJson data,
            HttpServletRequest request
    ) {
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            Map<Object, Object> response = new HashMap<>();
            validateScript(data.getScript(), data.getContext(), data.getDataTypeId(), response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    public void validateScript(String script, List<IntValuePair> context, int dataTypeId, Map<Object, Object> response) {
        ScriptExecutor executor = new ScriptExecutor();
        try {
            Map<String, IDataPoint> convertedContext = executor
                    .convertContext(context);
            PointValueTime pvt = executor.execute(script, convertedContext,
                    System.currentTimeMillis(), dataTypeId, -1);
            if(pvt.getTime() == -1) {
                response.put("response", LocalizableMessage.getMessage(Common.getBundle(), "dsEdit.meta.test.success", pvt.getValue()));
            } else {
                response.put("response", LocalizableMessage.getMessage(Common.getBundle(), "dsEdit.meta.test.successTs", pvt.getValue(), DateFunctions.getTime(pvt.getTime())));
            }
        } catch (DataPointStateException | ResultTypeException e) {
            response.put("response", e.getLocalizableMessage());
            LOG.warn(infoErrorExecutionScript(e, "validateScript: " + script));
        } catch (Exception e) {
            response.put("response", LocalizableMessage.getMessage(Common.getBundle(), "dsEdit.meta.test.scriptError", e.getMessage()));
            LOG.warn(infoErrorExecutionScript(e, "validateScript: " + script));
        }
    }
}
