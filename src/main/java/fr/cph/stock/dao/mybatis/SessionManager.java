/**
 * Copyright 2016 Carl-Philipp Harmant
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cph.stock.dao.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Abstract class that load DB objects
 *
 * @author Carl-Philipp Harmant
 */
public enum SessionManager {

	INSTANCE;

	/**
	 * Sql session
	 **/
	private final SqlSessionFactory sqlSessionFactory = Mybatis.INSTANCE.getSqlMapInstance();

	/**
	 * Open session to DB
	 *
	 * @return a session to access to the DB
	 */
	public SqlSession getSqlSessionFactory(final boolean autoCommit) {
		return sqlSessionFactory.openSession(autoCommit);
	}
}
