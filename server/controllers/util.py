from flask import request

## This is cool. We are able to pass the current user to the function with this data 
## If this token is missing OR invalid OR user no longer exists, return 400

def login_required(f):
        def decorated_function(*args, **kwargs):
                print(request.headers)
                return f([24], **kwargs)
        return decorated_function