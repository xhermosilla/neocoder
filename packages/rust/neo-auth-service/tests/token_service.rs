use neo_auth_service::TokenService;

fn create(expires_in: Option<i64>) -> TokenService {
    TokenService::new(
        "secret",
        "test",
        expires_in.unwrap_or(3600),
    )
}

#[test]
fn create_token_service() {
    let token_service = create(None);
    assert_eq!(token_service.exp, 3600);
}

#[test]
fn generate_token() {
    let token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();
    assert_eq!(token_service.decode(&token).unwrap().username, "test");
}

#[test]
fn decode_token() {
    let token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();
    let payload = token_service.decode(&token).unwrap();
    assert_eq!(payload.username, "test");
}

#[test]
fn decode_with_invalid_secret_token() {
    let token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();
    let token_service_invalid = TokenService::new("invalid", "test", 3600);
    let result = token_service_invalid.decode(&token);
    assert!(result.is_err());
}

#[test]
fn verify_token() {
    let token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();
    let payload = token_service.verify(&token).unwrap();
    assert_eq!(payload.username, "test");
}

#[test]
fn verify_expired_token() {
    let token_service = create(Some(0));
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();

    // Sleep for 1 second
    std::thread::sleep(std::time::Duration::from_secs(1));

    let result = token_service.verify(&token);
    assert!(result.is_err());
}

#[test]
fn verify_invalid_issuer_token() {
    let mut token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();
    token_service.iss = "invalid".to_string();

    let result = token_service.verify(&token);
    assert!(result.is_err());
}

#[test]
fn refresh_token() {
    let token_service = create(None);
    let token = token_service
        .generate("test", vec!["test".to_string()])
        .unwrap();

    // Sleep for 1 second
    std::thread::sleep(std::time::Duration::from_secs(1));

    let new_token = token_service.refresh(&token).unwrap();
    assert_ne!(token, new_token);
}
