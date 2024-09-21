# Neo Microservice Auth Api

`neo-ms-auth-api` is a RESTful service developed in Rust that provides JWT-based authentication functionalities. This service acts as an API for managing user authentication, leveraging the neo-auth-service crate to handle tokens and secure authentication.

## Features

- JWT-based Authentication: Generates and verifies JWT tokens for secure authentication.

## Api documentation

The API definition is available in the `swagger.yaml` file located in the root of the project. You can use Swagger Editor to visualize the API definition.

## Installation

To download the project, clone the repository from GitHub:

```bash
git clone https://github.com/xhermosilla/neocoder.gitp
cd neocoder/packages/rust/neo-ms-auth-api
```

## Usage

### Prerequisites

Make sure you have Rust installed. 

> If you don’t have it yet, you can install it from [here](https://www.rust-lang.org/tools/install).

### Configuration

The service requires some basic configurations, such as the secret key for tokens and other environment parameters. You can set these configurations in a .env file located at the root of the project:

| Variable            | Description                                     | Default      |
| :------------------ | :---------------------------------------------- | :----------- |
| APP_API_BASE_URL    | API base URL                                    | "neo/api/v1" |
| APP_AUTH_EXPIRATION | Contains the expiration time for the JWT tokens | 86400        |
| APP_AUTH_ISSUER     | Contains the issuer for the JWT tokens          | "neo"        |
| APP_AUTH_SECRET_KEY | Contains the secret key for the JWT tokens      | -            |
| APP_LOG_LEVEL       | Log level                                       | "INFO"       |
| APP_SERVER_HOST     | Server host                                     | "localhost"  |
| APP_SERVER_PORT     | Server port                                     | 3010         |

(The variables that not have a default value are required to be set)

For local development, you can create a `.env` file in the root of the project decrypting the `.env.enc` file with the following command:

```pre
sops -d .env.enc > .env
```

> If you don't have the `sops` installed, you can install it from [here](https://github.com/getsops/sops)

> You need to have the `sops` key to decrypt the file exported as SOPS_AGE_KEY environment variable. The key is stored in the `SOPS_AGE_KEY` secret in the repository. If you do not have access, request it from the person responsible for the project.

### Building the service

To build the service, you can use the following command:

```bash
cargo build
```

This command will compile the project and generate the necessary files in the `target` directory.

If you want to build the project for production, you can use the following command:

```bash
cargo build --release
```

This command will optimize the build for production, generating the necessary files in the `target/release` directory.

### Running the service

To start the service in development mode, you can use the following command:

```bash
cargo run
```

You can also run the service in watch mode, which will automatically restart the server when you make changes to the code:

```bash
cargo run-script watch
```

This command will start the API server, and it will listen for incoming requests. The service load the configurations from the `.env` file. 

You can access the API at `http://localhost:3010`.

### Testing the Rest Api service

To verify that everything is working correctly, you can run the provided tests:

```bash
cargo test
```

### Running coverage

You need to have the cargo-llvm-cov library installed:

```bash
cargo install cargo-llvm-cov
```

You can run coverage using:

```bash
cargo llvm-cov # or cargo llvm-cov --summary-only
```

You can also generate a report in HTML format:

```bash
cargo llvm-cov --open
```

## Using docker to run the service

You can use Docker to run the service. To build the Docker image, you can use the following command:

```bash
docker build -t xhermosilla/neo-ms-auth-api .
```

This command will build the Docker image with the name `xhermosilla/neo-ms-auth-api`. You can run the Docker container with the following command:

```bash
docker run -p 3010:3010 --env-file .env xhermosilla/neo-ms-auth-api
```

This command will start the Docker container and expose the service on port 3010. The service will load the configurations from the `.env` file.

## Contributing

Contributions are welcome! Feel free to submit a pull request or open an issue on GitHub.

## License

`neo-ms-auth-api`◊ is distributed under the MIT License. See LICENSE for more information.