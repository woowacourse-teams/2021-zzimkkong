create table preset
(
    id                            bigint       not null auto_increment primary key,
    name                          varchar(20)  not null,
    available_start_time          time         not null,
    available_end_time            time         not null,
    reservation_time_unit         integer      not null,
    reservation_minimum_time_unit integer      not null,
    reservation_maximum_time_unit integer      not null,
    reservation_enable            bit(1)       not null,
    enabled_day_of_week           varchar(255),
    manager_id                    bigint       not null,
    foreign key (manager_id) references member (id)
);
