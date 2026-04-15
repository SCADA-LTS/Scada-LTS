package org.scada_lts.dao.mailingList;

import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.vo.mailingList.MailingList;

import java.util.List;

public interface IMailingListMemberDAO {

    List<EmailRecipient> getEmailRecipient(int mailingListId);

    void insert(MailingList mailingList);

    void delete(int mailingListId);

    void deleteWithUserId(int userId);
}
