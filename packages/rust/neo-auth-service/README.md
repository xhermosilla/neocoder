# Neo Auth Service

`neo-auth-service` is a Rust library designed to facilitate JWT-based authentication. It provides essential tools for generating, verifying, and managing JSON Web Tokens (JWTs), as well as utilities for secure password handling.

## Installation

To use `neo-auth-service` in your project, add the dependency to your `Cargo.toml` file:

```toml
[dependencies]
neo-auth-service = "0.1.0"
```

## How to use

For detailed usage instructions and API documentation, please refer to the Rust Docs.

### TokenService

The `TokenService` struct provides methods for generating and verifying JWTs. To create a new `TokenService`, you need to provide a secret key, issuer, and token expiration time in seconds.

```rust
use neo_auth_service::TokenService;

let token_service = TokenService::new("secret_key", "issuer", 3600);

let token = token_service.generate("user_id", vec!["admin".to_string()]).unwrap();
```

## Running Tests

To ensure everything is working correctly, you can run the provided tests:

```bash
cargo test
```

## Running coverage

You need to have the _cargo-llvm-cov_ library installed:

```bash
cargo install cargo-llvm-cov
```

You can run coverage using:

```bash
cargo llvm-cov
```

```bash
cargo llvm-cov --open
```

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue on GitHub.

## License

_neo-auth-service_ is distributed under the MIT License. See LICENSE for more information.

