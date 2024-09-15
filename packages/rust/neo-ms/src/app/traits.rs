use actix_web::{HttpMessage, HttpRequest};
use neo_auth_service::Claims;

use crate::RequestState;

pub trait NeoAppState: Send + Sync {
    /// Validate token
    fn validate_token(&self, _token: &str) -> Result<Claims, Box<dyn std::error::Error>> {
        unimplemented!("validate_token method is not implemented")
    }

    /// Get claims
    fn get_claims(&self, req: &HttpRequest) -> Option<Claims> {
        req.extensions().get::<RequestState>().and_then(|state| state.claims.clone())
    }

    /// Get correlator
    fn get_corr(&self, req: &HttpRequest) -> String {
        self.get_state(req).corr.clone()
    }

    /// Get request state   
    fn get_state(&self, req: &HttpRequest) -> RequestState {
        req.extensions().get::<RequestState>().cloned().unwrap_or_default()
    }

    // Get token from auth header
    fn get_token_from_auth_header(&self, req: &HttpRequest) -> Option<String> {
        req.headers()
            .get("Authorization")
            .and_then(|header| header.to_str().ok())
            .and_then(|header_value| header_value.split_once(' '))
            .filter(|(prefix, _)| *prefix == "Bearer")
            .map(|(_, token)| token.to_string())
    }

    /// Set claims
    fn set_claims(&self, req: &HttpRequest, claims: Claims) {
        let mut request_state = self.get_state(req);
        request_state.claims = Some(claims);
        self.set_state(req, request_state);
    }

    /// Set correlator
    fn set_corr(&self, req: &HttpRequest, correlator: String) {
        let mut request_state = self.get_state(req);
        request_state.corr = correlator;
        self.set_state(req, request_state);
    }

    /// Set state
    fn set_state(&self, req: &HttpRequest, state: RequestState) {
        req.extensions_mut().insert::<RequestState>(state);
    }
}
