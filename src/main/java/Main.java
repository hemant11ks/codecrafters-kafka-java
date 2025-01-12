import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.err.println("Logs from your program will appear here!");

        int port = 9092;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                // Read message size (4 bytes)
                byte[] messageSizeBytes = new byte[4];
                in.read(messageSizeBytes);

                // Read request header (6 bytes)
                byte[] requestHeader = new byte[6];
                in.read(requestHeader);

                // Extract correlation ID (last 4 bytes of the request header)
                byte[] correlationIdBytes = new byte[4];
                System.arraycopy(requestHeader, 2, correlationIdBytes, 0, 4);

                // Prepare the response body
                ByteArrayOutputStream responseBodyStream = new ByteArrayOutputStream();
                DataOutputStream responseBody = new DataOutputStream(responseBodyStream);

                // Write the error code (2 bytes, 0 for no error)
                responseBody.writeShort(0);

                // Write the number of API keys (1 key for now)
                responseBody.writeShort(1);

                // Write the API key entry
                responseBody.writeShort(18); // API Key (18 for ApiVersions)
                responseBody.writeShort(0);  // Min Version
                responseBody.writeShort(4);  // Max Version

                // Convert response body to byte array
                byte[] responseBodyBytes = responseBodyStream.toByteArray();

                // Calculate total message size
                int totalSize = responseBodyBytes.length + 4; // Body length + correlation ID

                // Prepare the full response
                ByteArrayOutputStream fullResponseStream = new ByteArrayOutputStream();
                DataOutputStream fullResponse = new DataOutputStream(fullResponseStream);

                // Write the message size (4 bytes)
                fullResponse.writeInt(totalSize);

                // Write the correlation ID
                fullResponse.write(correlationIdBytes);

                // Write the response body
                fullResponse.write(responseBodyBytes);

                // Send the full response
                out.write(fullResponseStream.toByteArray());
                out.flush();

                System.out.println("Response sent successfully.");
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
