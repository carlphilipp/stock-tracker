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

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import fr.cph.stock.dao.mybatis.Mybatis;

/**
 * Abstract class that load DB objects
 * 
 * @author Carl-Philipp Harmant
 * 
 * @param <T>
 *            the type of object the current class will process
 */
public abstract class AbstractDao<T> implements IDao<T> {

	/** Sql session **/
	private SqlSessionFactory sqlSessionFactory = Mybatis.getSqlMapInstance();

	/**
	 * Open session to DB
	 * 
	 * @return a session to access to the DB
	 */
	protected final SqlSession getSqlSessionFactory() {
		return sqlSessionFactory.openSession();
	}

}
