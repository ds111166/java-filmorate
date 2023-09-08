insert into mpa (id, "name") select * from (
select 1, 'G' union
select 2, 'PG' union
select 3, 'PG-13' union
select 4, 'R' union
select 5, 'NC-17')
x where not exists(select * from mpa);

insert into genres (id, "name") select * from (
select 1, 'Комедия' union
select 2, 'Драма' union
select 3, 'Мультфильм' union
select 4, 'Триллер' union
select 5, 'Документальный' union
select 6, 'Боевик')
x where not exists(select * from genres);