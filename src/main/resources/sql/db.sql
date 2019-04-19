--add goods table
CREATE TABLE GOODS
(
  id serial PRIMARY KEY,
  name text NOT NULL,
  price numeric check (price > 0) NOT NULL,
  category text NOT NULL
);

insert into GOODS (name, price, category) values
('Чибата', 50, 'Хлеб'),
('Печенье', 30, 'Сладкое'),
('Булка', 45, 'Хлеб'),
('Кекс', 60, 'Сладкое');

select * from GOODS;
---------------------------------------

--add users table
CREATE TABLE USERS
(
  id serial PRIMARY KEY,
  login text not null unique,
  name text,
  secondname text
);

insert into USERS (login, name, secondname) values
('Bulkaed', 'Андрей', 'Петров')
('Kekser', 'Вова', 'Путин');

select * from USERS;
----------------------------------------

--add orders table
CREATE TABLE ORDERS (
    id serial PRIMARY KEY,
    user_id integer references USERS ON DELETE RESTRICT
);

insert into ORDERS (user_id) values (1);


select * from ORDERS;
------------------------------------
--create items table
CREATE TABLE ITEMS (
    good_id integer REFERENCES GOODS ON DELETE RESTRICT,
    order_id integer REFERENCES ORDERS ON DELETE CASCADE,
    quantity integer check (quantity > 0) NOT NULL,
    PRIMARY KEY (good_id, order_id)
);


insert into ITEMS (good_id, order_id, quantity) VALUES
(1, 1, 2),
(2, 1, 1);

select * from  ITEMS;

-----------------------------
select g.name, i.quantity from GOODS as g JOIN ITEMS as i on i.good_id = g.id join orders as o on o.id = i.order_id where o.id = 1

---------------------------
--add types of orders
create type order_status AS ENUM ('NEW', 'DONE', 'IN_PROGRESS', 'REJECTED', 'WAITING_FOR_CONFIRM');
alter table ORDERS add column status order_status not null default 'NEW';

--------------------------------------------

--add password to users
alter table USERS add column password varchar(100) not null default '';
update USERS set password = 'f6bcd09265d6ca53c14b3ce87d3f177b' where id = 1;
ALTER TABLE USERS ALTER COLUMN password DROP DEFAULT;
----------------------------------

--add roles to users
create type roles AS ENUM ('ADMIN', 'CLIENT', 'GOD', 'TRUSTED_CLIENT');
alter table USERS add column role roles not null default 'CLIENT';
update USERS set role = 'ADMIN' where id = 1;
insert into USERS (login, name, secondname, password) values
('Vovan', 'Вовка', 'Нитуп', 'russia');

-----------------------------------
--add order creation time
alter table ORDERS add column creation_date timestamp not null default current_timestamp;
--------------

--add mail to users
alter table users add column mail varchar(100) unique;
select * from USERS;
update USERS set mail = 'azaz@mail.com' where id = 1;
update USERS set mail = 'loverussia@mail.com' where id = 3;
alter table USERS alter column mail set not null;

--------------------------------------------