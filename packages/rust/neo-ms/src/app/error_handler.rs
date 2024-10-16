use super::model::ErrorResponse;
use actix_web::{error, http::StatusCode, Error, HttpRequest, HttpResponse};

pub fn error_handler(err: error::JsonPayloadError, _req: &HttpRequest) -> Error {
    error::InternalError::from_response(
        "",
        HttpResponse::BadRequest().json(ErrorResponse {
            code: StatusCode::BAD_REQUEST.as_u16(),
            error: "Bad request".to_string(),
            message: err.to_string(),
        }),
    )
    .into()
}
