import os
CUSTOMER_TYPE=1
DONOR_TYPE=0
MYSQL_HOST=os.getenv('MYSQL_HOST') or "localhost"
KAFKA_TOPIC=os.getenv("KAFKA_TOPIC") or "foodemp_test_topic"
REDIS_HOST=os.getenv("REDIS_HOST") or "localhost"