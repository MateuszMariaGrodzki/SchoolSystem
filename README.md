# School system 

## Run instructions

### Requirements

- `Running docker daemon`
- `Java` **&ge;17**
- `Gradle` **&ge;7.6**

### How to run
* In order to run server you must first create `jar` file:
  * Go to [server](./server) directory
  * Run command:
  ```
  gradle build -x test
  ```
  * this should create jar in `/build/libs` directory
* Go to directory with `docker-compose.yml` file and run command:
```
docker-compose up --build
```

### How to use api after start
In order to use api you need to create administrator account by executing on database following two insert queries:
``` 
INSERT INTO application_user(first_name, last_name, phone_number, email, password, role)
VALUES ('Admin', 'Admin', '000000000' , 'Admin@admin.pl', '$2a$10$vvUHLe0gPvQfa1gDunO1WuRCAovr5HT34ebr1F79ZUBBkymt6Ztxi', 'ADMIN');

INSERT INTO administrator(application_user_id) VALUES (1);
```
This will create new administrator account with credentials:
```
email: Admin@admin.pl
password: Avocado1!
```

There are 3 different simple ways to execute queries on database:

* Create new flyway migration: 
  * Go to `src/main/resources/db/migration` folder 
  * Create new .sql file matching defined flyway migration naming convention (which is defined in `README` file in `server` directory)
  * Copy insert queries into file
  * Restart project by running `docker-compose up --build` command
* Use `dbeaver` or another database tool
  * Open your database tool 
  * Connect to database (example is in `README` in `server` directory)
  * copy and execute insert queries
* Go inside database container and execute inserts
  * First execute command `docker ps` and find `CONTAINER ID` associated with postgres container
  * Next insert your `CONTAINER ID` to command `docker exec -it CONTAINERID psql -U schoolsystem -W schoolsystem` and execute it
  * You will be asked for password for user `schoolsystem` (password is the same as user)
  * You are now connected to database `schoolsystem` as user `schoolsystem`
  * From here you can execute insert statements from command line (don't forget of semicolon at the end of statement)
  * To exit type and execute command `\q`

After adding administrator you can log in:
```
Method: POST
Endpoint: /api/v1/token
Content-type : application/json
Body: {
"email": "Admin@admin.pl,
"password": "Avocado1!
}
```
successful log in results with code `200` and header `AUTHORIZATION` with jwt-token. 

### Api documentation: 
We use swagger to provide api documentation. 
After application start documentation is available for everyone on endpoint:
```
http://localhost:8080/api/swagger-ui/index.html
```