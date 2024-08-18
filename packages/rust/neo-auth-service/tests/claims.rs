use neo_auth_service::Claims;

fn get_claims() -> Claims {
    Claims {
        exp: 100,
        iat: 0,
        iss: "test".to_string(),
        username: "test".to_string(),
        roles: vec!["test".to_string()]
    }
}

#[test]
fn is_expired() {
    assert_eq!(get_claims().is_expired(), true);
}

#[test]
fn is_issued_by() {
    assert_eq!(get_claims().is_issued_by("test"), true);
    assert_eq!(get_claims().is_issued_by(""), false);
}

#[test]
fn is_issued_for() {
    assert_eq!(get_claims().is_issued_for("test"), true);
    assert_eq!(get_claims().is_issued_for("another"), false);
}

#[test]
fn has_role() {
    let claims = get_claims();
    assert_eq!(claims.has_role("test"), true);
    assert_eq!(claims.has_role("test2"), false);
    assert_eq!(claims.has_role(""), false);
}
