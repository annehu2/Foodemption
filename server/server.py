import controllers.controller as controller
import controllers.auth_controller as auth_controller
import controllers.donator_controller as donator_controller
import controllers.customer_controller as customer_controller
import controllers.filter_controller as filter_controller

from utils.enum import MYSQL_HOST
from flask import Flask
app = Flask(__name__)
 
## decorator unprotected route
app.add_url_rule('/cipher',view_func=controller.get_encrypted_data,methods=["GET"])
app.add_url_rule('/signup', view_func=auth_controller.signup, methods=['POST'])
app.add_url_rule('/login', view_func=auth_controller.signin, methods=['POST'])
app.add_url_rule('/test_create_customer', view_func=controller.test_create_customer, methods=['POST'])
app.add_url_rule('/test_create_donor', view_func=controller.test_create_donor, methods=['POST'])
app.add_url_rule('/new/donate', view_func=donator_controller.new_donate, methods=['POST'])
## decorator protected route
app.add_url_rule('/',view_func=controller.index)
app.add_url_rule('/logout', view_func=auth_controller.logout, methods=['POST'])
app.add_url_rule('/donate', view_func=donator_controller.donate_food, methods=['POST'])
app.add_url_rule('/food', view_func=controller.retrieve_food, methods=['GET'])
app.add_url_rule('/donations', view_func=donator_controller.retrieve_all_donations, methods=['GET'])
app.add_url_rule('/accept_claim', view_func=donator_controller.accept_food_claim, methods=['POST'])
app.add_url_rule('/verify_donor', view_func=donator_controller.verify_donor, methods=['POST'])
app.add_url_rule('/verify_customer', view_func=customer_controller.verify_customer, methods=['POST'])
app.add_url_rule('/claimed_food', view_func=controller.retrieve_all_claimed_food, methods=['GET'])
app.add_url_rule('/available_food', view_func=controller.retrieve_all_available_food, methods=['GET'])

app.add_url_rule('/filters_bulk_create', view_func=filter_controller.create_filters, methods=['GET'])
app.add_url_rule('/filter_intersect', view_func=filter_controller.filter_intersect)
app.add_url_rule('/customer_filter_update', view_func=filter_controller.update_customer_filter, methods=['PUT'])
app.add_url_rule('/food_filter_update', view_func=filter_controller.update_food_filter, methods=['PUT'])
app.add_url_rule('/kafka/food/filter/<food_id>', '/kafka/food/filter/<food_id>', filter_controller.get_device_tokens_base_on_food_filters, methods=['GET'])
# TODO: Look into docker's networking model and figure out how this work in another
#       machine (i.e EC2)
if __name__ == '__main__':
        print(MYSQL_HOST)
        app.run(debug=True, host="0.0.0.0",port=8000)