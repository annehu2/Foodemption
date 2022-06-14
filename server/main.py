import controllers.controller as controller
import controllers.auth_controller as auth_controller
from flask import Flask

app = Flask(__name__)

app.add_url_rule('/',view_func=controller.index)

app.add_url_rule('/cipher',view_func=controller.get_encrypted_data,methods=["GET"])

app.add_url_rule('/decrypt', view_func=controller.decrypt_token, methods=['POST'])

## decorator unprotected route
app.add_url_rule('/login', view_func=auth_controller.signin, methods=['POST'])

## decorator protected route
app.add_url_rule('/logout', view_func=auth_controller.logout, methods=['POST'])


if __name__ == '__main__':
        app.run(debug=True,port=8000)