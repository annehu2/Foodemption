 
import json, jwt
from functools import wraps
from flask import request
from manager.manager import get_login_data
## Using decorators, we are able to pass the current user to the function with this data 
## If this token is missing OR invalid OR user no longer exists, return 400


def authentication_required(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
                # print(request.headers)
                auth_tokens = request.headers.get("Authorization")
                if auth_tokens is None :
                    return json.dumps({"message":"You are not authenticated to use this route. No Auth token provided.", "status_code":400})

                decoded_user_data, error_msg = extract_user_from_token(auth_tokens)
        
                if decoded_user_data is None: 
                    return json.dumps({"message":"You are not authenticated to use this route. Error: {}".format(error_msg), "status_code":400})
                
                return f(decoded_user_data, **kwargs)
        return decorated_function

# We check against db here, with the user's ID
def extract_user_from_token(auth_token):
    user_object = None
    error_msg = None
    error_code = 200
    try:
        decoded_payload = jwt.decode(auth_token, "SecretCipher",algorithms="HS256")
        login_data = get_login_data(decoded_payload['email'])
          
        if login_data.is_logged_in is True:
                user_object = decoded_payload
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