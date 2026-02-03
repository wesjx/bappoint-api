package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.SlotTimes.SlotTimesDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("company/{companyId}/appointments")
public class AppointmentController {

    AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/available-times")
    public List<SlotTimesDTO> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date) {
        return appointmentService.findAvailableSlots(companyId, date);
    }
}
