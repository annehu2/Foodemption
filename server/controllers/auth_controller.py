from flask import request

import json,jwt

from controllers.middleware import authentication_required
from manager.manager import *

# Takes email, password and device token from user
# Peform a look up in the login table. Make sure they match
# If match, set device_token on this user entity. 
# Return a jwt token that encodes information (UUID, email, organization_name, type, is_verified)

def signup():
    user_signup_data = request.get_json()

    try:
        name = user_signup_data['name']
        email = user_signup_data['email']
        password = user_signup_data['password']
        device_token = user_signup_data['device_token']
        type = user_signup_data['type']

    except KeyError: 
        return json.dumps({"status_code":400,"data": {"jwt": "" }}),400

    user = create_user(name, email, password, device_token, type)

    if user == None:
        return json.dumps({"status_code":400, "data": {"jwt": "" }, "message": "Attempt to signup with duplicate email id."}),400

    login_data = get_login_data(email)
    user_data = get_user_object(login_data.user_uuid)
    
    # We can add more data if necessary
    jwt_token = jwt.encode({
        "email": login_data.user_email,
        "uuid": user_data.uuid,
        "organization_name": user_data.organization_name,
        "user_type": user_data.type,
    },"SecretCipher", algorithm="HS256")

    set_user_state_to_login(user_data.uuid, device_token)

    return json.dumps(
        {
            "status_code": 200, 
            "data": 
            {
                "jwt": jwt_token
            }
        }
    ), 200


def signin(): 
    user_login_data = request.get_json()
    
    try:
        email = user_login_data['email']
        password = user_login_data['password']
        device_token = user_login_data['device_token']
    
    except KeyError: 
        return json.dumps({"status_code":400, "data": {"jwt": "" }, "message": "Missing fields."}), 400

    login_data = get_login_data(email)

    # TODO: Implement password hashing. For now this will do
    if login_data is None or login_data.user_password != password:
        return json.dumps({"status_code":400, "data": {"jwt": "" }, "message": "Incorrect credentials."}), 400

    user_data = get_user_object(login_data.user_uuid)
    
    # We can add more data if necessary
    jwt_token = jwt.encode({
        "email": login_data.user_email,
        "uuid": user_data.uuid,
        "organization_name": user_data.organization_name,
        "user_type": user_data.type,
    },"SecretCipher", algorithm="HS256")

    set_user_state_to_login(user_data.uuid, device_token)

    return json.dumps(
        {
            "status_code": 200, 
            "data": 
            {
                "jwt": jwt_token
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
    return json.dumps({"status_code": 200, "message": "successfully logged user out"}), 200