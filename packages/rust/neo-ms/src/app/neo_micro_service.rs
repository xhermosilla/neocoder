use std::sync::Arc;

use crate::{app::middlewares::RequestStateMiddleware, str, Correlator, NeoAppState};

use super::{error_handler::error_handler, model::NeoApp};
use actix_web::{middleware::Logger, web, App, HttpServer};
use kv_log_macro as log;

pub struct NeoMicroService {}

impl NeoMicroService {
    pub async fn run<S, M>(app: NeoApp, state: S, modules: M)
    where
        S:  NeoAppState + Clone + Send + 'static ,
        M: Fn(&mut web::ServiceConfig) + Clone + Send + 'static,
    {
        let app_name = app.name;
        let config = app.server.clone();

        let neo_state: Arc<dyn NeoAppState> = Arc::new(state.clone());

        let log_error = |e: std::io::Error| {
            log::error!("Server cannot start {}", e.to_string(), { corr: Correlator::SYSTEM });
        };

        log::info!("Starting server", { corr: Correlator::SYSTEM, name: str!(app_name) });

        let server = HttpServer::new(move || {
            App::new()
                .wrap(RequestStateMiddleware)
                .wrap(Logger::default())
                .app_data(web::JsonConfig::default().error_handler(error_handler))
                .app_data(web::Data::new(state.clone()))
                .app_data(web::Data::new(neo_state.clone()))
                .configure(modules.clone())
                .configure(super::routes::service_config)
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

        log::info!("Server listening to {}:{}", config.host, config.port, { corr: Correlator::SYSTEM });

        if let Err(e) = server.await {
            log_error(e);
        }
    }
}
