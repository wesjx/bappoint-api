package com.wesleysilva.bappoint.appointments;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class AppointmentSpecifications {

    private AppointmentSpecifications() {
    }

    public static Specification<AppointmentModel> belongsToCompany(UUID companyId) {
        return (root, query, cb) ->
                cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<AppointmentModel> searchByCustomer(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("costumerName")), like),
                    cb.like(cb.lower(root.get("costumerEmail")), like),
                    cb.like(cb.lower(root.get("costumerPhone")), like)
            );
        };
    }

    public static Specification<AppointmentModel> hasStatus(AppointmentStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("appointmentStatus"), status);
        };
    }
}
