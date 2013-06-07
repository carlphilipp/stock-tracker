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

import fr.cph.stock.android.activity.ErrorActivity;
import fr.cph.stock.android.enumtype.UrlType;
import fr.cph.stock.android.task.MainTask;
import fr.cph.stock.android.web.Connect;
import android.view.View;

public class ErrorButtonOnClickListener implements View.OnClickListener{
	
	private ErrorActivity errorActivity;
	private String login;
	private String password;
	
	public ErrorButtonOnClickListener(ErrorActivity errorActivity, String login, String password){
		this.errorActivity = errorActivity;
		this.login = login;
		this.password = password;
	}

	@Override
	public void onClick(View v) {
		String params = Connect.URL_LOGIN + login + Connect.URL_PASSWORD + password;
		MainTask derp = new MainTask(errorActivity, UrlType.AUTH, params);
		derp.execute((Void) null);
	}

}
