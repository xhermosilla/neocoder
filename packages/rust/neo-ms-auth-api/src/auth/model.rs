use serde::{Serialize, Deserialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LoginRequest {
    // Password.
    pub password: String,
    // Username.
    pub username: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct TokenResponse {
    // Expiration time for the token.
    pub expires: i64,
    // Token for authentication.
    pub token: String,
    // Token type.
    pub token_type: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ValidateTokenResponse {
    // Expiration time for the token.
    pub expires: i64,
    // Expired time formatted.
    pub expires_formatted: String,
    // Is the token valid?
    pub valid: bool,
}

