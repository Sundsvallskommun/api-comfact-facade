package se.sundsvall.comfactfacade.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.integration.db.AccountRepository;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;
import se.sundsvall.comfactfacade.utility.EncryptionUtility;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.comfactfacade.api.ApiConstants.ACCOUNT_KEY_HEADER;
import static se.sundsvall.comfactfacade.service.AccountMapper.toAccount;
import static se.sundsvall.comfactfacade.service.AccountMapper.toAccountEntity;
import static se.sundsvall.comfactfacade.service.AccountMapper.toAccounts;
import static se.sundsvall.comfactfacade.service.AccountMapper.updateAccountEntity;

@Service
public class AccountService {

	private final AccountRepository accountRepository;

	private final EncryptionUtility encryptionUtility;

	public AccountService(final AccountRepository accountRepository, final EncryptionUtility encryptionUtility) {
		this.accountRepository = accountRepository;
		this.encryptionUtility = encryptionUtility;
	}

	public List<Account> getAccounts(final String municipalityId) {
		return toAccounts(accountRepository.findAllByMunicipalityId(municipalityId));
	}

	public Account getAccount(final String municipalityId, final String id) {
		return toAccount(getAccountEntity(municipalityId, id));
	}

	@Transactional
	public String createAccount(final String municipalityId, final Account account) {
		if (accountRepository.existsByMunicipalityIdAndAccountKey(municipalityId, account.getAccountKey())) {
			throw Problem.valueOf(CONFLICT, "An account with key '%s' already exists for municipalityId '%s'".formatted(account.getAccountKey(), municipalityId));
		}
		if (Boolean.TRUE.equals(account.getDefaultAccount())) {
			clearDefaultAccount(municipalityId);
		}
		final var entity = toAccountEntity(municipalityId, account)
			.withClientSecret(encryptionUtility.encrypt(account.getClientSecret().getBytes()));
		return accountRepository.save(entity).getId();
	}

	@Transactional
	public void updateAccount(final String municipalityId, final String id, final Account account) {
		final var entity = getAccountEntity(municipalityId, id);
		if (Boolean.TRUE.equals(account.getDefaultAccount()) && !entity.isDefaultAccount()) {
			clearDefaultAccount(municipalityId);
		}
		updateAccountEntity(entity, account);
		Optional.ofNullable(account.getClientSecret())
			.ifPresent(clientSecret -> entity.setClientSecret(encryptionUtility.encrypt(clientSecret.getBytes())));
		accountRepository.save(entity);
	}

	public void deleteAccount(final String municipalityId, final String id) {
		accountRepository.delete(getAccountEntity(municipalityId, id));
	}

	/**
	 * Resolves the account (with decrypted credentials) to use for a Comfact call. When an account key is given, the
	 * matching account is required - an unknown key is a client error. When no key is given, the municipality's default
	 * account is used. All Comfact credentials are data-driven, so a municipality without a default account cannot be
	 * called without an explicit account key.
	 */
	public AccountCredentials resolveAccount(final String municipalityId, final String accountKey) {
		final var entity = Optional.ofNullable(accountKey)
			.filter(key -> !key.isBlank())
			.map(key -> accountRepository.findByMunicipalityIdAndAccountKey(municipalityId, key)
				.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, "No account found for municipalityId '%s' and account key '%s'".formatted(municipalityId, key))))
			.orElseGet(() -> accountRepository.findByMunicipalityIdAndDefaultAccountTrue(municipalityId)
				.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, "No default account is configured for municipalityId '%s' - provide the %s header or configure a default account".formatted(municipalityId, ACCOUNT_KEY_HEADER))));

		return new AccountCredentials(
			entity.getId(),
			entity.getClientId(),
			encryptionUtility.decrypt(entity.getClientSecret()),
			entity.getComfactAccountId());
	}

	private void clearDefaultAccount(final String municipalityId) {
		accountRepository.findByMunicipalityIdAndDefaultAccountTrue(municipalityId)
			.ifPresent(previousDefault -> accountRepository.save(previousDefault.withDefaultAccount(false)));
	}

	private AccountEntity getAccountEntity(final String municipalityId, final String id) {
		return accountRepository.findByMunicipalityIdAndId(municipalityId, id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No account found for municipalityId '%s' and id '%s'".formatted(municipalityId, id)));
	}
}
