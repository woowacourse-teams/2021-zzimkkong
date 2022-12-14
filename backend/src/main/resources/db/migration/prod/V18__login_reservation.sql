-- member table
ALTER TABLE member ADD COLUMN user_name VARCHAR(20) NOT NULL;
ALTER TABLE member ADD COLUMN emoji VARCHAR(255) NOT NULL;

-- reservation table
ALTER TABLE reservation MODIFY COLUMN password VARCHAR(4);
ALTER TABLE reservation ADD COLUMN member_id BIGINT;
ALTER TABLE reservation ADD CONSTRAINT fk_reservation_member FOREIGN KEY (member_id) REFERENCES member (id);

-- indexing
CREATE INDEX i_memberid_date on reservation(member_id, date);