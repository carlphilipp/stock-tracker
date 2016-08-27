package fr.cph.stock.business;

import fr.cph.stock.entities.Follow;
import fr.cph.stock.entities.User;
import fr.cph.stock.exception.YahooException;

import java.util.List;

/**
 * Created by carl on 8/11/16.
 */
public interface FollowBusiness {

	void addFollow(User user, String ticker, Double lower, Double higher) throws YahooException;

	void updateFollow(User user, String ticker, Double lower, Double higher);

	void deleteFollow(int id);

	List<Follow> getListFollow(int userId);
}
