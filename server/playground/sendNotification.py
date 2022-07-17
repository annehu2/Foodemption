import firebase_admin
from firebase_admin import credentials,messaging

cred = credentials.Certificate("./fcmCreds.json")
app = firebase_admin.initialize_app(cred)


device_token = "fpxLT_hHRfCSydZG8wDNM3:APA91bHTkfJYvyKsE8UawEfPk5a4-GwX9STeoQvz0IMwSlnB7p0ChhhBOA7RD_MeuVP7yn4t86asj2C13I-2vtjToP_ykg6l6nuatFPREzxwTIF8reo3kgrfou_-uxS0QOgniXTeq_SE"
message = messaging.Message(
    data={
        'Score':'850',
        'time': '2:45'
    },
    token=device_token
)
response = messaging.send(message)
print ("response ", response)