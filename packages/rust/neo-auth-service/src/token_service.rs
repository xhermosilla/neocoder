use crate::Claims;
use jsonwebtoken as jwt;

/// Service for generating, verifying, and managing JSON Web Tokens (JWTs)
pub struct TokenService {
    /// Expiration Time, the time at which the token expires in seconds
    pub exp: i64,
    /// Issuer, the entity that issued the JWT
    pub iss: String,
    /// The secret key used to sign the JWT
    pub secret: String,
}

impl TokenService {
    /// Create a new AuthService
    pub fn new(secret: &str, iss: &str, exp: i64) -> TokenService {
        TokenService {
            exp,
            iss: iss.to_string(),
            secret: secret.to_string(),
        }
    }

    /// Generate a JWT for the given user
    pub fn generate(
        &self, user: &str, roles: Vec<String>,
    ) -> Result<String, Box<dyn std::error::Error>> {
        let claims = Claims {
            exp: chrono::Utc::now().timestamp() + self.exp as i64,
            iat: chrono::Utc::now().timestamp(),
            iss: self.iss.to_string(),
            username: user.to_string(),
            roles,
        };

        let token = jwt::encode(
            &jwt::Header::default(),
            &claims,
            &jwt::EncodingKey::from_secret(self.secret.as_ref()),
        )?;
        Ok(token)
    }

    /// Decode a JWT and return the payload
    pub fn decode(&self, token: &str) -> Result<Claims, Box<dyn std::error::Error>> {
        let token_data = jwt::decode::<Claims>(
            &token,
            &jwt::DecodingKey::from_secret(self.secret.as_ref()),
            &jwt::Validation::default(),
        )?;
        Ok(token_data.claims)
    }

    /// Verify a JWT and return the payload
    pub fn verify(&self, token: &str) -> Result<Claims, Box<dyn std::error::Error>> {
        let payload = jwt::decode::<Claims>(
            &token,
            &jwt::DecodingKey::from_secret(self.secret.as_ref()),
            &jwt::Validation::default(),
        )?
        .claims;

        if payload.is_expired() {
            return Err("Token has expired".into());
        }

        if !payload.is_issued_by(&self.iss) {
            return Err("Invalid issuer".into());
        }

        Ok(payload)
    }

    /// Refresh a JWT and return the new token
    pub fn refresh(&self, token: &str) -> Result<String, Box<dyn std::error::Error>> {
        let payload = self.verify(token)?;
        self.generate(&payload.username, payload.roles)
    }
}
