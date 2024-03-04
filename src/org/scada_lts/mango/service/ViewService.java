/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.mango.service;

/** 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

import br.org.scadabr.vo.exporter.util.FileUtil;
import com.serotonin.mango.view.ImageSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.*;
import org.scada_lts.dao.model.IdName;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.*;

import org.scada_lts.web.mvc.api.dto.ImageSetIdentifier;
import org.scada_lts.web.mvc.api.dto.UploadImage;
import org.scada_lts.web.beans.ApplicationBeans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import static java.util.stream.Collectors.toList;
import static org.scada_lts.utils.PathSecureUtils.getPartialPath;
import static org.scada_lts.utils.PathSecureUtils.toSecurePath;
import static org.scada_lts.utils.UploadFileUtils.*;
import static org.scada_lts.utils.StaticImagesUtils.getUploadsSystemFilePath;

@Service
public class ViewService {

	private Log LOG = LogFactory.getLog(ViewService.class);

	private final IViewDAO viewDAO;
	private final GetShareUsers<View> viewGetShareUsers;
	private final GetObjectsWithAccess<View, User> getViewsWithAccess;

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public ViewService() {
		this.viewDAO = ApplicationBeans.getViewDaoBean();
		this.viewGetShareUsers = new ViewGetShareUsers(this.viewDAO);
		this.getViewsWithAccess = new GetViewsWithAccess(this.viewDAO);
	}

	public ViewService(IViewDAO viewDAO) {
		this.viewDAO = viewDAO;
		this.viewGetShareUsers = new ViewGetShareUsers(viewDAO);
		this.getViewsWithAccess = new GetViewsWithAccess(viewDAO);
	}

	public List<View> getViews() {
		List<View> views = viewDAO.findAll();
		for (View view: views) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return views;
	}

	public List<View> getViews(int userId, int userProfileId) {
		List<View> views = getViewsWithAccess.getObjectsWithAccess(User.onlyIdAndProfile(userId, userProfileId));
		for (View view: views) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return views;
	}

	public List<ScadaObjectIdentifier> getAllViewsForUser(User user) {
		return getViewsWithAccess.getObjectIdentifiersWithAccess(user);
	}
	
	public List<IdName> getViewNames(int userId, int userProfileId) {
		return toIdNames(getViewIdentifiers(userId, userProfileId));
	}
	
	public List<IdName> getAllViewNames() {
		return toIdNames(viewDAO.findIdentifiers());
	}

	public View getView(int id) {
		View view = viewDAO.findById(id);
		if(view != null) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return view;
	}

	public View getViewByXid(String xid) {
		View view = viewDAO.findByXid(xid);
		if(view != null) {
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		}
		return view;
	}
	
	public View getView(String name) {
		View view = viewDAO.findByName(name);
		
		if (view == null) {
			return null;
		}
		view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		return view;
	}

	public List<ShareUser> getShareUsers(View view) {
		return viewGetShareUsers.getShareUsersWithProfile(view);
	}

	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(View.XID_PREFIX, "mangoViews");
	}
	
	public boolean isXidUnique(String xid, int exludeId) {
		return DAO.getInstance().isXidUnique(xid, exludeId, "mangoViews");
	}
	
	public void saveView(final View view) {
		LOG.debug("View name: " + view.getName());
		if (view.getId() == Common.NEW_ID) {
			viewDAO.save(view);
		} else {
			viewDAO.update(view);
		}
	}

	public int saveViewAPI(View view) throws IOException {
		LOG.debug("View name: " + view.getName());
		String backgroundFilename = view.getBackgroundFilename();
		setWidthAndHeight(view, backgroundFilename);
		int id = -1;
		if (view.getId() == Common.NEW_ID) {
			id = viewDAO.save(view).getId();
		} else {
			viewDAO.update(view);
		}
		return id;
	}

	private void setWidthAndHeight(View view, String backgroundFilename) throws IOException {
		if (backgroundFilename != null && !backgroundFilename.isEmpty()) {
			UploadImage uploadImage = createUploadImage(getUploadsSystemFilePath(Paths.get(backgroundFilename)).toFile());
			view.setHeight(uploadImage.getHeight());
			view.setWidth(uploadImage.getWidth());
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void removeView(final int viewId) {
		viewDAO.delete(viewId);
	}
	
	public void removeUserFromView(int viewId, int userId) {
		viewDAO.deleteViewForUser(viewId, userId);
	}


	public List<ScadaObjectIdentifier> getSimpleViews() {
		return viewDAO.findIdentifiers();
	}

	public List<ImageSetIdentifier> getImageSets() {
		List<ImageSetIdentifier> images = new ArrayList<>();
		for (ImageSet imageSet : Common.ctx.getImageSets()) {
			if (!imageSet.isDynamicImage()) {
				ImageSetIdentifier imageSetIdentifier = new ImageSetIdentifier(imageSet.getId(), imageSet.getName(), imageSet.getImageCount());
				images.add(imageSetIdentifier);
			}
		}
		return images;
	}

	public ImageSet getImageSet(String id) {
		return Common.ctx.getImageSet(id);
	}

	public List<UploadImage> getUploadImages() {
		List<UploadImage> files = new ArrayList<>();
		for(Path path: getUploadsSystemFilePaths()) {
			files.addAll(getUploadImages(path));
		}
		return files;
	}

	private List<UploadImage> getUploadImages(Path directory) {
		List<File> files = filteringUploadFiles(FileUtil.getFilesOnDirectory(directory));

		List<UploadImage> images = new ArrayList<>();
		for (File file : files) {
			images.add(createUploadImage(file));
		}

		return images;
	}

	public Optional<UploadImage> uploadBackgroundImage(MultipartFile multipartFile) {
		if(!isToUploads(multipartFile)) {
			return Optional.empty();
		}
		Path path = Paths.get(getUploadsSystemFileToWritePath() + FILE_SEPARATOR + multipartFile.getOriginalFilename());
		return toSecurePath(path)
				.flatMap(dist -> transferTo(multipartFile, dist))
				.map(this::createUploadImage);
	}

	private UploadImage createUploadImage(File file) {
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(file);
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		}
		int width = -1;
		int height = -1;
		if(bimg != null) {
			width = bimg.getWidth();
			height = bimg.getHeight();
		}
		return new UploadImage(file.getName(), getPartialPath(file), width, height);
	}

	public boolean checkUserViewPermissions(User user, View view) {
		return GetViewsWithAccess.hasViewReadPermission(user, view);
	}

	private static Optional<File> transferTo(MultipartFile multipartFile, File file) {
		try {
			multipartFile.transferTo(file);
			return Optional.of(file);
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private List<ScadaObjectIdentifier> getViewIdentifiers(int userId, int userProfileId) {
		return getViewsWithAccess.getObjectIdentifiersWithAccess(User.onlyIdAndProfile(userId, userProfileId));
	}

	private List<IdName> toIdNames(List<ScadaObjectIdentifier> identifiers) {
		return identifiers.stream().map(a -> new IdName(a.getId(), a.getName()))
				.collect(toList());
	}
}
