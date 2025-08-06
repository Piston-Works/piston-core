package org.pistonworks.core.api.service;

import org.pistonworks.core.api.logging.Logger;

public interface LoggingService {
    Logger getLogger(Class<?> clazz);
}

