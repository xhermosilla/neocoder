use mongodb::{Client, Collection};

use crate::entities::User;
use crate::Dao;

pub struct UserDao {
    /// Collection of users.
    collection: Collection<User>,
}

impl UserDao {
    /// Name of the users collection.
    pub const USERS_COLLECTION: &'static str = "users";

    /// Returns a new user dao.
    pub fn new(client: &Client, database: &str) -> Self {
        let collection = client.database(database).collection(UserDao::USERS_COLLECTION);
        UserDao { collection }
    }
}

impl Dao<User> for UserDao {
    /// Returns the collection.
    fn get_collection(&self) -> &Collection<User> {
        &self.collection
    }
}
