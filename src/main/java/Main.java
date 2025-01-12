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

            // Skip request_api_key (2 bytes) and request_api_version (2 bytes)
            in.skip(4);

            // Read the correlation_id (4 bytes, big-endian)
            byte[] correlationIdBytes = new byte[4];
            in.read(correlationIdBytes);

            // Log the correlation ID for debugging
            int correlationId = (correlationIdBytes[0] & 0xFF) << 24 |
                                (correlationIdBytes[1] & 0xFF) << 16 |
                                (correlationIdBytes[2] & 0xFF) << 8 |
                                (correlationIdBytes[3] & 0xFF);
            System.out.println("Received correlation ID: " + correlationId);

            // Prepare the response
            OutputStream out = clientSocket.getOutputStream();

            // Write message size (4 bytes, big-endian, value: 0)
            out.write(new byte[]{0, 0, 0, 0});

            // Write the correlation ID (4 bytes, big-endian)
            out.write(correlationIdBytes);

            // Ensure the output is sent
            out.flush();
            System.out.println("Response sent with correlation ID: " + correlationId);
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
}
