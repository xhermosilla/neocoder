use dotenv::dotenv;
use mongodb::{bson::oid::ObjectId, Client};
use neo_dao_service::{daos::UserDao, entities::User, Dao};

/// Returns new user dao.
async fn get_user_dao() -> UserDao {
    // Load environment variables from .env file
    dotenv().ok();
    let uri = std::env::var("APP_MONGODB_URI").unwrap();
    let client = Client::with_uri_str(uri).await.unwrap();
    UserDao::new(&client, "test")
}

/// Returns a user.
fn get_user(name: &str) -> User {
    User {
        email: String::from("mail"),
        name: String::from(name),
        password: String::from("password"),
        roles: None,
        id: None,
        preferences: None,
    }
}

#[tokio::test]
#[ignore]
async fn user_dao_find_all() {
    let dao: UserDao = get_user_dao().await;

    let result = dao.save(get_user("name")).await;
    let users = dao.find_all().await.ok().unwrap();

    // Assert users contains one user
    let user = users
        .iter()
        .find(|u| u.id.unwrap() == result.clone().unwrap().inserted_id.as_object_id().unwrap());
    assert!(user.is_some());

    // Cleanup
    dao.delete_by_id(&result.unwrap().inserted_id.as_object_id().unwrap())
        .await
        .unwrap();
}

#[tokio::test]
#[ignore]
async fn user_dao_find_by_field() {
    let dao: UserDao = get_user_dao().await;
    let result = dao.save(get_user("test001")).await;

    let inserted_id: ObjectId = result.unwrap().inserted_id.as_object_id().unwrap();
    let user = dao.find_by_field("name", "test001").await;

    assert!(user.ok().unwrap().is_some());

    // Cleanup
    dao.delete_by_id(&inserted_id).await.unwrap();
}

#[tokio::test]
#[ignore]
async fn user_dao_find_by_id() {
    let dao: UserDao = get_user_dao().await;
    let result = dao.save(get_user("name")).await;

    let inserted_id: ObjectId = result.unwrap().inserted_id.as_object_id().unwrap();
    let user = dao.find_by_id(&inserted_id).await;

    assert!(user.ok().unwrap().is_some());

    // Cleanup
    dao.delete_by_id(&inserted_id).await.unwrap();
}

#[tokio::test]
#[ignore]
async fn user_dao_save() {
    let dao: UserDao = get_user_dao().await;
    let result = dao.save(get_user("name")).await;
    assert_eq!(result.is_ok(), true);

    // Cleanup
    dao.delete_by_id(&result.unwrap().inserted_id.as_object_id().unwrap())
        .await
        .unwrap();
}

#[tokio::test]
#[ignore]
async fn user_dao_delete_by_id() {
    let dao: UserDao = get_user_dao().await;
    let result = dao.save(get_user("name")).await;

    let inserted_id: ObjectId = result.unwrap().inserted_id.as_object_id().unwrap();
    let delete_result = dao.delete_by_id(&inserted_id).await;

    assert_eq!(delete_result.is_ok(), true);
}
