drop table if exists donate;
drop table if exists matchingCorp;
drop table if exists company;
drop table if exists donor;
drop table if exists donation;
drop table if exists address;
drop table if exists category;
drop table if exists circle;
drop table if exists state;


create table circle(
	circleId		int primary key,
	circleName		varchar(50),
	donationTier	int	
);

create table category(
	typeId			int primary key,
	type 			varchar(50)
);

create table state(
	stateAbbr		char(2) primary key,
	stateName		varchar(25)
);

create table address(
	addrId			serial primary key,
	street			varchar(50),
	city			varchar(50),
	state 			char(2) references state(stateAbbr),
	zipcode			int
);

create table company(
	corpId			serial primary key,
	name 			varchar(50),
	address			int references address(addrId)
);

create table donor(
	donorId			serial primary key,
	name 			varchar(50),
	spouseName		varchar(50),
	YOG				int,
	category		int references category(typeId),
	address			int references address(addrId),
	circle			int references circle(circleId)
);
create table donation(
	donationId		serial primary key,
	amountPledged	int,
	amountDonated	int,
	paymentMethod	varchar(50),
	dateLastPayment	date
);

create table matchingCorp(
	donationId		int primary key references donation(donationId),
	corpId			int references company(corpId)
);

create table donate(
	donationId		int primary key references donation(donationId),
	donorId			int references donor(donorId),
	donationDate	date
);


insert into state values('AL', 'Alabama');
insert into state values('AK', 'Alaska');
insert into state values('AZ', 'Arizona');
insert into state values('AR', 'Arkansas');
insert into state values('CA', 'California');
insert into state values('CO', 'Colorado');
insert into state values('CT', 'Connecticut');
insert into state values('DE', 'Delaware');
insert into state values('DC', 'District of Columbia');
insert into state values('FL', 'Florida');
insert into state values('GA', 'Georgia');
insert into state values('HI', 'Hawaii');
insert into state values('ID', 'Idaho');
insert into state values('IL', 'Illinois');
insert into state values('IN', 'Indiana');
insert into state values('IA', 'Iowa');
insert into state values('KS', 'Kansas');
insert into state values('KY', 'Kentucky');
insert into state values('LA', 'Louisiana');
insert into state values('ME', 'Maine');
insert into state values('MD', 'Maryland');
insert into state values('MA', 'Massachusetts');
insert into state values('MI', 'Michigan');
insert into state values('MN', 'Minnesota');
insert into state values('MS', 'Mississippi');
insert into state values('MO', 'Missouri');
insert into state values('MT', 'Montana');
insert into state values('NE', 'Nebraska');
insert into state values('NV', 'Nevada');
insert into state values('NH', 'New Hampshire');
insert into state values('NJ', 'New Jersey');
insert into state values('NM', 'New Mexico');
insert into state values('NY', 'New York');
insert into state values('NC', 'North Carolina');
insert into state values('ND', 'North Dakota');
insert into state values('OH', 'Ohio');
insert into state values('OK', 'Oklahoma');
insert into state values('OR', 'Oregon');
insert into state values('PA', 'Pennsylvania');
insert into state values('RI', 'Rhode Island');
insert into state values('SC', 'South Carolina');
insert into state values('SD', 'South Dakota');
insert into state values('TN', 'Tennessee');
insert into state values('TX', 'Texas');
insert into state values('UT', 'Utah');
insert into state values('VT', 'Vermont');
insert into state values('VA', 'Virginia');
insert into state values('WA', 'Washington');
insert into state values('WV', 'West Virginia');
insert into state values('WI', 'Wisconsin');
insert into state values('WY', 'Wyoming');

insert into category values(1, 'Staff');
insert into category values(2, 'Student');
insert into category values(3, 'Alumni');
insert into category values(4, 'Parent');

insert into circle values(1, 'nocircle', 0);
insert into circle values(2, 'Supporter', 100);
insert into circle values(3, 'Contributor', 250);
insert into circle values(4, 'Benefactor', 500);
insert into circle values(5, 'Sponsor Circle', 1000);
insert into circle values(6, 'Bronze Circle', 2500);
insert into circle values(7, 'Silver Circle', 5000);
insert into circle values(8, 'Gold Circle', 10000);
insert into circle values(9, 'Platinum Circle', 25000);
insert into circle values(10, 'Presidents Circle', 50000);
