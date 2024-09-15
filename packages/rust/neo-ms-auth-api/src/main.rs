use actix_web::web::scope;
use config::Configuration;
use dotenv::dotenv;
use kv_log_macro as log;
use neo_ms::{AppConfig, Correlator, NeoApp, NeoMicroService, ServerConfiguration};
use state::AppState;

mod auth;
mod config;
mod state;

#[actix_web::main]
async fn main() {
    // Load environment variables from .env file
    dotenv().ok();

    // Initialize the logger system
    json_env_logger::init();

    // Load configuration from environment variables
    log::info!("Reading configuration from environment variables", { corr: Correlator::SYSTEM });
    let config: AppConfig<Configuration> = AppConfig::from_env();
    log::info!("Configuration loaded", { corr: Correlator::SYSTEM, config: serde_json::to_string(&config.cfg).unwrap() });

    // Create application state
    log::info!("Creating application state", { corr: Correlator::SYSTEM });
    let state = AppState::new(config.clone());
    log::info!("Application state created successfully", { corr: Correlator::SYSTEM });

    // Start the microservice with configuration
    NeoMicroService::run(
        NeoApp {
            name: "aura-ms-i18n",
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
