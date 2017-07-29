package fr.cph.stock.dropbox;

import fr.cph.stock.config.AppProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(value = Parameterized.class)
public class DropBoxDateTest {


	private DropBoxImpl dropBox;
	private File file;
	private String expected;

	public DropBoxDateTest(final File file, final String expected) {
		this.file = file;
		this.expected = expected;
		//final AppProperties appProperties = mock(AppProperties.class);
		AppProperties appProperties = new AppProperties();
		appProperties.getDropbox().setAccessToken("accessToken");
		appProperties.getDropbox().setClientId("clientId");
		this.dropBox = new DropBoxImpl(appProperties);
		//given(appProperties.getDropbox().getClientId()).willReturn("");
		//given(appProperties.getDropbox().getAccessToken()).willReturn("");
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
			{new File("28-08-2016-stock.tar.gz"), "21-08-2016"},
			{new File("30-08-2016-stock.tar.gz"), "23-08-2016"},
			{new File("02-09-2016-stock.tar.gz"), "26-08-2016"},
			{new File("08-08-2016-stock.tar.gz"), "01-08-2016"}
		});
	}

	@Test
	public void test() {
		assertThat(dropBox.calculateNewDateFromFileName(file), is(expected));
	}
}
