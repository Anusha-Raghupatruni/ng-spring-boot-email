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
>* Go inside the directory `/frontend/src/main/ng-email`, executes the command:
```sh
npm run start
```
>* By default the Angular SPA is located at: `http://localhost:4200`

### Deployment
The project is currently ready to be deployed into Heroku. Th file `Procfile` is included in the root directory.


## Current Project Status
I had in mind the goal to at least be able to setup the project and a working system end-to-end. And hopefully a smooth / 
seamless experience for the end-user. I also wanted to make a UI that is not just functional but it's usable with 
validations and users feedback.

There certainly a lot more that can be done (especially the backend) to make it more robust, efficient and scalable,
before it can be production ready (please see TODO section below). 

The following sections details what has been done so far:

### Project Set Up
* Frontend and Backend project setup
* Build and Deployment of a running system

### Frontend
* An Angular SPA using Angular Material for a clean looking user interface
* UI designed to help prevent user errors e.g. 
>* Disabling Sender field as soon as one email is filled 
>* Disabling submit button until all fields are valid
>* Use of the Angular Material Chip UI component to render the emails, which I think not just look better than plain 
text, but I feel they are more user friendly e.g. if you have to delete individual email address can just 'click' 
instead of keyboard 'backspacing' (in my opinion!)
* UI form fields validations using Angular Reactive form for immediate feedback
* Angular making HTTP requests to the Spring Boot backend with some 
* Appropriate messages displayed to users for different HTTP status returned from the HTTP requests
* The Angular SPA offers a way for the user to check for server status, so that they can still compose email if the
mail server is down. The user can freely re-check the mail server status and send email if server is available again.

### Backend
* A Spring Boot web application that can make contact to the external mail systems to send email
* A REST API for the Angular SPA to make requests to
* Basic validations for the parameters passed to the REST API using Validations
* Able to connect to two different external email systems with different mail sending requirements
* Basic error handling of the different HTTP status code returned from the external mail systems
* Basic try-and-fail mechanism for making requests to the external mail systems

### Testing
* Some Spring controller test cases
* Basic test cases for creating a new message to send to the two different external mail systems

### Deployment
* Setup and deploy the project into Heroku

## TODO
### Frontend
* Form validation: Missing validations for checking duplicate email entries in the different form fields
(In particular the SendGrid API does not accept duplicate email not just for the same field, but different fields too!)
* Form validation: Size limit on the content of the email message (this will also help reduce the number of errors 
responses back from external mail server i.e. HTTP status 413)
* Connection timeout settings for the HTTP requests and make the number of retries configurable
* Code:
>* Move common values into constants (especially the name of the form fields, error prone to typing!)
>* Move common component e.g. the Spinner Overlay, outside of the Send Email component for reuse
>* Move app component out to main app view and into their own component e.g. the server status toolbar can be made 
into an individual component on its own, and then added to the main app-component
>* Create custom Email Validator instead of Angular's (as the Angular one seems to be very basic!)

### Backend
* More parameters validations on the REST API to match the frontend (the validations on the backend should at the very 
least match those in the frontend). Ideally, I think it should have restrictions (if needed) than the frontend
* At the moment, the failover for connecting to the external email system is very basic. A smarter strategy can be used
to pick which external mail client to attempt first (and the choice can be dynamically change too! Not in the current
 for loop way!)
>* For example, a flag can be used to remember which was most recent system that failed. So when the next user email comes,
picked the other external mail client instead.
>* Potentially the different strategies can be coded into Java implementation classes, to allow for flexibility to 
switch between different strategies as well
* Code: 
>* Investigate using new Spring / Java features to handle client requests asynchronously. E.g. 
http://niels.nu/blog/2016/spring-async-rest.html
>* Setup up logging, this is essential when it comes to debugging or investigating issues. Also can help to collect 
information over time (audit) of which connection to external mail client has failed the most
>* Investigate using queuing to handle the incoming of of send email requests. So that requests are not blocked and 
potentially the requests can be handled in a more efficiently mannered.
>* Allow for configurations of connection timeout or pooling (in a real system, might look into using a more robust 
HTTP Client to submit the REST requests)
>* Make the number of retries to the external system configurable
>* Move constants and application configurations into properties files

### Testing
* Add Angular tests to the frontend (or use other frontend testing technology, e.g. Selenium)
* Unit tests some TypeScript classes / functions (e.g. Mocha / JEST)
* More unit tests for the Java REST API and DTOs
* Integration tests for connections to the external mail system (for the error cases)

### Documentation
The REST API should be documented e.g as YAML files