# Credit to Jay Kodeswaran
import redis, json

r = redis.Redis()
r.mset({"Crotia": "Zabreb", "Bahamas": "Nassau"}) # simple dictionary in redis
print(r.get("Bahamas")) # get value

new_donation_object = {
    'message_type': 'Donations',
    'payload':{
       'food_id':1,
       'filters':{
          'location': "20",
          'ingridents_filter': [1,2,3,4,5,6]
       },
       'message': "Your food should be ready for pick up in 5 minutes!"   
    }
}

r.rpush("USER_NANOID", json.dumps(new_donation_object)) # push list

while (r.llen("USER_NANOID") != 0): # get value one by one
    object_to_send = json.loads(r.rpop("USER_NANOID"))
    print(object_to_send)

# r.flushdb() # remove all fr