from email.policy import default
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:password@127.0.0.1:3306/foodDemptionDb'

db = SQLAlchemy(app)
migrate = Migrate(app, db)

#Todo: Somehow consolidate customers and donors into a single table. Otherwise
# Not possible to set constraint on (customers | donors) <-> Login table

# One to One relationship between donors and address
class Customers(db.Model):
    id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False, primary_key=True)
    # uuid = db.Column(db.String(32))
    # organization_name = db.Column(db.String(32))
    non_profit_license_num = db.Column(db.String(32))
    license_documentation_url = db.Column(db.String(64)) # Use S3 for this one
    is_verified = db.Column(db.Boolean, unique=False, default=False)
    
    def __repr__(self):
        return '<Customer:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))

# one donors should map to many foods
class Donors(db.Model):
    id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False, primary_key=True)
    # uuid = db.Column(db.String(32))
    # organization_name = db.Column(db.String(32))
    address_id = db.Column(db.Integer) 
    contact = db.Column(db.String(32))
    food_license_number = db.Column(db.String(32))
    license_documentation_url = db.Column(db.String(64)) # Using s3 for this
    is_verified = db.Column(db.Boolean, unique=False, default=False)
    address = db.relationship("Addresses",back_populates="donor", uselist=False)
    donations = db.relationship("Foods")

    def __repr__(self):
        return '<Donor:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))

class Users(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(128))
    organization_name = db.Column(db.String(32))
    type = db.Column(db.Integer) # 1 stands for customer, 0 stands for donors

    def __repr__(self):
        return '<User:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))

class Foods(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(128))
    title = db.Column(db.String(32))
    image_url = db.Column(db.String(512))
    description = db.Column(db.String(64))
    best_before = db.Column(db.String(12)) # Save an unix time stamp
    donor_id = db.Column(db.Integer, db.ForeignKey('donors.id'), nullable = False)
    def __repr__(self):
        return '<Food:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))

class Addresses(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(128))
    city_name = db.Column(db.String(20), nullable=False)
    street_name = db.Column(db.String(20), nullable=False)
    street_number = db.Column(db.String(20), nullable=False)
    postal_code = db.Column(db.String(6), nullable=False)
    building_name = db.Column(db.String(20))
    donor_id = db.Column(db.Integer, db.ForeignKey('donors.id'), nullable=False)
    donor = db.relationship('Donors', back_populates="address")
    __table_args__ =(
        db.UniqueConstraint('city_name','street_name','street_number'),
        db.UniqueConstraint('street_number','postal_code'),
    )
    def __repr__(self):
        return '<Address:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))

#  test_obj_id = db.Column(db.Integer, db.ForeignKey('test_db_object.id'), nullable=False)
#  
class Login(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    user_uuid = db.Column(db.String(128))
    user_email = db.Column(db.String(32))
    user_password = db.Column(db.String(32))
    device_token = db.Column(db.String(256))
    is_logged_in = db.Column(db.Boolean, default=False)

    def __repr__(self):
        return '<Login:{}>'.format(', '.join("%s: %s" % item for item in vars(self).items()))
