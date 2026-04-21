package com.matgdev.temvaga.ai.models.dto;


import java.util.List;

public record VagasDTO(List<VagaDTO> vagaDTOs) {

    public record VagaDTO(
        String cargo,
        String empresa,
        List<String> stack,
        String descricao,
        String salario,
        String local,
        String senioridade,
        String link
    ) {}
}