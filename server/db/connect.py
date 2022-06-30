from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from utils.enum import MYSQL_HOST

engine = create_engine(
    f"mysql://root:password@{MYSQL_HOST}:3306/foodDemptionDb",
)

Session = sessionmaker(bind=engine)
session = Session()
