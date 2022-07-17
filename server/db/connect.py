from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from utils.enum import MYSQL_HOST
from sqlalchemy_utils import database_exists, create_database

engine = create_engine(f'mysql://root:password@{MYSQL_HOST}:3306/foodDemptionDb')
if not database_exists(engine.url):
    create_database(engine.url)
 
engine = create_engine(
    f"mysql://root:password@{MYSQL_HOST}:3306/foodDemptionDb",
    pool_recycle=60*5,
    pool_pre_ping=True
)

Session = sessionmaker(bind=engine)
session = Session()