from flask import request

import json,jwt

from controllers.util import authentication_required
from manager.manager import  get_login_data, get_user_data, set_user_state_to_login

# Takes email, password and device token from user
# Peform a look up in the login table. Make sure they match
# If match, set device_token on this user entity. 
# Return a jwt token that encodes information (UUID, email, organization_name, type, is_verified)

def signin(): 
    user_login_data = request.get_json()
    
    email = user_login_data['email']
    password = user_login_data['password']
    device_token = user_login_data['device_token']
    
    if email is None or password is None or device_token is None: 
         return json.dumps({"status_code":400, "message":"Fields are missing!"})

    login_data = get_login_data(email)

    # TODO: Implement password hashing. For now this will do
    if login_data is None or login_data.user_password != password:
        return json.dumps({"status_code":403, "message":"Access denied"})

    user_data = get_user_data(login_data.user_id)
    
    # We can add more data if necessary
    jwt_token = jwt.encode({
        "email": login_data.user_email,
        "id": user_data.id, 
        "organization_name": user_data.organization_name,
        "user_type": user_data.type,
        "is_verified": user_data.is_verified
    },"SecretCipher", algorithm="HS256")

    set_user_state_to_login(login_data, device_token)

    return json.dumps({"status_code":200, "jwt": jwt_token})

# Not much to do here. Need to remove the device token
# The currentUser is an UserObject
# Perform a lookup in login table, set is_logged_in flag to false. This way 
# Once they log out their token is invalidated.
@authentication_required
def logout(currentUser):
    return json.dumps({"message":"successfully logged user out", "status_code":200})