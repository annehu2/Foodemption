from kafka import KafkaProducer
import firebase_admin
from firebase_admin import credentials,messaging
import json
configs = {
       "bootstrap_servers":"pkc-ymrq7.us-east-2.aws.confluent.cloud:9092",
       "security_protocol":"SASL_SSL",
       "sasl_mechanism":"PLAIN",
       "sasl_plain_username":"HE4UYYYMJXI3TQIL",
       "sasl_plain_password":"gnm8lEq6qSR3p6XBtyOXGpOudznDchigH1X7vs5Z3JWstjxMtIwDexVKLEX/Inh4"
}

reminder_object = {
    'message_type': 'Reminder',
    'payload':{
       'user_id':"1",
       'device_token': "fpxLT_hHRfCSydZG8wDNM3:APA91bHTkfJYvyKsE8UawEfPk5a4-GwX9STeoQvz0IMwSlnB7p0ChhhBOA7RD_MeuVP7yn4t86asj2C13I-2vtjToP_ykg6l6nuatFPREzxwTIF8reo3kgrfou_-uxS0QOgniXTeq_SE",
       'message': "Your food should be ready for pick up in 5 minutes!"   
    }
}

new_donation_object = {
    'message_type': 'Donations',
    'payload':{
       'food_id':1,
       'filters':{
          'location': "20",
          'ingridents_filter': [1,2,3,4,5,6]
       },
       'message': "Your food should be ready for pick up in 5 minutes!"   
    }
}

# KafkaProducer(value_serializer=lambda v: json.dumps(v).encode('utf-8'))
producer = KafkaProducer(**configs, value_serializer=lambda v: json.dumps(v).encode('utf-8'))
send_to_kafka = f'sending this message to kafka using'
producer.send('foodemp_test_topic',new_donation_object)
producer.close()
# return json.dumps({"status_code":200}), 200