package com.matgdev.temvaga.ai.agent;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.matgdev.temvaga.ai.models.dto.VagasDTO;
import com.matgdev.temvaga.ai.models.dto.VagasDTO.VagaDTO;
import com.matgdev.temvaga.ai.service.AdzunaService;
import com.matgdev.temvaga.ai.service.NotificationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api")
public class MainAgent {

    private final ChatClient.Builder ai;
    
    private final NotificationService notificationService;

    private final AdzunaService adzunaService;

    @Autowired
    public MainAgent(ChatClient.Builder ai , NotificationService notificationService , AdzunaService adzunaService) {
        this.ai = ai;
        this.notificationService = notificationService;
        this.adzunaService = adzunaService;
    }


    @GetMapping("/chat/health")
    public ResponseEntity<Object> health() {

        ChatClient chat = ai.build();
        return ResponseEntity.ok(chat.prompt("Olá").call().content());
    }

    @GetMapping("/telegram/test")
    public ResponseEntity<Object> telegramTest() {
        notificationService.sendToTelegram("olá, isso é apenas uma msg de teste!");
        return ResponseEntity.ok("Msg Telegram enviada!");
    }

    @GetMapping("/discord/test")
    public ResponseEntity<Object> discordTest() {
        notificationService.sendToDiscord("olá, isso é apenas uma msg de teste!");
        return ResponseEntity.ok("Msg discord enviada!");
    }

    @GetMapping("/adzuna/test")
    public List<VagaDTO> getMethodName() {
        return adzunaService.buscarVagas();
    }
    
    


    @Scheduled(fixedRate = 14400000) // 4 h
    public void temvagaAgent(){

        ChatClient agent = ai.build();

        String jsonVagas = adzunaService.buscarVagas().toString();

        
        String perfilUsuario = """
                        - area: backend ou fullstack
                        - stack: java, springboot, php, nodejs, react
                        - senioridade: pleno, senior
                        - modelo de trabalho: remoto ou híbrido
                        - penalizar vagas com: estagio, trainee, junior, presencial 
                        """;

        Map<String, Object> model = Map.of(
            "perfil", perfilUsuario, 
            "vagas", jsonVagas
        );
                                

        VagasDTO response = agent.prompt()
                    .system("""
                        Você é um agente de triagem de vagas tech.

                        Sua tarefa é:
                        - extrair cargo, stack, senioridade, idioma, modelo de trabalho e localização
                        - comparar com o perfil do usuário:

                        Após análise:
                        - atribuir score de 0 a 10
                        - retornar JSON com vagas score >= 6

                        Regras:
                        - priorize stack, senioridade e modelo
                        - penalize palavras de descarte
                        - se a vaga estiver incompleta, reduza a confiança
                        - não invente dados

                        Retorne apenas JSON válido com vagas score >= 6
                    """)
                    .user(u -> u.text("""
                        Aqui esta o perfil do usuario:
                        {perfil}

                        Aqui está o JSON de vagas:
                        {vagas}
                        """).params(model))
                    .call()
                    .entity(VagasDTO.class);

        notificationService.sendToTelegram(response);

    }

}
