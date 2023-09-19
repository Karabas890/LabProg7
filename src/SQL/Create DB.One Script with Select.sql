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
    login    varchar(255) NOT NULL UNIQUE,
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


-- Insert Enum values into WeaponTypes table
INSERT INTO WeaponTypes (title)
VALUES ('BOLT_RIFLE'),
       ('COMBI_FLAMER'),
       ('FLAMER'),
       ('BOLTGUN');

-- Insert Enum values into Categories table
INSERT INTO Categories (title)
VALUES ('SCOUT'),
       ('AGGRESSOR'),
       ('TACTICAL'),
       ('CHAPLAIN'),
       ('APOTHECARY'),
       ('PRIMARIS');


-- Drop temporary tables if they exist
DROP TABLE IF EXISTS temp_weapon, temp_category;

-- Assume we use the same weapon and category for all marines
-- Get the id of the BOLT_RIFLE from WeaponTypes
SELECT id
INTO TEMPORARY TABLE temp_weapon
FROM WeaponTypes
WHERE title = 'BOLT_RIFLE';

-- Get the id of the SCOUT from Categories
SELECT id
INTO TEMPORARY TABLE temp_category
FROM Categories
WHERE title = 'SCOUT';

-- Insert each Marine one by one
DO
$$
    DECLARE
        counter            INTEGER := 1;
        weapon_id          INTEGER := (SELECT id
                                       FROM temp_weapon
                                       LIMIT 1);
        category_id        INTEGER := (SELECT id
                                       FROM temp_category
                                       LIMIT 1);
        temp_coordinate_id INTEGER;
        temp_chapter_id    INTEGER;
        temp_user_id       INTEGER;
    BEGIN
        WHILE counter <= 100
            LOOP
                -- Insert Coordinates
                INSERT INTO Coordinates (x, y)
                VALUES (counter * 10.1, counter * 20)
                RETURNING id INTO temp_coordinate_id;

                -- Check if the inserted id is not 0
                IF temp_coordinate_id != 0 THEN
                    -- Insert Chapter
                    INSERT INTO Chapters (title, parent_legion)
                    VALUES ('Chapter' || counter, 'ParentLegion' || counter)
                    RETURNING id INTO temp_chapter_id;

                    -- Insert User
                    INSERT INTO Users (login, password)
                    VALUES ('User' || counter, '7e6a4309ddf6e8866679f61ace4f621b0e3455ebac2e831a60f13cd1')
                    RETURNING id INTO temp_user_id;

                    -- Check if the inserted id is not 0
                    IF temp_chapter_id != 0 AND temp_user_id != 0 THEN
                        -- Insert Marine
                        INSERT INTO Marines (name, creationDate, health, achievements, idCoordinate, idCategory,
                                             idWeaponType, idChapter, idUser)
                        VALUES ('Marine' || counter, now(), random() * 100 + 50, 'Achievement' || counter,
                                temp_coordinate_id, category_id, weapon_id, temp_chapter_id, temp_user_id);
                    END IF;
                END IF;

                counter := counter + 1;
            END LOOP;
    END
$$ LANGUAGE plpgsql;

-- Drop the temporary tables
DROP TABLE temp_weapon, temp_category;

-- Select all data from the tables
SELECT *
FROM Marines;
SELECT *
FROM Coordinates;
SELECT *
FROM Chapters;
SELECT *
FROM WeaponTypes;
SELECT *
FROM Categories;
SELECT *
FROM Users;
