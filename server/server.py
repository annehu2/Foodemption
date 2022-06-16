import controllers.controller as controller
import controllers.auth_controller as auth_controller
import controllers.donator_controller as donator_controller
from flask import Flask

app = Flask(__name__)

## decorator unprotected route
app.add_url_rule('/cipher',view_func=controller.get_encrypted_data,methods=["GET"])
app.add_url_rule('/login', view_func=auth_controller.signin, methods=['POST'])

## decorator protected route
app.add_url_rule('/',view_func=controller.index)
app.add_url_rule('/logout', view_func=auth_controller.logout, methods=['POST'])
app.add_url_rule('/donate', view_func=donator_controller.donate, methods=['POST'])

if __name__ == '__main__':
        app.run(debug=True,port=8000)