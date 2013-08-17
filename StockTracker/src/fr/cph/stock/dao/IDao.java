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

/**
 * Interface that defines basics operation to DB.
 * 
 * @author Carl-Philipp Harmant
 * 
 * @param <T>
 *            the type of object the current class will process
 */
public interface IDao<T> {

	/**
	 * Insert an object into DB
	 * 
	 * @param obj
	 *            the object to insert
	 */
	void insert(final T obj);

	/**
	 * Get an object from DB
	 * 
	 * @param id
	 *            the id of the object to get
	 * @return the object
	 */
	T select(final int id);

	/**
	 * Update an object in DB
	 * 
	 * @param obj
	 *            the object to update
	 */
	void update(final T obj);

	/**
	 * Delete an object in DB
	 * 
	 * @param obj
	 *            the object to delete
	 */
	void delete(final T obj);

}
