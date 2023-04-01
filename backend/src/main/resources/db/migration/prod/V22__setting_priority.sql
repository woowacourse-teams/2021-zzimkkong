ALTER TABLE setting ADD COLUMN priority integer not null;
UPDATE setting SET priority = 1;