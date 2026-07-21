package com.wesleysilva.bappoint.appointments.records;

import com.wesleysilva.bappoint.services.ServiceModel;

import java.util.List;

public record ServiceDetailsResult(
        List<ServiceModel>services,
        int totalDuration
) {}

