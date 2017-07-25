/**
 * Copyright 2017 Carl-Philipp Harmant
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

package fr.cph.stock.repository;

import java.util.Optional;

/**
 * Interface that defines basics operation to DB.
 *
 * @param <T> the type of object the current class will process
 * @author Carl-Philipp Harmant
 */
public interface DAO<T> {

	/**
	 * Insert an object into DB
	 *
	 * @param obj the object to insert
	 */
	void insert(T obj);

	/**
	 * Get an object from DB
	 *
	 * @param id the id of the object to get
	 * @return the object
	 */
	Optional<T> select(int id);

	/**
	 * Update an object in DB
	 *
	 * @param obj the object to update
	 */
	void update(T obj);

	/**
	 * Delete an object in DB
	 *
	 * @param obj the object to delete
	 */
	void delete(T obj);

}
