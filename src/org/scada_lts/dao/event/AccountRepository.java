package org.scada_lts.dao.event;

import main.java.com.model.Account;
import org.hibernate.mapping.Index;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static main.java.com.repository.AccountRepository_SQLs.*;


@Repository
public interface AccountRepository extends JpaRepository<Account, Index> {

    @Query(PIN_IS)
    Account findByPin(@Param("pin") String pin);

    @Query(EMAIL_IS)
    Account findByEmail(@Param("email") String email);

    @Query(EMAIL_IS_and_PIN_IS)
    Account findByEmailAndPin(@Param("email") String email, @Param("pin") String pin);

    @Query(NUMER_IS)
    Account findByNumerTelefonu(@Param("numer") String numer);

}
