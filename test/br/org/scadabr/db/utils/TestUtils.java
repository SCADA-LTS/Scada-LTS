package br.org.scadabr.db.utils;

import java.util.ArrayList;

import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.DataPointAccess;

public final class TestUtils {

	private TestUtils() {}

	public static String STRING_LENGTH_0 = "";
	public static String STRING_LENGTH_1 = "d";
	public static String STRING_LENGTH_2 = "ds";
	public static String STRING_LENGTH_3 = "ds_";
	public static String STRING_LENGTH_4 = "ds_1";
	public static String STRING_LENGTH_5 = "ds_1 ";
	public static String STRING_LENGTH_6 = "ds_1 -";
	public static String STRING_LENGTH_7 = "ds_1 - ";
	public static String STRING_LENGTH_8 = "ds_1 - 8";
	public static String STRING_LENGTH_9 = "ds_1 - 9c";
	public static String STRING_LENGTH_10 = "ds_1 - 10c";
	public static String STRING_LENGTH_11 = "ds_1 - 11ch";
	public static String STRING_LENGTH_12 = "ds_1 - 12cha";
	public static String STRING_LENGTH_13 = "ds_1 - 13char";
	public static String STRING_LENGTH_14 = "ds_1 - 14chara";
	public static String STRING_LENGTH_15 = "ds_1 - 15charac";
	public static String STRING_LENGTH_16 = "ds_1 - 16charact";
	public static String STRING_LENGTH_17 = "ds_1 - 17characte";
	public static String STRING_LENGTH_18 = "ds_1 - 18character";
	public static String STRING_LENGTH_19 = "ds_1 - 19characters";
	public static String STRING_LENGTH_20 = "ds_1 - 20characters2";
	public static String STRING_LENGTH_25 = "ds_1 - 25characters25char";
	public static String STRING_LENGTH_30 = "ds_1 - 30characters30character";
	public static String STRING_LENGTH_40 = "ds_1 - 40characters40characters40charact";
	public static String STRING_LENGTH_50 = "ds_1 - 50characters50characters50characters50chara";
	public static String STRING_LENGTH_60 = "ds_1 - 60characters60characters60characters60characters60cha";
	public static String STRING_LENGTH_70 = "ds_1 - 70characters70characters70characters70characters70characters70c";
	public static String STRING_LENGTH_80 = "ds_1 - 80characters80characters80characters80characters80characters80characters8";
	public static String STRING_LENGTH_90 = "ds_1 - 90characters90characters90characters90characters90characters90characters90character";
	public static String STRING_LENGTH_100 = "ds_1 - 100characters100characters100characters100characters100characters100characters100characters10";
	public static String STRING_LENGTH_135 = "ds_1 - 135characters135characters135characters135characters135characters135characters135characters135characters135characters135characte";
	public static String STRING_LENGTH_138 = "ds_1 - 138characters138characters138characters138characters138characters138characters138characters138characters138characters138characters1";
	public static String STRING_LENGTH_140 = "ds_1 - 140characters140characters140characters140characters140characters140characters140characters140characters140characters140characters140";

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

	public static User newUser(int id, String username) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
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
}
