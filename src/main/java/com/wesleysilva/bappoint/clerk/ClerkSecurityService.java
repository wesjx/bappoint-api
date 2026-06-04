package com.wesleysilva.bappoint.clerk;

import com.wesleysilva.bappoint.appointments.AppointmentRepository;
import com.wesleysilva.bappoint.company.CompanyRepository;
import com.wesleysilva.bappoint.offday.OffDaysRepository;
import com.wesleysilva.bappoint.operatinghours.OperatingHoursRepository;
import com.wesleysilva.bappoint.services.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("clerkSecurityService")
public class ClerkSecurityService {
    private final CompanyRepository companyRepository;
    private final ClerkAuthContext clerkAuthContext;
    private final ServiceRepository serviceRepository;
    private final OffDaysRepository offDayRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final AppointmentRepository appointmentRepository;

    public ClerkSecurityService(CompanyRepository companyRepository, ClerkAuthContext clerkAuthContext, ServiceRepository serviceRepository, OffDaysRepository offDayRepository, OperatingHoursRepository operatingHoursRepository, AppointmentRepository appointmentRepository) {
        this.companyRepository = companyRepository;
        this.clerkAuthContext = clerkAuthContext;
        this.serviceRepository = serviceRepository;
        this.offDayRepository = offDayRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public boolean isCompanyOwner(UUID companyId) {
        if (clerkAuthContext.isMaster()) return true;
        return companyRepository.findById(companyId)
                .map(c -> c.getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }

    public boolean isServiceOwner(UUID serviceId) {
        if (clerkAuthContext.isMaster()) return true;
        return serviceRepository.findById(serviceId)
                .map(s -> s.getSettings().getCompany().getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }

    public boolean isOffDayOwner(UUID offDayId) {
        if (clerkAuthContext.isMaster()) return true;
        return offDayRepository.findById(offDayId)
                .map(o -> o.getSettings().getCompany().getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }

    public boolean isOperatingHoursOwner(UUID operatingHoursId) {
        if (clerkAuthContext.isMaster()) return true;
        return operatingHoursRepository.findById(operatingHoursId)
                .map(o -> o.getSettings().getCompany().getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }

    public boolean isAppointmentOwner(UUID appointmentId) {
        if (clerkAuthContext.isMaster()) return true;
        return appointmentRepository.findById(appointmentId)
                .map(a -> a.getCompany().getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }
}