# Neo Microservice

`neo-ms` is a library that provides a set of common functionalities for developing microservices in Rust. This library includes utilities for logging, configuration, and error handling, among other features.

## Usage

### Creating a new microservice

To create a new microservice, you can use the `neo-ms` library. First, you need to add the library as a dependency in your `Cargo.toml` file:

```toml
[dependencies]
neo-ms = "0.1.0"
```

Then, you can use the library in your code:

```rust
use neo_ms::config::Config;

#[actix_web::main]
async fn main() {

    // Start the microservice with configuration
    NeoMicroService::run(
        NeoApp {
            name: "my-microservice",
            version: "1.0.0",
            server: ServerConfiguration {
                host: "localhost",
                port: "3010",
            },
        },
        AppState {
            app_name: String::from("Actix Web"),
        },
        |cfg| {
            cfg.route("/hello", web::get().to(|| HttpResponse::Ok().body("Hello, world!")));
        },
    )
    .await;
}
```

## Contributing

Contributions are welcome! Feel free to submit a pull request or open an issue on GitHub.

## License

`neo-ms-auth-api`◊ is distributed under the MIT License. See LICENSE for more information.
