alter table member drop index email;
create index i_email on member (email);
alter table member modify column email varchar(50) not null unique;

alter table reservation drop index date;
create index i_spaceid_date on reservation (space_id, date);
