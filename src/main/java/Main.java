import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.err.println("Logs from your program will appear here!");

        int port = 9092;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            // Wait for a client to connect
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                // Read message size (4 bytes, big-endian)
                byte[] messageSizeBytes = new byte[4];
                in.read(messageSizeBytes);

                // Read API version request (6 bytes)
                byte[] requestHeader = new byte[6];
                in.read(requestHeader);

                // Extract correlation ID (last 4 bytes of request header)
                byte[] correlationIdBytes = new byte[4];
                System.arraycopy(requestHeader, 2, correlationIdBytes, 0, 4);

                // Build the response
                ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                DataOutputStream response = new DataOutputStream(responseBuffer);

                // Write the correlation ID
                response.write(correlationIdBytes);

                // Write the error code (2 bytes, 0 for no error)
                response.writeShort(0);

                // Write number of API versions (1 for now)
                response.writeShort(1);

                // Write API version entry for key 18
                response.writeShort(18); // API key
                response.writeShort(0);  // MinVersion
                response.writeShort(4);  // MaxVersion

                // Get the response body as bytes
                byte[] responseBody = responseBuffer.toByteArray();

                // Write the message length (4 bytes) followed by the response body
                DataOutputStream output = new DataOutputStream(out);
                output.writeInt(responseBody.length);
                output.write(responseBody);

                // Ensure the output is sent
                output.flush();
                System.out.println("Response sent successfully.");
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
