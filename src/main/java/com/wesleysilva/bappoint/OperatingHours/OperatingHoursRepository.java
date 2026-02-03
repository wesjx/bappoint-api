package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OperatingHoursRepository extends JpaRepository<OperatingHoursModel, UUID> {
    List<OperatingHoursModel> findByWeekday(WeekDay weekday);
}
