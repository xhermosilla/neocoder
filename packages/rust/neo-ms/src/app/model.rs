use actix_web::{error::ResponseError, http::StatusCode, HttpResponse};

use derive_more::Display;
use neo_auth_service::Claims;
use serde::Serialize;

#[derive(Serialize)]
pub struct ErrorResponse {
    pub code: u16,
    pub error: String,
    pub message: String,
}

#[derive(Debug, Display)]
pub enum CustomError {
    #[display("Bad request")]
    BadRequest,
    #[display("{_0}")]
    InternalError(String),
    #[display("Resource not found")]
    NotFound,
    #[display("{_0}")]
    Unauthorized(String),
    #[display("Invalid credentials")]
    UnauthorizedDefault,
    #[display("Unknown Internal Error")]
    Unknown,
}

impl CustomError {
    pub fn name(&self) -> String {
        match self {
            Self::BadRequest => "BadRequest".to_string(),
            Self::InternalError(_) => "InternalError".to_string(),
            Self::NotFound => "NotFound".to_string(),
            Self::Unauthorized(_) => "Unauthorized".to_string(),
            Self::UnauthorizedDefault => "Unauthorized".to_string(),
            Self::Unknown => "Unknown".to_string(),
        }
    }
}

impl ResponseError for CustomError {
    fn status_code(&self) -> StatusCode {
        match *self {
            Self::BadRequest => StatusCode::BAD_REQUEST,
            Self::InternalError(_) => StatusCode::INTERNAL_SERVER_ERROR,
            Self::NotFound => StatusCode::NOT_FOUND,
            Self::Unauthorized(_) => StatusCode::UNAUTHORIZED,
            Self::UnauthorizedDefault => StatusCode::UNAUTHORIZED,
            Self::Unknown => StatusCode::INTERNAL_SERVER_ERROR,
        }
    }

    fn error_response(&self) -> HttpResponse {
        let status_code = self.status_code();
        let error_response = ErrorResponse {
            code: status_code.as_u16(),
            message: self.to_string(),
            error: self.name(),
        };
        HttpResponse::build(status_code).json(error_response)
    }
}

#[derive(Debug, Clone)]
pub struct NeoApp {
    /// Application name.
    pub name: &'static str,
    /// Application version.
    pub version: &'static str,
    /// Server configuration.
    pub server: ServerConfiguration,
}

#[derive(Debug, Clone)]
pub struct ServerConfiguration {
    /// Application server host.
    pub host: String,
    /// Application server port.
    pub port: u16,
}

#[derive(Debug, Clone)]
pub struct RequestState {
    /// Claims extracted from the token.
    pub claims: Option<Claims>,
    /// Correlation ID.
    pub corr: String,
}

impl Default for RequestState {
    fn default() -> Self {
        RequestState {
            claims: None,
            corr: Default::default(),
        }
    }
}
