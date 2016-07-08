package br.org.scadabr.vo.permission;

import java.io.Serializable;

import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.view.ShareUser;

public class Permission implements Serializable {

	protected int permission = ShareUser.ACCESS_NONE;
	protected int id;

	public static final int READ = 1;
	public static final int SET = 2;

	protected static final ExportCodes ACCESS_CODES = new ExportCodes();
	static {
		ACCESS_CODES.addElement(READ, "READ", "common.access.read");
		ACCESS_CODES.addElement(SET, "SET", "common.access.set");
	}

	/**
	 * Required by DWR. Should not be used otherwise.
	 */
	public Permission() {
		super();
	}

	public Permission(int id, int permissions) {
		this.id = id;
		this.permission = permissions;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public int getId() {
		return id;
	}

	public int getPermission() {
		return permission;
	}

}