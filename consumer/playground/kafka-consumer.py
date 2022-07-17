from kafka import KafkaConsumer
import firebase_admin, json
from firebase_admin import credentials,messaging


fcm_cred = credentials.Certificate('./fcmCreds.json') 
fcm_app = firebase_admin.initialize_app(fcm_cred)

kafka_configs = {
    "bootstrap_servers":"pkc-ymrq7.us-east-2.aws.confluent.cloud:9092",
    "security_protocol":"SASL_SSL",
    "group_id": "test_group",
    "sasl_mechanism":"PLAIN",
    "sasl_plain_username":"HE4UYYYMJXI3TQIL",
    "sasl_plain_password":"gnm8lEq6qSR3p6XBtyOXGpOudznDchigH1X7vs5Z3JWstjxMtIwDexVKLEX/Inh4"
}
# Todo: Would be nice to serialize an python class instead.
consumer = KafkaConsumer('foodemp_test_topic', **kafka_configs, value_deserializer=lambda m: json.loads(m.decode('utf-8')))
print("Connected to Kafka ready for msgs")

def dispatch_fcm(msg, device_token):
    fcm_msg = messaging.Message(
        data={
            'message':msg
        },
        token=device_token
    )
    messaging.send(fcm_msg)
    return 200

def handle_reminder_event(event):
    device_token = foodemp_event['payload']['device_token']
    foodemp_event_msg =  foodemp_event['payload']['message']
    dispatch_fcm(foodemp_event_msg, device_token)
    print("Successfull dispatched a a reminder object to device")    

def handle_donation_event(event):
    print("got donation event")

for message in consumer:
    # print(message.value)
    foodemp_event = message.value
    try:
        msg_type = foodemp_event['message_type']
        if msg_type == "Donations":
            handle_donation_event(foodemp_event)
        elif msg_type == "Reminder":
            handle_reminder_event(foodemp_event)
    except Exception as e:
        print(e)
    # print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
    #                                       message.offset, message.key,
    #                                       message.value))

