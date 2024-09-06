package org.main.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static java.rmi.server.LogStream.log;

public class Network {



    public static Object lock = new Object();


    public static String getIP() throws IOException {

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", "hostname -I | cut -d' ' -f1");
        Process process = pb.start();

        InputStream inputStream = process.getInputStream();
        Scanner scanner = new Scanner(inputStream);

        StringBuilder outputString = new StringBuilder();
        while (scanner.hasNextLine()) {
            synchronized (Network.lock) {
                String message = scanner.nextLine();
                outputString.append(message);
                outputString.append("\n");
                log(message);
            }
        }
        scanner.close();

        return outputString.toString().trim();

    }
}
