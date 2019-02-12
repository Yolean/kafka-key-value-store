package com.bakdata.streams_store;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * Flawed: Synchronous and not verifying successful response.
 */
public class OnUpdateREST implements OnUpdate {

	private String url;

    private final Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
	
	public OnUpdateREST(String webhookUrl) {
		this.url = webhookUrl;
	}
	
	@Override
	public void handle(String key) {
		Response res = client.target(url).request().post(
				Entity.entity(new KeyValueBean("key", key), MediaType.APPLICATION_JSON_TYPE));
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + '(' + this.url + ')';
	}

}
