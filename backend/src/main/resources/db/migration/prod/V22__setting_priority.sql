ALTER TABLE setting ADD COLUMN order integer not null;
UPDATE setting SET order = 1;