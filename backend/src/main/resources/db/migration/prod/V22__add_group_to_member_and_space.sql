-- 1. Member 테이블에 group_name 추가
ALTER TABLE member ADD COLUMN group_name VARCHAR(10) NOT NULL DEFAULT 'NONE';

-- 2. AllowedGroup 테이블 생성
CREATE TABLE allowed_group (
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    space_id   BIGINT      NOT NULL,
    group_name VARCHAR(10) NOT NULL,
    CONSTRAINT fk_allowed_group_space
        FOREIGN KEY (space_id) REFERENCES space (id) ON DELETE CASCADE
);

-- 3. 인덱스 생성
CREATE INDEX idx_allowed_group_space_id ON allowed_group(space_id);
CREATE INDEX idx_member_group_name ON member(group_name);
