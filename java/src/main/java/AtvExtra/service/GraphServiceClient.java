package AtvExtra.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GraphServiceClient {

    private final String baseUrl;
    private final HttpClient client;

    public GraphServiceClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    public void generateReport() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl+"/analysis"))
                    .header("Accept", "text/markdown")
                    .GET()
                    .build();

            Path filePath = Paths.get("analysis/analysis.md");
            HttpResponse<Path> response = client.send(request,
                    HttpResponse.BodyHandlers.ofFile(filePath));

            System.out.println("Arquivo baixado e salvo em: " + filePath.toAbsolutePath());
            System.out.println("Status code: " + response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao baixar o arquivo Markdown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateGraphic(String fileName, String type) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/plots/" + type))
                    .header("Accept", "text/html")
                    .GET()
                    .build();

            Path filePath = Paths.get("analysis/" + fileName);
            HttpResponse<Path> response = client.send(request,
                    HttpResponse.BodyHandlers.ofFile(filePath));

            if (response.statusCode() == 200) {
                System.out.println("Arquivo HTML salvo com sucesso em: " + filePath.toAbsolutePath());
            } else {
                System.out.println("Erro ao baixar HTML: " + response.statusCode());
                // Em caso de erro, tenta remover o arquivo se foi criado
                Files.deleteIfExists(filePath);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao baixar o arquivo HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void uploadCsv() {
        Path filePath = Paths.get("sorting_results.csv"); // Substitua pelo caminho do seu arquivo
        String boundary = "---" + System.currentTimeMillis() + "---";


        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileName = filePath.getFileName().toString();

        String requestBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: text/csv\r\n" + // Ajuste o Content-Type se necessário
                "\r\n" +
                new String(fileBytes) + "\r\n" +
                "--" + boundary + "--\r\n";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl+"/upload"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Código de status: " + response.statusCode());
        System.out.println("Corpo da resposta:\n" + response.body());
    }

}
