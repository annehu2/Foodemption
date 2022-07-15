from this import d
import uuid
 
from numpy import imag
from db import session
from models.app import Customers,Foods,Users,Login,Donors,Addresses, Filters
from utils.enum import CUSTOMER_TYPE

class ManagerException(Exception):
    pass

# Ideally we'd roll this as a transaction
def get_intersection(filterA, filterB):

    return list(set(filterA) & set(filterB))


def get_difference(filterA, filterB):

    return list(set(filterA) - set(filterB))

def add_set(listA, listB):
    return list(set(listA)|  set(listB))


def intersect_filter():
    a_food = session.query(Foods).filter(Foods.id == 1).first()
    a_customer = session.query(Customers).filter(Customers.id == 1).first()
    customer_object = a_customer.get()
    food_object = a_food.get()

    print(get_intersection(customer_object['filters'], food_object['filters']))
    return 20

def updatec_customer_filter(customer_id):
    return 20

# TODO: Refactor this logic
def apply_change_set_to_customer_filter(customer_id, change_set):

    # This lets us avoid the awkward case where we both want to apply and remove an ingrdient filter
    if len(get_intersection(change_set['add_filter'], change_set['remove_filter'])) != 0 :
        raise AssertionError

    customer_object = session.query(Customers).filter(Customers.id == customer_id).first()
    if customer_object is None:
        return 404
    customer_dto = customer_object.get()    
    customer_filters = customer_dto['filters']

    add_filter =  get_difference(change_set['add_filter'], customer_filters)
    remove_filter = get_intersection(change_set['remove_filter'], customer_filters)
    filter_object_to_add = session.query(Filters).filter(Filters.id.in_(add_filter)).all()
    filter_to_remove = session.query(Filters).filter(Filters.id.in_(remove_filter)).all()

    customer_object.filters.extend(filter_object_to_add)

    for f in filter_to_remove:
        customer_object.filters.remove(f)

    session.commit()

    return customer_object.get()


def apply_change_set_to_food_filter(customer_id, change_set):

    # This lets us avoid the awkward case where we both want to apply and remove an ingrdient filter
    if len(get_intersection(change_set['add_filter'], change_set['remove_filter'])) != 0 :
        raise AssertionError

    customer_object = session.query( Foods).filter(Customers.id == customer_id).first()
    if customer_object is None:
        return 404
    customer_dto = customer_object.get()    
    customer_filters = customer_dto['filters']

    add_filter =  get_difference(change_set['add_filter'], customer_filters)
    remove_filter = get_intersection(change_set['remove_filter'], customer_filters)
    filter_object_to_add = session.query(Filters).filter(Filters.id.in_(add_filter)).all()
    filter_to_remove = session.query(Filters).filter(Filters.id.in_(remove_filter)).all()

    customer_object.filters.extend(filter_object_to_add)

    for f in filter_to_remove:
        customer_object.filters.remove(f)

    session.commit()
    return customer_object.get()

#TODO instead, we should think about seeding with a .sql script
def create_all_filters():

    a_filter = session.query(Filters).filter(Filters.id == 1).first()
    if a_filter is not None:
        return 200

    a = Filters(id=1, description="ContainPeanuts")
    b = Filters(id=2, description="ContainShellfish")
    c = Filters(id=3, description= "ContainGluten")
    d = Filters(id=4, description="ContainEggs")
    e = Filters(id=5, description="ContainMilk")

    session.add_all([a,b,c,d,e])
    session.commit()

    return "ok"


def get_login_data(email):
    return session.query(Login).filter(Login.user_email == email).first()

# Todo: Need to differentiate a get customer vs get donator data
# Perform a join to fetch complete user data
def get_customer_data(userId):
    return session.query(*Users.__table__.columns, *Customers.__table__.columns).select_from(Users).join(Customers, (Users.id == userId) & (Users.id == Customers.id )).first()

def get_user_session_data(user_uuid):
    return session.query(*Users.__table__.columns, *Login.__table__.columns).select_from(Users).join(Login, (Users.uuid == user_uuid) & (Users.id == Login.user_id )).first()

def get_all_customers_who_are_loggedin():
    return session.query(*Users.__table__.columns, *Login.__table__.columns).select_from(Users).join(Login, (Users.type == CUSTOMER_TYPE) & (Users.id == Login.user_id ) & (Login.is_logged_in == True)).all()
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
        raise ManagerException("Attempt to signup with duplicate email id.")

def verify_customer(user_uuid, license_num, license_url):
    try:
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

    except:
        print("WARN: User already verified. Rolling back...")
        session.rollback()
        session.commit()
        raise ManagerException("Verification failed. User already verified.")

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

    except:
        print("WARN: User already verified. Rolling back...")
        session.rollback()
        session.commit()
        raise ManagerException("Verification failed. User already verified.")

    try:
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

    except:
        print("WARN: Duplicate address for donor. Rolling back...")
        session.rollback()
        session.delete(new_donor)
        session.commit()
        raise ManagerException("Verification failed. Duplicate address for donor.")

def add_food(title, description, image_url, best_before, donor_uuid):
    donor = get_donor_by_uuid(donor_uuid)
    if donor == None:
        raise ManagerException("Donor does not exist or user is not verified as donor.")
    
    donor_id = get_donor_by_uuid(donor_uuid).id
    new_food = Foods(uuid = str(uuid.uuid4()),
                    title = title,
                    image_url = image_url,
                    description = description,
                    best_before = best_before,
                    donor_id = donor_id,
                    is_claimed = False)
    session.add(new_food)
    session.commit()

    return new_food

def claim_food(donor_uuid, customer_uuid, food_uuid):
    donor = get_donor_by_uuid(donor_uuid)
    if donor == None:
        raise ManagerException("Donor does not exist or user is not verified as donor.")
    donor_id = donor.id

    customer = get_customer_by_uuid(customer_uuid)
    if customer == None:
        raise ManagerException("Customer does not exist or user is not verified as customer.")
    customer_id = customer.id

    food = session.query(Foods).filter(Foods.donor_id == donor_id and Foods.uuid == food_uuid)
    if food != None:
        food.update({Foods.is_claimed: True, Foods.customer_id: customer_id}, synchronize_session = False)
        session.commit()
    else:
        raise ManagerException("Failed to claim food. Food does not belong to current user or food does not exist.")

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

def get_customer_by_uuid(customer_uuid):
    user = session.query(Users).filter(Users.uuid == customer_uuid).first()
    customer = session.query(Customers).filter(Customers.id == user.id).first()
    return customer

def get_all_claimed_food():
    return session.query(Foods).filter(Foods.is_claimed == True).all()

def get_all_available_food():
    return session.query(Foods).filter(Foods.is_claimed == False).all()
