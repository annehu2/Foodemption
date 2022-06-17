# import uuid
from db import session
from models.app import Customers

def create_customer():
    new_customer = Customers(
        uuid="CUSTV1StGXR8_Z5jdHi6B-myTV1St",
        organization_name="Food Charities",
        non_profit_license_num="IXA-ASD-SD",
        license_documentation_url="XABC-EFG-HHI",
        is_verified=False
    )
    session.add(new_customer)
    session.commit()
    return new_customer

def fetch_customer():
    return 20