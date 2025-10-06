package com.goslint_judge_movil_backend.goslint_judge_movil_backend.service;

import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.request.ProblemaRequest;
import com.goslint_judge_movil_backend.goslint_judge_movil_backend.dto.response.ProblemaResponse;
import java.util.List;

public interface ProblemaService {
    ProblemaResponse create(ProblemaRequest r);
    List<ProblemaResponse> findAll();
    ProblemaResponse findById(Long id);
    ProblemaResponse update(Long id, ProblemaRequest r);
    void delete(Long id);
    List<ProblemaResponse> findByMaraton(Long maratonId);
}
