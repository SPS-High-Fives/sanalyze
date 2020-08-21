# Sanalyze

This is a generated App Engine Standard Java application from the appengine-standard-archetype archetype.

See the [Google App Engine standard environment documentation][ae-docs] for more
detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/


* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven](https://maven.apache.org/download.cgi) (at least 3.5)
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud)

## Setup

`
gcloud init
gcloud auth application-default login
`
- Download your service account credentials as a `json`, and upload it to the shell.
- Set its path in the environment variable `GOOGLE_APPLICATION_CREDENTIALS`.

### Database Setup

- Create a Cloud SQL instance through the Google Cloud interface.
- After creation, start it up by clicking 'Connect using Cloud Shell' in the instance' page.
- After the shell starts up, enter the following SQL commands to setup a database for this project.
`
CREATE DATABASE sanalyze;

USE sanalyze;

CREATE TABLE api_usage(
    id INT(8) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    clientid VARCHAR(50), 
    units INT(4),
    last_called TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_reset TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
`

### JDBC Setup

- Fill in the variables templated in `Constants.java`.
- Build drivers by running `mvn -P jar-with-dependencies clean package -DskipTests`.

### Database Credentials

- If the creds are in a file name named `databasevars.env`, run,

`
source databasevars.env
`

## Maven
### Running locally

    mvn appengine:run

### Deploying

    mvn appengine:deploy

## Testing

    mvn verify

As you add / modify the source code (`src/main/java/...`) it's very useful to add
[unit testing](https://cloud.google.com/appengine/docs/java/tools/localunittesting)
to (`src/main/test/...`).  The following resources are quite useful:

* [Junit4](http://junit.org/junit4/)
* [Mockito](http://mockito.org/)
* [Truth](http://google.github.io/truth/)

## Updating to latest Artifacts

An easy way to keep your projects up to date is to use the maven [Versions plugin][versions-plugin].

    mvn versions:display-plugin-updates
    mvn versions:display-dependency-updates
    mvn versions:use-latest-versions

Note - Be careful when changing `javax.servlet` as App Engine Standard uses 3.1 for Java 8, and 2.5
for Java 7.

Our usual process is to test, update the versions, then test again before committing back.

[plugin]: http://www.mojohaus.org/versions-maven-plugin/
