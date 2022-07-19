import redis
from utils.enum import REDIS_HOST

def buffer_notification_msgs(user_uuid, kafka_object):
    r = redis.Redis(host=REDIS_HOST)
    r.rpush(user_uuid, kafka_object)

def fetch_notification_msgs(user_uuid):
    kafka_msgs = []
    r = redis.Redis(host=REDIS_HOST)
    while (r.llen(user_uuid) != 0): # get value one by one
        kafka_msgs.append(r.rpop(user_uuid))
    return kafka_msgs