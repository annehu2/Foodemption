# import uuid
 
from db import session
from models.app import Customers, Users,Login

# Ideally we'd roll this as a transaction

def get_login_data(email):
    return session.query(Login).filter(Login.user_email == email).first()

# Todo: Need to differentiate a get customer vs get donator data
# Perform a join to fetch complete user data
def get_user_data(userId):
    return session.query(*Users.__table__.columns, *Customers.__table__.columns).select_from(Users).join(Customers, (Users.id == userId) & (Users.id == Customers.id )).first()

# Needs to flip login_data's device token to be "device_token"
# Needs to flip login_data's is_logged_in flag to true
def set_user_state_to_login(user_id, device_token):
    session.query(Login).filter(Login.user_id == user_id).update({Login.device_token: device_token, Login.is_logged_in: True}, synchronize_session = False )
    session.commit()

def set_user_state_to_logout(user_id):
    session.query(Login).filter(Login.user_id == user_id).update({ Login.is_logged_in: False}, synchronize_session = False )
    session.commit()

# Testing purposes
def create_customer():        
    user = Users(
            uuid="should_exist_UUID",
            organization_name="Food Charities",
            type=1
    )

    session.add(user)
    session.commit()
 
    new_customer = Customers(
            non_profit_license_num="IXA-ASD-SD",
            license_documentation_url="XABC-EFG-HHI",
            is_verified=True
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