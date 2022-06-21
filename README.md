# cs446-Foodemption!
open client in Android Studio Code
open server in IDE of your preference

# Backend setup guide:
1. Make sure docker is installed on your machine.
2. from `server` directory run `docker-compose up`. verify db is listenning on port 3306
3. Make sure python is installed. First install the dependencies `pip install flask flask_sqlalchemy flask_migrate pyjwt`
4. Try to execute `python -m flask db migrate` from `server/model`. We are likely to run into an error complaining bout mysql client. Depends on your OS the solution may be installing `mysqlclient` or something else.
5. Once the mysql client has been installed, run `python -m flask db migrate && python -m flask db upgrade` from `server/model` again. (Should make sure the database `foodDemptionDb` exists in the db instance on port 3306). 
6. Run `python server.py` to start the server. It should be running on port 8000.
