package com.oracle.cloud.wearable.streaming.analytics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.spark.sql.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import oracle.nosql.driver.AuthorizationProvider;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.NoSQLHandleConfig;
import oracle.nosql.driver.NoSQLHandleFactory;
import oracle.nosql.driver.Region;
import oracle.nosql.driver.iam.SignatureProvider;
import oracle.nosql.driver.ops.PutRequest;
import oracle.nosql.driver.ops.PutResult;
import oracle.nosql.driver.values.MapValue;

public class SaveToNoSqlDB {

	/* Name of your table */
	private static final String tableName = "EventData";
	private static String health_alert_table_name = "HealthAlertData";
	private static AuthorizationProvider ap;

	public static void main(String[] args) throws Exception {

		/* Set up an endpoint URL */
		Region region = Region.US_ASHBURN_1;// "us-ashburn-1";//;getRegion(args);
		System.out.println("Using region: " + region);


		String jsondata = "{\"data\":\"{\\\"deviceSerialNumber\\\":\\\"4CE0460D0G\\\",\\\"username\\\":\\\"john.doe\\\",\\\"heartRate\\\":111,\\\"systolicBP\\\":136,\\\"diastolicBP\\\":77,\\\"spo2Level\\\":90.44192364685841,\\\"readingTime\\\":1664440414766,\\\"eventType\\\":\\\"READING\\\"}\",\"id\":\"3630f931-7435-464b-8332-6769811fcfc3\",\"source\":\"/oracle/cloud/health/monitoring/server\",\"specversion\":\"1.0\",\"type\":\"StreamEvent\",\"datacontenttype\":\"application/json\",\"time\":\"2022-09-29T08:33:35.059Z\"}";
		org.json.JSONObject jo = new JSONObject(jsondata);
		/* Create a table and run operations. Be sure to close the handle */

		saveEventData(jo);

	}

	private static NoSQLHandle getNoSqlHandler(Region region, AuthorizationProvider ap) {
		NoSQLHandleConfig config = new NoSQLHandleConfig(region, ap);
		config.setDefaultCompartment(Environment._COMPARTMENT_ID);

		/*
		 * By default the handle will log to the console at level INFO. The default
		 * logger can be configured using a logging properties file specified on the
		 * command line, e.g.: -Djava.util.logging.config.file=logging.properties If a
		 * user-provided Logger is desired, create and set it here: Logger logger =
		 * Logger.getLogger("..."); config.setLogger(logger); NOTE: the referenced
		 * classes must be imported above
		 */
		config.setRequestTimeout(10000);

		NoSQLHandle handle = NoSQLHandleFactory.createNoSQLHandle(config);
		System.out.println("Acquired handle for service ");
		return handle;
	}

	private static AuthorizationProvider getAuthProvider(String profile) throws IOException {



		if ("LOCAL".equals(profile)) {

		
			System.out.println("profile is local" + profile);
			SignatureProvider signprovider = new SignatureProvider(Environment._TENANT_ID, Environment._USER_ID,
					Environment._FINGER_PRINT, new File(Environment._PVT_KEY_FILE_PATH), "".toCharArray(),
					Region.US_ASHBURN_1);
			ap = signprovider;
			return ap;
		}

		else {
			System.out.println("getting resource principal profile is" + profile);
			try {
				ap = SignatureProvider.createWithResourcePrincipal();
			} catch (Exception e) {
				System.out
						.println("exception happened while getting instance principal now getting resource principal");
				e.getStackTrace();
				
				return ap;
			}
			
		}
		return ap;
	}

	/**
	 * Create a table and do some operations.
	 */
	public static void saveEventData(org.json.JSONObject jo) throws Exception {

		String profile = System.getenv("nosql_profile");
		System.out.println("profile " + profile);
		NoSQLHandle handle;
		handle = getNoSqlHandler(Region.US_ASHBURN_1, getAuthProvider(profile));

		try {
			/* Make a row and write it */
			MapValue value = new MapValue().put("id", UUID.randomUUID().toString()).put("eventData", jo.toString());
			PutRequest putRequest = new PutRequest().setValue(value).setTableName(tableName);

			PutResult putResult = handle.put(putRequest);



		} catch (Exception e) {
			System.out.println("exception occured while saving events in NoSqlDB " + e.getMessage());
		} finally {
			handle.close();
		}

		
	}

	public static void saveHealthAlerts(List<String> eventList) throws Exception {
		String profile = System.getenv("nosql_profile");
		System.out.println("profile " + profile);
		NoSQLHandle handle;
		handle = getNoSqlHandler(Region.US_ASHBURN_1, getAuthProvider(profile));

		try {

			for (String str : eventList) {

			
				if (isValid(str)) {
					MapValue value = new MapValue().put("id", UUID.randomUUID().toString()).put("alertjson", str);
					PutRequest putRequest = new PutRequest().setValue(value).setTableName(health_alert_table_name);
					PutResult putResult = handle.put(putRequest);

				}
			}

		} catch (Exception e) {
			System.out.println("exception occured while saving health alerts " + e.getMessage());
		} finally {
			handle.close();
		}

	}

	public static void saveEventDataList(List<Row> eventList) throws Exception {

		String profile = System.getenv("nosql_profile");
		System.out.println("profile " + profile);
		NoSQLHandle handle;
		handle = getNoSqlHandler(Region.US_ASHBURN_1, getAuthProvider(profile));

		try {

			for (Row row : eventList) {

				org.json.JSONArray jsonArr = new org.json.JSONArray(row.getString(0));

				if (isValid(jsonArr.getString(0))) {
					MapValue value = new MapValue().put("id", UUID.randomUUID().toString()).put("eventData",
							jsonArr.getString(0));
					PutRequest putRequest = new PutRequest().setValue(value).setTableName(tableName);
					PutResult putResult = handle.put(putRequest);

				}
			}

		} catch (Exception e) {
			System.out.println("exception occured while health events" + e.getMessage());
		} finally {
			handle.close();
		}

		/* At this point, you can see your table in the Identity Console */
	}

	private static boolean isValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException e) {
			try {
				new JSONArray(json);
			} catch (JSONException ne) {
				return false;
			}
		}
		return true;
	}

}