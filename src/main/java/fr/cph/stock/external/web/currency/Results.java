package fr.cph.stock.external.web.currency;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class Results {
	private List<Rate> rate;
}
