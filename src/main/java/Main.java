import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.err.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 9092;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            // Wait for a client to connect
            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Read the request from the client
            InputStream in = clientSocket.getInputStream();

            // Read the message size (4 bytes, big-endian)
            byte[] messageSizeBytes = new byte[4];
            in.read(messageSizeBytes);

            // Read request_api_key (2 bytes, ignored) and request_api_version (2 bytes)
            byte[] apiVersionBytes = new byte[2];
            in.skip(2); // Skip request_api_key
            in.read(apiVersionBytes);
            int apiVersion = ((apiVersionBytes[0] & 0xFF) << 8) | (apiVersionBytes[1] & 0xFF);

            // Read correlation_id (4 bytes, big-endian)
            byte[] correlationIdBytes = new byte[4];
            in.read(correlationIdBytes);

            // Log extracted data
            System.out.println("Received API version: " + apiVersion);
            System.out.println("Received correlation ID: " + bytesToHex(correlationIdBytes));

            // Prepare the response
            OutputStream out = clientSocket.getOutputStream();

            // Write message_size (4 bytes, value: 0 for now)
            out.write(new byte[]{0, 0, 0, 0});

            // Write the correlation ID
            out.write(correlationIdBytes);

            // Determine if the version is unsupported
            if (apiVersion < 0 || apiVersion > 4) {
                // Write the error_code field (2 bytes, big-endian, value: 35)
                out.write(new byte[]{0x00, 0x23}); // 35 in hexadecimal
                System.out.println("Unsupported API version. Sent error code 35.");
            }

            // Ensure the output is sent
            out.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    // Helper method to convert bytes to hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
