/*
 * (c) 2018 grzegorz.bylica@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.service.pointhierarchy.PointHierarchyXidService;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchy;
import org.scada_lts.web.mvc.api.dto.FolderPointHierarchyExport;
import org.scada_lts.web.mvc.api.dto.PointHierarchyConsistencyCheck;
import org.scada_lts.web.mvc.api.dto.PointHierarchyExp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by at Grzesiek Bylica
 *
 * @author grzegorz.bylica@gmail.com
 */
@Controller
public class PointHierarchyAPI {

    private static final Log LOG = LogFactory.getLog(PointHierarchyAPI.class);

    private final PointHierarchyXidService pointHierarchyXidService;

    public PointHierarchyAPI(PointHierarchyXidService pointHierarchyXidService) {
        this.pointHierarchyXidService = pointHierarchyXidService;
    }

    @RequestMapping(value = "/api/pointHierarchy/pointMoveTo/{xid_point}/{xid_folder}", method = RequestMethod.PUT)
    public ResponseEntity<String> pointMoveTo(
            @PathVariable("xid_point") String xidPoint,
            @PathVariable("xid_folder") String xidFolder,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/pointMoveTo xidPoint:" + xidPoint + " xidFolder:" + xidFolder  );
        ResponseEntity<String> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                if (pointHierarchyXidService.movePoint(xidPoint, xidFolder)) {
                    result = new ResponseEntity<String>(String.valueOf(true), HttpStatus.OK);
                } else {
                    result = new ResponseEntity<String>(String.valueOf(false), HttpStatus.OK);
                }
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/folderMoveTo/{xid_folder}/{new_parent_xid_folder}", method = RequestMethod.PUT)
    public ResponseEntity<String> folderMoveTo(
            @PathVariable("xid_folder") String xidFolder,
            @PathVariable("new_parent_xid_folder") String newParentXidFolder,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/folderMoveTo xidFolder:" + xidFolder + " newParentXidFolder:" + newParentXidFolder);
        ResponseEntity<String> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {

                if (pointHierarchyXidService.moveFolder(xidFolder, newParentXidFolder)) {
                    result = new ResponseEntity<String>(String.valueOf(true), HttpStatus.OK);
                } else {
                    result = new ResponseEntity<String>(String.valueOf(false), HttpStatus.OK);
                }
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/changeName/{xid_folder}/{new_name}", method = RequestMethod.PUT)
    public ResponseEntity<String> folderUpdateName(
            @PathVariable("xid_folder") String xidFolder,
            @PathVariable("new_name") String newName,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/changeName/{xid_folder}/{new_name} xidFolder:" + xidFolder + " newName:" + newName);
        ResponseEntity<String> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                if (pointHierarchyXidService.updateNameFolder(xidFolder, newName)){
                    result = new ResponseEntity<String>(String.valueOf(true), HttpStatus.OK);
                } else {
                    result = new ResponseEntity<String>(String.valueOf(false), HttpStatus.OK);
                }
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/folderAdd", method = RequestMethod.POST)
    public ResponseEntity<String> folderAdd(
            @RequestBody FolderPointHierarchy folderPointHierarchy,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/folderAdd folderPointHierarchy:" + folderPointHierarchy);
        ResponseEntity<String> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                pointHierarchyXidService.folderAdd(folderPointHierarchy);
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

    @RequestMapping(value = "/api/pointHierarchy/folderCheckExist/{xid_folder}", method = RequestMethod.GET)
    public ResponseEntity<FolderPointHierarchy> folderCheckExist(
            @PathVariable("xid_folder") String xidFolder,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/folderCheckExist xidFolder:" + xidFolder);
        ResponseEntity<FolderPointHierarchy> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                FolderPointHierarchy fph = pointHierarchyXidService.folderCheckExist(xidFolder);
                result = new ResponseEntity<FolderPointHierarchy>(fph, HttpStatus.OK);
            } else {
                result = new ResponseEntity<FolderPointHierarchy>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<FolderPointHierarchy>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/export", method = RequestMethod.GET)
    public ResponseEntity<PointHierarchyExp> exportData(HttpServletRequest request) {

        LOG.info("/api/pointHierarchy/exportData");
        ResponseEntity<PointHierarchyExp> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                List<FolderPointHierarchy> lfph = pointHierarchyXidService.getFolders();
                List<FolderPointHierarchyExport> nlfph = pointHierarchyXidService.fillInThePoints(lfph);

                result = new ResponseEntity<PointHierarchyExp>(new PointHierarchyExp(nlfph), HttpStatus.OK);
            } else {
                result = new ResponseEntity<PointHierarchyExp>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<PointHierarchyExp>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/deleteFolder/{xid_folder}", method = RequestMethod.POST)
    public ResponseEntity<String> deleteFolder(
            @PathVariable("xid_folder") String xidFolder,
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/deleteFolder xidFolder:" + xidFolder);
        ResponseEntity<String> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                pointHierarchyXidService.deleteFolderXid(xidFolder);
            } else {
                result = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/cacheRefresh", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> cacheRefresh(HttpServletRequest request) {
        LOG.info("/api/pointHierarchy/cacheRefresh");
        ResponseEntity<Map<String, String>> result = null;
        Map<String, String> empty = new HashMap<>();
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                pointHierarchyXidService.cacheRefresh();
                result = new ResponseEntity<>(empty, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>(empty, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<>(empty, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/api/pointHierarchy/checkConsitencyPointHierarchy", method = RequestMethod.GET)
    public ResponseEntity<PointHierarchyConsistencyCheck> checkConsistencyPointHierarchy(
            HttpServletRequest request)  {

        LOG.info("/api/pointHierarchy/checkConsitencyPointHierarchy");
        ResponseEntity<PointHierarchyConsistencyCheck> result = null;
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                PointHierarchyConsistencyCheck phcc = new PointHierarchyConsistencyCheck();
                phcc.setPoints(pointHierarchyXidService.checkPointHierarchyConsistency());
                result = new ResponseEntity<PointHierarchyConsistencyCheck>(phcc, HttpStatus.OK);
            } else {
                result = new ResponseEntity<PointHierarchyConsistencyCheck>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<PointHierarchyConsistencyCheck>(HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @GetMapping(value = "/api/pointHierarchy/root")
    public ResponseEntity<PointHierarchyNode> getPointHierarchyRoot(HttpServletRequest request)  {

        LOG.info(request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                PointHierarchyNode root = pointHierarchyXidService.getPointHierarchyRoot(user);
                return ResponseEntity.ok(root);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/api/pointHierarchy/root/withEmptyDir")
    public ResponseEntity<PointHierarchyNode> getPointHierarchyWithEmptyRoot(HttpServletRequest request)  {

        LOG.info(request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                PointHierarchyNode withEmptyRoot = pointHierarchyXidService.getPointHierarchyWithEmptyRoot(user);
                return ResponseEntity.ok(withEmptyRoot);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/api/pointHierarchy/{key}")
    public @ResponseBody ResponseEntity<List<PointHierarchyNode>> getPointHierarchy(@PathVariable("key") int key, HttpServletRequest request) {
        LOG.info(request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<PointHierarchyNode> nodes = pointHierarchyXidService.getPointHierarchyByKey(user, key);
                return ResponseEntity.ok(nodes);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/api/pointHierarchy/{key}/withEmptyDir")
    public @ResponseBody ResponseEntity<List<PointHierarchyNode>> getPointHierarchyWithEmpty(@PathVariable("key") int key, HttpServletRequest request) {
        LOG.info(request.getRequestURI());
        try {
            User user = Common.getUser(request);
            if(user != null) {
                List<PointHierarchyNode> withEmptyNodes = pointHierarchyXidService.getPointHierarchyWithEmptyByKey(user, key);
                return ResponseEntity.ok(withEmptyNodes);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
