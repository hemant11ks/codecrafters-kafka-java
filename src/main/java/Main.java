import java.io.IOException;
import java.io.OutputStream;
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

            // Prepare the response
            OutputStream out = clientSocket.getOutputStream();

            // Send message size (4 bytes, big-endian)
            out.write(new byte[] {0, 0, 0, 0});

            // Send correlation ID (4 bytes, big-endian)
            out.write(new byte[] {0, 0, 0, 7});

            // Ensure the output is sent
            out.flush();
            System.out.println("Response sent with correlation ID: 7");
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
