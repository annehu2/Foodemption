/**  
    This is for populating the login table to test authentication feature, 
    since we won't demo signup.
*/ 

INSERT INTO customers VALUES (1,"CUSTV1StGXR8_Z5jdHi6B-myTV1St", "salvation_army", "IXA-ASD-SD", "https://www.s3.com", True);

INSERT INTO LOGIN values(1,"CUSTV1StGXR8_Z5jdHi6B-myTV1St", "test@gmail.com","1,2,3", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

INSERT INTO login values(1,1, "should_exist_UUID", "test@gmail.com","1234", "aaaaaaaaaaaaa", False)