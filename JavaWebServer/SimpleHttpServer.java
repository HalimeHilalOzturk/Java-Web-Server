import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class SimpleHttpServer {

    private static final int PORT = 1989;
    private static final String WEB_ROOT = ".";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Web sunucusu başlatıldı. Port: " + PORT);
            System.out.println("Tarayıcıdan erişmek için: http://localhost:" + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Sunucu başlatma hatası: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream out = socket.getOutputStream()
            ) {
                String requestLine = in.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    return;
                }

                System.out.println("Gelen İstek: " + requestLine);
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String requestedFile = tokens[1];

                if (!method.equals("GET")) {
                    sendResponse(out, "501 Not Implemented", "text/html", "<h1>501 Desteklenmeyen Metot</h1>".getBytes(StandardCharsets.UTF_8));
                    return;
                }

                if (requestedFile.equals("/")) {
                    sendDefaultPage(out);
                } else {
                    serveRequestedFile(out, requestedFile);
                }

            } catch (IOException e) {
                System.err.println("İstek işleme hatası: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Soket kapatma hatası: " + e.getMessage());
                }
            }
        }

        private void sendDefaultPage(OutputStream out) throws IOException {
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html lang=\"tr\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Öğrenci Profil Sayfası</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                    "            background-color: #f4f7f6;\n" +
                    "            color: #333;\n" +
                    "            margin: 40px;\n" +
                    "            line-height: 1.6;\n" +
                    "        }\n" +
                    "        .card {\n" +
                    "            background: #ffffff;\n" +
                    "            padding: 30px;\n" +
                    "            border-radius: 12px;\n" +
                    "            box-shadow: 0 4px 15px rgba(0,0,0,0.1);\n" +
                    "            max-width: 650px;\n" +
                    "            margin: 0 auto;\n" +
                    "            border-top: 6px solid #2c3e50;\n" +
                    "        }\n" +
                    "        h1 {\n" +
                    "            color: #2c3e50;\n" +
                    "            margin-bottom: 5px;\n" +
                    "            font-size: 32px;\n" +
                    "        }\n" +
                    "        h2 {\n" +
                    "            color: #e74c3c;\n" +
                    "            margin-top: 0;\n" +
                    "            font-weight: 500;\n" +
                    "            font-size: 22px;\n" +
                    "        }\n" +
                    "        p {\n" +
                    "            font-size: 16px;\n" +
                    "            color: #555;\n" +
                    "        }\n" +
                    "        .badge {\n" +
                    "            display: inline-block;\n" +
                    "            background: #3498db;\n" +
                    "            color: white;\n" +
                    "            padding: 4px 10px;\n" +
                    "            border-radius: 4px;\n" +
                    "            font-size: 13px;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card\">\n" +
                    "        <h1>Halime Hilal Öztürk</h1>\n" +
                    "        <h2>1250505050</h2>\n" +
                    "        <hr style=\"border: 0; border-top: 1px solid #eee; margin: 20px 0;\">\n" +
                    "        <p><strong>Biyografi:</strong> Kırklareli Üniversitesi Yazılım Mühendisliği 2. sınıf öğrencisiyim. Nesneye Yönelik Programlama ve Web Geliştirme alanlarında projeler üretmekteyim.</p>\n" +
                    "        <span class=\"badge\">Pure Java HTTP Server</span>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            byte[] bodyBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
            sendResponse(out, "200 OK", "text/html; charset=UTF-8", bodyBytes);
        }

        private void serveRequestedFile(OutputStream out, String filePath) throws IOException {
            File file = new File(WEB_ROOT, filePath);

            if (!file.exists() || file.isDirectory()) {
                String notFoundHtml = "<h1>404 Sayfa Bulunamadi</h1>";
                sendResponse(out, "404 Not Found", "text/html; charset=UTF-8", notFoundHtml.getBytes(StandardCharsets.UTF_8));
                return;
            }

            String contentType = getContentType(filePath);
            byte[] fileData = readFileBytes(file);

            sendResponse(out, "200 OK", contentType, fileData);
        }

        private void sendResponse(OutputStream out, String statusCode, String contentType, byte[] content) throws IOException {
            String header = "HTTP/1.1 " + statusCode + "\r\n" +
                    "Server: Pure Java Web Server\r\n" +
                    "Date: " + new Date() + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + content.length + "\r\n" +
                    "Connection: close\r\n\r\n";

            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(content);
            out.flush();
        }

        private String getContentType(String fileRequested) {
            if (fileRequested.endsWith(".html") || fileRequested.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (fileRequested.endsWith(".css")) return "text/css";
            if (fileRequested.endsWith(".js")) return "text/javascript";
            if (fileRequested.endsWith(".png")) return "image/png";
            if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg")) return "image/jpeg";
            return "text/plain";
        }

        private byte[] readFileBytes(File file) throws IOException {
            try (FileInputStream fileIn = new FileInputStream(file)) {
                byte[] fileData = new byte[(int) file.length()];
                fileIn.read(fileData);
                return fileData;
            }
        }
    }
}
