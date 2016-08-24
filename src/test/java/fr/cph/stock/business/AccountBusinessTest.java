package fr.cph.stock.business;

import fr.cph.stock.business.impl.AccountBusinessImpl;
import fr.cph.stock.dao.AccountDAO;
import fr.cph.stock.entities.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountBusinessTest {

	@Mock
	private AccountDAO accountDAO = mock(AccountDAO.class);

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
