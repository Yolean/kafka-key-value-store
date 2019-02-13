package com.bakdata.streams_store;

import java.util.concurrent.Future;

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
		Future<Response> res = client.target(url).request().async().post(
				Entity.entity(key, MediaType.TEXT_PLAIN_TYPE));
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + '(' + this.url + ')';
	}

}
