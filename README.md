# HTTP/3 vs HTTP/2 Benchmarking

A research project comparing HTTP/3 over QUIC with HTTP/2 over TCP
under different network conditions and web workloads.

## Objectives

- Compare response time and throughput.
- Measure request completion and error rates.
- Study transport behavior under different network conditions.
- Examine performance across browsing, video, image, and audio workloads.

## Workloads

| Workload | Description |
|---|---|
| Browsing | Concurrent requests for web pages |
| Video | Range-based video requests |
| Mixed | 30% browsing, 30% video, 20% images, 20% audio |

## Project Structure

- `src/quic/` - Custom Java HTTP/3 workload clients.
- `src/http2/` - HTTP/2 client or test implementation (to be added
  after verification).
- `docs/` - Architecture, methodology, and setup documentation.
- `scripts/` - Experiment and analysis scripts.
- `results/charts/` - Reviewed result visualizations.

## Technology

- HTTP/3 over QUIC
- HTTP/2 over TCP
- Java and the Kwik/Flupke HTTP/3 client
- AWS EC2
- Nginx and Caddy
- JMeter, tcpdump, and Wireshark

## Current Status

This repository is being assembled from the experiment's custom
workload code and supporting materials. The HTTP/2 implementation,
reproducible setup instructions, and validated results will be added
as they are verified.

## Reproducibility

Test results depend on the server configuration, client environment,
network conditions, workload size, and concurrency. These details
will be documented with the experiment results.

## Security

Do not commit private keys, credentials, TLS key logs, packet captures
containing sensitive data, or other private configuration.

## Acknowledgements

The HTTP/3 workload clients use the Kwik/Flupke libraries.
See the upstream project for its source and licensing information.
