use actix_web::{
    dev::{forward_ready, Service, ServiceRequest, ServiceResponse, Transform},
    http::header::{HeaderName, HeaderValue},
    web,
};
use futures_util::future::LocalBoxFuture;
use std::{
    future::{ready, Ready},
    sync::Arc,
};
use uuid::Uuid;

use crate::NeoAppState;

pub struct RequestStateMiddleware;

impl<S> Transform<S, ServiceRequest> for RequestStateMiddleware
where
    S: Service<ServiceRequest, Response = ServiceResponse, Error = actix_web::Error>,
    S::Future: 'static,
{
    type Response = ServiceResponse;
    type Error = actix_web::Error;
    type InitError = ();
    type Transform = RequestStateService<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ready(Ok(RequestStateService { service }))
    }
}

impl RequestStateMiddleware {
    fn get_header(req: &ServiceRequest, header: &str) -> Option<String> {
        req.headers().get(header).and_then(|h| h.to_str().ok()).map(|s| s.to_string())
    }
}

pub struct RequestStateService<S> {
    service: S,
}

impl<S> Service<ServiceRequest> for RequestStateService<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse, Error = actix_web::Error>,
    S::Future: 'static,
{
    type Response = ServiceResponse;
    type Error = S::Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    forward_ready!(service);

    fn call(&self, req: ServiceRequest) -> Self::Future {
        let app_state: Option<&web::Data<Arc<dyn NeoAppState>>> = req.app_data();
        let corr = RequestStateMiddleware::get_header(&req, "correlator").unwrap_or_else(|| Uuid::new_v4().to_string());

        if let Some(state) = app_state {
            state.set_corr(req.request(), corr.clone());
        }

        let fut = self.service.call(req);
        Box::pin(async move {
            let mut response = fut.await?;
            println!("Setting correlator: {}", corr);
            response
                .headers_mut()
                .insert(HeaderName::from_static("correlator"), HeaderValue::from_str(&corr).unwrap());
            Ok(response)
        })
    }
}
