# Neo Microservice Auth Api

`neo-ms-auth-api` is a RESTful service developed in Java that provides JWT-based authentication functionalities. This service acts as an API for managing user authentication, leveraging the `neo-auth-service` library to handle tokens and ensure secure authentication.

## Features

- JWT-based Authentication: Generates and verifies JWT tokens for secure authentication.

## Api documentation

The API definition is available in the `swagger.yaml` file located in the root of the project. You can use Swagger Editor to visualize the API definition.

## Installation

To download the project, clone the repository from GitHub:

```bash
git clone https://github.com/xhermosilla/neocoder.git
cd neocoder/packages/java/neo-ms-auth-api
```

## Usage

### Prerequisites

Make sure you have Java installed. 

> If you don’t have it yet, you can install it from [here](https://www.oracle.com/es/java/technologies/downloads/).

### Configuration

The service requires some basic configurations, such as the secret key for tokens and other environment parameters. You can set these settings on the `application.properties` file located at the src/main/resources folder:

- #### Auth service configuration
auth.token.secretKey= -
auth.token.issuer=neo
auth.token.expiration=86400000

- #### Login configuration
auth.login.defaultUser=admin
auth.login.defaultPassword=admin

- #### Server configuration
server.port=3010
server.servlet.context-path=/neo/api/v1
spring.application.name=neo-ms-auth-api

(The variables that not have a default value are required to be set)

### Building the service

To build the service, you can use the following command:

```bash
./gradlew build
```

This command will compile the project and generate the necessary files in the `target` directory.

If you want to build the project for production, you can use the following command:

```bash
./gradlew assembleRelease
```

### Running the service

To start the service in development mode, you can use the following command:

```bash
./gradlew bootRun
```

This command will start the API server, and it will listen for incoming requests. The service load the configurations from the `application.properties` file. 

You can access the API at `http://localhost:3010`.

### Testing the Rest Api service

Test will be available soon.

### Running coverage

Coverage will be available soon.

## Contributing

Contributions are welcome! Feel free to submit a pull request or open an issue on GitHub.

## License

`neo-ms-auth-api`◊ is distributed under the MIT License. See LICENSE for more information.