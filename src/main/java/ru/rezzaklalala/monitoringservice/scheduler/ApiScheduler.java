package ru.rezzaklalala.monitoringservice.scheduler;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.rezzaklalala.monitoringservice.service.ApiService;



@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiScheduler {


    ApiService apiService;

    @Autowired
    public ApiScheduler(ApiService apiService) {
        this.apiService = apiService;
    }

    Logger log = LoggerFactory.getLogger(ApiScheduler.class);

    @Scheduled(fixedRate = 60000)
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void fetchApiData() {
        apiService.fetchAndSaveData();
    }

    @Recover
    public void recover(Exception e) {
        log.error("All attempts failed: {}", e.getMessage());
    }
}
