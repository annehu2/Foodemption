from kafka import KafkaConsumer
configs = {
    "bootstrap_servers":"pkc-ymrq7.us-east-2.aws.confluent.cloud:9092",
    "security_protocol":"SASL_SSL",
    "group_id": "test_group",
    "sasl_mechanism":"PLAIN",
    "sasl_plain_username":"HE4UYYYMJXI3TQIL",
    "sasl_plain_password":"gnm8lEq6qSR3p6XBtyOXGpOudznDchigH1X7vs5Z3JWstjxMtIwDexVKLEX/Inh4"
}
consumer = KafkaConsumer('foodemp_notif_pipe', 
                        bootstrap_servers="pkc-ymrq7.us-east-2.aws.confluent.cloud:9092",
                        group_id="test_group",
                        security_protocol="SASL_SSL",
                        sasl_mechanism="PLAIN",
                        sasl_plain_username="HE4UYYYMJXI3TQIL",
                        sasl_plain_password="gnm8lEq6qSR3p6XBtyOXGpOudznDchigH1X7vs5Z3JWstjxMtIwDexVKLEX/Inh4"
                    )

for message in consumer:
    print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))  