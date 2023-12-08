package org.sdle.service;

import org.sdle.repository.ICRDTRepository;

public interface ICRDTExecutionService<T> {
    void run(ICRDTRepository<T> repository) throws InterruptedException;
}
