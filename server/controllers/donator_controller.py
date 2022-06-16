 
import jwt
 
from flask import request

from controllers.util import authentication_required

@authentication_required
def donate(data):
    print(data)
    return "hello world!"