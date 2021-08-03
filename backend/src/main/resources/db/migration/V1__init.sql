DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS space;
DROP TABLE IF EXISTS map;
DROP TABLE IF EXISTS member;

create table member
(
    id           bigint      not null auto_increment primary key,
    email        varchar(50) not null,
    password     varchar(20) not null,
    organization varchar(20) not null
);

create table map
(
    id            bigint      not null auto_increment primary key,
    map_drawing   longtext    not null,
    map_image_url longtext    not null,
    name          varchar(20) not null,
    member_id     bigint      not null,
    foreign key (member_id) references member (id)
);

create table space
(
    id                            bigint       not null auto_increment primary key,
    area                          varchar(255) not null,
    color                         varchar(25),
    coordinate                    varchar(255),
    description                   varchar(255),
    map_image                     varchar(255) not null,
    name                          varchar(20)  not null,
    available_end_time            time         not null,
    available_start_time          time         not null,
    disabled_weekdays             varchar(255),
    reservation_enable            bit(1)       not null,
    reservation_maximum_time_unit integer      not null,
    reservation_minimum_time_unit integer      not null,
    reservation_time_unit         integer      not null,
    text_position                 varchar(6),
    map_id                        bigint       not null,
    foreign key (map_id) references map (id)
);

create table reservation
(
    id          bigint       not null auto_increment primary key,
    description varchar(100) not null,
    start_time  DateTime     not null,
    end_time    DateTime     not null,
    password    varchar(20)  not null,
    user_name   varchar(20)  not null,
    space_id    bigint       not null,
    foreign key (space_id) references space (id)
);

insert into member (id, email, password, organization)
values (1, "pobi@woowa.com", "test1234", "woowacourse");

insert into map (id, name, map_drawing, map_image_url, member_id)
values (1, "루터회관", "abcde", "https://d1dgzmdd5f1fx6.cloudfront.net/thumbnails/1.png", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (1, "area", "#FFE3AC", "29, 229", "description", "map_image", "회의실1", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (2, "area", "#FFE3AC", "88, 229", "description", "map_image", "회의실2", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (3, "area", "#FFE3AC", "510, 220", "description", "map_image", "회의실3", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (4, "area", "#FFE3AC", "584, 220", "description", "map_image", "회의실4", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (5, "area", "#FFE3AC", "668, 335", "description", "map_image", "회의실5", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (6, "area", "#CCDFFB", "208, 289", "description", "map_image", "페어룸1", "23:59:59", "00:00", null, true, 1440, 10,
        10, "left", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (7, "area", "#CCDFFB", "208, 318", "description", "map_image", "페어룸2", "23:59:59", "00:00", null, true, 1440, 10,
        10, "left", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (8, "area", "#CCDFFB", "208, 347", "description", "map_image", "페어룸3", "23:59:59", "00:00", null, true, 1440, 10,
        10, "left", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (9, "area", "#CCDFFB", "208, 376", "description", "map_image", "페어룸4", "23:59:59", "00:00", null, true, 1440, 10,
        10, "left", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (10, "area", "#CCDFFB", "208, 404", "description", "map_image", "페어룸5", "23:59:59", "00:00", null, true, 1440,
        10, 10, "left", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (11, "area", "#D8FBCC", "259, 336", "description", "map_image", "트랙방", "23:59:59", "00:00", null, true, 1440, 10,
        10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (12, "area", "#FED7D9", "100, 90", "description", "map_image", "백엔드 강의장", "23:59:59", "00:00", null, true, 1440,
        10, 10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (13, "area", "#FED7D9", "560, 40", "description", "map_image", "프론트엔드 강의장1", "23:59:59", "00:00", null, true,
        1440, 10, 10, "bottom", 1);

insert into space (id, area, color, coordinate, description, map_image, name, available_end_time, available_start_time,
                   disabled_weekdays, reservation_enable, reservation_maximum_time_unit, reservation_minimum_time_unit,
                   reservation_time_unit, text_position, map_id)
values (14, "area", "#FED7D9", "560, 140", "description", "map_image", "프론트엔드 강의장2", "23:59:59", "00:00", null, true,
        1440, 10, 10, "bottom", 1);
