/**
 * 
 */
package com.serotonin.mango.web.servlet;

import com.serotonin.mango.Common;
import com.serotonin.mango.dao_cache.DaoInstances;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Data servlet for EnhancedImageChart.
 * 
 * @author Jacek Rogoznicki
 */
public class EnhancedImageChartServlet extends BaseInfoServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int viewId = getIntRequestParameter(request, "viewId", -1);
        int viewComponentId = getIntRequestParameter(request, "vcId", -1);
        User user = Common.getUser(request);
        View view = DaoInstances.ViewDao.getView(viewId);
        if (view == null) {
            view = user.getView();
        }
    }
}
