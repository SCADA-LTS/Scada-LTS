/**
 * 
 */
package com.serotonin.mango.web.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.EnhancedImageChartComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

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
        ViewDao viewDao = new ViewDao();
        User user = Common.getUser(request);
        View view = viewDao.getView(viewId);
        if (view == null) {
            view = user.getView();
        }
        if (view != null) {
            Permissions.ensureViewPermission(user, view);

            // Make sure the owner still has permission to all of the points in the view, and that components are
            // otherwise valid.
            view.validateViewComponents(false);
            ViewComponent vc = view.getViewComponent(viewComponentId);
            if (vc instanceof EnhancedImageChartComponent) {
                EnhancedImageChartComponent chart = (EnhancedImageChartComponent) vc;
                String data = chart.generateImageChartData(true);
                IOUtils.write(data, response.getOutputStream());
            }
        }
    }
}
