package com.serotonin.mango.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.db.AbstractMySQLDependentTest;
import br.org.scadabr.db.dao.mocks.MockViewDao;
import br.org.scadabr.db.scenarios.DatalessDatabaseScenario;
import br.org.scadabr.db.utils.TestUtils;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

public class ViewDaoTest extends AbstractMySQLDependentTest {

	private static final int FIRST = 0;
	private static final int SECOND = 1;
	private static final int THIRD = 2;

	@Test
	public void getViewNamesWithReadOrWritePermissionsShouldReturnEmptyListIfHaveNonePermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, owner.getId());

		ShareUser oldPermission = new ShareUser();
		oldPermission.setUserId(user.getId());
		oldPermission.setAccessType(ShareUser.ACCESS_NONE);
		view.getViewUsers().add(oldPermission);
		viewDao.saveView(view);

		assertTrue(new ViewDao().getViewNamesWithReadOrWritePermissions(
				user.getId(), user.getUserProfile()).isEmpty());
	}

	@Test
	public void getViewNamesWithReadOrWritePermissionsShouldReturnOnlyElementsWithoutNonePermissions()
			throws DAOException {
		useScenario(new DatalessDatabaseScenario());

		User owner = TestUtils.createUser();
		User user = TestUtils.createUser();

		MockViewDao viewDao = new MockViewDao();
		View view = viewDao.createView("view", 1, owner.getId());
		View view2 = viewDao.createView("view2", 2, owner.getId());
		View view3 = viewDao.createView("view3", 3, owner.getId());
		View view4 = viewDao.createView("view4", 4, user.getId());

		ShareUser oldPermissionV1 = new ShareUser();
		oldPermissionV1.setUserId(user.getId());
		oldPermissionV1.setAccessType(ShareUser.ACCESS_NONE);
		view.getViewUsers().add(oldPermissionV1);
		viewDao.saveView(view);

		ShareUser oldPermissionV2 = new ShareUser();
		oldPermissionV2.setUserId(user.getId());
		oldPermissionV2.setAccessType(ShareUser.ACCESS_READ);
		view2.getViewUsers().add(oldPermissionV2);
		viewDao.saveView(view2);

		ShareUser oldPermissionV3 = new ShareUser();
		oldPermissionV3.setUserId(user.getId());
		oldPermissionV3.setAccessType(ShareUser.ACCESS_SET);
		view3.getViewUsers().add(oldPermissionV3);
		viewDao.saveView(view3);

		assertEquals(
				3,
				new ViewDao().getViewNamesWithReadOrWritePermissions(
						user.getId(), user.getUserProfile()).size());

		assertEquals(
				view2.getId(),
				new ViewDao()
						.getViewNamesWithReadOrWritePermissions(user.getId(),
								user.getUserProfile()).get(FIRST).getKey());

		assertEquals(
				view3.getId(),
				new ViewDao()
						.getViewNamesWithReadOrWritePermissions(user.getId(),
								user.getUserProfile()).get(SECOND).getKey());

		assertEquals(
				view4.getId(),
				new ViewDao()
						.getViewNamesWithReadOrWritePermissions(user.getId(),
								user.getUserProfile()).get(THIRD).getKey());
	}

}
