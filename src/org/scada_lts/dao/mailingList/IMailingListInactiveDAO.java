package org.scada_lts.dao.mailingList;

import com.serotonin.mango.vo.mailingList.MailingList;

import java.util.List;

public interface IMailingListInactiveDAO {

    List<Integer> getInactiveInterval(int mailingListId);

    void insert(MailingList mailingList);

    void delete(int mailingListId);
}
