package com.wesleysilva.bappoint.offday;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OffDaysRepository extends JpaRepository<OffDaysModel, UUID> {
    List<OffDaysModel> findBySettingsCompanyIdAndDate(UUID companyId, LocalDate date);
    List<OffDaysModel> findBySettingsCompanyId(UUID companyId);
}
