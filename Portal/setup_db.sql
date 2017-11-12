CREATE TABLE Files (
  Name VARCHAR(50),
  ID serial primary key,
  -- In Bytes
  Size INT,
  -- In Bytes
  ChunkSize INT
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

INSERT INTO Files (Name, Size, ChunkSize)
VALUES ('CC_1916_07_10_TheVagabond.mp4', 150003982, 1024*1024),
       ('night_of_the_living_dead_512kb.mp4', 415473786, 1024*1024),
       ('PopeyeAliBaba_512kb.mp4', 73918195, 1024*1024),
       ('tl_512kb.mp4', 28211605, 1024*1024);

INSERT INTO Keywords (FileId, Keyword)
VALUES (1, 'mp4'),
 (1, 'vagabond'),
 (1, 'comedy'),
 (2, 'mp4'),
 (2, 'night'),
 (2, 'horror'),
 (2, 'sci-fi'),
 (3, 'mp4'),
 (3, 'popeye'),
 (3, 'comedy'),
 (3, 'animation'),
 (4, 'comedy'),
 (4, 'animation'),
 (4, 'mp4'),
 (4, 'tl');
