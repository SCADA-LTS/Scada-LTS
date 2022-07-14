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
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import static java.util.stream.Collectors.toList;
import static org.scada_lts.utils.PathSecureUtils.getPartialPath;
import static org.scada_lts.utils.PathSecureUtils.getRealPath;
import static org.scada_lts.utils.PathSecureUtils.toSecurePath;
import static org.scada_lts.utils.UploadFileUtils.filteringUploadFiles;
import static org.scada_lts.utils.UploadFileUtils.isToUploads;

@Service
public class ViewService {

	private Log LOG = LogFactory.getLog(ViewService.class);

	private final IViewDAO viewDAO;
	@Deprecated
	private static Map<Integer, List<IdName>> usersPermissions = new HashMap<Integer, List<IdName>>();
	private final GetShareUsers<View> viewGetShareUsers;
	private final GetObjectsWithAccess<View, User> getViewsWithAccess;

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public ViewService() {
		this.viewDAO = ApplicationBeans.getViewDaoBean();
		this.viewGetShareUsers = ApplicationBeans.getViewGetShareUsersBean();
		this.getViewsWithAccess = new GetViewsWithAccess(this.viewDAO);
	}

	public ViewService(IViewDAO viewDAO, ViewGetShareUsers viewGetShareUsers) {
		this.viewDAO = viewDAO;
		this.viewGetShareUsers = viewGetShareUsers;
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

	public List<ScadaObjectIdentifier> getAllViews() {
		return viewDAO.findIdentifiers();
	}

	public List<ScadaObjectIdentifier> getAllViewsForUser(User user) {
		if (user.isAdmin())
			return viewDAO.findIdentifiers();
		List<View> views = getViews(user.getId(), user.getUserProfile());
		List<ScadaObjectIdentifier> simpleList = new ArrayList<>();
		for (View view : views) {
			simpleList.add(new ScadaObjectIdentifier(view.getId(),view.getXid(), view.getName()));
		}
		return simpleList;
	}
	
	public List<IdName> getViewNames(int userId, int userProfileId) {
		return toIdNames(getViewIdentifiers(userId, userProfileId));
	}
	
	public List<IdName> getAllViewNames() {
		return toIdNames(viewDAO.findIdentifiers());
	}

	@Deprecated
	public List<IdName> getViewNamesWithReadOrWritePermissions(
			int userId, int userProfileId) {
		List<IdName> allPermissions = usersPermissions.get(userId);
		if (allPermissions == null) {
			allPermissions = updateViewUsersPermissions(userId, userProfileId);
		}
		return allPermissions;
	}

	@Deprecated
	private List<IdName> updateViewUsersPermissions(int userId,
			int userProfileId) {

		List<IdName> allPermissions = toIdNames(getViewIdentifiers(userId, userProfileId));

		User user = new UserDao().getUser(userId);

		for (Iterator<IdName> iterator = allPermissions.iterator(); iterator.hasNext();) {

			IdName idDaViewComView = (IdName) iterator.next();

			View view = viewDAO.findById(idDaViewComView.getId());

			if (view.getUserAccess(user) == ShareUser.ACCESS_NONE) {
				iterator.remove();
			}
		}
		//usersPermissions.put(userId, allPermissions);
		return allPermissions;
	}

	public View getView(int id) {
		View view = viewDAO.findById(id);
		if(view != null)
			view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
		return view;
	}
	
	public View getViewByXid(String xid) {
		return viewDAO.findByXid(xid);
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

		//sharing an object doesn't work
		//saveViewUsers(view);

		//TODO why don't update
		//usersPermissions.clear();
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

		//usersPermissions.clear();
		return id;
	}

	private void setWidthAndHeight(View view, String backgroundFilename) throws IOException {
		if (backgroundFilename != null && !backgroundFilename.isEmpty()) {
			UploadImage uploadImage = createUploadImage(new File(getBackgroundImagePath(backgroundFilename)));
			view.setHeight(uploadImage.getHeight());
			view.setWidth(uploadImage.getWidth());
		}
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void removeView(final int viewId) {
		//viewDAO.deleteViewForUser(viewId);
		viewDAO.delete(viewId);
		//usersProfileService.updateViewPermissions();
	}

	@Deprecated
	private void saveViewUsers(final View view) {
		// Delete anything that is currently there.
		viewDAO.deleteViewForUser(view.getId());

		//viewDAO.batchUpdateInfoUsers(view);
		
		// Update cache
		List<ShareUser> shareUsers = view.getViewUsers();
		for (Iterator<ShareUser> iterator = shareUsers.iterator(); iterator.hasNext();) {
			ShareUser shareUser = iterator.next();
			usersPermissions.remove(shareUser.getUserId());
			// updateViewUsersPermissions(shareUser.getUserId());
		}
	}	
	
	public void removeUserFromView(int viewId, int userId) {
		viewDAO.deleteViewForUser(viewId, userId);
		//usersProfileService.updateViewPermissions();
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
		List<File> files = filteringUploadFiles(FileUtil.getFilesOnDirectory(getUploadsPath()));

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
		Path path = Paths.get(getUploadsPath() + FILE_SEPARATOR + multipartFile.getOriginalFilename());
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

	private String getUploadsPath() {
		return getRealPath(FILE_SEPARATOR) + FILE_SEPARATOR + "uploads";
	}

	private String getBackgroundImagePath(String backgroundFilename) {
		return getRealPath(FILE_SEPARATOR) + FILE_SEPARATOR + backgroundFilename;
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
