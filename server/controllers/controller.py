 
import jwt
 
from flask import request

from controllers.util import login_required

 
 

@login_required
def index(data):
    print(data)
    return "hello world!"


 
def get_encrypted_data():
    return jwt.encode({"some": "data"} ,"SecretCipher", algorithm="HS256")

def decrypt_token():
    print(request.get_json())
    data = request.get_json()
    
    try:
            payload = jwt.decode(data['token'], "SecretCipher",algorithms="HS256")
            print(payload)
    except jwt.ExpiredSignatureError:
            msg = 'Signature has expired.'
            error_code = 400
    except jwt.DecodeError:
            msg = 'Error decoding signature.'
            error_code = 400 
    except jwt.InvalidTokenError:
            error_code = 400
            msg = 'Invalid Token'
    finally:
            return "invalid"
    
 
 
 