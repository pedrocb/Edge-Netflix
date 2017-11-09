CREATE TABLE Files (
  Name VARCHAR(50),
  ID serial primary key,
  -- In Bytes
  Size INT
);

CREATE TABLE Seeders (
  FileId INT,
  Address VARCHAR(50),
  Port INT,
  Bitrate INT,
  ID serial primary key
);

CREATE TABLE Keywords (
  FileId INT,
  Keyword VARCHAR(30)
);

INSERT INTO Files (Name, Size)
VALUES ('CC_1916_07_10_TheVagabond.mp4', 150003982),
       ('night_of_the_living_dead_512kb.mp4', 415473786),
       ('PopeyeAliBaba_512kb.mp4', 73918195),
       ('tl_512kb.mp4', 28211605);

INSERT INTO Keywords (FileId, Keyword)
VALUES (1, 'mp4'),
 (1, 'vagabond'),
 (2, 'mp4'),
 (2, 'night'),
 (2, 'horror'),
 (3, 'mp4'),
 (3, 'popeye'),
 (4, 'mp4'),
 (4, 'tl');
