from kafka import KafkaConsumer
consumer = KafkaConsumer('foodemp_notif_pipe', bootstrap_servers='kafka0:29090', group_id="test_group")

for message in consumer:
    print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))  