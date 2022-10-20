/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.view.ViewDTO;
import org.scada_lts.dao.model.view.ViewDTOValidator;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.web.mvc.api.dto.ImageSetIdentifier;
import org.scada_lts.web.mvc.api.dto.UploadImage;
import org.scada_lts.web.mvc.api.dto.view.GraphicalViewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.scada_lts.utils.MailingListApiUtils.*;
import static org.scada_lts.utils.ValidationUtils.formatErrorsJson;
import static org.scada_lts.utils.ValidationUtils.validId;
import static org.scada_lts.utils.ViewApiUtils.*;

/**
 * @author Arkadiusz Parafiniuk arkadiusz.parafiniuk@gmail.com
 */
@Controller
@RequestMapping(path = "/api/view")
public class ViewAPI {

    private static final Log LOG = LogFactory.getLog(ViewAPI.class);
    private static final String NULL_IMAGE_PATH = "null";

    @Resource
    ViewService viewService;

    @GetMapping(value = "/getAll")
    public ResponseEntity<List<ScadaObjectIdentifier>> getAll(HttpServletRequest request) {
        LOG.info("/api/view/getAll");
        try {
            User user = Common.getUser(request);
            if (user != null && user.isAdmin()) {
                return new ResponseEntity<>(viewService.getAllViews(),HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getModificationTime/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getModificationTime(@PathVariable("id") Integer id, HttpServletRequest request) {
        LOG.info("/api/view/getModificationTime/{id} id:"+id);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class ViewJSON implements Serializable {
                    private long id;
                    private long mtime;

                    ViewJSON(long id, long mtime) {
                        this.setId(id);
                        this.setMtime(mtime);
                    }

                    public long getId() {
                        return id;
                    }

                    public void setId(long id) {
                        this.id = id;
                    }

                    public long getMtime() {
                        return mtime;
                    }

                    public void setMtime(long mtime) {
                        this.mtime = mtime;
                    }
                }

                View view = new View();
                if (user.isAdmin()) {
                    view = viewService.getView(id);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }


                ViewJSON viewJSON = new ViewJSON(view.getId(), view.getModificationTime());

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(viewJSON);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getByXid/{xid}", method = RequestMethod.GET)
    public ResponseEntity<String> getByXid(@PathVariable("xid") String xid, HttpServletRequest request) {
        LOG.info("/api/view/getByXid/{xid} xid:"+xid);

        try {
            User user = Common.getUser(request);

            if (user != null) {
                class ViewJSON implements Serializable {
                    private long id;
                    private String xid;
                    private long mtime;

                    ViewJSON(long id, String xid, long mtime) {
                        this.setId(id);
                        this.setXid(xid);
                        this.setMtime(mtime);
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

                    public long getMtime() {
                        return mtime;
                    }

                    public void setMtime(long mtime) {
                        this.mtime = mtime;
                    }
                }

                View view = new View();
                if (user.isAdmin()) {
                    view = viewService.getViewByXid(xid);
                } else {
                    return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
                }


                ViewJSON viewJSON = new ViewJSON(view.getId(), view.getXid(), view.getModificationTime());

                String json = null;
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(viewJSON);

                return new ResponseEntity<String>(json, HttpStatus.OK);
            }

            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/createView", method = RequestMethod.POST)
    public ResponseEntity<String> createView(HttpServletRequest request, @RequestBody ViewDTO viewDTO) {
        LOG.info("/api/view/createView");

        ResponseEntity<String> result;
        ViewDTOValidator validator = new ViewDTOValidator();

        BindException errors = new BindException(viewDTO, "viewDTO");
        validator.validate(viewDTO, errors);


        try {
            class ViewJSON implements Serializable {
                private static final long serialVersionUID = 8076556272526329317L;
                private String name;
                private String xid;
                private int userId;
                private int resolution;
                private String filename;

                public ViewJSON(String name, String xid, int userId, int resolution, String filename) {
                    this.name = name;
                    this.xid = xid;
                    this.userId = userId;
                    this.resolution = resolution;
                    this.filename = filename;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getXid() {
                    return xid;
                }

                public void setXid(String xid) {
                    this.xid = xid;
                }

                public int getUserId() {
                    return userId;
                }

                public void setUserId(int userId) {
                    this.userId = userId;
                }

                public int getResolution() {
                    return resolution;
                }

                public void setResolution(int resolution) {
                    this.resolution = resolution;
                }

                public String getFilename() {
                    return filename;
                }

                public void setFilename(String filename) {
                    this.filename = filename;
                }
            }

            User user = Common.getUser(request);

            if (!errors.hasErrors()) {
                if (user.isAdmin()) {

                    View view = new View();
                    view.setName(viewDTO.getName());
                    view.setXid(viewDTO.getXid());
                    view.setResolution(viewDTO.getSize());
                    if(!viewDTO.getImagePath().equals(NULL_IMAGE_PATH)) {
                        view.setBackgroundFilename(viewDTO.getImagePath());
                    } else {
                        view.setBackgroundFilename(null);
                    }
                    view.setUserId(user.getId());

                    viewService.saveView(view);

                    ObjectMapper mapper = new ObjectMapper();
                    String json = null;
                    ViewJSON viewJSON = new ViewJSON(
                            view.getName(),
                            view.getXid(),
                            view.getUserId(),
                            view.getResolution(),
                            view.getBackgroundFilename()
                    );

                    json = mapper.writeValueAsString(viewJSON);

                    result = new ResponseEntity<String>(json, HttpStatus.OK);
                } else {
                    result = new ResponseEntity<String>("Acces unauthorized, logged user has no admin permissions ",HttpStatus.UNAUTHORIZED);
                }
            } else {
                result = new ResponseEntity<String>("Invalid arguments in JSON file " , HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } catch (Exception e) {
            LOG.error(e);
            result = new ResponseEntity<String>("Something went wrong ",HttpStatus.BAD_REQUEST);
        }
        return result;

    }

    @GetMapping(value = "/getAllForUser")
    public ResponseEntity<List<ScadaObjectIdentifier>> getAllForUser(HttpServletRequest request) {
        LOG.info("/api/view/getAllForUser");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(viewService.getAllViewsForUser(user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "")
    public ResponseEntity<View> getView(@RequestParam(required = false) Integer id,
                                          @RequestParam(required = false) String xid,
                                          HttpServletRequest request) {
        LOG.info("/api/view");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validId(id, xid);
                if(!error.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                Optional<View> view = getViewByIdOrXid(id, xid, viewService);

                if (view.isPresent()) {
                    if (viewService.checkUserViewPermissions(user, view.get())){
                        return new ResponseEntity<>(view.get(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<Map<String, String>> createView(@RequestBody GraphicalViewDTO viewDTO, HttpServletRequest request) {
        LOG.info("/api/view");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validateGraphicalViewDTO(viewDTO, user);
                if (!error.isEmpty()) {
                    Map<String, String> response = new HashMap<>();
                    response.put("errors", error);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                View view = viewDTO.createViewFromBody(user);
                Map<String, String> response = new HashMap<>();
                response.put("viewId", String.valueOf(viewService.saveViewAPI(view)));
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "")
    public ResponseEntity<String> updateView(@RequestBody GraphicalViewDTO viewDTO, HttpServletRequest request) {
        LOG.info("/api/view");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validateGraphicalViewUpdate(viewDTO, user);
                if (!error.isEmpty()) {
                    return ResponseEntity.badRequest().body(formatErrorsJson(error));
                }
                return findAndUpdateView(viewDTO, user);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "")
    public ResponseEntity<String> deleteView(@RequestParam(required = false) Integer id,
                                        @RequestParam(required = false) String xid,
                                        HttpServletRequest request) {
        LOG.info("/api/view");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validId(id, xid);
                if(!error.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                deleteViewByIdOrXid(id, xid, viewService);
                return new ResponseEntity<>("deleted", HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/imageSets")
    public ResponseEntity<List<ImageSetIdentifier>> getImageSets(HttpServletRequest request) {
        LOG.info("/api/view/imageSets");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(viewService.getImageSets(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/imageSets/{id}")
    public ResponseEntity<ImageSet> getImageSet(@PathVariable String id, HttpServletRequest request) {
        LOG.info("/api/view/imageSets/{id}");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                String error = validId(id);
                if(!error.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                ImageSet imageSet = viewService.getImageSet(id);
                return new ResponseEntity<>(imageSet, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/uploads")
    public ResponseEntity<List<UploadImage>> getUploads(HttpServletRequest request) {
        LOG.info("/api/view/uploads");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return new ResponseEntity<>(viewService.getUploadImages(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/uploads")
    public ResponseEntity<UploadImage> uploadBackgroundImage(@RequestPart MultipartFile file, HttpServletRequest request) {
        LOG.info("/api/view");
        try {
            User user = Common.getUser(request);
            if (user != null) {
                return viewService.uploadBackgroundImage(file)
                        .map(uploadImage -> new ResponseEntity<>(uploadImage, HttpStatus.CREATED))
                        .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/generateXid")
    public ResponseEntity<String> getUniqueXid(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null && user.isAdmin()) {
                return new ResponseEntity<>(viewService.generateUniqueXid(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<Map<String, Object>> isXidUnique(
            @RequestParam String xid,
            @RequestParam Integer id,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("unique", viewService.isXidUnique(xid, id));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> findAndUpdateView(GraphicalViewDTO body, User user) {
        return getGraphicalView(body.getId(), viewService)
                .map(toUpdate -> updateGraphicalView(toUpdate, body, user)).
                orElse(new ResponseEntity<>(formatErrorsJson("View not found"), HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<String> updateGraphicalView(View toUpdate, GraphicalViewDTO body, User user) {
        if (isXidChanged(toUpdate.getXid(), body.getXid()) &&
                isViewPresent(body.getXid(), viewService)){
            return new ResponseEntity<>(formatErrorsJson("This XID is already in use"), HttpStatus.BAD_REQUEST);
        }
        updateValueGraphicalView(toUpdate, body, user);
        try {
            viewService.saveViewAPI(toUpdate);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Saving failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("{\"status\":\"updated\"}", HttpStatus.OK);
    }

}
