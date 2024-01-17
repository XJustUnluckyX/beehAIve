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
    spectrogram varchar(35) not null,
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

INSERT INTO Hive (nickname, hive_type, creation_date, beekeeper_email, bee_species) VALUES
('Tokyo','Langstroth','2024-01-14','n.gallotta@gmail.com','Apis millifera'),
('Berlino','Warre','2024-01-14','n.gallotta@gmail.com','Apis millifera'),
('Lisbona','Top-Bar','2024-01-14','n.gallotta@gmail.com','Apis millifera'),
('Arnia-1','Langstroth','2024-01-14','s.valente@gmail.com','Apis millifera'),
('Arnia-2','Warre','2024-01-14','s.valente@gmail.com','Apis millifera'),
('Arnia-3','Top-Bar','2024-01-14','s.valente@gmail.com','Apis millifera'),
('Aldo','Langstroth','2024-01-14','f.festa@gmail.com','Apis millifera'),
('Giovanni','Warre','2024-01-14','f.festa@gmail.com','Apis millifera'),
('Giacomo','Top-Bar','2024-01-14','f.festa@gmail.com','Apis millifera'),
('Dredge','Langstroth','2024-01-14','a.depasquale@gmail.com','Apis millifera'),
('Hillbilly','Warre','2024-01-14','a.depasquale@gmail.com','Apis millifera'),
('Trapper','Top-Bar','2024-01-14','a.depasquale@gmail.com','Apis millifera');

INSERT INTO Production (product, weight, notes, registration_date, hive_ID, beekeeper_email) VALUES
('Honey',1,'First harvest of the season.','2024-01-14',1,'n.gallotta@gmail.com'),
('Honey',1.5,'Collected from strong hive.','2024-01-14',4,'s.valente@gmail.com'),
('Honey',2,'Favorable weather.','2024-01-14',7,'f.festa@gmail.com'),
('Honey',2.5,'Bees were happy today.','2024-01-14',10,'a.depasquale@gmail.com');

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

# INSERT INTO Anomaly (anomaly_name, resolved, detection_date, sensor_ID, hive_ID, beekeeper_email) VALUES
# ('Temperature Out of Range',1,'2024-01-15 09:00:00',1,1,'n.gallotta@gmail.com'),
# ('Humidity Out of Range',0,'2024-01-16 17:00:00',1,1,'n.gallotta@gmail.com');

INSERT INTO Operation (operation_name, operation_type, operation_status, operation_date, notes, hive_ID, beekeeper_email) VALUES
('Temperature Adjustment','Transfer','Completed','2024-01-15','Adjusted hive temperature by providing additional insulation during cold weather.',1,'n.gallotta@gmail.com');

INSERT INTO Measurement (sensor_ID, hive_ID, measurement_date, weight, spectrogram, temperature, ambient_temperature, humidity, ambient_humidity, queen_present) VALUES
(1,1,'2024-01-14 12:00:00', 80.32, '2027_01_14_19_35_00_spect.png', 34.3, 23.47, 20.42, 20.67, 1),
(2,2,'2024-01-14 12:00:00', 78.43, '2027_01_14_19_36_00_spect.png', 34.6, 23.71, 20.32, 20.16, 1),
(3,3,'2024-01-14 12:00:00', 81.76, '2027_01_14_19_37_00_spect.png', 34.5, 23.90, 20.65, 20.28, 1),
(4,4,'2024-01-14 12:00:00', 80.09, '2027_01_14_19_35_00_spect.png', 34.8, 23.87, 20.54, 20.09, 1),
(5,5,'2024-01-14 12:00:00', 81.45, '2027_01_14_19_36_00_spect.png', 34.2, 23.22, 20.11, 20.84, 1),
(6,6,'2024-01-14 12:00:00', 79.17, '2027_01_14_19_37_00_spect.png', 34.7, 23.42, 20.76, 20.54, 1),
(7,7,'2024-01-14 12:00:00', 78.39, '2027_01_14_19_35_00_spect.png', 34.7, 23.11, 20.43, 20.21, 1),
(8,8,'2024-01-14 12:00:00', 80.90, '2027_01_14_19_36_00_spect.png', 34.5, 23.04, 20.65, 20.99, 1),
(9,9,'2024-01-14 12:00:00', 81.02, '2027_01_14_19_37_00_spect.png', 34.8, 23.84, 20.42, 20.44, 1),
(10,10,'2024-01-14 12:00:00', 82.38, '2027_01_14_19_35_00_spect.png', 34.0, 23.93, 20.78, 20.87, 1),
(11,11,'2024-01-14 12:00:00', 82.94, '2027_01_14_19_36_00_spect.png', 34.2, 23.33, 20.23, 20.32, 1),
(12,12,'2024-01-14 12:00:00', 80.69, '2027_01_14_19_37_00_spect.png', 35.8, 23.30, 20.12, 20.12, 1);