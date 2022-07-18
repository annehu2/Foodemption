# foodemp_notif_pipe
from kafka import KafkaConsumer
import firebase_admin, json, http.client
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
consumer = KafkaConsumer('foodemp_notif_pipe', **kafka_configs, value_deserializer=lambda m: json.loads(m.decode('utf-8')))
print("Connected to Kafka broker! ready for msgs")

def dispatch_fcm(msg, device_token):
    fcm_msg = messaging.Message(
        android=messaging.AndroidConfig(priority="high"),
        data={
            'message':msg
        },
        token=device_token
    )
    messaging.send(fcm_msg)
    return 200

def handle_reminder_event(event):
    print("received reminder event")
    device_token = event['payload']['device_token']
    foodemp_event_msg =  event['payload']['message']
    dispatch_fcm(foodemp_event_msg, device_token)
    print("Successfull dispatched a a reminder object to device")    

def handle_donation_event(event):
    print("Received new donation event")
    device_to_push_notif = get_all_interested_users(event)

    for device_token in device_to_push_notif:
        foodemp_event_message = event['payload']['message']
        dispatch_fcm(foodemp_event_message, device_token)

    print("Donation Notification dispatch complete")

# TODO: Create an end point API side that returns
#       a list of device tokens from people that are interested in this
def get_all_interested_users(event):
    # make an http request to API
    http_conn = http.client.HTTPConnection("fooddempapi",8000)
    http_conn.request("GET","/kafka/food/filter/1")
    http_response = http_conn.getresponse()
    resp_data = http_response.read()
    # print(resp_data)

    try:
        response_object = json.loads(resp_data)
        return response_object['device_tokens']
    except KeyError:
        return []
    # return ["e9l7RTT9Qm-sgS3KW5npSt:APA91bG4b_SRFvMCJTc2eAfZC4LnH1CyfCl4mxbVeha9fqbIUVgRrBhArgHqQf0P-3Ay-lAYX6mQ1nA0Zecm-ikiE_t6IsMEAEWcXkNeRQ9A0iA1LmNNe3p95lJYKcQ7t6IqG3AfIZaZ"]




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
