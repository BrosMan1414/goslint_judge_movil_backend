package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.impl;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ResumenMaratonResponse;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Retroalimentacion;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.ResultadoEnvio;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.enums.TipoRetro;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.EnvioRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.MaratonRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.ProblemaRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.repository.RetroalimentacionRepository;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.service.ResumenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumenServiceImpl implements ResumenService {

    private final EnvioRepository envioRepository;
    private final ProblemaRepository problemaRepository;
    private final RetroalimentacionRepository retroRepository;

        public ResumenServiceImpl(EnvioRepository envioRepository,
                                                          ProblemaRepository problemaRepository,
                                                          RetroalimentacionRepository retroRepository,
                                                          MaratonRepository maratonRepository) {
        this.envioRepository = envioRepository;
        this.problemaRepository = problemaRepository;
        this.retroRepository = retroRepository;
    }

    @Override
    public ResumenMaratonResponse obtenerResumen(Long equipoId, Long maratonId) {
        int totalProblemas = problemaRepository.findByMaraton_Id(maratonId).size();
        List<com.goslint_judge_movil_backend.goslint_judge_movil_backend.model.Envio> envios = envioRepository.findByEquipo_IdAndProblema_Maraton_Id(equipoId, maratonId);
        int totalEnvios = envios.size();
        long aceptados = envios.stream().filter(e -> e.getResultado() == ResultadoEnvio.Accepted).count();
        int intentosFallidos = (int) (totalEnvios - aceptados);
        double porcentaje = totalEnvios == 0 ? 0.0 : aceptados * 100.0 / totalEnvios;

        List<Retroalimentacion> feedbacks = retroRepository.findTop5ByEnvio_Equipo_IdAndEnvio_Problema_Maraton_IdAndTipoOrderByFechaDesc(
                equipoId, maratonId, TipoRetro.ia);

        List<ResumenMaratonResponse.FeedbackItem> items = feedbacks.stream().map(r -> ResumenMaratonResponse.FeedbackItem.builder()
                .envioId(r.getEnvio().getId())
                .problemaId(r.getEnvio().getProblema().getId())
                .problemaTitulo(r.getEnvio().getProblema().getTitulo())
                .comentario(r.getComentario())
                .build()).collect(Collectors.toList());

        return ResumenMaratonResponse.builder()
                .equipoId(equipoId)
                .maratonId(maratonId)
                .totalProblemas(totalProblemas)
                .totalEnvios(totalEnvios)
                .aceptados((int) aceptados)
                .porcentajeAceptados(porcentaje)
                .intentosFallidos(intentosFallidos)
                .recientesFeedback(items)
                .build();
    }
}
