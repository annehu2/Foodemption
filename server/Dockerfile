FROM ubuntu:20.04
ENV TZ=US/Eastern
RUN mkdir server
COPY . ./server
WORKDIR /server

# Install python
RUN apt-get update && apt-get -y install python3.8 python3-pip
# # Install extra deps because of mysqlclient
RUN apt-get install -y python3-dev default-libmysqlclient-dev build-essential

RUN pip install flask flask_sqlalchemy flask_migrate pyjwt mysqlclient boto3 numpy gunicorn
RUN pip install sqlalchemy-utils
# # Set up docker time zone. Or mysql-server install will hang
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

 
EXPOSE 8000
CMD ["bash", "start.sh"]
