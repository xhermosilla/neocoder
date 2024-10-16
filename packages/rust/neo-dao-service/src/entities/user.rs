use mongodb::bson::oid::ObjectId;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
pub struct User {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    /// Unique identifier
    pub id: Option<ObjectId>,
    /// User name
    pub name: String,
    /// User email
    pub email: String,
    /// Password
    pub password: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// Roles assigned to the user
    pub roles: Option<Vec<String>>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// Preferences
    pub preferences: Option<Preferences>,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Preferences {
    /// Theme
    pub theme: String,
    /// Language
    pub language: String,
}
