package com.medicalclinic.adapter.appointment;

import enums.Timeframe;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class AppointmentSpecifications {

    private AppointmentSpecifications() {
    }

    public static Specification<AppointmentEntity> hasPatient(Long patientId) {
        if (patientId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<AppointmentEntity> hasDoctor(Long doctorId) {
        if (doctorId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("doctor").get("id"), doctorId);
    }

    public static Specification<AppointmentEntity> hasSpecialization(String specialization) {
        if (specialization == null || specialization.isBlank()) {
            return null;
        }
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("doctor").get("medicalSpecialty")), specialization.toLowerCase());
    }

    public static Specification<AppointmentEntity> startsBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return null;
        }
        if (from == null) {
            return (root, query, cb) -> cb.lessThan(root.get("startDateTime"), to);
        }
        if (to == null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDateTime"), from);
        }
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startDateTime"), from),
                cb.lessThan(root.get("startDateTime"), to));
    }

    public static Specification<AppointmentEntity> inTimeframe(Timeframe timeframe) {
        if (timeframe == null || timeframe == Timeframe.ALL) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (timeframe == Timeframe.PAST) {
            return (root, query, cb) -> cb.lessThan(root.get("endDateTime"), now);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDateTime"), now);
    }

    public static Specification<AppointmentEntity> isFree() {
        return (root, query, cb) -> cb.isNull(root.get("patient"));
    }
}