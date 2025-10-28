package org.scada_lts.dao.model;

import com.serotonin.mango.vo.User;
import org.scada_lts.web.beans.validation.xss.XssProtect;

/**
 * Scada Object Identifier
 *
 * This class contain a simple Scada Object
 * description that can be used in multiple
 * places among the project where user want
 * to list business object without details.
 */
public class UserIdentifier extends BaseObjectIdentifier {

    private BaseObjectIdentifier baseObjectIdentifier;

    @XssProtect
    private String username;
    private boolean admin;

    private boolean disabled;

    public UserIdentifier() {
        super(-1, "");
        this.baseObjectIdentifier = new BaseObjectIdentifier(-1, "");
    }

    public UserIdentifier(User user) {
        super(user.getId(), "");
        this.baseObjectIdentifier = new BaseObjectIdentifier(user.getId(), "");
        this.username = user.getUsername();
        this.admin = user.isAdmin();
        this.disabled = user.isDisabled();
    }

    public UserIdentifier(int id, String xid, String username) {
        super(id, xid);
        this.baseObjectIdentifier = new BaseObjectIdentifier(id, xid);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int getId() {
        return baseObjectIdentifier.getId();
    }

    @Override
    public void setId(int id) {
        baseObjectIdentifier.setId(id);
    }

    @Override
    public String getXid() {
        return baseObjectIdentifier.getXid();
    }

    @Override
    public void setXid(String xid) {
        baseObjectIdentifier.setXid(xid);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
