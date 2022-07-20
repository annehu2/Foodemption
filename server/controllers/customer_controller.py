import json, os
import base64
import boto3
from botocore.client import Config
from flask import request
from controllers.middleware import customer_only
from manager.manager import ManagerException
import manager.manager as manager
from utils.redis_accecssor import buffer_food_messages
from utils.kafka_producer import sendMessageToKafka
from utils.enum import KAFKA_TOPIC

@customer_only
def verify_customer(currently_authenticated_user):
    verification_data = request.get_json()
    try:
        non_profit_license_num = verification_data["non_profit_license_num"]
        license_documentation_url = verification_data["license_documentation_url"]
        customer_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(verification_data)
        return json.dumps({"message": "Fields are missing!"}), 400

    try:
        manager.verify_customer(customer_uuid, non_profit_license_num, license_documentation_url)

    except ManagerException as e:
        return json.dumps({"message": str(e)}), 400

    return "", 200


def get_kafka_message_for_requests(customer_uuid, pickup_time, food_uuid, food_data):
    new_request_object = {
        'message_type': 'Requests',
        'payload':{
            'food_uuid': food_uuid, 
            'data': food_data
            'customer_uuid': customer_uuid,
            'pickup_time': pickup_time,
            'message': "You have a new food claim request."  
        }
    }
    return new_request_object

@customer_only
def make_food_claim(currently_authenticated_user):
    food_data = request.get_json()

    try:
        customer_uuid = currently_authenticated_user["uuid"]
        food_uuid = food_data["food_uuid"]
        pickup_time = food_data["pickup_time"]
    except KeyError: 
        return json.dumps({"message": "Fields are missing!"}), 400

    try:
        food = get_food(food_uuid)
        buffer_notification_object = get_kafka_message_for_requests(customer_uuid, pickup_time, food_uuid, 
            {   
                "uuid": food.uuid,
                "title": food.title,
                "image_url": food.image_url,
                "description": food.description,
                "best_before": food.best_before,
                "is_claimed": food.is_claimed
            }
        )
        buffer_food_messages(food_uuid, json.dumps(buffer_notification_object))
        sendMessageToKafka(KAFKA_TOPIC, [json.dumps(buffer_notification_object)])

    except ManagerException as e:
        return json.dumps({"message": str(e)}), 400
    
    return "", 200
