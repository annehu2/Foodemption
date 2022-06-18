FROM ubuntu:20.04

# COPY server.py server.py
# COPY db db
# COPY manager manager
# COPY controllers controllers
# COPY .env .env
RUN mkdir server
COPY . ./server
# Install python
RUN apt-get update && apt-get -y install python3.8 python3-pip
# Install extra deps because of mysqlclient
RUN apt-get install -y python3-dev default-libmysqlclient-dev build-essential

RUN apt-get install sudo -y

# Aliasing python with python3
RUN alias python=python3

# Install all python dependencies
RUN pip install flask flask_sqlalchemy flask_migrate pyjwt mysqlclient

 

WORKDIR /server
EXPOSE 8000
CMD ["python3", "server.py"]