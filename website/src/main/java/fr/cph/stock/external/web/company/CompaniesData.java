package fr.cph.stock.external.web.company;

import lombok.Data;

import java.util.List;

@Data
public class CompaniesData {
	private Query query = new Query();

	@Data
	public class Query {
		private Results results = new Results();

		@Data
		public class Results {
			private List<Quote> quote;
		}
	}
}
