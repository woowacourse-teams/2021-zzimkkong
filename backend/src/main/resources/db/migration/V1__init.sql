DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS space;
DROP TABLE IF EXISTS map;
DROP TABLE IF EXISTS member;

create table member (
    id bigint not null auto_increment primary key,
    email varchar(50) not null,
    password varchar(20) not null,
    organization varchar(20) not null
);

create table map (
    id bigint not null auto_increment primary key,
    map_drawing longtext not null,
    map_image_url longtext not null,
    name varchar(20) not null,
    member_id bigint not null,
    foreign key (member_id) references member (id)
);

create table space (
    id bigint not null auto_increment primary key,
    area varchar(255) not null,
    color varchar(25),
    coordinate varchar(255),
    description varchar(255),
    map_image varchar(255) not null,
    name varchar(20) not null,
    available_end_time time not null,
    available_start_time time not null,
    disabled_weekdays varchar(255),
    reservation_enable bit(1) not null,
    reservation_maximum_time_unit integer not null,
    reservation_minimum_time_unit integer not null,
    reservation_time_unit integer not null,
    text_position varchar(6),
    map_id bigint not null,
    foreign key (map_id) references map (id)
);

create table reservation (
    id bigint not null auto_increment primary key,
    description varchar(100) not null,
    start_time DateTime not null,
    end_time DateTime not null,
    password varchar(20) not null,
    user_name varchar(20) not null,
    space_id bigint not null,
    foreign key (space_id) references space (id)
);
