package fr.cph.stock.external.web.company;

import lombok.Data;

@Data
public class CompanyData {
	private Query query = new Query();

	@Data
	public class Query {
		private Results results = new Results();

		@Data
		public class Results {
			private Quote quote;
		}
	}
}
