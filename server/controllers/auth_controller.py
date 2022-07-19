from flask import request

import json,jwt

from controllers.middleware import authentication_required
from manager.manager import *
from utils.enum import KAFKA_TOPIC
from utils.kafka_producer import sendMessageToKafka
from utils.redis_accecssor import fetch_notification_msgs

def signup():
    user_signup_data = request.get_json()

    try:
        name = user_signup_data['name']
        email = user_signup_data['email']
        password = user_signup_data['password']
        device_token = user_signup_data['device_token']
        type = user_signup_data['type']

    except KeyError: 
        return json.dumps({"data": {"jwt": "", "uuid": ""}}),400

    try:
        user = create_user(name, email, password, device_token, type)

    except ManagerException as e:
        return json.dumps({"data": {"jwt": "", "uuid": ""}, "message": str(e)}),400

    login_data = get_login_data(email)

    # We can add more data if necessary
    jwt_token = jwt.encode({
        "email": login_data.user_email,
        "uuid": user.uuid,
        "organization_name": user.organization_name,
        "user_type": user.type,
    },"SecretCipher", algorithm="HS256")

    set_user_state_to_login(user.uuid, device_token)

    return json.dumps(
        {
            "data": 
            {
                "jwt": jwt_token,
                "uuid": user.uuid
            }
        }
    ), 200

# Takes email, password and device token from user
# Peform a look up in the login table. Make sure they match
# If match, set device_token on this user entity. 
# Return a jwt token that encodes information (UUID, email, organization_name, type, is_verified)
def signin(): 
    user_login_data = request.get_json()
    
    try:
        email = user_login_data['email']
        password = user_login_data['password']
        device_token = user_login_data['device_token']

    except KeyError: 
        return json.dumps({"data": {"jwt": "" }, "message": "Fields are missing!"}), 400

    login_data : Login = get_login_data(email)

    # TODO: Implement password hashing. For now this will do
    if login_data is None or login_data.user_password != password:
        return json.dumps({"data": {"jwt": "" }, "message": "Incorrect credentials."}), 400

    user_data: Users = get_user_object(login_data.user_uuid)
    
    buffered_notification = fetch_notification_msgs(login_data.user_uuid)
    print("notifications to deliver ", buffered_notification)
    sendMessageToKafka(KAFKA_TOPIC,  buffered_notification)

    jwt_token = jwt.encode({
        "email": login_data.user_email,
        "uuid": user_data.uuid,
        "organization_name": user_data.organization_name,
        "user_type": user_data.type,
    },"SecretCipher", algorithm="HS256")

    set_user_state_to_login(user_data.uuid, device_token)

    return json.dumps(
        {
            "data": 
            {
                "email": login_data.user_email,
                "user_type": user_data.type,
                "org": user_data.organization_name,
                "jwt": jwt_token,
                "uuid": user_data.uuid
            }
        }
    ), 200

# Not much to do here. Need to remove the device token
# The currentUser is an UserObject
# Perform a lookup in login table, set is_logged_in flag to false. This way 
# Once they log out their token is invalidated.
@authentication_required
def logout(currentUser):
    set_user_state_to_logout(currentUser['uuid'])
    return "", 200