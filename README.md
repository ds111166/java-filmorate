# java-filmorate


Template repository for Filmorate project.
1. add <artifactId>spring-boot-starter-jdbc</artifactId>
2. add <artifactId>spring-boot-starter-test</artifactId>
3. add <artifactId>spring-boot-starter-data-jpa</artifactId>
4. add <artifactId>h2</artifactId> <version>2.2.220</version> - нет пометки об уязвимости
5. Сконфигурировал базу данных для рабочего режима с помощью файла настроек application.properties
6. добавил скрипты создания БД в shema.sql, сверил структуру с классами
7. добавил скрипты заполнения справочников жанров и рейтигов а data.sql
8. создал UserDbStorage, FilmDbStorage, LikeDbStorage, FriendDbStorage (# lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier)
9. add in lombok.config "lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier" для автоматического копирования анотации в конструктор
10. доделан UserDbStorage
11. сделан интерфейс FilmGenreStorage
12. сделан FilmGenreDbStorage(возможны изменения)
13. сделан FilmDbStorage
14. сделаны эндпоинт для получение жанров GET /genre, GET /genres/{id}  - контроллер, сервер, стораге
15. сделаны эндпоинт для получение рейтингов GET /mpa, GET /mpa/{id} - контроллер, сервер, стораге
16. сделано FriendDbStorage
17. сделано LikeDbStorage
18. написан интеграционный тест для MpaDbStorage 
19. написан интеграционный тест для FilmDbStorage
20. написан интеграционный тест для FilmGenreDbStorage
21. написан интеграционный тест для FriendDbStorage
22. написан интеграционный тест для GenreDbStorage
23. написан интеграционный тест для LikeDbStorage
24. написан интеграционный тест для UserDbStorage
25. заменил  DataIntegrityViolationException на NotFoundException путем предварительного вызова getById соответстьвующих storage!!!! в storage and tets!!!!
26. сделано ограничение на поле direct дружбы direction int  not null check (((direction = 0) or (direction = '-1'::integer) or (direction = 1)))
27. исправлено InMemoryFriendStorage
28. добавил ЛОГИРОВАНИЕ
29. ВЫПОЛНИТ тесты POSTMAN
