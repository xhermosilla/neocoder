FROM rust:latest

WORKDIR /usr/src

# TODO: Copy dependencies, remove when libraries are published
COPY ./../neo-ms-auth-api ./neo-ms-auth-api

RUN ls -la

WORKDIR /usr/src/app

COPY . .

RUN cargo build --release

RUN cargo install --path .

CMD ["/usr/local/cargo/bin/app"]