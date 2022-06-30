import uuid
 
from numpy import imag
from db import session
from models.app import Customers,Foods,Users,Login,Donors,Addresses

# Ideally we'd roll this as a transaction

def get_login_data(email):
    return session.query(Login).filter(Login.user_email == email).first()

# Todo: Need to differentiate a get customer vs get donator data
# Perform a join to fetch complete user data
def get_customer_data(userId):
    return session.query(*Users.__table__.columns, *Customers.__table__.columns).select_from(Users).join(Customers, (Users.id == userId) & (Users.id == Customers.id )).first()

def get_user_session_data(user_uuid):
    return session.query(*Users.__table__.columns, *Login.__table__.columns).select_from(Users).join(Login, (Users.uuid == user_uuid) & (Users.id == Login.user_id )).first()

# Needs to flip login_data's device token to be "device_token"
# Needs to flip login_data's is_logged_in flag to true
def set_user_state_to_login(user_uuid, device_token):
    session.query(Login).filter(Login.user_uuid == user_uuid).update({Login.device_token: device_token, Login.is_logged_in: True}, synchronize_session = False )
    session.commit()

def set_user_state_to_logout(user_uuid):
    session.query(Login).filter(Login.user_uuid == user_uuid).update({ Login.is_logged_in: False}, synchronize_session = False )
    session.commit()

# Testing purposes
def create_customer():        
    user = Users(
        uuid=str(uuid.uuid4()),
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

    new_login = Login(
        user_uuid = str(uuid.uuid4()),
        user_email = "jack@gmail.com",
        user_password = "password",
        device_token = "12345_android",
        is_logged_in = False
    )

    session.enable_relationship_loading(new_login)
    new_login.user_id = user.id
    new_login.user_uuid = user.uuid
    session.add(new_login)
    session.commit()

    return user


# todo: adding address will fail if it is a duplicate address (i.e integrity contraint fails)
# but that means an additional donor is added with address_id = 0 rather than correct id

def create_donor():   

    session.rollback() 

    user = Users(
        uuid=str(uuid.uuid4()),
        organization_name="New Pizza Place",
        type=0
    )

    session.add(user)
    session.commit()

    new_donor = Donors(
        address_id = 0,
        contact = "000-000-0000",
        food_license_number = "LICENSE00000",
        license_documentation_url = "test_url",
        is_verified = True,
    )

    session.enable_relationship_loading(new_donor)
    new_donor.id = user.id

    session.add(new_donor)
    session.commit()

    new_address = Addresses(
        uuid = str(uuid.uuid4()),
        city_name = "Waterloo",
        street_name = "University Ave W",
        street_number = "116",
        postal_code = "N2L3E2",
        building_name = "NA"
    )

    session.enable_relationship_loading(new_address)
    new_address.donor_id = new_donor.id
    session.add(new_address)
    session.commit()

    session.query(Donors).filter(Donors.id == new_donor.id).update({ Donors.address_id: new_address.id}, synchronize_session = False )
    session.commit()

    new_login = Login(
                user_uuid = str(uuid.uuid4()),
                user_email = "test_donor@gmail.com",
                user_password = "password",
                device_token = "12345_android",
                is_logged_in = False
    )

    session.enable_relationship_loading(new_login)
    new_login.user_id = user.id
    new_login.user_uuid = user.uuid
    session.add(new_login)
    session.commit()

    return user

def get_user_object(uuid):
    return session.query(Users).filter(Users.uuid == uuid).first()

def fetch_customer():
    return 20

def add_food(title, description, image_url, best_before, donor_uuid):
    donor_id = get_donor_by_uuid(donor_uuid).id
    new_food = Foods(uuid = str(uuid.uuid4()),
                    title = title,
                    image_url = image_url,
                    description = description,
                    best_before = best_before,
                    donor_id = donor_id)
    session.add(new_food)
    session.commit()

    return new_food

def get_food(uuid):
    return session.query(Foods).filter(Foods.uuid == uuid).first()

def get_food_by_donor(donor_uuid):
    donor = get_donor_by_uuid(donor_uuid)
    return session.query(Foods).filter(Foods.donor_id == donor.id).all()

def get_donor_by_uuid(donor_uuid):
    user = session.query(Users).filter(Users.uuid == donor_uuid).first()
    donor = session.query(Donors).filter(Donors.id == user.id).first()
    return donor