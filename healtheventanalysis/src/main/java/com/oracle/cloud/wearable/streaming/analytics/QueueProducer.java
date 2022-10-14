package com.oracle.cloud.wearable.streaming.analytics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.json.JSONObject;

import com.google.gson.reflect.TypeToken;
import com.oracle.bmc.queue.QueueClient;
import com.oracle.bmc.queue.model.PutMessagesDetails;
import com.oracle.bmc.queue.model.PutMessagesDetailsEntry;
import com.oracle.bmc.queue.requests.PutMessagesRequest;
import com.oracle.bmc.queue.responses.PutMessagesResponse;

import io.cloudevents.CloudEvent;

public class QueueProducer {

	static QueueClient dpClient;
	static {
		dpClient = new QueueClient(GetProvider.getProvider(System.getenv("queue_profile")));
		dpClient.setEndpoint(Environment._DP_ENDPOINT);

	}

	public static void main(String[] args) {

		List mlist = new ArrayList();
		mlist.add("abc");
		mlist.add("abc");
		mlist.add("efg");
		mlist.add(new Double(22.33));

		Row row = RowFactory.create(mlist.toArray());
		List<Row> rows = new ArrayList<Row>();
		rows.add(row);
		for (int i=0;i<20;i++) {
			sendHealthMessage(rows, "abc", 123);
			
		}
		

	}

	/**
	 * send health message
	 * 
	 * @param message_list
	 * @param thersholdName
	 * @param thersholdValue
	 */
	public static void sendHealthMessage(List<Row> message_list, String thersholdName, int thersholdValue) {

		List<PutMessagesDetailsEntry> messages = new ArrayList<PutMessagesDetailsEntry>();
		List<String> healthAlertList= new ArrayList<String>();
		int i = 0;
		try {

			for (Row row : message_list) {

				Alert healthEvent = Alert.builder().username((String) row.get(2))
						.deviceSerialNumber((String) row.get(1)).observedValue( Math. round((Double) row.get(3)))
						.alertDateTime(new Date()).threshold(thersholdValue).alertingParameter(thersholdName).build();

				Type listType = new TypeToken<CloudEvent>() {
				}.getType();
				String cloudevent = new String(CloudEventGenerator.getCloudEventObject(healthEvent));

				String jsonString ="{ \"jsonstring\":\""+cloudevent+"\"  }";
				healthAlertList.add(jsonString);
				messages.add(PutMessagesDetailsEntry.builder().content(new JSONObject(jsonString).toString()).build());
				i++;
				if (i == 20 || i == message_list.size()) {
					System.out.println("sending message to queue total is " + i);
					PutMessagesRequest putMessageRequest = PutMessagesRequest.builder().queueId(Environment._QUEUE_ID)
							.putMessagesDetails(PutMessagesDetails.builder().messages(messages).build()).build();
					sendMessage(putMessageRequest);
					SaveToNoSqlDB.saveHealthAlerts(healthAlertList);
					i = 0;
					healthAlertList.clear();
					messages.clear();
				}

			}

		} catch (Exception e) {

			System.out.println("Exception occurred while sending message to queue " + e.getMessage());
		}

	}

	/**
	 * send message to queue using queue client
	 * 
	 * @param request
	 */
	public static void sendMessage(PutMessagesRequest request) {

		PutMessagesResponse response = dpClient.putMessages(request);
		System.out.println("response " + response.get__httpStatusCode__());

	}

}
