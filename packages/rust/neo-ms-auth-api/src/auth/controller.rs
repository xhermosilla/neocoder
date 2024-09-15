use actix_web::{post, web, HttpRequest};
use chrono::FixedOffset;
use kv_log_macro as log;
use neo_ms::CustomError;
use neo_ms::{str, NeoAppState};

use crate::auth::model::ValidateTokenResponse;
use crate::state::AppState;

use super::model::{LoginRequest, TokenResponse};

/// Authenticate using username and password.
///
/// Path: [POST] /login
#[post("/login")]
pub async fn login(
    ctx: web::Data<AppState>, req: HttpRequest, credentials: web::Json<LoginRequest>,
) -> Result<web::Json<TokenResponse>, CustomError> {
    let corr = ctx.get_corr(&req);

    log::info!("Login request", { corr: str!(corr), username: str!(&credentials.username) });

    if credentials.username != "admin" || credentials.password != "admin" {
        log::error!("Login unauthorized", { corr: str!(corr) });
        return Err(CustomError::UnauthorizedDefault);
    }

    let token = ctx
        .token_service
        .generate(&credentials.username, vec![String::from("admin")])
        .map_err(|_| CustomError::Unknown)?;

    log::info!("Login successful", { corr: str!(corr), username: str!(&credentials.username) });

    return Ok(web::Json(TokenResponse {
        expires: ctx.config.cfg.auth_expiration,
        token,
        token_type: "Bearer".to_string(),
    }));
}

/// Refresh the token.
///
/// Path: [POST] /refresh
pub async fn refresh(ctx: web::Data<AppState>, req: HttpRequest) -> Result<web::Json<TokenResponse>, CustomError> {
    let corr = ctx.get_corr(&req);
    let old_token = ctx.get_token_from_auth_header(&req).unwrap();

    log::info!("Refresh token request", { corr: str!(corr) });

    let token = ctx
        .token_service
        .refresh(&old_token)
        .map_err(|_| CustomError::InternalError("Error refreshing token".to_string()))?;

    log::info!("Token refreshed", { corr: str!(corr) });

    return Ok(web::Json(TokenResponse {
        expires: ctx.config.cfg.auth_expiration,
        token,
        token_type: "Bearer".to_string(),
    }));
}

/// Validate the token.
///
/// Path: [POST] /validate
pub async fn validate(ctx: web::Data<AppState>, req: HttpRequest) -> Result<web::Json<ValidateTokenResponse>, CustomError> {
    let corr = ctx.get_corr(&req);
    let token = ctx.get_token_from_auth_header(&req).unwrap();

    log::info!("Validate token request", { corr: str!(corr) });

    let claims = ctx
        .token_service
        .verify(&token)
        .map_err(|_| CustomError::Unauthorized("Invalid token".to_string()))?;

    // Format the expiration time with the correct timezone (UTC+2).
    let exp_formatted = chrono::DateTime::from_timestamp(claims.exp, 0)
        .unwrap()
        .with_timezone(&FixedOffset::east_opt(2 * 3600).unwrap());

    log::info!("Token validated", { corr: str!(corr), claims: serde_json::to_string(&claims).unwrap() });

    return Ok(web::Json(ValidateTokenResponse {
        expires: claims.exp,
        valid: true,
        expires_formatted: exp_formatted.to_string(),
    }));
}
