UPDATE member SET user_name = CONCAT(
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1),
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1),
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1),
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1),
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1),
        SUBSTRING('abcdefghijklmnopqrstuvwxyz', FLOOR(RAND()*26) + 1, 1)
    );
UPDATE member SET emoji = 'MAN_MEDIUM_LIGHT_SKIN_TONE_TECHNOLOGIST';

ALTER TABLE member ADD UNIQUE (user_name);