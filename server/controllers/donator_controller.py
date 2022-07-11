import json, os
import base64
import boto3
from botocore.client import Config
from flask import request
from controllers.middleware import authentication_required, donator_only
from manager.manager import add_food, get_food_by_donor, verify_donor, claim_food, ManagerException
from botocore.vendored import requests

def upload_to_s3(image_base64, file_name):
        return "test_url"
        print(os.getenv('ACCESS_KEY_ID'))

        s3 = boto3.resource('s3', 
            aws_access_key_id=os.getenv('ACCESS_KEY_ID'),
            aws_secret_access_key=os.getenv('ACCESS_SECRET_KEY'),
            config=Config(signature_version='s3v4')
        )
        bucket_name = 'foodemptionimages'
        obj = s3.Object(bucket_name, file_name)
        obj.put(Body=base64.b64decode(image_base64))
        # retrieve bucket location
        location = boto3.client('s3', 
            aws_access_key_id=os.getenv('ACCESS_KEY_ID'),
            aws_secret_access_key=os.getenv('ACCESS_SECRET_KEY')
        ).get_bucket_location(Bucket=bucket_name)['LocationConstraint']
        # retrieve object url
        object_url = "https://%s.s3-%s.amazonaws.com/%s" % (bucket_name, location, file_name)

        return object_url

@donator_only
def accept_food_claim(currently_authenticated_user):
    food_data = request.get_json()
    try:
        customer_uuid = food_data["customer_uuid"]
        donor_uuid = currently_authenticated_user["uuid"]
        food_uuid = food_data["food_uuid"]
    except KeyError: 
        return json.dumps({"status_code": 400, "message": "Fields are missing!"}), 400
    try:
        claim_food(donor_uuid, food_uuid, customer_uuid)

    except ManagerException as e:
        return json.dumps({"status_code": 400, "message": str(e)}), 400
    
    return json.dumps({"status_code": 200}), 200

'''
request example:
    {
        "contact": "000-000-0000",
        "food_license_number": "LICENSE00000", 
        "license_documentation_url": "test_url", 
        "address": 
            {
                "city_name": "Waterloo", 
                "street_name": "University Ave W",
                "street_number": "116",
                "postal_code": "N2L3E2",
                "building_name": "NA"
            }
    }
'''
@donator_only
def verify(currently_authenticated_user):
    verification_data = request.get_json()
    try:
        contact = verification_data["contact"]
        food_license_number = verification_data["food_license_number"] 
        license_documentation_url = verification_data["license_documentation_url"]
        address = verification_data["address"]
        donor_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(verification_data)
        return json.dumps({"status_code": 400, "message": "Fields are missing!"}), 400

    try:
        verify_donor(donor_uuid, contact, food_license_number, license_documentation_url, address)

    except ManagerException as e:
        return json.dumps({"status_code": 400, "message": str(e)}), 400

    return json.dumps({"status_code": 200}), 200

@donator_only
def donate_food(currently_authenticated_user):
    donation_data = request.get_json()
    # print(currently_authenticated_user)
    # print(donation_data)

    try:
        title = donation_data["title"]
        description = donation_data["description"] 
        image_base64 = donation_data["image_base64"]
        best_before = donation_data["best_before"]
        donor_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(donation_data)
        return json.dumps({"status_code": 400, "message": "Fields are missing!"}), 400
    
    # print(title, description, image_base64, best_before, donor_uuid)

    image_url = upload_to_s3(image_base64, title)

    try:
        food_data = add_food(title, description, image_url, best_before, donor_uuid)
    except ManagerException as e:
        return json.dumps({"status_code": 400, "message": str(e)}), 400

    return json.dumps({"status_code":200, "data": {"uuid": food_data.uuid}}), 200

@donator_only
def retrieve_all_donations(currently_authenticated_user):
    try:
        donor_uuid = currently_authenticated_user["uuid"]
    except KeyError: 
        return json.dumps({"status_code": 400, "message": "Fields are missing!"})

    donations = get_food_by_donor(donor_uuid)

    return json.dumps(
        {
            "status_code": 200, 
            "data": [ { "uuid": food.uuid,
                        "title": food.title,
                        "image_url": food.image_url,
                        "description": food.description,
                        "best_before": food.best_before,
                        "is_claimed": food.is_claimed} for food in donations ]
        }
    ), 200
'''
---- TESTS ----

signup:

curl -v -H "Content-Type: application/json" -X POST -d '{"name": "New Pizza Place", "email": "test_donor@gmail.com", "password": "password", "device_token": "12345_android", "type": "0"}' http://0.0.0.0:8000/signup
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /signup HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 130
> 
* upload completely sent off: 130 out of 130 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:18:35 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 296
< Connection: close
< 
* Closing connection 0
{"status_code": 200, "data": {"jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90"}}%

or create test user:

curl -v -H "Content-Type: application/json" -X POST http://0.0.0.0:8000/test_create_donor

now we can login with email: test_donor@gmail.com, password: password

curl -v -H "Content-Type: application/json" -X POST -d '{"email": "test_donor@gmail.com", "password": "password", "device_token": "12345_android"}' http://0.0.0.0:8000/login

Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /login HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 90
> 
* upload completely sent off: 90 out of 90 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Fri, 24 Jun 2022 20:35:21 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 296
< Connection: close
< 
* Closing connection 0
{"status_code": 200, "data": {"jwt": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjUxYTI5M2E0LTUyYzEtNDEzMS04MTBiLWFkMTdhMzNhODg0MSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.DSxmgDvgkKvaNyW4dtobs5nHrEOimZ7pXUK6AWXS5M4"}}%

using other email like tanavya@gmail.com will fail

curl -v -H "Content-Type: application/json" -X POST -d '{"email": "tanavya@gmail.com", "password": "password", "device_token": "12345_android"}' http://0.0.0.0:8000/login

Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /login HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 87
> 
* upload completely sent off: 87 out of 87 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Wed, 22 Jun 2022 13:19:33 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 48
< Connection: close
< 
* Closing connection 0
{"status_code": 403, "message": "Access denied"}%

or incorrect password will also fail

curl -v -H "Content-Type: application/json" -X POST -d '{"email": "test_donor@gmail.com", "password": "wrong_password", "device_token": "12345_android"}' http://0.0.0.0:8000/login

Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /login HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Content-Length: 96
> 
* upload completely sent off: 96 out of 96 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Wed, 22 Jun 2022 13:21:52 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 48
< Connection: close
< 
* Closing connection 0
{"status_code": 403, "message": "Access denied"}%

test add food will fail because user is not verified as donor

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X POST -d '{"title": "pizza", "description": "large margherita pizza", "best_before":"1656084484", "image_base64": "test"}' http://0.0.0.0:8000/donate

Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /donate HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90
> Content-Length: 111
> 
* upload completely sent off: 111 out of 111 bytes
< HTTP/1.1 400 BAD REQUEST
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:31:31 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 89
< Connection: close
< 
* Closing connection 0
{"status_code": 400, "message": "Donor does not exist or user is not verified as donor."}

verify donor as user

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X POST -d \
    '{ "contact": "000-000-0000", "food_license_number": "LICENSE00000", "license_documentation_url": "test_url", "address": { "city_name": "Waterloo", "street_name": "University Ave W", "street_number": "116", "postal_code": "N2L3E2", "building_name": "NA" } }' \
    http://0.0.0.0:8000/verify_donor
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /verify_donor HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90
> Content-Length: 256
> 
* upload completely sent off: 256 out of 256 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:32:12 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 20
< Connection: close
< 
* Closing connection 0
{"status_code": 200}%

test add food again

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X POST -d '{"title": "pizza", "description": "large margherita pizza", "best_before":"1656084484", "image_base64": "test"}' http://0.0.0.0:8000/donate

Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> POST /donate HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90
> Content-Length: 111
> 
* upload completely sent off: 111 out of 111 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:33:10 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 78
< Connection: close
< 
* Closing connection 0
{"status_code": 200, "data": {"uuid": "6e69d7bf-15f3-40e2-8a47-6430c2ea2954"}}%

test retrieve food

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X GET -d '{"uuid": "6e69d7bf-15f3-40e2-8a47-6430c2ea2954"}' http://0.0.0.0:8000/food
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> GET /food HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90
> Content-Length: 48
> 
* upload completely sent off: 48 out of 48 bytes
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:34:07 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 212
< Connection: close
< 
* Closing connection 0
{"status_code": 200, "data": {"uuid": "6e69d7bf-15f3-40e2-8a47-6430c2ea2954", "title": "pizza", "image_url": "test_url", "description": "large margherita pizza", "best_before": "1656084484", "is_claimed": false}}%

test retrieve all donations

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X GET http://0.0.0.0:8000/donations
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 0.0.0.0...
* TCP_NODELAY set
* Connected to 0.0.0.0 (127.0.0.1) port 8000 (#0)
> GET /donations HTTP/1.1
> Host: 0.0.0.0:8000
> User-Agent: curl/7.64.1
> Accept: */*
> Content-Type: application/json
> Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90
> 
< HTTP/1.1 200 OK
< Server: Werkzeug/2.1.2 Python/3.9.7
< Date: Mon, 11 Jul 2022 01:34:54 GMT
< Content-Type: text/html; charset=utf-8
< Content-Length: 214
< Connection: close
< 
* Closing connection 0
{"status_code": 200, "data": [{"uuid": "6e69d7bf-15f3-40e2-8a47-6430c2ea2954", "title": "pizza", "image_url": "test_url", "description": "large margherita pizza", "best_before": "1656084484", "is_claimed": false}]}%

retrieve all claimed food

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X GET http://0.0.0.0:8000/claimed_food

retrieve all available food

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X GET http://0.0.0.0:8000/available_food

mark food as claimed #todo

curl -v -H "Content-Type: application/json" -H "Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3RfZG9ub3JAZ21haWwuY29tIiwidXVpZCI6IjU2ZTQ4YmJlLTYyN2EtNGUyZC1iYmUzLTUyMGNhZDc1ZGJkYSIsIm9yZ2FuaXphdGlvbl9uYW1lIjoiTmV3IFBpenphIFBsYWNlIiwidXNlcl90eXBlIjowfQ.Mjy5eiJj-vfMQS_MbRj-TYhv5jP0YozfQEAMLzeQx90" -X POST -d '{"customer_uuid": "---", "food_uuid": "---"}' http://0.0.0.0:8000/accept_claim
'''
