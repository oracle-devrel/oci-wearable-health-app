package com.oracle.cloud.wearable.streaming.analytics;

import static org.apache.spark.sql.functions.array;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.explode;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.functions.to_json;
import static org.apache.spark.sql.functions.window;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.DataStreamReader;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.StructType;

public class HealthEventAnalysis {

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org.apache").setLevel(Level.WARN);

		ResourceBundle rd = ResourceBundle.getBundle("application");
		String bootstrapServersRd;
		String streamOrKafkaTopicName;
		String kafkaStreamPoolId;
		if (!"LOCAL".equals(System.getenv("queue_profile"))) {
			bootstrapServersRd = System.getenv("BOOT_STRAP_SERVER");
			streamOrKafkaTopicName = System.getenv("STREAM_NAME");
			kafkaStreamPoolId = System.getenv("STREAM_POOL_ID");
		} else {
			System.out.println("locally running it");
			bootstrapServersRd = rd.getString("com.kafkaclientoci.bootstrapServers");
			streamOrKafkaTopicName = rd.getString("com.kafkaclientoci.streamOrKafkaTopicName");
			kafkaStreamPoolId = rd.getString("com.kafkaclientoci.streamPoolId");

		}
		String saslJaasConfig = rd.getString("sasl.jaas.config");
		String kafkaAuthentication = "plain"; // args[2];
		System.out.println("kafkaAuthentication " + kafkaAuthentication);
		String jsonPath = "./src/main/resources/health_data.json";
		String jsonPathdataschema = "./src/main/resources/data_schema.json";
		SparkSession spark;
		if (args.length != 0) {
			if (!"".equals(args[0]) && !(args[0] == null))
				jsonPath = args[0];
			if (!"".equals(args[1]) && !(args[1] == null))
				jsonPathdataschema = args[1];
		}
		SparkConf conf = new SparkConf();

		if (conf.contains("spark.master")) {
			spark = SparkSession.builder().appName("HealthEventAnalysis")
					.config("spark.sql.streaming.minBatchesToRetain", "10").config("spark.sql.shuffle.partitions", "1")
					.config("spark.sql.streaming.stateStore.maintenanceInterval", "300").getOrCreate();
		} else {

			spark = SparkSession.builder().appName("StructuredKafkaWordCount").master("local[*]")
					.config("spark.sql.streaming.minBatchesToRetain", "10").config("spark.sql.shuffle.partitions", "1")
					.config("spark.sql.streaming.stateStore.maintenanceInterval", "300").getOrCreate();
		}

		StructType jsonSchemafromCloudEvent = spark.read().json(jsonPath).schema();
		StructType jsonSchemaforData = spark.read().json(jsonPathdataschema).schema();
		// get environment variables;
		String QUEUE_OCID = System.getenv("QUEUE_OCID");
		String DP_CLIENT = System.getenv("DP_CLIENT");
		System.out.println("QUEUE_OCID" + QUEUE_OCID + "DP_CLIENT" + DP_CLIENT);
		// adding in the environment for queue
		if (!"LOCAL".equals(System.getenv("queue_profile"))) {
			Environment._DP_ENDPOINT = DP_CLIENT;
			Environment._QUEUE_ID = QUEUE_OCID;
		}
		DataStreamReader dataStreamReader = spark.readStream().format("kafka")
				.option("kafka.bootstrap.servers", bootstrapServersRd).option("subscribe", streamOrKafkaTopicName)
				.option("kafka.security.protocol", "SASL_SSL").option("kafka.max.partition.fetch.bytes", 1024 * 1024) // limit
				.option("startingOffsets", "latest");

		if ("LOCAL".equals(System.getenv("queue_profile"))) {

			// use plain for local
			// USE Resource Principal for Dataflow deployment
			System.out.println("Using PLAIN module for Dataflow kafkaStreamPoolId ");
			dataStreamReader.option("kafka.sasl.mechanism", "PLAIN").option("kafka.sasl.jaas.config", saslJaasConfig);

		} else {

			// USE Resource Principal for Dataflow deployment
			System.out.println("Using RP module for Dataflow kafkaStreamPoolId " + kafkaStreamPoolId);
			dataStreamReader.option("kafka.sasl.mechanism", "OCI-RSA-SHA256").option("kafka.sasl.jaas.config",
					"com.oracle.bmc.auth.sasl.ResourcePrincipalsLoginModule required intent=\"streamPoolId:"
							+ kafkaStreamPoolId + "\";");

		}

		// Create DataFrame representing the stream of input lines from Kafka
		Dataset<Row> lines = dataStreamReader.load().selectExpr("CAST(value AS STRING) as cloudevent", "timestamp")
				.select(from_json(col("cloudevent"), jsonSchemafromCloudEvent).as("cloudevent"), col("timestamp"))
				.withColumn("cloudevent", explode(array(col("cloudevent")))).select("cloudevent.*", "timestamp");

		Dataset<Row> eventData = lines.withColumn("eventData", from_json(col("data"), jsonSchemaforData))// explode(array(col("data"))))
				.select("eventData.*", "timestamp");

		lines.printSchema();
//		eventData.createOrReplaceTempView("dataview");
		// saving all events in nosql db

		lines.select(to_json(array(col("data"))).as("eventData")).writeStream()
				.foreachBatch((Dataset<Row> batchDF, Long batchId) -> {
					System.out.println("inside process health event to nosqldb");
//					batchDF.show();
					List<Row> message_list = batchDF.collectAsList();
					if (message_list != null) {

						System.out.println("number of records in the dataset to save " + message_list.size());
					}
					SaveToNoSqlDB.saveEventDataList(message_list);

				}).outputMode("append").trigger(Trigger.ProcessingTime("2 minutes")).start();

		// adding watermark for aggregate events
		int SysBP_Thershold = 120;
		int DiasBP_Thershold = 80;
		int Spo2_Thershold = 90;
		int heartrate_Thershold = 140;
		String WINDOW_TRIGGER_TIME = "2 minutes";
		String WATERMARK_TIME = "1 second";
		// systolic bp
		eventData.withWatermark("timestamp", WATERMARK_TIME)
				.groupBy(window(functions.col("timestamp"), WINDOW_TRIGGER_TIME), col("deviceSerialNumber"),
						col("username")

				).agg(functions.avg("systolicBP").as("systolic_bp_avg")).where("systolic_bp_avg >" + SysBP_Thershold)
				.writeStream().foreachBatch((Dataset<Row> batchDF, Long batchId) -> {
					System.out.println("inside sending messages to queue systolic");
					batchDF.show();
					List<Row> message_list = batchDF.collectAsList();
					if (message_list != null) {

						System.out.println("number of records for Systolic BP queue alerts " + message_list.size());
					}
					QueueProducer.sendHealthMessage(message_list, "Systolic BP", SysBP_Thershold);

				}).outputMode("append").trigger(Trigger.ProcessingTime(WINDOW_TRIGGER_TIME)).start();

		// spo2 level
		eventData.withWatermark("timestamp", WATERMARK_TIME)
				.groupBy(window(functions.col("timestamp"), WINDOW_TRIGGER_TIME), col("deviceSerialNumber"),
						col("username")

				).agg(functions.avg("spo2Level").alias("spo2_level_avg")).where("spo2_level_avg <" + Spo2_Thershold)
				.writeStream().foreachBatch((Dataset<Row> batchDF, Long batchId) -> {
					System.out.println("inside sending messages to queue spo2");
					batchDF.show();
					List<Row> message_list = batchDF.collectAsList();
					if (message_list != null) {

						System.out.println("number of records for Spo2 level queue alerts " + message_list.size());
					}
					QueueProducer.sendHealthMessage(message_list, "Spo2", Spo2_Thershold);

				}).outputMode("append").trigger(Trigger.ProcessingTime(WINDOW_TRIGGER_TIME)).start();

		// diastolic bp level
		eventData.withWatermark("timestamp", WATERMARK_TIME)
				.groupBy(window(functions.col("timestamp"), WINDOW_TRIGGER_TIME), col("deviceSerialNumber"),
						col("username")

				).agg(functions.avg("diastolicBP").alias("diastolicBP_bp_avg"))
				.where("diastolicBP_bp_avg <" + DiasBP_Thershold).writeStream()
				.foreachBatch((Dataset<Row> batchDF, Long batchId) -> {
					System.out.println("inside sending messages to queue diastolic bp");
					batchDF.show();
					List<Row> message_list = batchDF.collectAsList();
					if (message_list != null) {

						System.out.println("number of records for diastolic bp queue alerts " + message_list.size());
					}
					QueueProducer.sendHealthMessage(message_list, "diastolicBP", DiasBP_Thershold);

				}).outputMode("append").trigger(Trigger.ProcessingTime(WINDOW_TRIGGER_TIME)).start();

		// heart rate level

		eventData.withWatermark("timestamp", WATERMARK_TIME)
				.groupBy(window(functions.col("timestamp"), WINDOW_TRIGGER_TIME), col("deviceSerialNumber"),
						col("username")

				).agg(functions.avg("heartRate").alias("heartRate_bp_avg"))
				.where("heartRate_bp_avg >" + heartrate_Thershold).writeStream()
				.foreachBatch((Dataset<Row> batchDF, Long batchId) -> {
					System.out.println("inside sending messages to queue heart rate");
					batchDF.show();
					List<Row> message_list = batchDF.collectAsList();
					if (message_list != null) {

						System.out.println("number of records for heart rate queue alerts " + message_list.size());
					}
					QueueProducer.sendHealthMessage(message_list, "heartrate", heartrate_Thershold);

				}).outputMode("append").trigger(Trigger.ProcessingTime(WINDOW_TRIGGER_TIME)).start();

		// watermarking ends here

		spark.streams().awaitAnyTermination();

	}

}
