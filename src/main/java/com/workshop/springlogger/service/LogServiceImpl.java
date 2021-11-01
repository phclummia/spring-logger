package com.workshop.springlogger.service;

import com.workshop.springlogger.data.model.LogEntity;
import com.workshop.springlogger.data.repository.LogEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogEntityRepository repository;

    @Override
    public void log(LogEntity logEntity) {
        repository.saveAndFlush(logEntity);
    }
}
