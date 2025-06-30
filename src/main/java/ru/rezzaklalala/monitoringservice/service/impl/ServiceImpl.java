package ru.rezzaklalala.monitoringservice.service.impl;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.rezzaklalala.monitoringservice.kafka.KafkaProducer;
import ru.rezzaklalala.monitoringservice.model.ApiDataEntity;
import ru.rezzaklalala.monitoringservice.repository.ApiDataRepository;
import ru.rezzaklalala.monitoringservice.service.ApiService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceImpl implements ApiService {

    RestTemplate restTemplate;
    ApiDataRepository apiDataRepository;
    KafkaProducer kafkaProducer;

    @Autowired
    public ServiceImpl(ApiDataRepository apiDataRepository, RestTemplate restTemplate, KafkaProducer kafkaProducer) {
        this.apiDataRepository = apiDataRepository;
        this.restTemplate = restTemplate;
        this.kafkaProducer = kafkaProducer;
    }

    @Scheduled(fixedRate = 60000)
    public void fetchAndSaveData(){
        var url = "https://vk.com/";
        var entity = new ApiDataEntity();

        try{
            var response = restTemplate.getForObject(url, String.class);
            entity.setSuccess(true);
            entity.setPayload(response);
            kafkaProducer.send("api-data", response);
        } catch (Exception e) {
            entity.setSuccess(false);
            entity.setPayload(e.getMessage());
            kafkaProducer.send("api-errors", e.getMessage());
        }

        apiDataRepository.save(entity);
    }
}
