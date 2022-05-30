-- CREATE Setting Table
create table setting
(
    id                            bigint auto_increment primary key,
    space_id                      bigint not null,
    enabled_day_of_week           varchar(255) null,
    setting_start_time            time   not null,
    setting_end_time              time   not null,
    reservation_maximum_time_unit int    not null,
    reservation_minimum_time_unit int    not null,
    reservation_time_unit         int    not null,

    foreign key (space_id) references space (id)
);

create index space_id on setting (space_id);

-- CHANGE Columns
ALTER TABLE preset CHANGE available_start_time setting_start_time time not null;
ALTER TABLE preset CHANGE available_end_time setting_end_time time not null;

-- Data Migration
INSERT INTO
    setting (space_id,
             enabled_day_of_week,
             setting_start_time,
             setting_end_time,
             reservation_time_unit,
             reservation_minimum_time_unit,
             reservation_maximum_time_unit)
SELECT id,
       enabled_day_of_week,
       available_start_time,
       available_end_time,
       reservation_time_unit,
       reservation_minimum_time_unit,
       reservation_maximum_time_unit
FROM space;

-- DROP Columns
ALTER TABLE space DROP COLUMN enabled_day_of_week;
ALTER TABLE space DROP COLUMN reservation_time_unit;
ALTER TABLE space DROP COLUMN reservation_minimum_time_unit;
ALTER TABLE space DROP COLUMN reservation_maximum_time_unit;
