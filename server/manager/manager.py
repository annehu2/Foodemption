# import uuid
from db import session
from models.app import Customers, Users
from sqlalchemy.orm import Query


# Ideally we'd roll this as a transaction
def create_customer():        
    user = Users(
            uuid="should_exist_UUID",
            organization_name="Food Charities",
        type=1
    )

    session.add(user)
    session.commit()
 
    # user_object = get_user_object(user.uuid)
    # if user_object is None:
    #         print("ASDASDASDSAD")
    #         session.rollback()
    #         return None
    new_customer = Customers(
            non_profit_license_num="IXA-ASD-SD",
            license_documentation_url="XABC-EFG-HHI",
            is_verified=False
    )
    session.enable_relationship_loading(new_customer)
    new_customer.id = user.id
    session.add(new_customer)

    session.commit()

    return user

def get_user_object(uuid):
    
    return session.query(Users).filter(uuid == uuid).first()

def fetch_customer():
    return 20