use serde::{Deserialize, Serialize};

/// The payload of a JSON Web Token (JWT)
#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct Claims {
    /// Expiration Time, the time at which the token expires
    /// This is a Unix timestamp
    pub exp: i64,
    /// Issued At, the time at which the token was generated
    /// This is a Unix timestamp
    pub iat: i64,
    /// Issuer, the entity that issued the JWT
    pub iss: String,
    /// User name, the name of the user
    pub username: String,
    /// Roles, the roles assigned to the subject
    pub roles: Vec<String>,
}

impl Claims {
    /// Is token expired?
    pub fn is_expired(&self) -> bool {
        self.exp < chrono::Utc::now().timestamp()
    }

    /// Is token issued by the given issuer?
    pub fn is_issued_by(&self, issuer: &str) -> bool {
        self.iss == issuer
    }

    /// Is token issued for the given user?
    pub fn is_issued_for(&self, user: &str) -> bool {
        self.username == user
    }

    /// Does the token have the given role?
    pub fn has_role(&self, role: &str) -> bool {
        self.roles.contains(&role.to_string())
    }
}
