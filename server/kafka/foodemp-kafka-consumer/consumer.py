from kafka import KafkaConsumer
import firebase_admin
from firebase_admin import credentials,messaging

cred = credentials.Certificate("./fcmCreds.json")
app = firebase_admin.initialize_app(cred)

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
print("Connected to Kafka ready for msgs")
for message in consumer:
    device_token = "fpxLT_hHRfCSydZG8wDNM3:APA91bHTkfJYvyKsE8UawEfPk5a4-GwX9STeoQvz0IMwSlnB7p0ChhhBOA7RD_MeuVP7yn4t86asj2C13I-2vtjToP_ykg6l6nuatFPREzxwTIF8reo3kgrfou_-uxS0QOgniXTeq_SE"
    fcm_message = messaging.Message(
     data={
         'Score':'850',
        'time': '2:45'
     },
     token=device_token
    )
    response = messaging.send(fcm_message)
    print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))  