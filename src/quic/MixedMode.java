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
import java.util.ArrayList;
import java.util.List;

public class MixedMode {

    static class RequestTask implements Runnable {

        private final HttpClient client;
        private final String server;
        private final String path;
        private final String type;
        private final boolean video;
        private final int requestNumber;

        RequestTask(
                HttpClient client,
                String server,
                String path,
                String type,
                boolean video,
                int requestNumber) {

            this.client = client;
            this.server = server;
            this.path = path;
            this.type = type;
            this.video = video;
            this.requestNumber = requestNumber;
        }

        @Override
        public void run() {

            URI requestUri =
                    URI.create("http://" + server + "/" + path);

            HttpRequest.Builder requestBuilder =
                    HttpRequest.newBuilder()
                            .uri(requestUri);

            /*
             * Same video workload used in our
             * existing QUIC video tests:
             * first 100 KB using HTTP Range.
             */
            if (video) {
                requestBuilder.header(
                        "Range",
                        "bytes=0-102400"
                );
            }

            HttpRequest request =
                    requestBuilder.build();

            try {

                long start =
                        System.currentTimeMillis();

                HttpResponse<String> response =
                        client.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                long end =
                        System.currentTimeMillis();

                System.out.println(
                        "\n========== REQUEST " +
                        requestNumber +
                        " =========="
                );

                System.out.println(
                        "Type: " + type
                );

                System.out.println(
                        "Path: " + path
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
                        "\nRequest " +
                        requestNumber +
                        " failed: " +
                        e.getMessage()
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.err.println(
                        "\nRequest " +
                        requestNumber +
                        " interrupted"
                );
            }
        }
    }


    public static void main(String[] args) throws Exception {

        if (args.length < 2) {

            System.err.println(
                    "Usage: MixedMode <server address> <users>"
            );

            System.exit(1);
        }


        String server = args[0];

        int users =
                Integer.parseInt(args[1]);


        /*
         * Mixed Mode:
         *
         * 30% Browsing
         * 30% Video
         * 20% Images
         * 20% Audio
         *
         * Exact distribution used for our
         * supported user counts.
         */

        int browsing;
        int video;
        int images;
        int audio;


        if (users == 10) {

            browsing = 3;
            video = 3;
            images = 2;
            audio = 2;

        } else if (users == 25) {

            browsing = 8;
            video = 7;
            images = 5;
            audio = 5;

        } else if (users == 50) {

            browsing = 15;
            video = 15;
            images = 10;
            audio = 10;

        } else if (users == 100) {

            browsing = 30;
            video = 30;
            images = 20;
            audio = 20;

        } else if (users == 150) {

            browsing = 45;
            video = 45;
            images = 30;
            audio = 30;

        } else if (users == 200) {

            browsing = 60;
            video = 60;
            images = 40;
            audio = 40;

        } else {

            System.err.println(
                    "Supported users: 10, 25, 50, 100, 150, 200"
            );

            System.exit(1);

            return;
        }


        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "           QUIC MIXED MODE TEST"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Server   : " + server
        );

        System.out.println(
                "Users    : " + users
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Browsing : " + browsing
        );

        System.out.println(
                "Video    : " + video
        );

        System.out.println(
                "Images   : " + images
        );

        System.out.println(
                "Audio    : " + audio
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Distribution: 30% / 30% / 20% / 20%"
        );

        System.out.println(
                "=========================================="
        );


        // =====================================
        // QUIC LOGGER
        // =====================================

        Logger stdoutLogger =
                new SysOutLogger();

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


        // =====================================
        // ONE HTTP/3 CLIENT
        // =====================================

        HttpClient client =
                ((Http3ClientBuilder)
                        Http3Client.newBuilder())
                        .logger(stdoutLogger)
                        .connectTimeout(
                                Duration.ofSeconds(10)
                        )
                        .build();


        List<Thread> threads =
                new ArrayList<>();

        int requestNumber = 1;


        // =====================================
        // BROWSING
        // =====================================

        for (int i = 0; i < browsing; i++) {

            threads.add(
                    new Thread(
                            new RequestTask(
                                    client,
                                    server,
                                    "example.html",
                                    "BROWSING",
                                    false,
                                    requestNumber++
                            )
                    )
            );
        }


        // =====================================
        // VIDEO
        // =====================================

        for (int i = 0; i < video; i++) {

            threads.add(
                    new Thread(
                            new RequestTask(
                                    client,
                                    server,
                                    "videoFile/video1.mp4",
                                    "VIDEO",
                                    true,
                                    requestNumber++
                            )
                    )
            );
        }


        // =====================================
        // IMAGES
        // =====================================

        String[] imageFiles = {
                "imgFile/img1.jpg",
                "imgFile/img2.jpg"
        };

        for (int i = 0; i < images; i++) {

            String path =
                    imageFiles[i % imageFiles.length];

            threads.add(
                    new Thread(
                            new RequestTask(
                                    client,
                                    server,
                                    path,
                                    "IMAGE",
                                    false,
                                    requestNumber++
                            )
                    )
            );
        }


        // =====================================
        // AUDIO
        // =====================================

        String[] audioFiles = {
                "audioFile/audio1.mp3",
                "audioFile/audio2.mp3"
        };

        for (int i = 0; i < audio; i++) {

            String path =
                    audioFiles[i % audioFiles.length];

            threads.add(
                    new Thread(
                            new RequestTask(
                                    client,
                                    server,
                                    path,
                                    "AUDIO",
                                    false,
                                    requestNumber++
                            )
                    )
            );
        }


        // =====================================
        // START ALL REQUESTS
        // =====================================

        System.out.println();
        System.out.println(
                "Starting " +
                threads.size() +
                " concurrent requests..."
        );

        long testStart =
                System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }


        // =====================================
        // WAIT FOR ALL REQUESTS
        // =====================================

        for (Thread thread : threads) {
            thread.join();
        }


        long testEnd =
                System.currentTimeMillis();


        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       ALL REQUESTS COMPLETE"
        );

        System.out.println(
                "Total test time: " +
                (testEnd - testStart) +
                " ms"
        );

        System.out.println(
                "Total requests: " +
                threads.size()
        );

        System.out.println(
                "=========================================="
        );
    }
}
