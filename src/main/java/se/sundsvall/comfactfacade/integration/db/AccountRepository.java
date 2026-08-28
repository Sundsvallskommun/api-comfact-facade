package se.sundsvall.comfactfacade.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;

@CircuitBreaker(name = "accountRepository")
public interface AccountRepository extends JpaRepository<AccountEntity, String> {

	Optional<AccountEntity> findByMunicipalityIdAndAccountKey(String municipalityId, String accountKey);

	Optional<AccountEntity> findByMunicipalityIdAndId(String municipalityId, String id);

	List<AccountEntity> findAllByMunicipalityId(String municipalityId);

	boolean existsByMunicipalityIdAndAccountKey(String municipalityId, String accountKey);

	Optional<AccountEntity> findByMunicipalityIdAndDefaultAccountTrue(String municipalityId);
}
