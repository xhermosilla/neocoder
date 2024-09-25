use actix_web::{get, http::StatusCode, web, HttpResponse, Responder};
use serde_json::json;

use crate::ErrorResponse;

/// Health check.
///
/// Check if the server is running and responding
#[get("/health")]
async fn health() -> impl Responder {
    HttpResponse::Ok().json(json!({"status": "OK"}))
}

/// Default handler.
///
/// Default handler for not found requests
async fn default_handler() -> impl Responder {

    HttpResponse::NotFound().json(ErrorResponse {
        code: StatusCode::NOT_FOUND.as_u16(),
        error: "Not found".to_string(),
        message: "The requested resource was not found".to_string(),
    })
}

pub fn service_config(cfg: &mut web::ServiceConfig) {
    cfg.service(health).default_service(web::route().to(default_handler));
}
