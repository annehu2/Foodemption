from flask import request

import json

from controllers.util import authentication_required
# Just username, password, and device token
def signin(): 
    user_login_data = request.get_json()
    
    email = user_login_data['email']
    password = user_login_data['password']
    device_token = user_login_data['device_token']

    return json.dumps({"email": email, "device_token": device_token,"jwt":"abdefgjokas"})

# Not much to do here. Need to remove the device token
# associated with the currentUser so they no longer receive the notification
# Alternatively, we could also incl a flag to notify us that user no longer is logged in

@authentication_required
def logout(currentUser):
    return json.dumps({"message":"successfully logged user out", "status_code":200})
