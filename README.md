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