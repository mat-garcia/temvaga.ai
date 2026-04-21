package com.matgdev.temvaga.ai.service;

import com.matgdev.temvaga.ai.models.dto.VagasDTO;

public interface NotificationService {
    void sendToDiscord(String message);
    void sendToTelegram(String message);
    void sendToTelegram(VagasDTO response);
}

