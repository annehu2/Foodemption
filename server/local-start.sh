rm -rf models/migrations/versions && mkdir -p models/migrations/versions
touch models/migrations/versions/.keep
mysql < ./db/db_creation.sql -h 127.0.0.1 -uroot -ppassword
cd ./models
python3 -m flask db migrate
python3 -m flask db upgrade
python3 ../server.py
