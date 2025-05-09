package AtvExtra;

import AtvExtra.service.GraphServiceClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResultVisualization {

    private static final String PYTHON_SERVICE_URL = "http://127.0.0.1:5000";

    public static void main(String[] args) {
        generateReportsAndGraphs();
    }

    private static void generateReportsAndGraphs() {
        System.out.println("\nConectando ao serviço Python para gerar análises e gráficos...");

        GraphServiceClient client = new GraphServiceClient(PYTHON_SERVICE_URL);

        client.uploadCsv();

        try {
            Path analysisDir = Paths.get("analysis");
            if (!Files.exists(analysisDir)) {
                Files.createDirectories(analysisDir);
            }

            client.generateReport();
            System.out.println("Análise básica concluída!");


            System.out.println("Gerando gráficos...");

            client.generateGraphic("performance_array_type.html", "performance_array_type");
            client.generateGraphic("size_impact.html", "size_impact");
            client.generateGraphic("parallel_efficiency.html", "parallel_efficiency");
            client.generateGraphic("speedup_vs_threads.html", "speedup_vs_threads");
            client.generateGraphic("parallelization_overhead.html", "parallelization_overhead");
            client.generateGraphic("efficiency_heatmap.html", "efficiency_heatmap");
            client.generateGraphic("execution_time_variability.html", "execution_time_variability");
            client.generateGraphic("size_vs_max_speedup.html", "size_vs_max_speedup");

            System.out.println("\nTodos os resultados foram salvos em: " + analysisDir.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o serviço Python: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
