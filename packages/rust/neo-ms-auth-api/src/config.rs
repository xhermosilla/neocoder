use serde::{Deserialize, Serialize, Serializer};

fn api_base_url() -> String { String::from("/neo/api/v1") }
fn auth_expiration() -> i64 { 86400 }
fn auth_issuer() -> String { String::from("neo") }
fn log_level() -> String { String::from("INFO") }
fn server_host() -> String { String::from("localhost") }
fn server_port() -> u16 { 3010 }

fn hidden<S>(_: &str, s: S) -> Result<S::Ok, S::Error> where S: Serializer{
    s.serialize_str("xxxxx")
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Configuration {
    /// API base URL
    /// APP_API_BASE_URL
    #[serde(default = "api_base_url")]
    pub api_base_url: String,
    /// Auth expiration time in seconds. Default is 86400 seconds (24 hours)
    /// APP_AUTH_EXPIRATION
    #[serde(default = "auth_expiration")]
    pub auth_expiration: i64,
    /// Auth issuer
    /// APP_AUTH_ISSUER
    #[serde(default = "auth_issuer")]
    pub auth_issuer: String,
    /// Auth secret key
    /// APP_AUTH_SECRET_KEY
    #[serde(serialize_with = "hidden")]
    pub auth_secret_key: String,
    /// Log level
    /// APP_LOG_LEVEL
    #[serde(default = "log_level")]
    pub log_level: String,
    /// Server host
    /// APP_SERVER_HOST
    #[serde(default = "server_host")]
    pub server_host: String,
    /// Server port
    /// APP_SERVER_PORT
    #[serde(default = "server_port")]
    pub server_port: u16,
}
