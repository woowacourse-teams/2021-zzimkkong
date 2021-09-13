ALTER TABLE reservation ADD COLUMN date date;
UPDATE reservation SET date = date(start_time);
ALTER TABLE reservation MODIFY COLUMN date date NOT NULL;
