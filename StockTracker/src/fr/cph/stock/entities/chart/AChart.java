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

package fr.cph.stock.entities.chart;

/**
 * This class represents an abstract chart
 * 
 * @author Carl-Philipp Harmant
 * 
 */
public abstract class AChart implements IChart {

	/** Data **/
	private String data;
	/** Title **/
	private String title;
	/** Draw **/
	private String draw;
	/** Colors **/
	private String colors;

	/**
	 * Get colors
	 * 
	 * @return the colors
	 */
	public final String getColors() {
		return colors;
	}

	/**
	 * Set colors
	 * 
	 * @param colors
	 *            the colors
	 */
	public final void setColors(final String colors) {
		this.colors = colors;
	}

	/**
	 * Get draw
	 * 
	 * @return the draw
	 */
	public final String getDraw() {
		return draw;
	}

	/**
	 * Set draw
	 * 
	 * @param draw
	 *            the draw
	 */
	public final void setDraw(final String draw) {
		this.draw = draw;
	}

	/**
	 * Get title
	 * 
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Set title
	 * 
	 * @param title
	 *            the title
	 */
	public final void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Get data
	 * 
	 * @return the data
	 */
	public final String getData() {
		return data;
	}

	/**
	 * Set data
	 * 
	 * @param data
	 *            the data
	 */
	public final void setData(final String data) {
		this.data = data;
	}
}
