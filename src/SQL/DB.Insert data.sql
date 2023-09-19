-- Connect to the DB
\c DBMarines;

-- Drop temporary tables if they exist
DROP TABLE IF EXISTS temp_weapon, temp_category;

-- Assume we use the same weapon and category for all marines
-- Get the id of the BOLT_RIFLE from WeaponType
SELECT id INTO TEMPORARY TABLE temp_weapon FROM WeaponType WHERE type = 'BOLT_RIFLE';

-- Get the id of the SCOUT from Categories
SELECT id INTO TEMPORARY TABLE temp_category FROM Categories WHERE category = 'SCOUT';

-- Insert each Marine one by one
DO $$
    DECLARE
        counter INTEGER := 1;
        weapon_id INTEGER := (SELECT id FROM temp_weapon LIMIT 1);
        category_id INTEGER := (SELECT id FROM temp_category LIMIT 1);
        temp_coordinate_id INTEGER;
        temp_chapter_id INTEGER;
        temp_user_id INTEGER;
    BEGIN
        WHILE counter <= 100 LOOP
                -- Insert Coordinates
                INSERT INTO Coordinates (x, y) VALUES (counter * 10.1, counter * 20) RETURNING id INTO temp_coordinate_id;

                -- Insert Chapter
                INSERT INTO Chapters (name, parent_legion) VALUES ('Chapter' || counter, 'ParentLegion' || counter) RETURNING id INTO temp_chapter_id;

                -- Insert User
                INSERT INTO Users (login, password) VALUES ('User' || counter, '7e6a4309ddf6e8866679f61ace4f621b0e3455ebac2e831a60f13cd1') RETURNING id INTO temp_user_id;

                -- Insert Marine
                INSERT INTO Marines (name, creationDate, health, achievements, idCoordinate, idCategory, idWeaponType, idChapter, idUser)
                VALUES ('Marine' || counter, now(), random() * 100 + 50, 'Achievement' || counter, temp_coordinate_id, category_id, weapon_id, temp_chapter_id, temp_user_id);

                counter := counter + 1;
            END LOOP;
    END
$$ LANGUAGE plpgsql;

-- Drop the temporary tables
DROP TABLE temp_weapon, temp_category;

-- Select all data from the tables
SELECT * FROM Marines;
SELECT * FROM Coordinates;
SELECT * FROM Chapters;
SELECT * FROM WeaponType;
SELECT * FROM Categories;
SELECT * FROM Users;