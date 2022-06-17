from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

engine = create_engine(
    "mysql://root:password@localhost:3306/foodDemptionDb",
)

Session = sessionmaker(bind=engine)
session = Session()
