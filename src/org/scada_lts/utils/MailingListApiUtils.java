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
import org.scada_lts.web.mvc.api.dto.CreateMailingList;
import org.scada_lts.web.mvc.api.dto.UpdateMailingList;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.serotonin.timer.CronExpression.isValidExpression;
import static org.scada_lts.serorepl.utils.StringUtils.isEmpty;
import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.*;

public final class MailingListApiUtils {

    private static final Log LOG = LogFactory.getLog(MailingListApiUtils.class);

    private MailingListApiUtils() {}

    public static String validateMailingListCreate(CreateMailingList body) {
        String msg = msgIfNullOrInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfNullOrInvalid("Correct cron;", body.getCronPattern(), a -> !isValidExpression(a));
        msg += msgIfNullOrInvalid("Correct entries;", body.getEntries(), a -> !hasEntries(a));
        msg += validateMailingListBody(body.getName(), body.getEntries(), body.getInactiveIntervals());
        return msg;
    }

    public static String validateMailingListUpdate(UpdateMailingList body) {
        String msg = msgIfNull("Correct id;", body.getId());
        msg += msgIfNonNullAndInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfNonNullAndInvalid("Correct cron;", body.getCronPattern(), a -> !isValidExpression(a));
        msg += validateMailingListBody(body.getName(), body.getEntries(), body.getInactiveIntervals());
        return msg;
    }

    private static String validateMailingListBody(String name, List<EmailRecipient> entries, Set<Integer> inactiveIntervals) {
        String msg = msgIfNull("Correct name;", name);
        msg += validateEntriesList(entries);
        msg += msgIfNonNullAndInvalid("Correct inactiveIntervals;", inactiveIntervals, a -> !validInactiveIntervals(a));
        return msg;
    }

    private static String validateEntriesList(List<EmailRecipient> entries) {
        StringBuilder msg = new StringBuilder();
        if (entries != null) {
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
        }
        return msg.toString();
    }

    public static boolean validUserId(Integer id){
        return id != 0;
    }

    public static String validateMailingListDelete(Integer id) {
        return msgIfNull("Correct id;", id);
    }

    public static void updateValueMailingList(MailingList toUpdate, UpdateMailingList source) {
        setIf(source.getXid(), toUpdate::setXid, a -> !isEmpty(a));
        setIf(source.getId(), toUpdate::setId, Objects::nonNull);
        setIf(source.getName(), toUpdate::setName, Objects::nonNull);
        setIf(source.getEntries(), toUpdate::setEntries, Objects::nonNull);
        setIf(source.getCronPattern(), toUpdate::setCronPattern, Objects::nonNull);
        setIf(source.getCollectInactiveEmails(), toUpdate::setCollectInactiveEmails, Objects::nonNull);
        setIf(source.getInactiveIntervals(), toUpdate::setInactiveIntervals, Objects::nonNull);
        toUpdate.setDailyLimitSentEmails(false);
        toUpdate.setDailyLimitSentEmailsNumber(0);
    }

    public static MailingList createMailingListFromBody(CreateMailingList source) {
        MailingList mailingList = new MailingList();
        setIf(source.getXid(), mailingList::setXid, a -> !isEmpty(a));
        setIf(source.getName(), mailingList::setName, Objects::nonNull);
        setIf(source.getEntries(), mailingList::setEntries, Objects::nonNull);
        setIf(source.getCronPattern(), mailingList::setCronPattern, Objects::nonNull);
        setIf(source.getCollectInactiveEmails(), mailingList::setCollectInactiveEmails, Objects::nonNull);
        setIf(source.getInactiveIntervals(), mailingList::setInactiveIntervals, Objects::nonNull);
        mailingList.setId(Common.NEW_ID);
        mailingList.setDailyLimitSentEmails(false);
        mailingList.setDailyLimitSentEmailsNumber(0);
        return mailingList;
    }

    private static boolean validInactiveIntervals(Set<Integer> inactiveIntervals){
        if (inactiveIntervals.stream().anyMatch(Objects::isNull))
            return false;
        return inactiveIntervals.stream().allMatch(i -> (i >= 0 && i < 672));
    }

    private static boolean hasEntries(List<EmailRecipient> entries) {
        return entries.size() > 0;
    }

    public static Optional<MailingList> getMailingList(String xid, MailingListService mailingListService) {
        try {
            MailingList mailingList = mailingListService.getMailingList(xid);
            return Optional.ofNullable(mailingList);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Optional<MailingList> getMailingList(int id, MailingListService mailingListService) {
        try {
            MailingList mailingList = mailingListService.getMailingList(id);
            return Optional.ofNullable(mailingList);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static boolean isMailingListPresent(String xid, MailingListService mailingListService){
        return getMailingList(xid, mailingListService).isPresent();
    }

    public static boolean isMailingListPresent(Integer id, MailingListService mailingListService){
        return getMailingList(id, mailingListService).isPresent();
    }

    public static boolean isXidChanged(String currentXid, String newXid) {
        return !currentXid.equals(newXid);
    }
}
