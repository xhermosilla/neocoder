use std::sync::Arc;

use neo_auth_service::{Claims, TokenService};
use neo_ms::{AppConfig, NeoAppState};

use crate::config::Configuration;

/// Application state
#[derive(Clone)]
pub struct AppState {
    // Configuration
    pub config: AppConfig<Configuration>,
    // Token service
    pub token_service: Arc<TokenService>,
}

impl AppState {
    /// Create new application state
    pub fn new(config: AppConfig<Configuration>) -> Self {
        let cfg: &Configuration = &config.cfg;
        let token_service = Arc::new(TokenService::new(&cfg.auth_secret_key, &cfg.auth_issuer, cfg.auth_expiration));
        AppState { config, token_service }
    }
}

/// Implement validation for bearer token
/// It will be used by the security middleware
impl NeoAppState for AppState {

    /// Validate token
    fn validate_token(&self, token: &str) -> Result<Claims, Box<dyn std::error::Error>> {
        self.token_service.verify(token)
    }
}
