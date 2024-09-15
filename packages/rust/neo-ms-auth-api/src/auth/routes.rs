use actix_web::{guard, web};
use neo_ms::SecurityMiddleware;

use super::controller;

/// Register routes for auth module.
pub fn routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/auth")
            .service(controller::login)
            .service(
                web::resource("/refresh")
                    .guard(guard::Post())
                    .wrap(SecurityMiddleware)
                    .route(web::post().to(controller::refresh)),
            )
            .service(
                web::resource("/validate")
                    .guard(guard::Get())
                    .wrap(SecurityMiddleware)
                    .route(web::get().to(controller::validate)),
            ),
    );
}
