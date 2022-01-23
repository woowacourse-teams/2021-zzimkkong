ALTER TABLE map CHANGE COLUMN map_image_url thumbnail longtext not null;

SET foreign_key_checks = 0;

TRUNCATE TABLE map;
TRUNCATE TABLE member;
TRUNCATE TABLE preset;
TRUNCATE TABLE reservation;
TRUNCATE TABLE space;

SET foreign_key_checks = 1;