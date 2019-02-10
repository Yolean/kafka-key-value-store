package com.bakdata.streams_store.binary;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class BinaryBase64Serde implements Serde<String>, Deserializer<String>, Serializer<String> {

	private Encoder encoder = Base64.getEncoder();

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public void close() {
	}

	@Override
	public Serializer<String> serializer() {
		return this;
	}

	@Override
	public Deserializer<String> deserializer() {
		return this;
	}

	@Override
	public byte[] serialize(String topic, String data) {
		throw new UnsupportedOperationException("Serialization not implemented for key-value service");
	}

	@Override
	public String deserialize(String topic, byte[] data) {
		return encoder.encodeToString(data);
	}

}
