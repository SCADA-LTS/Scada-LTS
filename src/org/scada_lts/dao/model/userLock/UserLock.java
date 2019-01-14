package org.scada_lts.dao.model.userLock;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class UserLock {

    private int userId;
    private short lockType;
    private long TypeKey;
    private long timestamp;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public short getLockType() {
        return lockType;
    }

    public void setLockType(short lockType) {
        this.lockType = lockType;
    }

    public long getTypeKey() {
        return TypeKey;
    }

    public void setTypeKey(long typeKey) {
        TypeKey = typeKey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserLock{" +
                "userId=" + userId +
                ", lockType=" + lockType +
                ", TypeKey=" + TypeKey +
                ", timestamp=" + timestamp +
                '}';
    }
}
