insert into mpa (id, name) values (1, 'G') /*where not exists (select * from mpa where id=1and name='G')*/;
insert into mpa (id, name) values (2, 'PG');
insert into mpa (id, name) values (3, 'PG-13');
insert into mpa (id, name) values (3, 'R');
insert into mpa (id, name) values (4, 'NC-17');

insert into genre (id, name) values (1, 'Комедия');
insert into genre (id, name) values (2, 'Драма');
insert into genre (id, name) values (3, 'Мультфильм');
insert into genre (id, name) values (4, 'Триллер');
insert into genre (id, name) values (5, 'Документальный');
insert into genre (id, name) values (6, 'Боевик');