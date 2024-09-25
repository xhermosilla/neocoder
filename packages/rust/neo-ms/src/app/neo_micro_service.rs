use actix_files as fs;
use std::sync::Arc;

use crate::{app::middlewares::RequestStateMiddleware, Correlator, NeoAppState};

use super::{error_handler::error_handler, model::NeoApp};
use actix_web::{middleware::Logger, web, App, HttpServer};

pub struct NeoMicroService {}

impl NeoMicroService {
    pub async fn run<S, M>(app: NeoApp, state: S, modules: M)
    where
        S: NeoAppState + Clone + Send + 'static,
        M: Fn(&mut web::ServiceConfig) + Clone + Send + 'static,
    {
        let app_name = app.name;
        let config = app.server.clone();

        let neo_state: Arc<dyn NeoAppState> = Arc::new(state.clone());

        let log_error = |e: std::io::Error| {
            log::error!(corr = Correlator::SYSTEM; "Server cannot start {}", e.to_string());
        };

        log::info!(corr = Correlator::SYSTEM, name = app_name; "Starting server");

        let server = HttpServer::new(move || {
            App::new()
                .wrap(RequestStateMiddleware)
                .wrap(Logger::default())
                .app_data(web::JsonConfig::default().error_handler(error_handler))
                .app_data(web::Data::new(state.clone()))
                .app_data(web::Data::new(neo_state.clone()))
                .configure(modules.clone())
                .configure(super::routes::service_config)
                .service(fs::Files::new("/api-doc", "./static/api-doc").show_files_listing())
        })
        .bind((config.host.clone(), config.port));

        let server = match server {
            Ok(server) => server,
            Err(e) => {
                log_error(e);
                return;
            }
        }
        .run();

        log::info!(corr = Correlator::SYSTEM; "Server listening to {}:{}", config.host, config.port);

        if let Err(e) = server.await {
            log_error(e);
        }
    }
}
