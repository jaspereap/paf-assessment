package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {
		String endpoint = UriComponentsBuilder
			.fromUriString("https://api.frankfurter.app/latest")
			.queryParam("amount", amount)
			.queryParam("from", from)
			.queryParam("to", to)
			.build()
			.toString();
		// System.out.println(endpoint);

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> resp = template.getForEntity(endpoint, String.class);
		if (resp.getStatusCode() != HttpStatusCode.valueOf(200)) {
			return -1000f;
		}
		JsonReader reader = Json.createReader(new StringReader(resp.getBody()));
		JsonObject object = reader.readObject().getJsonObject("rates");
		// System.out.println(object.getJsonNumber("SGD").doubleValue());
		float rates = (float) object.getJsonNumber("SGD").doubleValue();
		return rates;
	}
}
