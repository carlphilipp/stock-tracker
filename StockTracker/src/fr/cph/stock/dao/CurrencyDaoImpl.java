/**
 * Copyright 2013 Carl-Philipp Harmant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import fr.cph.stock.entities.CurrencyData;

public class CurrencyDaoImpl extends AbstractDao<CurrencyData> {

	@Override
	public void insert(CurrencyData currencyData) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.insert("CurrencyData.insertOneCurrencyData", currencyData);
			session.commit();
		} finally {
			session.close();
		}

	}

	@Override
	public CurrencyData select(int id) {
		SqlSession session = getSqlSessionFactory();
		CurrencyData currencyData = null;
		try {
			currencyData = session.selectOne("CurrencyData.selectOneCurrencyData", id);
		} finally {
			session.close();
		}
		return currencyData;
	}

	@Override
	public void update(CurrencyData currencyData) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.update("CurrencyData.updateOneCurrencyData", currencyData);
			session.commit();
		} finally {
			session.close();
		}
	}

	@Override
	public void delete(CurrencyData currencyData) {
		SqlSession session = getSqlSessionFactory();
		try {
			session.delete("CurrencyData.deleteOneCurrencyData", currencyData);
			session.commit();
		} finally {
			session.close();
		}
	}
	

	public CurrencyData selectOneCurrencyDataWithParam(CurrencyData currencyD){
		SqlSession session = getSqlSessionFactory();
		CurrencyData currencyData = null;
		try {
			currencyData = session.selectOne("CurrencyData.selectOneCurrencyDataWithParam", currencyD);
		} finally {
			session.close();
		}
		return currencyData;
	}

	public List<CurrencyData> selectListCurrency(String currency) {
		SqlSession session = getSqlSessionFactory();
		List<CurrencyData> currencyDataList = new ArrayList<CurrencyData>();
		try {
			currencyDataList = session.selectList("CurrencyData.selectListCurrencyData", currency);
		} finally {
			session.close();
		}
		return currencyDataList;
	}
	
	public List<CurrencyData> selectListAllCurrency() {
		SqlSession session = getSqlSessionFactory();
		List<CurrencyData> currencyDataList = new ArrayList<CurrencyData>();
		try {
			currencyDataList = session.selectList("CurrencyData.selectListAllCurrencyData");
		} finally {
			session.close();
		}
		return currencyDataList;
	}

}
