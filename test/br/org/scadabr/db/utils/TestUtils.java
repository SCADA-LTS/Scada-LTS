package br.org.scadabr.db.utils;

import java.util.ArrayList;
import java.util.Random;

import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.viconics.ViconicsPointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;

public class TestUtils {

	public static User createUser() {
		User user = new User();
		user.setUsername("anUser");
		user.setPassword("password");
		user.setEmail("An email");
		user.setPhone("phone");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setHomeUrl("url");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);
		user.setDataSourcePermissions(new ArrayList<Integer>());
		user.setDataPointPermissions(new ArrayList<DataPointAccess>());

		new UserDao().saveUser(user);
		return user;
	}

	public static User newUser(int id) {
		User user = new User();
		user.setId(id);
		user.setUsername("anUser");
		user.setPassword("password");
		user.setEmail("An email");
		user.setPhone("phone");
		user.setAdmin(true);
		user.setDisabled(false);
		user.setHomeUrl("url");
		user.setReceiveAlarmEmails(1);
		user.setReceiveOwnAuditEvents(true);
		user.setDataSourcePermissions(new ArrayList<Integer>());
		user.setDataPointPermissions(new ArrayList<DataPointAccess>());
		return user;
	}

	public static DataPointVO newPointSettable(int id, int folderId) {
		DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
		dataPoint1.setId(id);
		dataPoint1.setXid("DP_" + id);
		dataPoint1.setName("dp_" + id);
		dataPoint1.setDataSourceId(99999);
		dataPoint1.setDataSourceName("ds_name");
		dataPoint1.setDataSourceTypeId(9);
		dataPoint1.setDataSourceXid("DS_XID");
		dataPoint1.setPointFolderId(folderId);
		VirtualPointLocatorVO pointLocatorVO = new VirtualPointLocatorVO();
		pointLocatorVO.setSettable(true);
		dataPoint1.setPointLocator(pointLocatorVO);
		return dataPoint1;
	}

	public static DataPointVO newPointSettable(int id, DataSourceVO<?> dataSource, int folderId) {
		DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
		dataPoint1.setId(id);
		dataPoint1.setXid("DP_" + id);
		dataPoint1.setName("dp_" + id);
		dataPoint1.setDataSourceId(dataSource.getId());
		dataPoint1.setDataSourceName(dataSource.getName());
		dataPoint1.setDataSourceTypeId(dataSource.getType().getId());
		dataPoint1.setDataSourceXid(dataSource.getXid());
		dataPoint1.setPointFolderId(folderId);
		VirtualPointLocatorVO pointLocatorVO = new VirtualPointLocatorVO();
		pointLocatorVO.setSettable(true);
		dataPoint1.setPointLocator(pointLocatorVO);
		return dataPoint1;
	}

	public static DataPointVO newPointNonSettable(int id, DataSourceVO<?> dataSource, int folderId) {
		DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
		dataPoint1.setId(id);
		dataPoint1.setXid("DP_" + id);
		dataPoint1.setName("dp_" + id);
		dataPoint1.setDataSourceId(dataSource.getId());
		dataPoint1.setDataSourceName(dataSource.getName());
		dataPoint1.setDataSourceTypeId(dataSource.getType().getId());
		dataPoint1.setDataSourceXid(dataSource.getXid());
		dataPoint1.setPointFolderId(folderId);
		VirtualPointLocatorVO pointLocatorVO = new VirtualPointLocatorVO();
		pointLocatorVO.setSettable(false);
		dataPoint1.setPointLocator(pointLocatorVO);
		return dataPoint1;
	}

	public static DataSourceVO<?> newDataSource(int id) {
		VirtualDataSourceVO dataSource = new VirtualDataSourceVO();
		dataSource.setId(id);
		dataSource.setName("ds_" + id);
		dataSource.setXid("DS_" + id);
		return dataSource;
	}
}
