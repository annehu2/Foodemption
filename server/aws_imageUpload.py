import boto3
from botocore.client import Config

ACCESS_KEY_ID = 'AKIAZQURHGTRFYNCKLZM'
ACCESS_SECRET_KEY = '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'
BUCKET_NAME = 'foodemption'

data = open('test.jpg', 'rb')

# Print out bucket names
for bucket in s3.buckets.all():
    print(bucket.name)

# S3 Connect
s3 = boto3.resource(
    's3',
    aws_access_key_id=ACCESS_KEY_ID,
    aws_secret_access_key=ACCESS_SECRET_KEY,
    config=Config(signature_version='s3v4')
)

# Image download
s3.Bucket(BUCKET_NAME).put_object(Key='test.jpg', Body=data)
# This is where we want to download it too.

print ("Done")