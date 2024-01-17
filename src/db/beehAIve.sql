DROP DATABASE IF EXISTS beehaive;
CREATE DATABASE beehaive;
USE beehaive;

# Tabelle
CREATE TABLE Beekeeper (
    email varchar(50) primary key,
    passwordhash varchar(100) not null,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    company_name varchar(100),
    company_PIVA varchar(100) unique,
    subscribed boolean not null,
    payment_due double not null,
    subscr_expiration_date date
);

CREATE TABLE Bee (
    scientific_name varchar(50) primary key,
    common_name varchar(50) not null,
    bee_description varchar(300) not null,
    photo varchar(50) not null
);

CREATE TABLE Hive (
    ID int primary key auto_increment,
    nickname varchar(50) not null,
    hive_type varchar(30) not null,
    creation_date date not null,
    beekeeper_email varchar(50) not null,
    bee_species varchar(50) not null,
	hive_health int not null,
    uncompleted_operations boolean not null,
    foreign key (beekeeper_email) references Beekeeper(email),
    foreign key (bee_species) references Bee(scientific_name)
);

CREATE TABLE Production (
    ID int primary key auto_increment,
    product varchar(50) not null,
    weight double not null,
    notes varchar(300) not null,
    registration_date date not null,
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (hive_ID) references Hive(ID),
    foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Sensor (
    ID int primary key auto_increment,
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (hive_ID) references Hive(ID),
	foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Anomaly (
    ID int primary key auto_increment,
    anomaly_name varchar(100) not null,
    resolved boolean not null,
    detection_date datetime not null,
    sensor_ID int not null,
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (sensor_ID) references Sensor(ID),
    foreign key (hive_ID) references Hive(ID),
    foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Operation (
    ID int primary key auto_increment,
    operation_name varchar(70) not null,
    operation_type varchar(50) not null,
    operation_status varchar(30) not null,
    operation_date date not null,
    notes varchar(300),
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (hive_ID) references Hive(ID),
    foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Measurement (
	ID int primary key auto_increment,
    sensor_ID int not null,
    hive_ID int not null,
    measurement_date datetime not null,
    weight double not null,
    spectrogram char(30) not null,
    temperature double not null,
    ambient_temperature double not null,
    humidity double not null,
    ambient_humidity double not null,
    queen_present boolean not null,
    foreign key (sensor_ID) references Sensor(ID),
    foreign key (hive_ID) references Hive(ID)
);

# Inserimenti - Stato Zero
INSERT INTO Beekeeper (email, passwordhash, first_name, last_name, company_name, company_PIVA, subscribed, payment_due, subscr_expiration_date) VALUES
('n.gallotta@gmail.com',SHA2('Password-123',256),'NicolÃ²','Gallotta','The London Bee Company','GBLBC123456789',1,599,'2027-01-14'),
('s.valente@gmail.com',SHA2('Password-123',256),'Sara','Valente','Bee Raw','USBR123456789',1,599,'2027-01-14'),
('f.festa@gmail.com',SHA2('Password-123',256),'Francesco','Festa','Rowse Honey','GBRH123456789',1,599,'2027-01-14'),
('a.depasquale@gmail.com',SHA2('Password-123',256),'Andrea','De Pasquale','Beekeeper\'s Naturals','GBBN123456789',1,599,'2027-01-14');

INSERT INTO Bee (scientific_name, common_name, bee_description, photo) VALUES
('Apis millifera','Honeybee','This is the most common and widely raised species for honey production worldwide. Honeybees are divided into various subspecies.','honeybee.png'),
('Apis cerana','Asian honeybee','Raised in some regions of Asia, they are also employed for honey production.','asian_honeybee.png'),
('Apis dorsata','Giant honeybee','Known for their size and open hives, larger than Apis mellifera. They are primarily found in certain regions of Asia.','giant_honeybee.png'),
('Apis florea','Dwarf honeybee','Among the smallest bee species. Mainly found in Asia and Africa.','dwarf_honeybee.png');

INSERT INTO Hive (nickname, hive_type, creation_date, beekeeper_email, bee_species, hive_health, uncompleted_operations) VALUES
('Tokyo','Langstroth','2027-01-14','n.gallotta@gmail.com','Apis millifera', 2, false),
('Berlino','Warre','2027-01-14','n.gallotta@gmail.com','Apis millifera', 1, false),
('Lisbona','Top-Bar','2027-01-14','n.gallotta@gmail.com','Apis millifera', 1, false),
('Arnia-1','Langstroth','2027-01-14','s.valente@gmail.com','Apis millifera', 1, false),
('Arnia-2','Warre','2027-01-14','s.valente@gmail.com','Apis millifera', 1, false),
('Arnia-3','Top-Bar','2027-01-14','s.valente@gmail.com','Apis millifera', 1, false),
('Aldo','Langstroth','2027-01-14','f.festa@gmail.com','Apis millifera', 1, false),
('Giovanni','Warre','2027-01-14','f.festa@gmail.com','Apis millifera', 1, false),
('Giacomo','Top-Bar','2027-01-14','f.festa@gmail.com','Apis millifera', 1, false),
('Dredge','Langstroth','2027-01-14','a.depasquale@gmail.com','Apis millifera', 1, false),
('Hillbilly','Warre','2027-01-14','a.depasquale@gmail.com','Apis millifera', 1, false),
('Trapper','Top-Bar','2027-01-14','a.depasquale@gmail.com','Apis millifera', 1, false);

INSERT INTO Production (product, weight, notes, registration_date, hive_ID, beekeeper_email) VALUES
('Honey',1,'First harvest of the season.','2027-01-14',1,'n.gallotta@gmail.com'),
('Honey',1.5,'Collected from strong hive.','2027-01-14',4,'s.valente@gmail.com'),
('Honey',2,'Favorable weather.','2027-01-14',7,'f.festa@gmail.com'),
('Honey',2.5,'Bees were happy today.','2027-01-14',10,'a.depasquale@gmail.com');

INSERT INTO Sensor (hive_ID, beekeeper_email) VALUES
(1,'n.gallotta@gmail.com'),
(2,'n.gallotta@gmail.com'),
(3,'n.gallotta@gmail.com'),
(4,'s.valente@gmail.com'),
(5,'s.valente@gmail.com'),
(6,'s.valente@gmail.com'),
(7,'f.festa@gmail.com'),
(8,'f.festa@gmail.com'),
(9,'f.festa@gmail.com'),
(10,'a.depasquale@gmail.com'),
(11,'a.depasquale@gmail.com'),
(12,'a.depasquale@gmail.com');

INSERT INTO Anomaly (anomaly_name, resolved, detection_date, sensor_ID, hive_ID, beekeeper_email) VALUES
('Temperature Out of Range',1,'2027-01-14 09:00:00',1,1,'n.gallotta@gmail.com'),
('Humidity Out of Range',0,'2027-01-16 17:00:00',1,1,'n.gallotta@gmail.com');

INSERT INTO Operation (operation_name, operation_type, operation_status, operation_date, notes, hive_ID, beekeeper_email) VALUES
('Temperature Adjustment','Transfer','Completed','2027-01-15','Adjusted hive temperature by providing additional insulation during cold weather.',1,'n.gallotta@gmail.com');

INSERT INTO Measurement (sensor_ID, hive_ID, measurement_date, weight, spectrogram, temperature, ambient_temperature, humidity, ambient_humidity, queen_present) VALUES
(1,1,'2027-01-14 19:35:00', 40, '2027_01_14_19_35_00_spect.png', 35, 20, 20, 40, 1),
(1,1,'2027-01-14 19:36:00', 39, '2027_01_14_19_35_00_spect.png', 35, 20, 20, 40, 1),
(2,2,'2027-01-14 19:36:00', 75, '2027_01_14_19_36_00_spect.png', 34, 20, 20, 40, 1),
(3,3,'2027-01-14 19:37:00', 110, '2027_01_14_19_37_00_spect.png', 35.5, 20, 20, 40, 1),
(4,4,'2027-01-14 19:35:00', 45, '2027_01_14_19_35_00_spect.png', 35.8, 17, 20, 80, 1),
(5,5,'2027-01-14 19:36:00', 90, '2027_01_14_19_36_00_spect.png', 33.2, 17, 20, 80, 1),
(6,6,'2027-01-14 19:37:00', 125, '2027_01_14_19_37_00_spect.png', 36, 23, 20, 80, 1),
(7,7,'2027-01-14 19:35:00', 50, '2027_01_14_19_35_00_spect.png', 34.7, 24, 20, 50, 1),
(8,8,'2027-01-14 19:36:00', 85, '2027_01_14_19_36_00_spect.png', 35.5, 24, 20, 50, 1),
(9,9,'2027-01-14 19:37:00', 120, '2027_01_14_19_37_00_spect.png', 33.8, 24, 20, 50, 1),
(10,10,'2027-01-14 19:35:00', 55, '2027_01_14_19_35_00_spect.png', 35, 14, 20, 60, 1),
(11,11,'2027-01-15 19:36:00', 95, '2027_01_14_19_36_00_spect.png', 34.2, 14, 20, 60, 1),
(12,12,'2027-01-15 19:37:00', 130, '2027_01_14_19_37_00_spect.png', 35.8, 14, 20, 60, 1);