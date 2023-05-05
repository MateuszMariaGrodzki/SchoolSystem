# School system 

## Run instructions

### Requirements

- `Running docker daemon`
- `Gradle` **&ge;7.6**

### How to
* In order to run server you must first create `jar` file:
  * Go to [server](./server) directory
  * Run command:
  ```
  gradle build -x test
  ```
  * this should create jar in `/build/libs` directory
* Go to directory with `docker-compose.yml` file and run command:
```
docker-compose up
```