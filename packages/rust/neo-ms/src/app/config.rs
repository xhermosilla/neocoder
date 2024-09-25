use serde::{de::DeserializeOwned, Deserialize, Serialize};

use crate::Correlator;

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct AppConfig<T> {
    /// App name
    pub name: String,
    /// App configuration
    pub cfg: T,
}

impl<T> AppConfig<T>
where
    T: DeserializeOwned,
{
    /// Load application configuration from environment variables or exit the process
    pub fn from_env() -> AppConfig<T> {
        Self::from_env_with_prefix("APP_")
    }

    /// Load application configuration from environment variables or exit the process using the provided prefix
    pub fn from_env_with_prefix(prefix: &str) -> AppConfig<T> {
        match envy::prefixed(prefix).from_env::<T>() {
            Ok(cfg) => AppConfig {
                name: std::env::var(format!("{}NAME", prefix)).unwrap_or_else(|_| "App".into()),
                cfg,
            },
            Err(e) => {
                log::error!(corr = Correlator::SYSTEM; "Cannot load app configuration: {}", e.to_string());
                std::process::exit(1);
            }
        }
    }
}
