package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.View;
import com.serotonin.util.SerializationHelper;

public class MockViewDao extends ViewDao {

	public View createView(String xid, int id, int userId) {
		View vo = new View();

		/*vo.setId(doInsert(
				"insert into mangoViews (xid, id, name, userId, anonymousAccess, data) values (?,?,?,?,?,?)",
				new Object[] { xid, id, xid, userId, 0,
						SerializationHelper.writeObjectToArray(vo) }));

		vo.setXid(xid);
		vo.setName(xid);
		vo.setUserId(userId);
		vo.setId(id);
		return vo;*/
		return null;
	}

}
