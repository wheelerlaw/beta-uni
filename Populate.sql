insert into address(street, city, state, zipcode) values('35 Devereux St.', 'Marblehead', 'MA', 01945);
insert into donor(name, yog, spouseName, address, category, circle) values('Wheeler', 2017, 'Someone', 1, 2, 1);
update donor set name='Bob', yog=1994, spouseName='Anne', address=1, category=1, circle=1 where donorId=1;