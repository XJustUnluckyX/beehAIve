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
    company_PIVA varchar(100),
    isSubscribed boolean not null,
    subscr_expiration_date datetime
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
    creation_date datetime not null,
    beekeeper_email varchar(50) not null,
    bee_species varchar(50) not null,
    foreign key (beekeeper_email) references Beekeeper(email),
    foreign key (bee_species) references Bee(scientific_name) 
);

CREATE TABLE Production (
	ID int primary key auto_increment,
    product varchar(50) not null,
    weight double not null,
    notes varchar(300) not null,
    registration_date datetime not null,
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (hive_ID) references Hive(ID),
    foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Sensor (
	ID int primary key auto_increment,
    hive_ID int not null,
	foreign key (hive_ID) references Hive(ID)
);

CREATE TABLE Anomaly (
	ID int primary key auto_increment,
    anomaly_name varchar(100) not null,
    isResolved boolean not null,
    detection_date datetime not null,
    sensor_ID int not null,
    hive_ID int not null,
    foreign key (sensor_ID) references Sensor(ID),
    foreign key (hive_ID) references Hive(ID)
);

CREATE TABLE Operation (
	ID int primary key auto_increment,
    operation_name varchar(70) not null,
    operation_type varchar(50) not null,
    operation_status varchar(30) not null,
    notes varchar(300),
    hive_ID int not null,
    beekeeper_email varchar(50) not null,
    foreign key (hive_ID) references Hive(ID),
    foreign key (beekeeper_email) references Beekeeper(email)
);

CREATE TABLE Measurement (
	sensor_ID int not null auto_increment,
    measurement_date datetime not null,
    weight double not null,
    spectrogram char(30) not null,
    temperature double not null,
    ambient_temperature double not null,
    humidity double not null,
    ambient_humidity double not null,
    isQueenPresent boolean not null,
    primary key (sensor_ID, measurement_date),
    foreign key (sensor_ID) references Sensor(ID)
);

# Inserimenti - Stato Zero
INSERT INTO Beekeeper (email, passwordhash, first_name, last_name, company_name, company_PIVA, isSubscribed, subscr_expiration_date) VALUES
('n.gallotta@gmail.com',SHA2('Password-123',256),'Nicolò','Gallotta','The London Bee Company','GB123456789',1,'2027-01-14 19:30:00'),
('s.valente@gmail.com',SHA2('Password-123',256),'Sara','Valente','The London Bee Company','GB123456789',1,'2027-01-14 19:30:00'),
('f.festa@gmail.com',SHA2('Password-123',256),'Francesco','Festa','The London Bee Company','GB123456789',1,'2027-01-14 19:30:00'),
('a.depasquale@gmail.com',SHA2('Password-123',256),'Andrea','De Pasquale','The London Bee Company','GB123456789',1,'2027-01-14 19:30:00');

INSERT INTO Bee (scientific_name, common_name, bee_description, photo) VALUES
('Apis millifera','Honeybee','This is the most common and widely raised species for honey production worldwide. Honeybees are divided into various subspecies.','honeybee.png'),
('Apis cerana','Asian honeybee','Raised in some regions of Asia, they are also employed for honey production.','asian_honeybee.png'),
('Apis dorsata','Giant honeybee','Known for their size and open hives, larger than Apis mellifera. They are primarily found in certain regions of Asia.','giant_honeybee.png'),
('Apis florea','Dwarf honeybee','Among the smallest bee species. Mainly found in Asia and Africa.','dwarf_honeybee.png');

INSERT INTO Hive (nickname, hive_type, creation_date, beekeeper_email, bee_species) VALUES
('Tokyo','Langstroth','2027-01-14 19:35:00','n.gallotta@gmail.com','Apis millifera'),
('Berlino','Warre','2027-01-14 19:36:00','n.gallotta@gmail.com','Apis millifera'),
('Lisbona','Top-Bar','2027-01-14 19:37:00','n.gallotta@gmail.com','Apis millifera'),
('Arnia-1','Langstroth','2027-01-14 19:35:00','s.valente@gmail.com','Apis millifera'),
('Arnia-2','Warre','2027-01-14 19:36:00','s.valente@gmail.com','Apis millifera'),
('Arnia-3','Top-Bar','2027-01-14 19:37:00','s.valente@gmail.com','Apis millifera'),
('Aldo','Langstroth','2027-01-14 19:35:00','f.festa@gmail.com','Apis millifera'),
('Giovanni','Warre','2027-01-14 19:36:00','f.festa@gmail.com','Apis millifera'),
('Giacomo','Top-Bar','2027-01-14 19:37:00','f.festa@gmail.com','Apis millifera'),
('Dredge','Langstroth','2027-01-14 19:35:00','a.depasquale@gmail.com','Apis millifera'),
('Hillbilly','Warre','2027-01-14 19:36:00','a.depasquale@gmail.com','Apis millifera'),
('Trapper','Top-Bar','2027-01-14 19:37:00','a.depasquale@gmail.com','Apis millifera');

INSERT INTO Production (product, weight, notes, registration_date, hive_ID, beekeeper_email) VALUES
('Honey',1,'First harvest of the season.','2027-01-14 20:00:00',1,'n.gallotta@gmail.com'),
('Honey',1.5,'Collected from strong hive.','2027-01-14 20:31:00',4,'s.valente@gmail.com'),
('Honey',2,'Favorable weather.','2027-01-14 20:58:00',7,'f.festa@gmail.com'),
('Honey',2.5,'Bees were happy today.','2027-01-14 20:22:00',10,'a.depasquale@gmail.com');

INSERT INTO Sensor (hive_ID) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10),
(11),
(12);

INSERT INTO Anomaly (anomaly_name, isResolved, detection_date, sensor_ID, hive_ID) VALUES
('Temperature Out of Range',1,'2027-01-14 19:35:30',1,1);

INSERT INTO Operation (operation_name, operation_type, operation_status, notes, hive_ID, beekeeper_email) VALUES
('Temperature Adjustment','Transfer','Completed','Adjusted hive temperature by providing additional insulation during cold weather.',1,'n.gallotta@gmail.com');

INSERT INTO Measurement (measurement_date, weight, spectrogram, temperature, ambient_temperature, humidity, ambient_humidity, isQueenPresent) VALUES
('2027-01-14 19:35:00', 40, '2027_01_14_19_35_00_spect.png', 35, 20, 20, 40, 1),
('2027-01-14 19:36:00', 75, '2027_01_14_19_36_00_spect.png', 34, 20, 20, 40, 1),
('2027-01-14 19:37:00', 110, '2027_01_14_19_37_00_spect.png', 35.5, 20, 20, 40, 1),
('2027-01-14 19:35:00', 45, '2027_01_14_19_35_00_spect.png', 35.8, 17, 20, 80, 1),
('2027-01-14 19:36:00', 90, '2027_01_14_19_36_00_spect.png', 33.2, 17, 20, 80, 1),
('2027-01-14 19:37:00', 125, '2027_01_14_19_37_00_spect.png', 36, 23, 20, 80, 1),
('2027-01-14 19:35:00', 50, '2027_01_14_19_35_00_spect.png', 34.7, 24, 20, 50, 1),
('2027-01-14 19:36:00', 85, '2027_01_14_19_36_00_spect.png', 35.5, 24, 20, 50, 1),
('2027-01-14 19:37:00', 120, '2027_01_14_19_37_00_spect.png', 33.8, 24, 20, 50, 1),
('2027-01-14 19:35:00', 55, '2027_01_14_19_35_00_spect.png', 35, 14, 20, 60, 1),
('2027-01-14 19:36:00', 95, '2027_01_14_19_36_00_spect.png', 34.2, 14, 20, 60, 1),
('2027-01-14 19:37:00', 130, '2027_01_14_19_37_00_spect.png', 35.8, 14, 20, 60, 1);