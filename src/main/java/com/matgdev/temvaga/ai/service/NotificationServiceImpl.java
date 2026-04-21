package com.matgdev.temvaga.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.matgdev.temvaga.ai.models.dto.VagasDTO;
import com.matgdev.temvaga.ai.models.dto.VagasDTO.VagaDTO;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String telegramChatId;

    @Override
    public void sendToDiscord(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("content", message);
        restTemplate.postForEntity(discordWebhookUrl, body, String.class);
    }
    
    @Override
    public void sendToTelegram(String message) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";
        Map<String, String> body = new HashMap<>();
        body.put("chat_id", telegramChatId);
        body.put("text", message);
        restTemplate.postForEntity(url, body, String.class);
    }

    @Override
    public void sendToTelegram(VagasDTO vagasDTO) {

        StringBuilder msg = new StringBuilder();

        msg.append("🚀 *Vagas Encontradas*\n\n");

        int i = 1;

        for (VagaDTO v : vagasDTO.vagaDTOs()) {

            msg.append("*").append(i++).append("\\. ").append(escape(v.cargo())).append("*\n");

            msg.append("🏢 ").append(escape(v.empresa())).append("\n");

            if (v.senioridade() != null && !v.senioridade().isEmpty()) {
                msg.append("🎯 ").append(escape(v.senioridade())).append("\n");
            }

            if (v.local() != null && !v.local().isEmpty()) {
                msg.append("📍 ").append(escape(v.local())).append("\n");
            }

            if (v.stack() != null && !v.stack().isEmpty()) {
                msg.append("🧠 ");
                msg.append(
                    v.stack().stream()
                        .map(this::escape)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")
                );
                msg.append("\n");
            }

            if (v.salario() != null && !v.salario().isEmpty()) {
                msg.append("💰 ").append(escape(v.salario())).append("\n");
            }

            if (v.link() != null && !v.link().isEmpty()) {
                msg.append("🔗 [Ver vaga](").append(v.link()).append(")\n");
            }

            msg.append("\n").append(escape("---------------------")).append("\n\n");
        }

        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", telegramChatId);
        body.put("text", msg.toString());
        body.put("parse_mode", "MarkdownV2");

        restTemplate.postForEntity(url, body, String.class);
    }

    /* UTILS */
    private String escape(String text) {
        if (text == null) return "";

        return text
            .replace("_", "\\_")
            .replace("*", "\\*")
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("~", "\\~")
            .replace("`", "\\`")
            .replace(">", "\\>")
            .replace("#", "\\#")
            .replace("+", "\\+")
            .replace("-", "\\-")
            .replace("=", "\\=")
            .replace("|", "\\|")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace(".", "\\.")
            .replace("!", "\\!");
    }
}
