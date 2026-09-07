package com.buysell.modules.device.repository;

import com.buysell.modules.device.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Device d WHERE d.id = :id")
    Optional<Device> findByIdWithLock(@Param("id") UUID id);

    @Query(value = "SELECT pg_advisory_xact_lock(hashtext(:imei))", nativeQuery = true)
    void acquireImeiLock(@Param("imei") String imei);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Device d WHERE d.imei1 = :imei OR d.imei2 = :imei")
    boolean existsByImeiCrossField(@Param("imei") String imei);
    
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Device d WHERE (d.imei1 = :imei OR d.imei2 = :imei) AND d.id != :id")
    boolean existsByImeiCrossFieldExcludeId(@Param("imei") String imei, @Param("id") UUID id);

    @Query("SELECT d FROM Device d WHERE d.imei1 = :imei OR d.imei2 = :imei")
    Optional<Device> findByImei(@Param("imei") String imei);
    
    @Query("SELECT d FROM Device d WHERE " +
           "(cast(:imei as text) IS NULL OR d.imei1 LIKE CONCAT('%', cast(:imei as text), '%') OR d.imei2 LIKE CONCAT('%', cast(:imei as text), '%')) AND " +
           "(cast(:brand as text) IS NULL OR LOWER(d.brand) LIKE LOWER(CONCAT('%', cast(:brand as text), '%'))) AND " +
           "(cast(:model as text) IS NULL OR LOWER(d.model) LIKE LOWER(CONCAT('%', cast(:model as text), '%')))")
    Page<Device> searchDevices(@Param("imei") String imei, 
                               @Param("brand") String brand, 
                               @Param("model") String model, 
                               Pageable pageable);
}
