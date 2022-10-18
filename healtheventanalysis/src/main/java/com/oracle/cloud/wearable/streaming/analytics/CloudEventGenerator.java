package com.oracle.cloud.wearable.streaming.analytics;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

public class CloudEventGenerator {

	private static final String DATA_CONTENT_TYPE = "application/json";
	private static final String SOURCE = "/oracle/cloud/health/monitoring/stream-analytics";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static byte[] getCloudEventObject(Alert alert) throws JsonProcessingException {

		try {
			CloudEvent event = CloudEventBuilder.v1().withId(UUID.randomUUID().toString()).withType("StreamEvent")
					.withTime(OffsetDateTime.now()).withSource(URI.create(SOURCE))
					.withData("application/json", MAPPER.writeValueAsString(alert).getBytes()).build();
			EventFormat format = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

			byte[] serialized = format.serialize(event);
			return Base64.getEncoder().encode(serialized);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {

		Alert alert = Alert.builder().username("abc").deviceSerialNumber("def").observedValue(134L)
				.alertDateTime(new Date()).threshold(125).build();

		CloudEvent event = null;
		try {
			event = CloudEventBuilder.v1().withId(UUID.randomUUID().toString()).withType("StreamEvent")
					.withTime(OffsetDateTime.now()).withSource(URI.create(SOURCE))
					.withData("application/json", MAPPER.writeValueAsString(alert).getBytes()).build();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		EventFormat format = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

		byte[] serialized = format.serialize(event);
		Base64.getEncoder().encode(serialized);

		event = format.deserialize(serialized);
		System.out.println("event " + event);
		
	}
}
