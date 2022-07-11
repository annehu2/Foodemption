import json, os
import base64
import boto3
from botocore.client import Config
from flask import request
from controllers.middleware import customer_only
from manager.manager import ManagerException
import manager.manager as manager

@customer_only
def verify_customer(currently_authenticated_user):
    verification_data = request.get_json()
    try:
        non_profit_license_num = verification_data["non_profit_license_num"]
        license_documentation_url = verification_data["license_documentation_url"]
        customer_uuid = currently_authenticated_user["uuid"]

    except KeyError: 
        print(verification_data)
        return json.dumps({"status_code": 400, "message": "Fields are missing!"}), 400

    try:
        manager.verify_customer(customer_uuid, non_profit_license_num, license_documentation_url)

    except ManagerException as e:
        return json.dumps({"status_code": 400, "message": str(e)}), 400

    return json.dumps({"status_code": 200}), 200