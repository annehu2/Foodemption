from flask import request

import json
# Just username, password, and device token
def signin(): 
    user_login_data = request.get_json()
    
    email = user_login_data['email']
    password = user_login_data['password']
    device_token = user_login_data['device_token']

    return json.dumps({"email": email, "password": password, "device_token": device_token})

def logout():
    return "tests"