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

package fr.cph.stock.external.impl;

import fr.cph.stock.exception.YahooException;
import fr.cph.stock.external.YahooGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * This class take care of the connexion to YahooGatewayImpl API. It uses YQL language.
 *
 * @author Carl-Philipp Harmant
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Log4j2
public class YahooGatewayImpl implements YahooGateway {

	@NonNull
	private final RestTemplate restTemplate;

	@Override
	public final <T> T getObject(final String yqlQuery, final Class<T> clazz) throws YahooException {
		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("q", yqlQuery);
		queryParams.add("format", "json");
		queryParams.add("diagnostics", "false");
		queryParams.add("env", "store://datatables.org/alltableswithkeys");
		final URI uri = UriComponentsBuilder.newInstance()
			.scheme("http")
			.host("query.yahooapis.com")
			.pathSegment("v1", "public", "yql")
			.queryParams(queryParams)
			.build().toUri();
		return restTemplate.getForEntity(uri, clazz).getBody();
	}
}
