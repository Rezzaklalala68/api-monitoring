package ru.rezzaklalala.monitoringservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rezzaklalala.monitoringservice.model.ApiDataEntity;
import ru.rezzaklalala.monitoringservice.model.ApiDataEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiDataRepository extends JpaRepository<ApiDataEntity, UUID> {
    List<ApiDataEntity> findTop10ByOrderByCreatedAtDesc();
}
