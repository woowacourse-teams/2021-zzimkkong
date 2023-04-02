ALTER TABLE setting ADD COLUMN priority_order integer not null;
UPDATE setting SET priority_order = 1;