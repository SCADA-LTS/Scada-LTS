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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(MailingList mailingList);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void update(MailingList mailingList);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void delete(int id);

    @Transactional(readOnly = true)
    List<MailingList> getMailingLists(Set<Integer> ids);
}
