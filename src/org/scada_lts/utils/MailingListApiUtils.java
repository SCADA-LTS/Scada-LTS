package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.mailingList.UserEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.MailingListService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.MailingListDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.serotonin.timer.CronExpression.isValidExpression;
import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.*;

public final class MailingListApiUtils {

    private static final Log LOG = LogFactory.getLog(MailingListApiUtils.class);

    private MailingListApiUtils() {}

    public static String validateMailingListCreate(MailingList body) {
        String msg = msgIfNullOrInvalid("Correct id;", body.getId(), a -> !validMailingListIsNewId(a));
        msg += msgIfNullOrInvalid("Correct xid;", body.getXid(), a -> !validMailingListXid(a));
        msg += validateMailingListBody(body.getName(), body.getEntries(), body.getCronPattern());
        return msg;
    }

    public static String validateMailingListUpdate(MailingListDTO body) {
        String msg = msgIfNullOrInvalid("Correct id;", body.getId(), a -> !validMailingListId(a));
        msg += msgIfNullOrInvalid("Correct xid;",
                body.getXid(), a -> !validMailingListXidUpdate(body.getId(), a));
        msg += validateMailingListBody(body.getName(), body.getEntries(), body.getCronPattern());
        return msg;
    }

    private static String validateMailingListBody(String name, List<EmailRecipient> entries, String cronPattern) {
        String msg = msgIfNull("Correct name;", name);
        msg += validateEntriesList(entries);
        msg += msgIfNonNullAndInvalid("Correct cron", cronPattern, a -> !isValidExpression(a));
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
        return msgIfNullOrInvalid("Correct id;", id, a -> !validMailingListId(a));
    }

    public static void updateValueMailingListPost(MailingList body){
        if (body.getCronPattern() == null)
            body.setCronPattern("");
        body.setDailyLimitSentEmails(false);
        body.setDailyLimitSentEmailsNumber(0);
    }

    public static Optional<MailingList> getMailingList(int id, MailingListService mailingListService) {
        try {
            MailingList mailingList = mailingListService.getMailingList(id);
            return Optional.ofNullable(mailingList);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static void updateValueMailingList(MailingList toUpdate, MailingListDTO source) {
        setIf(source.getXid(), toUpdate::setXid, a -> !StringUtils.isEmpty(a));
        setIf(source.getId(), toUpdate::setId, Objects::nonNull);
        setIf(source.getName(), toUpdate::setName, Objects::nonNull);
        setIf(source.getEntries(), toUpdate::setEntries, Objects::nonNull);
        setIf(source.getCronPattern(), toUpdate::setCronPattern, Objects::nonNull);
        setIf(source.getCollectInactiveEmails(), toUpdate::setCollectInactiveEmails, Objects::nonNull);
        toUpdate.setDailyLimitSentEmails(false);
        toUpdate.setDailyLimitSentEmailsNumber(0);
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

    private static boolean validMailingListId(Integer id){
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

    private static boolean validMailingListIsNewId(int id) {
        return id == Common.NEW_ID;
    }
}
