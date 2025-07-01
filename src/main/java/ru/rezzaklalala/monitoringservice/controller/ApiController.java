package ru.rezzaklalala.monitoringservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rezzaklalala.monitoringservice.model.ApiDataEntity;
import ru.rezzaklalala.monitoringservice.repository.ApiDataRepository;
import ru.rezzaklalala.monitoringservice.service.ApiService;

import java.util.List;

@RestController

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiController {
    ApiDataRepository apiDataRepository;
    ApiService service;

    @Autowired
    public ApiController(ApiDataRepository apiDataRepository, ApiService service) {
        this.apiDataRepository = apiDataRepository;
        this.service = service;
    }

    @Operation(summary = "Проверка статуса сервиса", description = "Возвращает OK, если сервис работает")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сервис работает")
    })
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Service is running");
    }

    @Operation(summary = "Получить последние 10 записей", description = "Доступ только для ADMIN. Возвращает 10 последних записей из базы.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно возвращены данные")
    })
    @GetMapping("/data")
    public List<ApiDataEntity> getLatestData() {
        return apiDataRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Operation(summary = "Принудительный опрос API", description = "Осуществляет немедленный опрос API вручную")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен, данные опрошены")
    })
    @GetMapping("/fetch-now")
    public ResponseEntity<String> fetchNow() {
        service.fetchAndSaveData();
        return ResponseEntity.ok("Fetched");
    }
}
