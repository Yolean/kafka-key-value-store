package com.bakdata.streams_store;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("messages")
public class RestService {

    private final KafkaStreams streams;
    private final String storeName;
    private HostInfo hostInfo;
    private Server jettyServer;
    private final Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();

    public RestService(final KafkaStreams streams, final String storeName, final String hostName, final int port) {
        this.streams = streams;
        this.storeName = storeName;
        this.hostInfo = new HostInfo(hostName, port);
    }

    public void start() throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        jettyServer = new Server(hostInfo.port());
        jettyServer.setHandler(context);

        ResourceConfig rc = new ResourceConfig();
        rc.register(this);
        rc.register(JacksonFeature.class);

        ServletContainer sc = new ServletContainer(rc);
        ServletHolder holder = new ServletHolder(sc);
        context.addServlet(holder, "/*");

        jettyServer.start();
    }

    public void stop() throws Exception {
        if (jettyServer != null) {
            jettyServer.stop();
        }
    }

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String valueByKey(@PathParam("key") final String key, @Context UriInfo uriInfo) {

        final StreamsMetadata metadata = streams.metadataForKey(storeName, key, Serdes.String().serializer());
        if (metadata == null) {
            throw new NotFoundException();
        }

         if (!metadata.hostInfo().equals(hostInfo)) {
             return fetchValue(metadata.hostInfo(), uriInfo.getPath(), new GenericType<String>() {});
         }

        final ReadOnlyKeyValueStore<String, String> store = streams.store(storeName, QueryableStoreTypes.keyValueStore());
        if (store == null) {
            throw new NotFoundException();
        }

        final String value = store.get(key);
        if (value == null) {
            throw new NotFoundException();
        }

        return value;
    }

    private <T> T fetchValue(final HostInfo host, final String path, GenericType<T> responseType) {
        return client.target(String.format("http://%s:%d/%s", host.host(), host.port(), path))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(responseType);
    }

    @GET()
    @Path("/processors")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProcessorMetadata> processors() {
        return streams.allMetadataForStore(storeName)
                .stream()
                .map(metadata -> new ProcessorMetadata(
                        metadata.host(),
                        metadata.port(),
                        getTopicPartitions(metadata))
                )
                .collect(Collectors.toList());
    }

	private List<Integer> getTopicPartitions(StreamsMetadata metadata) {
		return metadata.topicPartitions().stream()
                .map(TopicPartition::partition)
                .collect(Collectors.toList());
	}
	
	/**
	 * All keys in this instance (none from the partitions not represented here)
	 *
	 */
	@GET()
	@Path("/keys")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> keys() {
		final ReadOnlyKeyValueStore<String, String> store = streams.store(storeName,
				QueryableStoreTypes.keyValueStore());
		if (store == null) {
			throw new NotFoundException();
		}

		KeyValueIterator<String, String> all = store.all();
		List<String> response = new LinkedList<String>();
		while (all.hasNext()) {
			response.add(all.next().key);
		}

		return response;
	}
	
}

