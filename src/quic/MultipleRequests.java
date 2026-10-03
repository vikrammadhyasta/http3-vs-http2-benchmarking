package tech.kwik.flupke.sample;

import tech.kwik.flupke.Http3Client;
import tech.kwik.flupke.Http3ClientBuilder;
import tech.kwik.core.log.Logger;
import tech.kwik.core.log.SysOutLogger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MultipleRequests {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println(
                    "Missing argument, expected: <server address> <path> [<path>...]"
            );
            System.exit(1);
        }

        // Enable detailed QUIC logging
        Logger stdoutLogger = new SysOutLogger();
        stdoutLogger.useRelativeTime(true);

        stdoutLogger.logInfo(true);
        stdoutLogger.logWarning(true);

        // QUIC packet/decryption information
        stdoutLogger.logPackets(true);
        stdoutLogger.logDecrypted(true);
        stdoutLogger.logSecrets(false);

        // QUIC transport behavior
        stdoutLogger.logRecovery(true);
        stdoutLogger.logCongestionControl(true);
        stdoutLogger.logFlowControl(true);

        // HTTP/3 stream information
        stdoutLogger.logStream(true);

        // Statistics
        stdoutLogger.logStats(true);

        // Build ONE HTTP/3 client with the logger
        HttpClient client = ((Http3ClientBuilder) Http3Client.newBuilder())
                .logger(stdoutLogger)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        Thread[] threads = new Thread[args.length - 1];

        for (int i = 1; i < args.length; i++) {

            final String path = args[i];
            final int requestNumber = i;

            threads[i - 1] = new Thread(() -> {

                URI requestUri =
                        URI.create("http://" + args[0] + "/" + path);

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(requestUri)
                                .header("Range", "bytes=0-102400")
                                .build();

                try {

                    long start = System.currentTimeMillis();

                    HttpResponse<String> response =
                            client.send(
                                    request,
                                    HttpResponse.BodyHandlers.ofString()
                            );

                    long end = System.currentTimeMillis();

                    System.out.println(
                            "\n========== REQUEST " +
                            requestNumber +
                            " =========="
                    );

                    System.out.println(
                            "Response: " +
                            response.statusCode()
                    );

                    System.out.println(
                            "Time: " +
                            (end - start) +
                            " ms"
                    );

                    System.out.println(
                            "Body: " +
                            response.body().length() +
                            " bytes"
                    );

                } catch (IOException e) {

                    System.err.println(
                            "Request " +
                            requestNumber +
                            " failed: " +
                            e.getMessage()
                    );

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    System.err.println(
                            "Request " +
                            requestNumber +
                            " interrupted"
                    );
                }

            });

            threads[i - 1].start();
        }

        // Wait for ALL requests to finish
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n========== ALL REQUESTS COMPLETE ==========");
    }
}
