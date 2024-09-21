FROM rust:1.81 as builder

RUN apt-get update && apt-get install -y pkg-config libssl-dev

WORKDIR /usr/src/app

# Copy source code
COPY Cargo.toml Cargo.lock ./
COPY ./src ./src
COPY ./static ./static

# Build the release application
RUN cargo build --release --bin neo-ms-auth-api

FROM gcr.io/distroless/cc as runtime

WORKDIR /usr/src/app

# Copy the binary from the builder image
COPY --from=builder /usr/src/app/target/release/neo-ms-auth-api /usr/src/app/neo-ms-auth-api
COPY --from=builder /usr/src/app/static /usr/src/app/static

ENTRYPOINT ["/usr/src/app/neo-ms-auth-api"]
