use serde::{Deserialize, Serialize, Serializer};

fn api_base_url() -> String { String::from("/neo/api/v1") }
fn auth_expiration() -> i64 { 86400 }
fn auth_issuer() -> String { String::from("neo") }
fn server_host() -> String { String::from("localhost") }
fn server_port() -> u16 { 3010 }

fn hidden<S>(_: &str, s: S) -> Result<S::Ok, S::Error> where S: Serializer{
    s.serialize_str("xxxxx")
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Configuration {
    #[serde(default = "api_base_url")]
    pub api_base_url: String,
    /// Auth expiration time in seconds. Default is 86400 seconds (24 hours)
    #[serde(default = "auth_expiration")]
    pub auth_expiration: i64,
    /// Auth issuer
    #[serde(default = "auth_issuer")]
    pub auth_issuer: String,
    /// Auth secret key
    #[serde(serialize_with = "hidden")]
    pub auth_secret_key: String,
    /// Server host
    #[serde(default = "server_host")]
    pub server_host: String,
    /// Server port
    #[serde(default = "server_port")]
    pub server_port: u16,
}
