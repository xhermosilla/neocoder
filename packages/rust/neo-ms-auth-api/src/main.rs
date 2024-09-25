use std::str::FromStr;

use actix_web::web::scope;
use dotenv::dotenv;
use log::{set_max_level, LevelFilter};
use structured_logger::Builder;

use neo_ms::{AppConfig, Correlator, NeoApp, NeoMicroService, ServerConfiguration};
use neo_ms_auth_api::{auth, config::Configuration, state::AppState};

#[actix_web::main]
async fn main() {
    // Load environment variables from .env file
    dotenv().ok();

    // Initialize the logger system
    Builder::with_level("debug").init();

    // Load configuration from environment variables
    log::info!( corr = Correlator::SYSTEM; "Reading configuration from environment variables");
    let config: AppConfig<Configuration> = AppConfig::from_env();

    set_max_level(LevelFilter::from_str(&config.cfg.log_level).expect("Invalid log level"));

    log::info!(
        corr = Correlator::SYSTEM,
        config = serde_json::to_string(&config.cfg).unwrap_or("".to_string());
        "Configuration loaded"
    );

    // Create application state
    log::info!( corr = Correlator::SYSTEM; "Creating application state");
    let state = AppState::new(config.clone());
    log::info!( corr = Correlator::SYSTEM; "Application state created successfully");

    // Start the microservice with configuration
    NeoMicroService::run(
        NeoApp {
            name: "neo-ms-auth-api",
            version: "1.0.0",
            server: ServerConfiguration {
                host: config.cfg.server_host,
                port: config.cfg.server_port,
            },
        },
        state,
        move |cfg| {
            cfg.service(scope(&config.cfg.api_base_url).configure(auth::routes));
        },
    )
    .await;
}
