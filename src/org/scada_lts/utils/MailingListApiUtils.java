package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.MailingListService;

import java.util.List;

import static org.scada_lts.utils.ValidationUtils.*;

public final class MailingListApiUtils {

    private static final Log LOG = LogFactory.getLog(MailingListApiUtils.class);

    private MailingListApiUtils() {}

    public static String validateMailingListCreate(MailingList body) {
        String msg = msgIfNullOrInvalid("Correct id;", body.getId(), a -> !validMailingListId(a));
        msg += msgIfNullOrInvalid("Correct xid;", body.getXid(), a -> !validMailingListXid(a));
        msg += validateMailingListBody(body);
        return msg;
    }

    public static String validateMailingListUpdate(MailingList body) {
        String msg = msgIfNullOrInvalid("Correct id;", body.getId(), a -> !validMailingListIdDelete(a));
        msg += msgIfNullOrInvalid("Correct xid;",
                body.getXid(), a -> !validMailingListXidUpdate(body.getId(), a));
        msg += validateMailingListBody(body);
        return msg;
    }

    private static String validateMailingListBody(MailingList body) {
        String msg = msgIfNull("Correct name;", body.getName());
        msg += validateEntriesList(body.getEntries());
        msg += msgIfNonNullAndInvalid("Correct dailyLimitSentEmailsNumber, it must be >= 0, value {0};",
                body.getDailyLimitSentEmailsNumber(), a -> a < 0);
        return msg;
    }

    private static String validateEntriesList(List<EmailRecipient> entries) {
        StringBuilder msg = new StringBuilder();
        for (EmailRecipient recipient : entries) {
            msg.append(msgIfNull("Correct recipient;", recipient));
            if (recipient != null) {
                msg.append(msgIfNullOrInvalid("Recipient does not exist for type {0};", recipient.getRecipientType(),
                        a -> !EmailRecipient.validEmailRecipientType(a)));
                if (recipient.getRecipientType() == EmailRecipient.TYPE_USER) {
                    UserEntry userEntry = (UserEntry) recipient;
                    msg.append(msgIfNullOrInvalid("Correct userId;", userEntry.getUserId(), a -> !validUserId(a)));
                }
                if (recipient.getRecipientType() == EmailRecipient.TYPE_ADDRESS) {
                    AddressEntry addressEntry = (AddressEntry) recipient;
                    msg.append(msgIfNull("Correct address;", addressEntry.getAddress()));
                }
            }
        }
        return msg.toString();
    }

    public static String validateMailingListDelete(Integer id) {
        return msgIfNullOrInvalid("Correct id;", id, a -> !validMailingListIdDelete(a));
    }

    public static void updateValueMailingList(MailingList body){
        if (body.getCronPattern() == null)
            body.setCronPattern("");
    }

    private static boolean validMailingListXid(String xid){
        try {
            MailingListService mailingListService = new MailingListService();
            MailingList mailingList = mailingListService.getMailingList(xid);
            return mailingList == null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validMailingListIdDelete(Integer id){
        try {
            MailingListService mailingListService = new MailingListService();
            MailingList mailingList = mailingListService.getMailingList(id);
            return mailingList != null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean validMailingListXidUpdate(int id, String xid){
        boolean exists = !validMailingListXid(xid);
        if (exists) {
            try {
                MailingListService mailingListService = new MailingListService();
                MailingList mailingList = mailingListService.getMailingList(id);
                return mailingList.getXid().equals(xid);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                return false;
            }
        } else
            return true;
    }

    private static boolean validMailingListId(int id) {
        return id == Common.NEW_ID;
    }
}
