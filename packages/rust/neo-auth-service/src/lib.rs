//! # Neo Auth Service
//!
//! `neo-auth-service` is a Rust library designed to facilitate JWT-based authentication. 
//! It provides tools for generating, verifying, and managing JSON Web Tokens (JWTs), 
//! along with utilities for secure password handling.
//!
//! ## Usage Example
//!
//! Here's a basic example of how to use the `TokenService` to generate a JWT:
//!
//! ```rust
//! use neo_auth_service::TokenService;
//!
//! let token_service = TokenService::new("secret_key", "issuer", 3600);
//! let token = token_service.generate("user_id", vec!["admin".to_string()]).unwrap();
//! ```
//!
//! For more details, see the [README](https://github.com/xhermosilla/neocoder/packages/rust/neo-auth-service#readme) 
//! or the full API documentation.

mod claims;
mod token_service;

pub use claims::Claims;
pub use token_service::TokenService;
