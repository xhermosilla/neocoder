FROM rust:latest

WORKDIR /usr/src

# TODO: Copy dependencies, remove when libraries are published
COPY ./../aura-ms ./aura-ms

RUN ls -la

WORKDIR /usr/src/app

COPY . .

RUN cargo build --release

RUN cargo install --path .

CMD ["/usr/local/cargo/bin/app"]