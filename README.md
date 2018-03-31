# Angular Spring Boot Email Sending System

This an email sending system that integrates with third party mailing server 
to send emails given some user inputs.

Specifically this project uses Angular for the frontend and Spring Boot for 
the backend integration. 

The aim of the system is to provide a smooth experience to the user.

## Technology Used
* Angular Material - The UI framework used for the design of the SPA
* Angular 5 - The frontend SPA allowing user's to compose and send email
* Spring Boot 2.0 - To make HTTP connections to 3rd party mail system and provide a HTTP REST API for the SPA.
* Java 1.8 - To run Spring Boot
* Maven 3 - Build tools for the Spring Boot based project

## Local Development
### Ease of Development
* Personally I found it often that the frontend and backend projects are built separately, that testing of the 
entire application cannot be completed, which means developer testing might be limited for some.
* I deliberately tried to setup a project that can run the back and front end together, so that the end to end flow of requests can be seen through.

### Pre-requsite
* The current system is able to integrate with:
>* [SendGrid](https://sendgrid.com)
>* [Mailgun](https://www.mailgun.com)

* Upon registrations with these systems, an API key will be provided. Please add these as environment variables.
The name of the environment variables are (see their uses in `/backend/src/main/resources/application.properties`):
>* `GATEWAY_MAILGUN_API_KEY` - Mail Gun API key
>* `GATEWAY_MAILGUN_BASE_URL` - Mail Gun base endpoint URL (which includes a uniques sandbox URL, please see your settings 
in MailGun) e.g https://api.mailgun.net/v3/sandboxxxxxxx.mailgun.org
>* `GATEWAY_SENDGRID_API_KEY` - SendGrid API key
>* `GATEWAY_SENDGRID_BASE_URL` - Send Grid base endpoint URL i.e. https://api.sendgrid.com/v3/mail

### Building
* The project can be build by Maven and it will build each of the modules inside the project:
>* `backend` - The backend module of the project, based on Spring Boot
>* `frontend` - The frontend module of the project, based on Angular (setup with Angular CLI)
* Once [Maven](https://maven.apache.org/) is setup and configured, executes the command:
```sh
mvn clean install
```
* This will build the Angular SPA as a .jar file which is then wrapped into the Spring Boot .jar file
* To run the Spring Boot application, executes the command:
```sh
java -jar backend/target/email-backend-1.0-SNAPSHOT.jar
```
>* By default the Spring Boot web application is located at: `http://localhost:8080`
>* The frontend module build would have implicitly done a Node and NPM install of packages including 
Angular CLI.
>* The Angular CLI development server can be used to view and debug the frontend code.
>>* Go inside the directory `/frontend/src/main/ng-email`, executes the command:
```sh
npm run start
```
>>* By default the Anuglar SPA is located at: `http://localhost:8080`

### Deployment
The project is currently ready to be deployed into Heroku. Th file `Procfile` is included in the root directory.
