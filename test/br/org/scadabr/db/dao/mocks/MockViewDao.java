package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.view.View;

public class MockViewDao {

	public View createView(String xid, int id, int userId) {
		View vo = new View();
		vo.setId(id);
		vo.setXid(xid);
		vo.setUserId(userId);
		return vo;
	}

	public void saveView(View view) {

	}
}
