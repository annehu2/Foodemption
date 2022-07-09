FROM ubuntu:20.04
ENV TZ=US/Eastern
COPY . .
# WORKDIR /server
# Install python
RUN apt-get update && apt-get -y install python3-venv python3.8 python3-pip
RUN pip install kafka-python firebase-admin
# # Set up docker time zone. Or mysql-server install will hang
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

 
EXPOSE 8000
CMD ["python3", "consumer.py"]
