package se.sundsvall.comfactfacade.service;

import java.util.List;
import java.util.Optional;
import se.sundsvall.comfactfacade.api.model.Account;
import se.sundsvall.comfactfacade.integration.db.model.AccountEntity;

public final class AccountMapper {

	private AccountMapper() {}

	static Account toAccount(final AccountEntity entity) {
		// The client secret is deliberately never mapped back to the API model.
		return Account.builder()
			.withId(entity.getId())
			.withAccountKey(entity.getAccountKey())
			.withComfactAccountId(entity.getComfactAccountId())
			.withClientId(entity.getClientId())
			.withDescription(entity.getDescription())
			.withDefaultAccount(entity.isDefaultAccount())
			.withCreated(entity.getCreated())
			.withModified(entity.getModified())
			.build();
	}

	static List<Account> toAccounts(final List<AccountEntity> entities) {
		return Optional.ofNullable(entities).orElseGet(List::of).stream()
			.map(AccountMapper::toAccount)
			.toList();
	}

	static AccountEntity toAccountEntity(final String municipalityId, final Account account) {
		// The client secret is set by the service after encryption.
		return AccountEntity.create()
			.withMunicipalityId(municipalityId)
			.withAccountKey(account.getAccountKey())
			.withComfactAccountId(account.getComfactAccountId())
			.withClientId(account.getClientId())
			.withDescription(account.getDescription())
			.withDefaultAccount(Boolean.TRUE.equals(account.getDefaultAccount()));
	}

	static AccountEntity updateAccountEntity(final AccountEntity entity, final Account account) {
		// The client secret is updated by the service after encryption.
		Optional.ofNullable(account.getAccountKey()).ifPresent(entity::setAccountKey);
		Optional.ofNullable(account.getComfactAccountId()).ifPresent(entity::setComfactAccountId);
		Optional.ofNullable(account.getClientId()).ifPresent(entity::setClientId);
		Optional.ofNullable(account.getDescription()).ifPresent(entity::setDescription);
		Optional.ofNullable(account.getDefaultAccount()).ifPresent(entity::setDefaultAccount);
		return entity;
	}
}
