use actix_web::{
    http::{self, header::ContentType},
    test,
    web::{self, Bytes},
    App,
};
use ctor::ctor;

use neo_ms::AppConfig;
use neo_ms_auth_api::{auth, config::Configuration, state::AppState};

#[ctor]
fn setup() {
    dotenv::dotenv().ok();
}

/// Get the application state.
fn get_state() -> AppState {
    let config: AppConfig<Configuration> = AppConfig::from_env();
    AppState::new(config)
}

/// Test the login endpoint.
async fn login_test(request: &str, status: u16, expected: Option<&str>) {
    let app = test::init_service(App::new().app_data(web::Data::new(get_state())).service(auth::controller::login)).await;
    let req = test::TestRequest::post()
        .uri("/login")
        .set_payload(request.to_string())
        .insert_header(ContentType::json())
        .to_request();
    let resp = test::call_service(&app, req).await;
    assert_eq!(resp.status(), status);

    if let Some(expected) = expected {
        let body = test::read_body(resp).await;
        assert_eq!(body, Bytes::from(expected.to_string()));
    }
}

#[actix_web::test]
async fn login_unauthorized() {
    login_test(
        r#"{"username": "invalid", "password": "invalid"}"#,
        401,
        Some(r#"{"code":401,"error":"Unauthorized","message":"Invalid credentials"}"#),
    )
    .await;
}

#[actix_web::test]
async fn login_success() {
    login_test(r#"{"username": "admin", "password": "admin"}"#, 200, None).await;
}

#[actix_web::test]
async fn refresh_token_empty() {
    let req = test::TestRequest::default().insert_header(ContentType::json()).to_http_request();

    let resp = auth::controller::refresh(web::Data::new(get_state()), req).await;
    assert!(resp.is_err());
    assert_eq!(format!("{:?}", resp.err().unwrap()), "Unauthorized(\"Invalid token\")");
}

#[actix_web::test]
async fn refresh_token_invalid() {
    let req = test::TestRequest::default()
        .insert_header(ContentType::json())
        .insert_header((http::header::AUTHORIZATION, "Bearer invalid"))
        .to_http_request();

    let resp = auth::controller::refresh(web::Data::new(get_state()), req).await;
    assert!(resp.is_err());
    assert_eq!(format!("{:?}", resp.err().unwrap()), "InternalError(\"Error refreshing token\")");
}

#[actix_web::test]
async fn refresh_token_success() {
    let token = get_state().token_service.generate("admin", vec!["admin".to_string()]).unwrap();
    let req = test::TestRequest::default()
        .insert_header(ContentType::json())
        .insert_header((http::header::AUTHORIZATION, format!("Bearer {}", token)))
        .to_http_request();

    let resp = auth::controller::refresh(web::Data::new(get_state()), req).await;
    assert!(resp.is_ok());
    assert!(resp.as_ref().unwrap().expires > 0);
    assert!(resp.as_ref().unwrap().token_type == "Bearer")
}

#[actix_web::test]
async fn validate_token_empty() {
    let req = test::TestRequest::default().insert_header(ContentType::json()).to_http_request();

    let resp = auth::controller::validate(web::Data::new(get_state()), req).await;
    assert!(resp.is_err());
    assert_eq!(format!("{:?}", resp.err().unwrap()), "Unauthorized(\"Invalid token\")");
}

#[actix_web::test]
async fn validate_token_invalid() {
    let req = test::TestRequest::default()
        .insert_header(ContentType::json())
        .insert_header((http::header::AUTHORIZATION, "Bearer invalid"))
        .to_http_request();

    let resp = auth::controller::validate(web::Data::new(get_state()), req).await;
    assert!(resp.is_err());
    assert_eq!(format!("{:?}", resp.err().unwrap()), "Unauthorized(\"Invalid token\")");
}

#[actix_web::test]
async fn validate_token_success() {
    let token = get_state().token_service.generate("admin", vec!["admin".to_string()]).unwrap();
    let req = test::TestRequest::default()
        .insert_header(ContentType::json())
        .insert_header((http::header::AUTHORIZATION, format!("Bearer {}", token)))
        .to_http_request();

    let resp = auth::controller::validate(web::Data::new(get_state()), req).await;
    assert!(resp.is_ok());
    assert!(resp.as_ref().unwrap().expires > 0);
    assert!(resp.as_ref().unwrap().valid == true);
}
