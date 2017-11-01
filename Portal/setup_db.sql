CREATE TABLE Files (
  Name VARCHAR(50),
  ID serial primary key
);

CREATE TABLE Seeders (
  FileID INT,
  Address VARCHAR(50),
  Port INT,
  Bitrate INT,
  ID serial primary key
);

INSERT INTO Files (Name)
VALUES ('CC_1916_07_10_TheVagabond.mp4'),
       ('night_of_the_living_dead_512kb.mp4'),
       ('PopeyeAliBaba_512kb.mp4'),
       ('tl_512kb.mp4');

