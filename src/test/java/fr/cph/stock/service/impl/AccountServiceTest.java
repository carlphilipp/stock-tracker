package fr.cph.stock.service.impl;

import fr.cph.stock.repository.AccountRepository;
import fr.cph.stock.entities.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@InjectMocks
	private AccountServiceImpl accountBusiness;

	@Test
	public void testAddAccount() {
		final Account account = Account.builder().build();
		accountBusiness.addAccount(account);

		verify(accountRepository).insert(isA(Account.class));
	}

	@Test
	public void testUpdateAccount() {
		final Account account = Account.builder().build();
		accountBusiness.updateAccount(account);

		verify(accountRepository).update(isA(Account.class));
	}

	@Test
	public void testDeleteAccount() {
		final Account account = Account.builder().build();
		accountBusiness.deleteAccount(account);

		verify(accountRepository).delete(isA(Account.class));
	}
}
