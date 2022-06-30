from flask import request

import json,jwt

from controllers.middleware import authentication_required
from manager.manager import  get_login_data, get_user_object, set_user_state_to_login, set_user_state_to_logout

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
        return json.dumps({"status_code":400,"data": {"jwt": "" }}),400

    login_data = get_login_data(email)

    # TODO: Implement password hashing. For now this will do
    if login_data is None or login_data.user_password != password:
        return json.dumps({"status_code":400,"data": {"jwt": "" }}),400

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
    return json.dumps({"message":"successfully logged user out", "status_code":200})