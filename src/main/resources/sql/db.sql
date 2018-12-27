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

CREATE TABLE USERS
(
  id serial PRIMARY KEY,
  login text not null,
  name text,
  secondname text
);

insert into USERS (login, name, secondname) values
('Bulkaed', 'Андрей', 'Петров');

select * from USERS;
----------------------------------------

CREATE TABLE ORDERS (
    id serial PRIMARY KEY,
    user_id integer references USERS
);

insert into ORDERS (user_id) values (1);

select * from ORDERS;
------------------------------------

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