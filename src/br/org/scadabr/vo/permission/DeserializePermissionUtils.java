package br.org.scadabr.vo.permission;

import com.serotonin.json.JsonObject;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.util.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import java.text.MessageFormat;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class DeserializePermissionUtils extends Permission {

    private DeserializePermissionUtils() {}

    public static <T> void updatePermission(Permission permission,
                                         Function<String, T> get,
                                         ToIntFunction<T> getId,
                                         JsonObject json,
                                         String fieldXidName,
                                         String missingMsg) throws LocalizableJsonException {
        String xid = json.getString(fieldXidName);
        if (StringUtils.isEmpty(xid))
            throw new LocalizableJsonException("emport.error.permission.missing", fieldXidName);
        T object;
        try {
            object = get.apply(xid);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizableJsonException("emport.error.missingObject", MessageFormat.format(missingMsg, xid));
        }
        if (object == null || getId.applyAsInt(object) == Common.NEW_ID)
            throw new LocalizableJsonException("emport.error.missingObject", MessageFormat.format(missingMsg, xid));
        permission.setId(getId.applyAsInt(object));

        String permissionLevel = json.getString("permission");
        if (StringUtils.isEmpty(permissionLevel))
            throw new LocalizableJsonException("emport.error.missing", "permission", ACCESS_CODES.getCodeList());
        int permissionLevelInt = ACCESS_CODES.getId(permissionLevel);
        permission.setPermission(permissionLevelInt);
        if (permissionLevelInt == -1)
            throw new LocalizableJsonException("emport.error.invalid", "permission", permissionLevel, ACCESS_CODES.getCodeList());
    }
}
