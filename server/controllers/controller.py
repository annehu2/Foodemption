 
import jwt
 
from flask import request
import json
from controllers.middleware import authentication_required
from manager.manager import create_test_customer, create_test_donor, get_food

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
    new_customer = create_test_customer()
    if new_customer == None:
        return json.dumps({"status_code": 400, "message": "Tried to create duplicate customer"}), 400
    else:
        return json.dumps({"status_code": 200}), 200

def test_create_donor():
    new_donor = create_test_donor()
    if new_donor == None:
        return json.dumps({"status_code": 400, "message": "Tried to create duplicate donor"}), 400
    else:
        return json.dumps({"status_code": 200}), 200

def retrieve_food():
    food_data = request.get_json()

    try:
        food_uuid = food_data["uuid"]
    except KeyError: 
        return json.dumps({"status_code": 400, "message": "Fields are missing!"}), 400

    food = get_food(food_uuid)

    return json.dumps(
        {
            "status_code": 200, 
            "data": {
                "uuid": food.uuid,
                "title": food.title,
                "image_url": food.image_url,
                "description": food.description,
                "best_before": food.best_before,
                "is_claimed": food.is_claimed
            }
        }
    )
