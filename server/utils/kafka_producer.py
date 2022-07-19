from email.mime import message
from kafka import KafkaProducer
import json
 
def sendMessageToKafka(topic_name, message_object_list):
    configs = {
       "bootstrap_servers":"pkc-ymrq7.us-east-2.aws.confluent.cloud:9092",
       "security_protocol":"SASL_SSL",
       "sasl_mechanism":"PLAIN",
       "sasl_plain_username":"HE4UYYYMJXI3TQIL",
       "sasl_plain_password":"gnm8lEq6qSR3p6XBtyOXGpOudznDchigH1X7vs5Z3JWstjxMtIwDexVKLEX/Inh4"
    }
    producer = KafkaProducer(**configs,  value_serializer=lambda v: json.dumps(v).encode('utf-8'))
    for message_object in message_object_list:
        producer.send(topic_name, json.loads(message_object))