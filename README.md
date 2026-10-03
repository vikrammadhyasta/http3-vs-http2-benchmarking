# HTTP/3 over QUIC vs. HTTP/2 over TCP: Performance Benchmarking

A comparative performance study of **HTTP/3 over QUIC** and **HTTP/2 over TCP** using AWS EC2, JMeter, custom Java workload clients, and packet-level network analysis. The experiments examine web workloads under different client loads and across AWS deployment regions.

## 1. Research Objectives

The objective of this project is to investigate how HTTP/3 over QUIC and HTTP/2 over TCP behave under different web workloads, request loads, and deployment locations.

The experiment focuses on:

* Comparing response time and throughput.
* Measuring request completion and error rates.
* Observing transport-level behavior using packet captures.
* Evaluating browsing, video, audio, image, and mixed workloads.
* Examining performance under increasing concurrent user loads.
* Comparing results from AWS deployments in Mumbai and the United States.

The study is intended to provide experimental observations under controlled conditions, rather than claim that one protocol is universally faster.

## 2. Experimental Overview

The experiment was conducted in two AWS regions:

| Region                | AWS region   | Purpose                                       |
| --------------------- | ------------ | --------------------------------------------- |
| Mumbai, India         | `ap-south-1` | Initial deployment and benchmarking           |
| US East (N. Virginia) | `us-east-1`  | Repeat experiment to examine regional effects |

Separate server environments were configured for QUIC/HTTP/3 and non-QUIC/HTTP/2. Workload files and test configurations were kept consistent where possible to support comparison.

The general workflow was:

1. Launch and configure EC2 instances in the Mumbai region.
2. Configure the QUIC and non-QUIC server environments, including security groups, ports, and server settings.
3. Deploy the same test content to both environments.
4. Configure JMeter and run the workload tests.
5. Capture network traffic and inspect protocol behavior.
6. Collect JMeter reports, CSV summaries, packet captures, and logs.
7. Repeat the experiment in the US region using the same general methodology.
8. Organize and review the collected results for analysis.

## 3. System Architecture

### Server environments

Two protocol-specific server environments were used:

* **QUIC environment:** HTTP/3 over QUIC using UDP port 443.
* **Non-QUIC environment:** HTTP/2 over TCP using TCP port 443.

Caddy was used in the protocol-serving setup, with server protocol configuration adjusted to support the intended protocol. Nginx configuration and Docker-related deployment materials are also included in the repository.

Both environments served comparable web content, including HTML pages, images, audio files, and video files.

### AWS infrastructure

The environments were deployed on AWS EC2 instances in Mumbai and later in the US region.

The EC2 security groups and server configuration were adjusted to allow the traffic required by each protocol. The key transport distinction was:

| Protocol | Transport     | Port |
| -------- | ------------- | ---- |
| HTTP/2   | TCP           | 443  |
| HTTP/3   | QUIC over UDP | 443  |

HTTP/3 testing requires UDP traffic to reach the QUIC server. HTTP/2 testing uses TCP. Security group and server rules must match the intended test configuration.

### Load generation

Apache JMeter was used to generate workloads. A dedicated client environment in the same AWS region as the servers was used for the US-region experiment to reduce unrelated network-path differences.

For the HTTP/3 workload, custom Java clients based on the Kwik and Flupke libraries were used. JMeter plans also include HTTP/2 sampler configurations.

## 4. Workloads

The experiment included several workload types representing different web activities.

| Workload | Description                                                  |
| -------- | ------------------------------------------------------------ |
| Browsing | Concurrent requests for web pages and associated resources   |
| Video    | Video file requests, including range-based requests          |
| Audio    | Requests for audio resources                                 |
| Images   | Requests for image resources                                 |
| Mixed    | A combination of browsing, video, images, and audio requests |

The mixed workload was configured with the following distribution:

| Traffic type | Share |
| ------------ | ----: |
| Browsing     |   30% |
| Video        |   30% |
| Images       |   20% |
| Audio        |   20% |

Video workloads used HTTP range-based requests; the experiment design included 100 KB range requests.

## 5. Load and Test Matrix

The workload plans covered increasing user loads, including:

| Concurrent users | Test purpose               |
| ---------------: | -------------------------- |
|               10 | Lower-load behavior        |
|               25 | Light-to-moderate load     |
|               50 | Moderate load              |
|              100 | Higher load                |
|              150 | Extended load profile      |
|              200 | Extended high-load profile |

The repository contains result files for several of these load levels. The presence of a result file or test plan does not by itself establish that every protocol-workload-load combination was completed successfully. The final analysis should identify the exact completed runs from the corresponding JMeter logs and result metadata.

For a valid comparison, the same workload definition, file sizes, request patterns, thread counts, ramp-up settings, loops, and measurement approach should be used for both protocols wherever the test supports it.

## 6. Tools and Technologies

| Component                  | Technology                                                |
| -------------------------- | --------------------------------------------------------- |
| Cloud infrastructure       | AWS EC2                                                   |
| Regions                    | Mumbai (`ap-south-1`), US East (N. Virginia, `us-east-1`) |
| HTTP/3 client              | Java, Kwik, Flupke                                        |
| HTTP/2 testing             | Apache JMeter with HTTP/2 sampler configuration           |
| HTTP/3 workload generation | Custom Java workload clients and JMeter integration       |
| Web servers                | Caddy and supporting Nginx configuration                  |
| Containerization           | Docker                                                    |
| Load testing               | Apache JMeter                                             |
| Packet capture             | `tcpdump`                                                 |
| Packet analysis            | Wireshark and TShark                                      |
| Build system               | Gradle                                                    |
| Programming language       | Java                                                      |

## 7. Packet Capture and Network Analysis

Packet captures were collected during testing to examine transport-level behavior and support interpretation of the performance measurements.

The packet-analysis workflow used:

* `tcpdump` to capture network traffic during selected test runs.
* Wireshark to inspect packet exchanges and transport behavior.
* TShark to filter and extract packet-level information for analysis.
* Application and server logs to supplement packet-level observations.

The analysis focused on transport characteristics relevant to HTTP/2 over TCP and HTTP/3 over QUIC, including the distinction between TCP and UDP transport, packet exchanges, and observed behavior during workload execution.

Packet captures and logs should be associated with their region, protocol, workload, and load profile. A packet capture should only be described as proof of a specific protocol when the captured traffic or supporting endpoint evidence establishes that protocol. QUIC payloads are encrypted, so packet captures alone may not expose all HTTP-level details.

Sensitive captures, TLS key logs, private keys, credentials, and other confidential artifacts must not be published.

## 8. Performance Metrics

The experiment collected or targeted the following performance indicators:

| Metric              | Description                                                   |
| ------------------- | ------------------------------------------------------------- |
| Response time       | Time taken to complete a request                              |
| Throughput          | Amount of successful work completed over time                 |
| Request count       | Number of requests recorded in a test                         |
| Error rate          | Proportion of requests that failed                            |
| Packet behavior     | Transport-level behavior observed in packet captures          |
| Regional comparison | Differences observed between deployments in Mumbai and the US |

Where available, aggregate reports and summary CSVs are used to organize test measurements. Comparisons should be made only between runs with matching workload and load settings, and should report the measurement units and aggregation method.

## 9. Regional Comparison

The experiment was performed first in Mumbai and then repeated in the US region.

The regional comparison is intended to examine how the deployment location affects observed performance while maintaining a consistent test methodology.

For meaningful interpretation, document the following for each run:

* AWS region and instance details.
* Server protocol and configuration.
* JMeter client location.
* Workload and concurrent user count.
* Ramp-up, loop, and duration settings.
* Test start time and result file.
* Packet capture and associated logs.
* Evidence used to confirm the negotiated protocol.

Regional results should not be attributed solely to the HTTP protocol if other conditions, such as server load, client placement, network route, or instance configuration, differed.

## 10. Repository Structure

```text
http3-vs-http2-benchmarking/
├── deployment/
│   └── ec2-protocol-environment/
│       ├── Dockerfile
│       ├── Dockerfile.quic
│       ├── etc/
│       ├── non_quic.sh
│       └── trans_proto/
│           ├── audioFile/
│           ├── imgFile/
│           └── videoFile/
├── gradle/
│   └── wrapper/
├── scripts/
│   └── jmeter/
│       ├── http2-test.jmx
│       ├── http3-test.jmx
│       └── smoke-test.jmx
├── src/
│   └── quic/
│       ├── BrowsingRequests.java
│       ├── MixedMode.java
│       └── MultipleRequests.java
├── results/
│   └── raw/
│       ├── quic/
│       ├── non-quic/
│       └── aggregate-report-10.csv
├── .gitignore
├── build.gradle
├── gradlew
├── README.md
└── settings.gradle
```

The `results/raw/` directory contains collected CSV outputs organized into QUIC and non-QUIC folders. These are raw experiment artifacts and should be interpreted alongside their original test plans, logs, and protocol-verification evidence.

## 11. Setup and Build

### Prerequisites

* Linux environment
* Java 11-compatible runtime or development environment
* Gradle wrapper
* Network access to download Gradle dependencies
* Apache JMeter for executing the JMeter test plans
* AWS EC2 environments configured for the intended protocol

The project uses Gradle and declares Kwik/Flupke dependencies.

### Compile the Java workload clients

Clone the repository:

```bash
git clone https://github.com/vikrammadhyasta/http3-vs-http2-benchmarking.git
cd http3-vs-http2-benchmarking
```

Make the Gradle wrapper executable if necessary:

```bash
chmod +x gradlew
```

Compile the Java source:

```bash
./gradlew clean compileJava
```

A successful build confirms that the current Java source compiles against the dependencies declared by the project. It does not by itself validate the AWS deployment or protocol negotiation.

## 12. Running the Experiments

The repository includes JMeter plans for HTTP/2, HTTP/3, and a smoke test.

Before executing a test:

1. Confirm that the intended server is running and reachable.
2. Verify the endpoint, TLS certificate, port, and protocol configuration.
3. Confirm the JMeter plan uses the intended protocol sampler or client.
4. Set the intended workload, thread count, ramp-up, and loop settings.
5. Save the JMeter output to a uniquely named result file.
6. Record the server logs and packet capture associated with the run.

JMeter can be run in non-GUI mode, for example:

```bash
jmeter -n -t scripts/jmeter/http2-test.jmx -l results-http2.jtl
```

```bash
jmeter -n -t scripts/jmeter/http3-test.jmx -l results-http3.jtl
```

These commands are templates. The test plans may require endpoint, plugin, variable, or environment-specific configuration before they can be run successfully. Confirm the actual sampler and protocol settings in each plan before treating the output as a valid protocol comparison.

## 13. Results and Interpretation

Raw test outputs are stored under `results/raw/`. They include aggregate and summary CSV files for selected QUIC and non-QUIC runs.

The result files should be treated as experimental observations rather than universal performance claims. Before producing final comparative charts or conclusions:

* Match each result to its original test plan and run configuration.
* Confirm the actual protocol used for each run.
* Check for incomplete, failed, or header-only result files.
* Compare equivalent workloads and user loads.
* Record the region, client location, and server configuration.
* Report the number of samples and relevant summary statistics.
* Preserve the raw data and document any exclusions.

No result should be presented as a confirmed HTTP/2-versus-HTTP/3 measurement unless the protocol used in that run has been verified.

## 14. Limitations and Reproducibility

Performance measurements can be affected by server resources, client capacity, network routing, region, TLS configuration, request distribution, concurrency, caching, and background traffic.

The repository is being organized from the experiment's workload code, deployment materials, and collected outputs. Not all historical test runs may have complete metadata or independent protocol-verification evidence.

To support reproducibility, future result summaries should include the complete test configuration, software versions, region, instance types, run date, protocol evidence, and the exact input data used.

## 15. Security and Data Handling

Do not commit:

* AWS private keys or credentials.
* TLS private keys or secrets.
* TLS key-log files.
* Sensitive packet captures.
* Environment files containing credentials.
* Private server configuration or logs containing sensitive information.

Review all artifacts before publishing them to a public repository. Ensure that test media and other redistributed assets may legally be shared.

## 16. Acknowledgements

The HTTP/3 workload clients use the Kwik and Flupke libraries. Refer to their upstream repositories for source code, licensing terms, and attribution requirements.
