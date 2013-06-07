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

package fr.cph.stock.android.listener;

import android.app.ActionBar.LayoutParams;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ErrorMainOnClickListener implements View.OnClickListener{
	
	private ListView listView;
	private TextView errorView;
	
	public ErrorMainOnClickListener(ListView listView, TextView errorView){
		this.listView = listView;
		this.errorView = errorView;
	}

	@Override
	public void onClick(View v) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) LayoutParams.MATCH_PARENT,
				(int) LayoutParams.MATCH_PARENT);
		listView.setLayoutParams(params);
		errorView.setText("");
	}

}
