use actix_web::{
    dev::{forward_ready, Service, ServiceRequest, ServiceResponse, Transform},
    web, Error,
};
use core::panic;
use futures_util::future::LocalBoxFuture;
use std::{
    future::{ready, Ready},
    sync::Arc,
};

use crate::{CustomError, NeoAppState};

// Define el middleware
pub struct SecurityMiddleware;

impl<S> Transform<S, ServiceRequest> for SecurityMiddleware
where
    S: Service<ServiceRequest, Response = ServiceResponse, Error = actix_web::Error>,
    S::Future: 'static,
{
    type Response = ServiceResponse;
    type Error = actix_web::Error;
    type InitError = ();
    type Transform = SecurityMiddlewareService<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ready(Ok(SecurityMiddlewareService::<S> { service }))
    }
}

pub struct SecurityMiddlewareService<S> {
    service: S,
}

impl<S> Service<ServiceRequest> for SecurityMiddlewareService<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse, Error = Error>,
    S::Future: 'static,
{
    type Response = ServiceResponse;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    forward_ready!(service);

    fn call(&self, req: ServiceRequest) -> Self::Future {
        let app_state: Option<&web::Data<Arc<dyn NeoAppState>>> = req.app_data();

        if app_state.is_none() {
            panic!("Token validator not found. Please add the TokenValidator to the app data.");
        }

        let corr = app_state.unwrap().get_corr(req.request());
        let token = app_state.unwrap().get_token_from_auth_header(req.request());

        if let (Some(token), Some(state)) = (token, app_state) {
            let claims = state.validate_token(&token);
            if let Ok(claims) = claims {
                log::info!(corr = corr.as_str(), claims= serde_json::to_string(&claims).unwrap().as_str(); "Request authorized");

                state.set_claims(req.request(), claims);
                return Box::pin(self.service.call(req));
            }
        }

        log::warn!(corr = corr.as_str(); "Request unauthorized");
        Box::pin(async move { Err(CustomError::Unauthorized("Invalid token".to_string()).into()) })
    }
}
