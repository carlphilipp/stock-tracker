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

package fr.cph.stock.dao.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * @author Carl-Philipp Harmant
 *
 */

public final class Mybatis {

	/** Constructor **/
	private Mybatis() {
	}

	private static final SqlSessionFactory SQL_SESSION_FACTORY;

	static {
		try {
			String resource = "mybatis/mybatis-config.xml";
			InputStream inputStream = Resources.getResourceAsStream(resource);
			SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("Erreur durant l'initialisation de MyBatis. Cause: " + e);
		}
	}

	/**
	 * Get a session factory
	 *
	 * @return a sql session factory
	 */
	public static SqlSessionFactory getSqlMapInstance() {
		return SQL_SESSION_FACTORY;
	}
}
