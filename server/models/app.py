from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:password@localhost:3306/foodDemptionDb'

db = SQLAlchemy(app)
migrate = Migrate(app, db)

 
#Todo: Somehow consolidate customers and donors into a single table. Otherwise
# Not possible to set constraint on (customers | donors) <-> Login table

# One to One relationship between donors and address
class Customers(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(32))
    organization_name = db.Column(db.String(32))
    non_profit_license_num = db.Column(db.String(32))
    license_documentation_url = db.Column(db.String(64)) # Use S3 for this one
    is_verified = db.Column(db.Boolean, unique=False, default=False)
    
    def __repr__(self):
        return f'<User: {self.name}>'

# one donors should map to many foods
class Donors(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(32))
    organization_name = db.Column(db.String(32))
    address_id = db.Column(db.Integer)
    contact = db.Column(db.String(32))
    food_license_number = db.Column(db.String(32))
    license_documentation_url = db.Column(db.String(64)) # Using s3 for this
    is_verified = db.Column(db.Boolean, unique=False, default=False)
    address = db.relationship("Addresses",back_populates="donor", uselist=False)
    donations = db.relationship("Foods")

    def __repr__(self):
        return f'<User: {self.name}>'

class Foods(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(32))
    tile = db.Column(db.String(32))
    description = db.Column(db.String(64))
    best_before = db.Column(db.String(12)) # Save an unix time stamp
    donor_id = db.Column(db.Integer, db.ForeignKey('donors.id'), nullable = False)
    def __repr__(self):
        return f'<User: {self.name}>'

class Addresses(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(32))
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
        return f'<User: {self.name}>'

#  test_obj_id = db.Column(db.Integer, db.ForeignKey('test_db_object.id'), nullable=False)
class Login(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_uuid = db.Column(db.String(32))
    user_email = db.Column(db.String(32))
    user_password = db.Column(db.String(32))
    device_token = db.Column(db.String(256))

    def __repr__(self):
        return f'<User: {self.name}>'