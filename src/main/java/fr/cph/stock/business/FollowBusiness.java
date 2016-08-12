package fr.cph.stock.business;

import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;

import java.util.List;

/**
 * Created by carl on 8/11/16.
 */
public interface FollowBusiness {

	void addFollow(final User user, final String ticker, final Double lower, final Double higher) throws YahooException;

	void updateFollow(final User user, final String ticker, final Double lower, final Double higher);

	void deleteFollow(final int id);

	List<Follow> getListFollow(final int userId);
}
