package com.matgdev.temvaga.ai.service;


import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.matgdev.temvaga.ai.models.dto.VagasDTO.VagaDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdzunaService {

    @Value("${adzuna.app.id}")
    private String APP_ID;

    @Value("${adzuna.app.key}")
    private String APP_KEY;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ================= NORMALIZAÇÃO =================

    private String limparTexto(String txt) {
        return txt == null ? "" : txt.replaceAll("\\s+", " ").trim();
    }

    private String normalizarTexto(String txt) {
        String clean = limparTexto(txt);
        String normalized = Normalizer.normalize(clean, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

    private String detectarModelo(String texto) {
        String t = normalizarTexto(texto);

        if (t.matches(".*\\b(home office|remote|remoto|100% remoto|100 remoto)\\b.*")) return "Remoto";
        if (t.matches(".*\\b(hibrido|hybrid)\\b.*")) return "Híbrido";
        if (t.matches(".*\\b(presencial|onsite|on-site)\\b.*")) return "Presencial";

        return "";
    }

    private String detectarSenioridade(String titulo, String descricao) {
        String t = normalizarTexto(titulo + " " + descricao);

        if (t.matches(".*\\b(senior|sr|senior)\\b.*") || t.matches(".*(5\\+ anos|5 anos).*"))
            return "Senior";

        if (t.matches(".*\\b(pleno|mid-level|mid level|midlevel)\\b.*"))
            return "Pleno";

        if (t.matches(".*\\b(junior|jr)\\b.*"))
            return "Junior";

        return "";
    }

    private List<String> extrairStack(String texto) {
        Map<String, Pattern> patterns = Map.of(
                "Java", Pattern.compile("\\bjava\\b(?!script)", Pattern.CASE_INSENSITIVE),
                "PHP", Pattern.compile("\\bphp\\b", Pattern.CASE_INSENSITIVE),
                "Node.js", Pattern.compile("\\bnode(\\.?\\s*js)?\\b", Pattern.CASE_INSENSITIVE),
                "React", Pattern.compile("\\breact(\\.js)?\\b", Pattern.CASE_INSENSITIVE),
                "TypeScript", Pattern.compile("\\btypescript\\b", Pattern.CASE_INSENSITIVE),
                "JavaScript", Pattern.compile("\\bjavascript\\b", Pattern.CASE_INSENSITIVE)
        );

        return patterns.entrySet().stream()
                .filter(e -> e.getValue().matcher(texto).find())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private String formatarSalario(JsonNode item) {
        if (item.has("salary_min") && item.has("salary_max")) {
            return item.get("salary_min").asText() + "-" + item.get("salary_max").asText();
        }
        if (item.has("salary_min")) return item.get("salary_min").asText();
        if (item.has("salary_max")) return item.get("salary_max").asText();
        return "";
    }

    private VagaDTO mapear(JsonNode item) {
        String titulo = limparTexto(item.path("title").asText());
        String empresa = limparTexto(item.path("company").path("display_name").asText());
        String descricao = limparTexto(item.path("description").asText());
        String local = limparTexto(item.path("location").path("display_name").asText());

        String texto = titulo + " " + descricao;

        return new VagaDTO(
                titulo,
                empresa,
                extrairStack(texto),
                descricao,
                formatarSalario(item),
                local,
                detectarSenioridade(titulo, descricao),
                item.path("redirect_url").asText()
        );
    }

    // ================= BUSCA =================

    public List<VagaDTO> buscarVagas() {
        try {
            String base = "https://api.adzuna.com/v1/api/jobs/br/search/1";

            String url1 = base + "?app_id=" + APP_ID + "&app_key=" + APP_KEY +
                    "&results_per_page=30&what=backend%20java&where=Florianopolis%20SC&content-type=application/json";

            String url2 = base + "?app_id=" + APP_ID + "&app_key=" + APP_KEY +
                    "&results_per_page=30&what=backend%20java%20remoto&where=Brasil&content-type=application/json";

            List<JsonNode> resultados = new ArrayList<>();
            resultados.addAll(fetch(url1));
            resultados.addAll(fetch(url2));

            return resultados.stream()
                    .map(this::mapear)
                    .filter(v -> v.cargo() != null && !v.cargo().isEmpty() && v.link() != null && !v.link().isEmpty())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar vagas", e);
        }
    }

    private List<JsonNode> fetch(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode json = mapper.readTree(response.body());
        JsonNode results = json.path("results");

        List<JsonNode> lista = new ArrayList<>();
        results.forEach(lista::add);

        return lista;
    }
}
