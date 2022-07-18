 
import jwt,json
 
from flask import request

from controllers.middleware import authentication_required
from manager.manager import apply_change_set_to_customer_filter, apply_change_set_to_food_filter, get_all_customers, create_all_filters, intersect_filter
from controllers.middleware import customer_only, donator_only
from utils.redis_accecssor import buffer_notification_msgs

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

# Get all login data, check if user is customer
def get_device_tokens_base_on_food_filters(food_id):
    
    device_tokens = []
    loggedin_customers = get_all_customers()
    for customer in loggedin_customers:
        if customer.is_logged_in:
            device_tokens.append(customer.device_token)
        else:
            buffer_donation_object = get_kafka_message_for_donations(food_id)
            buffer_notification_msgs(customer.user_uuid, json.dumps(buffer_donation_object))
        
    return json.dumps({"message": "successful！","device_tokens": device_tokens })


# TODO Implement it such that information regarding filters comes form backend
def get_kafka_message_for_donations(food_id):
    new_donation_object = {
    'message_type': 'Donations',
    'payload':{
       'food_id': food_id,
       'filters':{
          'location': "20",
          'ingridents_filter': [1,2]
       },
       'message': "You have a pending donation in your area!"   
    }
    }
    return new_donation_object

# TODO: Implement check to ensure the food actually belongs to authenticated user
@donator_only
def update_food_filter(authenticated_user):
    try:
        filter_update_request = request.get_json()
        change_set = filter_update_request['change_set']
        food_id = filter_update_request['food_id']
        food_object = apply_change_set_to_food_filter(food_id, change_set)
        return json.dumps({"message":"Update succesful", "food": food_object})
    except KeyError:
        return json.dumps({"message":"Unproper structured data"}),400

@customer_only
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