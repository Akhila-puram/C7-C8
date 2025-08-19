package com.aaseya.MDM.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.aaseya.MDM.OMSConstant;
import com.aaseya.MDM.OperateConstant;
import com.aaseya.MDM.dto.AccessToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service // Add this annotation
public class OperateService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public String searchProcessInstances(String parentId, String string) {
		String uri = OperateConstant.SEARCH_PROCESS_INSTANCES;

		ResponseEntity<String> response = null;
		logger.info("Complete Variables: " + string);

		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(0);
			restTemplate.setRequestFactory(requestFactory);
			logger.info("Complete task URI::: " + uri);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Authorization Token from OperateConstant
			String AuthToken = "Bearer " + getOperateToken(); // Uses the method to retrieve the token
			headers.add("Authorization", AuthToken);

			// Constructing the request body
			ObjectMapper mapper = new ObjectMapper();
			String requestJson = "";
			Map<String, Object> requestBody = new HashMap<>();

			// Populate the "filter" part of the body
			Map<String, Object> filter = new HashMap<>();
			filter.put("parentKey", parentId);

			requestBody.put("filter", filter);

			// Convert the request body to JSON
			try {
				requestJson = mapper.writeValueAsString(requestBody);
				logger.info("JSON request: " + requestJson);
			} catch (Exception e) {
				logger.error("Error while converting request to JSON", e);
			}

			HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
			logger.info("Request body: " + entity.getBody());

			// Sending the request
			response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			logger.info("Response received");

			// Parse the response to extract the subprocessInstanceKey
			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				String responseBody = response.getBody();
				if (responseBody != null && !responseBody.isEmpty()) {
					// Parse JSON response
					Map<String, Object> responseMap = mapper.readValue(responseBody,
							new TypeReference<Map<String, Object>>() {
							});
					List<Map<String, Object>> items = (List<Map<String, Object>>) responseMap.get("items");
					if (items != null && !items.isEmpty()) {
						Map<String, Object> firstItem = items.get(0);
						String subprocessInstanceKey = String.valueOf(firstItem.get("key"));
						logger.info("Subprocess instance key: " + subprocessInstanceKey);
						return subprocessInstanceKey;
					} else {
						logger.info("No subprocess instances found.");
					}
				}
			} else {
				logger.error("Failed to retrieve subprocess instances. Status code: " + response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while sending request: " + e.getMessage());
		}

		return null;
	}

	// This method would retrieve the token based on the constants from
	// OperateConstant
	private String getOperateToken() {
		// You need to implement token retrieval logic based on the constants from
		// OperateConstant
		String tokenUrl = OperateConstant.TOKEN_URL;
		String clientId = OperateConstant.CLIENT_ID;
		String clientSecret = OperateConstant.CLIENT_SECRET;
		String grantType = OperateConstant.GRANT_TYPE;
		String audience = OperateConstant.AUDIENCE;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("grant_type", grantType);
		body.add("audience", audience);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);
		Map<String, Object> responseBody = response.getBody();

		return responseBody != null ? (String) responseBody.get("access_token") : null;
	}

	public String getProcessDefinitionKeyByInstanceKey(long instanceKey) {
	    String uri = OperateConstant.Get_PROCESS_INSTANCE_BY_KEY + "/" + instanceKey;

		ResponseEntity<String> response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Authorization Token from OperateConstant
			String authToken = "Bearer " + getOperateToken();
			headers.add("Authorization", authToken);

			HttpEntity<String> entity = new HttpEntity<>(headers);
			response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				String responseBody = response.getBody();
				if (responseBody != null && !responseBody.isEmpty()) {
					// Parse JSON response
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> responseMap = mapper.readValue(responseBody,
							new TypeReference<Map<String, Object>>() {
							});
					Object processDefinitionKey = responseMap.get("processDefinitionKey");
					return processDefinitionKey != null ? processDefinitionKey.toString() : null;
				}
			} else {
				logger.error("Failed to retrieve process instance. Status code: " + response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception occurred while sending request: " + e.getMessage());
		}

		return null;
	}
}
