import json, os
import base64
from turtle import pen
import boto3
from botocore.client import Config
from flask import request
from controllers.middleware import authentication_required, donator_only
from manager.manager import ManagerException
from kafka import KafkaProducer
import manager.manager as manager
from utils.redis_accecssor import list_pending_messages
from utils.kafka_producer import sendMessageToKafka
from utils.enum import KAFKA_TOPIC

def upload_to_s3(image_base64, file_name):
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


def new_donate():
    new_donation_object = {
    'message_type': 'Donations',
    'payload':{
       'food_id':1,
       'filters':{
          'location': "20",
          'ingridents_filter': [1,2,3,4,5,6]
       },
       'message': "Your food should be ready for pick up in 5 minutes!"   
    }
    }
    new_donation_object = json.dumps(new_donation_object)
    sendMessageToKafka(KAFKA_TOPIC, [new_donation_object])
    
    return json.dumps({"status_code":200}), 200

# only donors can mark a food as claimed because they own the food
@donator_only
def accept_food_claim(currently_authenticated_user):
    food_data = request.get_json()

    try:
        customer_uuid = food_data["customer_uuid"]
        donor_uuid = currently_authenticated_user["uuid"]
        food_uuid = food_data["food_uuid"]
    except KeyError: 
        return json.dumps({"message": "Fields are missing!"}), 400

    try:
        manager.claim_food(donor_uuid, customer_uuid, food_uuid)
    except ManagerException as e:
        return json.dumps({"message": str(e)}), 400
    
    return "", 200

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
def verify_donor(currently_authenticated_user):
    verification_data = request.get_json()
    try:
        contact = verification_data["contact"]
        food_license_number = verification_data["food_license_number"] 
        license_documentation_url = verification_data["license_documentation_url"]
        address = verification_data["address"]
        donor_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(verification_data)
        return json.dumps({"message": "Fields are missing!"}), 400

    try:
        manager.verify_donor(donor_uuid, contact, food_license_number, license_documentation_url, address)

    except ManagerException as e:
        return json.dumps({"message": str(e)}), 400

    return "", 200

@donator_only
def donate_food(currently_authenticated_user):
    donation_data = request.get_json()

    try:
        title = donation_data["title"]
        description = donation_data["description"] 
        image_base64 = donation_data["image_base64"]
        best_before = donation_data["best_before"]
        donor_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(donation_data)
        return json.dumps({"message": "Fields are missing!"}), 400
    
    # print(title, description, image_base64, best_before, donor_uuid)

    image_url = upload_to_s3(image_base64, title)
    # image_url ="google.com"
    try:
        food_data = manager.add_food(title, description, image_url, best_before, donor_uuid)

        # TODO integrate the ingriedient filter coming from the frontend
        new_donation_object = {
            'message_type': 'Donations',
            'payload':{
            'food_id': food_data.id,
            'filters':{
            'location': "20",
            'ingridents_filter': [1,2,3,4,5,6]
            },
            'message': "Your food should be ready for pick up in 5 minutes!"   
        }
        }
        new_donation_object = json.dumps(new_donation_object)
        print("reading from kafka topic...", KAFKA_TOPIC)
        sendMessageToKafka(KAFKA_TOPIC, [ new_donation_object])

    except ManagerException as e:
        return json.dumps({"message": str(e)}), 400

    return json.dumps({"data": {"uuid": food_data.uuid}}), 200

@donator_only
def retrieve_all_donations(currently_authenticated_user):
    try:
        donor_uuid = currently_authenticated_user["uuid"]
    except KeyError: 
        return json.dumps({"message": "Fields are missing!"})

    donations = manager.get_food_by_donor(donor_uuid)

    return json.dumps(
        {
            "data": [ { "uuid": food.uuid,
                        "title": food.title,
                        "image_url": food.image_url,
                        "description": food.description,
                        "best_before": food.best_before,
                        "is_claimed": food.is_claimed} for food in donations ]
        }
    ), 200


@donator_only
def retrieve_all_pending_claims(current_authenticated_user):
     try:
        donor_uuid = currently_authenticated_user["uuid"]
    except KeyError: 
        return json.dumps({"message": "Fields are missing!"})

    donations = manager.get_food_by_donor(donor_uuid)

    all_pending_claims = []
    for food in donations:
        food_uuid = food.uuid
        pending_claims = list_pending_messages(food_uuid)
        pending_claims = [ json.loads(pending_claim)['payload'] for pending_claim in pending_claims]
        all_pending_claims += pending_claims
    
    return json.dumps({
        "data":[{
            "pickup_time": pending_claim['pickup_time'],
            "customer_name": pending_claim['customer_name'],
            "customer_uuid": pending_claim['customer_uuid'],
            "data": pending_claim["data"]
        } for pending_claim in all_pending_claims]
    })