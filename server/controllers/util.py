from flask import request
import json
from functools import wraps
import jwt
## Using decorators, we are able to pass the current user to the function with this data 
## If this token is missing OR invalid OR user no longer exists, return 400


def authentication_required(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
                # print(request.headers)
                auth_tokens = request.headers.get("Authorization")
                if auth_tokens is None :
                    return json.dumps({"message":"You are not authenticated to use this route", "status_code":400})
                
                decoded_user_data = extract_user_from_token(auth_tokens)
        
                if decoded_user_data["success"] == False: 
                    return json.dumps({"message":"You are not authenticated to use this route", "status_code":400})
                
                return f(decoded_user_data["user_object"], **kwargs)
        return decorated_function

def extract_user_from_token(auth_token):
    user_object = None
    msg = None
    error_code = 200
    try:
        decoded_payload = jwt.decode(auth_token, "SecretCipher",algorithms="HS256")
        user_object = decoded_payload['user_data']

    except jwt.ExpiredSignatureError:
            msg = 'Signature has expired.'
            error_code = 400
            
    except jwt.DecodeError:
            msg = 'Error decoding signature.'
            error_code = 400 
    except jwt.InvalidTokenError:
            error_code = 400
            msg = 'Invalid Token'
    
    if error_code == 400:
        return {"success": False, "reason": msg, "user_object": user_object}

    return {"success": True, "reason": None, "user_object": user_object}