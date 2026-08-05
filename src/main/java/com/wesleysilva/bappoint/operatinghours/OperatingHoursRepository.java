package com.wesleysilva.bappoint.operatinghours;

import com.wesleysilva.bappoint.settings.SettingsModel;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperatingHoursRepository extends JpaRepository<OperatingHoursModel, UUID> {
    List<OperatingHoursModel> findByWeekday(WeekDay weekday);

    Optional<OperatingHoursModel> findBySettingsAndWeekday(SettingsModel settings, WeekDay weekday);

    List<OperatingHoursModel> findByWeekdayAndSettingsId(WeekDay weekday, UUID settingsId);

    Optional<OperatingHoursModel> findBySettingsCompanyIdAndWeekday(UUID companyId, WeekDay weekday);
}
