import json, jwt
from functools import wraps
from flask import request
from manager.manager import  get_user_session_data
from utils.enum import CUSTOMER_TYPE, DONOR_TYPE

 
def authentication_required(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
                
                authenticated_user = _get_authenticated_user()
                if authenticated_user is None:
                        return json.dumps({"message":"You are not authenticated to use this route.", "status_code":403}), 403

                return f(authenticated_user, **kwargs)
        return decorated_function

def donator_only(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
    
                authenticated_user = _get_authenticated_user()
                if authenticated_user is None or authenticated_user.type == CUSTOMER_TYPE:
                        return json.dumps({"message":"You are not authenticated to use this route.", "status_code":403}), 403

                return f(authenticated_user, **kwargs)
        return decorated_function

def customer_only(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
                authenticated_user = _get_authenticated_user()
                if authenticated_user is None or authenticated_user.type == DONOR_TYPE:
                        return json.dumps({"message":"You are not authenticated to use this route.", "status_code":403}), 403

                return f(authenticated_user, **kwargs)
        return decorated_function

def _get_authenticated_user():
        auth_tokens = request.headers.get("Authorization")
        if auth_tokens is None :
                return None

        decoded_user_data, _error_msg = extract_user_from_token(auth_tokens)
        return decoded_user_data

# We check against db here, with the user's ID
def extract_user_from_token(auth_token):
    user_object = None
    error_msg = None
    error_code = 200
    try:
        decoded_payload = jwt.decode(auth_token, "SecretCipher",algorithms="HS256")

        login_data = get_user_session_data(decoded_payload['uuid'])
        
        if login_data == None:
                error_code = 400
                error_msg = "User does not exist."
        elif login_data.is_logged_in is True:
                user_object = login_data
        else:
                error_code = 400
                error_msg = "User not logged in."
 
    except jwt.ExpiredSignatureError:
            msg = 'Signature has expired.'
            error_code = 400
            
    except jwt.DecodeError:
            msg = 'Error decoding signature.'
            error_code = 400 
    except jwt.InvalidTokenError:
            error_code = 400
            msg = 'Invalid Token'
    return user_object, error_msg