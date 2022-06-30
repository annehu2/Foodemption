# Assume working directory is ./server

function enter_wkdir {
    cd "/server";
}

function start_mysql_and_create_db {
    enter_wkdir;
    sudo /etc/init.d/mysql start && mysql mysql < ./db/db_creation.sql;
}

function run_db_migrations {
    enter_wkdir;
    cd ./models && python3 -m flask db migrate && python3 -m flask db upgrade;
}

function run_python_api_server {
    enter_wkdir;
    gunicorn --bind 0.0.0.0:8000 server:app;
}


function boot_strap {
    enter_wkdir;
    start_mysql_and_create_db;
    run_db_migrations;
    run_python_api_server;
}

boot_strap;
# sudo /etc/init.d/mysql start && mysql mysql < ./db/db_creation.sql && cd ./models && python3 -m flask db migrate && python3 -m flask db upgrade && python3 ../server.py