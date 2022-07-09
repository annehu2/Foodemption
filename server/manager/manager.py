from this import d
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
def create_test_customer():
    user = create_user("Food Charities", "jack@gmail.com", "password", "12345_android", 1)
    if user == None:
        return user
    else:
        verify_customer(user.uuid)
        return user

def create_test_donor():
    user = create_user("New Pizza Place", "test_donor@gmail.com", "password", "12345_android", 0)
    if user == None:
        return user
    else:
        verify_donor(user.uuid, "000-000-0000", "LICENSE00000", "test_url", 
            {
                "city_name": "Waterloo", 
                "street_name": "University Ave W",
                "street_number": "116",
                "postal_code": "N2L3E2",
                "building_name": "NA"
            })
        return user

def create_user(name, email, password, device_token, type):        
    try:
        user = Users(
            uuid=str(uuid.uuid4()),
            organization_name=name,
            type=type
        )

        session.add(user)
        session.commit()

        new_login = Login(
            user_uuid = str(uuid.uuid4()),
            user_email = email,
            user_password = password,
            device_token = device_token,
            is_logged_in = False
        )

        session.enable_relationship_loading(new_login)
        new_login.user_id = user.id
        new_login.user_uuid = user.uuid
        session.add(new_login)
        session.commit()

        return user

    except:
        print("WARN: Duplicate user. Rolling back...")
        session.rollback()
        session.delete(user)
        session.commit()
        return None

def verify_customer(user_uuid, license_num, license_url):
    user = get_user_object(user_uuid)
    new_customer = Customers(
        non_profit_license_num=license_num,
        license_documentation_url=license_url,
        is_verified=True
    )

    session.enable_relationship_loading(new_customer)
    new_customer.id = user.id
    session.add(new_customer)
    session.commit()

# returns 0 if successful 
# returns 1 if failed (duplicate address)
def verify_donor(user_uuid, phone, license_num, license_url, address):
    try:
        user = get_user_object(user_uuid)
        new_donor = Donors(
            address_id = 0,
            contact = phone,
            food_license_number = license_num,
            license_documentation_url = license_url,
            is_verified = True,
        )

        session.enable_relationship_loading(new_donor)
        new_donor.id = user.id

        session.add(new_donor)
        session.commit()

        new_address = Addresses(
            uuid = str(uuid.uuid4()),
            city_name = address["city_name"],
            street_name = address["street_name"],
            street_number = address["street_number"],
            postal_code = address["postal_code"],
            building_name = address["building_name"],
        )

        session.enable_relationship_loading(new_address)
        new_address.donor_id = new_donor.id
        session.add(new_address)
        session.commit()
        return 0

    except:
        print("WARN: Duplicate address for donor. Rolling back...")
        session.rollback()
        session.delete(new_donor)
        session.commit()
        return 1


def add_food(title, description, image_url, best_before, donor_uuid):
    donor_id = get_donor_by_uuid(donor_uuid).id
    new_food = Foods(uuid = str(uuid.uuid4()),
                    title = title,
                    image_url = image_url,
                    description = description,
                    best_before = best_before,
                    donor_id = donor_id,
                    customer_uuid = 0,
                    is_claimed = False)
    session.add(new_food)
    session.commit()

    return new_food

def claim_food(donor_uuid, food_uuid, customer_uuid):
    donor_id = get_donor_by_uuid(donor_uuid).id
    food = session.query(Foods).filter(Foods.donor_id == donor_id and Foods.uuid == food_uuid)
    if food != None:
        food.update({Foods.is_claimed: True, Foods.customer_uuid: customer_uuid}, synchronize_session = False)
        session.commit()
        return 0
    else:
        return 1

def get_user_object(uuid):
    return session.query(Users).filter(Users.uuid == uuid).first()

def get_food(uuid):
    return session.query(Foods).filter(Foods.uuid == uuid).first()

def get_food_by_donor(donor_uuid):
    donor = get_donor_by_uuid(donor_uuid)
    return session.query(Foods).filter(Foods.donor_id == donor.id).all()

def get_donor_by_uuid(donor_uuid):
    user = session.query(Users).filter(Users.uuid == donor_uuid).first()
    donor = session.query(Donors).filter(Donors.id == user.id).first()
    return donor

def get_claimed_food_by_donor(donor_uuid):
    donor = get_donor_by_uuid(donor_uuid)
    return session.query(Foods).filter(Foods.donor_id == donor.id and Foods.is_claimed == True).all()

