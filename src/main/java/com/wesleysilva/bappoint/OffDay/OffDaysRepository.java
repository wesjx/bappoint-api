package com.wesleysilva.bappoint.OffDay;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OffDaysRepository extends JpaRepository<UUID, OffDayModel> {
}
