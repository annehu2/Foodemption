 
import jwt,json
 
from flask import request

from controllers.middleware import authentication_required
from manager.manager import apply_change_set_to_customer_filter, create_donor, create_all_filters, intersect_filter
from controllers.middleware import consumer_only, donator_only
# from server.controllers.middleware import consumer_only
# from server.manager.manager import intersect_filter

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


def create_filters():
    res = create_all_filters()
    return "ok"


def filter_intersect():
    intersect_filter()
    return "ok"

@donator_only
def update_food_filter(authenticated_user):
    try:
        print("asd")
    except KeyError:
        return json.dumps({"message":"Unproper structured data"}),400

@consumer_only 
def update_customer_filter(authenticated_user):
     
    try:
        filter_update_request = request.get_json()
        change_set = filter_update_request['change_set']
         
        customer_object = apply_change_set_to_customer_filter(authenticated_user['id'], change_set)
        return json.dumps({"message": "Update succesful!", "customer": customer_object})
    except KeyError:
        return json.dumps({"message":"Inpropertly structured data"}),400
    except AssertionError:
        return json.dumps({"message": "The filter cannot be present in both addition and removal changeset"}),400
    return "ok", 200