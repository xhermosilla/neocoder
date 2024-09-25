FROM rust:1.81 as builder

RUN apt-get update && apt-get install -y pkg-config libssl-dev musl-tools

WORKDIR /usr/src/app

# Copy source code
COPY Cargo.toml Cargo.lock ./
COPY ./src ./src
COPY ./static ./static

# Build the release application
RUN rustup target add x86_64-unknown-linux-musl
RUN cargo build --release --target=x86_64-unknown-linux-musl --bin neo-ms-auth-api

# Cambia a Alpine como imagen de runtime
FROM alpine:latest as runtime

# Install dependencies
RUN apk add --no-cache ca-certificates

WORKDIR /usr/src/app

# Copy the binary from the builder image
COPY --from=builder /usr/src/app/target/x86_64-unknown-linux-musl/release/neo-ms-auth-api /usr/src/app/neo-ms-auth-api
COPY --from=builder /usr/src/app/static /usr/src/app/static

ENTRYPOINT ["/usr/src/app/neo-ms-auth-api"]
