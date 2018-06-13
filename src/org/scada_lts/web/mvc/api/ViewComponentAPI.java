package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.view.component.SimplePointComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.web.mvc.api.dto.ViewComponentDTO;
import org.scada_lts.web.mvc.api.dto.ViewHTMLComponentDTO;
import org.scada_lts.web.mvc.api.dto.ViewSimplePointComponentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
@Controller
public class ViewComponentAPI {

    private static final Log LOG = LogFactory.getLog(ViewComponentAPI.class);

    @Resource
    ViewService viewService;

    @Resource
    DataPointService dataPointService;

    @RequestMapping(value = "/api/component/getAllComponentsFromView/{xid}", method = RequestMethod.GET)
    public ResponseEntity<String> getAllComponentsFromView(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/component/addComponentToView/{xid} xid:" + xid);

        ResponseEntity<String> result;

        try {
            User user = Common.getUser(request);

            if (user.isAdmin()) {
                View view = viewService.getViewByXid(xid);

                List<ViewComponent> viewComponentList = new ArrayList<>();
                viewComponentList = view.getViewComponents();

                List<ViewComponentDTO> viewComponentDTOList = new ArrayList<>();
                for (ViewComponent vc : viewComponentList) {
                    ViewComponentDTO viewComponentDTO = new ViewComponentDTO(vc.getId(), vc.getIndex(), vc.getDefName(), vc.getIdSuffix(), vc.getStyle(), vc.getX(), vc.getY());
                    viewComponentDTOList.add(viewComponentDTO);
                }

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(viewComponentDTOList);
                result = new ResponseEntity<String>(json, HttpStatus.OK);
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        return result;


    }

    @RequestMapping(value = "/api/component/addHTMLComponentToView/{xid}", method = RequestMethod.POST)
    public ResponseEntity<String> addHTMLComponentToView(@PathVariable("xid") String xid, HttpServletRequest request, @RequestBody ViewHTMLComponentDTO viewHTMLComponentDTO) {
        LOG.info("/api/component/addHTMLComponentToView/{xid} xid:" + xid);

        ResponseEntity<String> result;

        try {
            User user = Common.getUser(request);

            if (user.isAdmin()) {
                View view = viewService.getViewByXid(xid);

                view.setViewUsers(viewService.getShareUsers(view));

                HtmlComponent htmlComponent = new HtmlComponent();

                convertJSONToObject(viewHTMLComponentDTO, htmlComponent);

                htmlComponent.setContent(viewHTMLComponentDTO.getContent());

                view.addViewComponent(htmlComponent);

                viewService.saveView(view);

                result = new ResponseEntity<String>(HttpStatus.OK);
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        return result;
    }

    @RequestMapping(value = "/api/component/addSimplePointComponentToView/{xid}", method = RequestMethod.POST)
    public ResponseEntity<String> addSimplePointComponentToView(@PathVariable("xid") String xid, HttpServletRequest request, @RequestBody ViewSimplePointComponentDTO viewSimplePointComponentDTODTO) {
        LOG.info("/api/component/addSimplePointComponentToView/{xid} xid:" + xid);

        ResponseEntity<String> result;

        try {
            User user = Common.getUser(request);

            if (user.isAdmin()) {
                View view = viewService.getViewByXid(xid);

                view.setViewUsers(viewService.getShareUsers(view));

                SimplePointComponent simplePointComponent = new SimplePointComponent();

                convertJSONToObject(viewSimplePointComponentDTODTO, simplePointComponent);

                simplePointComponent.setBkgdColorOverride(viewSimplePointComponentDTODTO.getBkgdColorOverride());
                simplePointComponent.setDisplayControls(viewSimplePointComponentDTODTO.isDisplayControls());
                simplePointComponent.setNameOverride(viewSimplePointComponentDTODTO.getNameOverride());
                simplePointComponent.setSettableOverride(viewSimplePointComponentDTODTO.isSettableOverride());
                simplePointComponent.tsetDataPoint(dataPointService.getDataPoint(viewSimplePointComponentDTODTO.getDataPointXid()));
                simplePointComponent.setDisplayPointName(viewSimplePointComponentDTODTO.isDisplayPointName());
                simplePointComponent.setStyleAttribute(viewSimplePointComponentDTODTO.getStyleAttribute());

                view.addViewComponent(simplePointComponent);

                System.out.println(simplePointComponent.toString());

                viewService.saveView(view);

                result = new ResponseEntity<String>(HttpStatus.OK);
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        return result;
    }

    private ViewComponent convertJSONToObject(ViewComponentDTO viewComponentDTO, ViewComponent viewComponent) {
        viewComponent.setIndex(viewComponentDTO.getIndex());
        viewComponent.setIdSuffix(viewComponentDTO.getIdSuffix());
        viewComponent.setLocation(viewComponentDTO.getX(), viewComponentDTO.getY());
        viewComponent.setStyle(viewComponentDTO.getStyle());
        viewComponent.setX(viewComponentDTO.getX());
        viewComponent.setY(viewComponentDTO.getY());
        return viewComponent;
    }

}
