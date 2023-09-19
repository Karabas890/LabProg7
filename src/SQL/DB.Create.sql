-- Create the sequence
DROP SEQUENCE IF EXISTS marines_id_seq CASCADE;
CREATE SEQUENCE marines_id_seq;

-- Create the tables
DROP TABLE IF EXISTS Coordinates CASCADE;
CREATE TABLE Coordinates
(
    id integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    x  real    NOT NULL,
    y  integer NOT NULL
);
ALTER TABLE Coordinates
    ADD CONSTRAINT unique_x_y UNIQUE (x, y);


DROP TABLE IF EXISTS Chapters CASCADE;
CREATE TABLE Chapters
(
    id            integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    title         varchar(255) NOT NULL,
    parent_legion varchar(255) NOT NULL
);

DROP TABLE IF EXISTS WeaponTypes CASCADE;
CREATE TABLE WeaponTypes
(
    id    integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    title varchar(255) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS Categories CASCADE;
CREATE TABLE Categories
(
    id    integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    title varchar(255) NOT NULL UNIQUE
);

-- Create the table Users
DROP TABLE IF EXISTS Users CASCADE;
CREATE TABLE Users
(
    id       integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    login    varchar(255) NOT NULL,
    password varchar(255) NOT NULL
);

DROP TABLE IF EXISTS Marines CASCADE;
CREATE TABLE Marines
(
    id           integer PRIMARY KEY DEFAULT nextval('marines_id_seq'),
    name         varchar(255) NOT NULL,
    creationDate timestamp    NOT NULL,
    health       real         NOT NULL,
    achievements text,
    idCoordinate integer REFERENCES Coordinates (id),
    idCategory   integer REFERENCES Categories (id),
    idWeaponType integer REFERENCES WeaponTypes (id),
    idChapter    integer REFERENCES Chapters (id),
    idUser       integer REFERENCES Users (id)
);
