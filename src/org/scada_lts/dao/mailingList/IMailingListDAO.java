package org.scada_lts.dao.mailingList;

import com.serotonin.mango.vo.mailingList.MailingList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface IMailingListDAO {
    MailingList getMailingList(int id);

    MailingList getMailingList(String xid);

    List<MailingList> getMailingLists();

    List<ScadaObjectIdentifier> getSimpleMailingLists();

    int insert(MailingList mailingList);

    void update(MailingList mailingList);

    void delete(int id);

    List<MailingList> getMailingLists(Set<Integer> ids);
}
