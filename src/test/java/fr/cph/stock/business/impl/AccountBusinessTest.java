package fr.cph.stock.business.impl;

import fr.cph.stock.dao.AccountDAO;
import fr.cph.stock.entities.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountBusinessTest {

	@Mock
	private AccountDAO accountDAO;

	@InjectMocks
	private AccountBusinessImpl accountBusiness;

	@Test
	public void testAddAccount() {
		final Account account = new Account();
		accountBusiness.addAccount(account);

		verify(accountDAO).insert(isA(Account.class));
	}

	@Test
	public void testUpdateAccount() {
		final Account account = new Account();
		accountBusiness.updateAccount(account);

		verify(accountDAO).update(isA(Account.class));
	}

	@Test
	public void testDeleteAccount() {
		final Account account = new Account();
		accountBusiness.deleteAccount(account);

		verify(accountDAO).delete(isA(Account.class));
	}
}
