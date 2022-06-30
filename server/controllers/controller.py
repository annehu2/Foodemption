 
import jwt
 
from flask import request

from controllers.middleware import authentication_required
from manager.manager import create_customer, create_donor

@authentication_required
def index(data):
    print(data)
    return "hello world!"
 
## TODO: Store the sec key "SecretCipher as an env variable"
def get_encrypted_data():
    return jwt.encode({"user_data": {
        "email": "jack@gmail.com",
        "device_token": "12345_android",
        "uuId" : "USR12345"
    }} ,"SecretCipher", algorithm="HS256") 


def test_create_customer():
    new_customer = create_customer()
    return new_customer.uuid

def test_create_donor():
    new_donor = create_donor()
    return new_donor.uuid