package org.scada_lts.web.mvc.api.components.synoptic_panel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.synopticpanel.SynopticPanelDTO;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.service.SynopticPanelService;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SynopticPanelAPI {

    @Resource
    SynopticPanelService synopticPanelService;

    @RequestMapping(value = "/api/synoptic-panel/time", method = RequestMethod.GET)
    public ResponseEntity<String> getSynopticPanel(HttpServletRequest request) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/api/synoptic-panel/list", method = RequestMethod.GET)
    public ResponseEntity<String> getPanelList(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);

            if(user != null) {
                List<SynopticPanel> listSynopticPanels;
                listSynopticPanels = synopticPanelService.getPanelSelectList();
                List<SynopticPanelListJSON> list = new ArrayList<>();
                for(SynopticPanel sp:listSynopticPanels) {
                    SynopticPanelListJSON splJSON = new SynopticPanelListJSON(
                            sp.getId(), sp.getXid(), sp.getName()
                    );
                    list.add(splJSON);
                }
                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(list);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/synoptic-panel/getId/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getSynopticPanel(@PathVariable("id") int id, HttpServletRequest request) {

        try {
            User user = Common.getUser(request);
            if(user != null) {
                SynopticPanel sp = synopticPanelService.getSynopticPanel(id);
                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(sp);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
//            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/synoptic-panel/deleteId/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> deleteSynopticPanel(@PathVariable("id") int id, HttpServletRequest request) {

        try {
            User user = Common.getUser(request);
            if(user != null) {
                SynopticPanel synopticPanel = new SynopticPanel();
                synopticPanel.setId(id);
                synopticPanelService.deletePanel(synopticPanel);
                return new ResponseEntity<String>("Done", HttpStatus.OK);
            }
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
//            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }



    @RequestMapping(value = "/api/synoptic-panel/create", method = RequestMethod.POST)
    public ResponseEntity<String> createSynopticPanel(HttpServletRequest request, @RequestBody SynopticPanelDTO synopticPanelDTO) {
        ResponseEntity<String> result;

        try {
            User user = Common.getUser(request);
            if(user.isAdmin()) {
                SynopticPanel synopticPanel = new SynopticPanel();
                synopticPanel.setId(synopticPanelDTO.getId());
                synopticPanel.setName(synopticPanelDTO.getName());
                synopticPanel.setXid(synopticPanelDTO.getXid());
                synopticPanel.setVectorImage(synopticPanelDTO.getVectorImage());
                synopticPanel.setComponentData(synopticPanelDTO.getComponentData());
                synopticPanelService.savePanel(synopticPanel);
                result = new ResponseEntity<String>("Done", HttpStatus.OK);
            } else {
                result = new ResponseEntity<String>("Not Logged In", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            result = new ResponseEntity<String>("Something went wrong ",HttpStatus.BAD_REQUEST);
        }
        return result;

    }


}

class SynopticPanelListJSON implements Serializable {
    private long id;
    private String xid;
    private String name;

    SynopticPanelListJSON(long id, String xid, String name) {
        this.setId(id);
        this.setXid(xid);
        this.setName(name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



