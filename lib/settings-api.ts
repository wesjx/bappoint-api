const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

async function apiFetch(path: string, options: RequestInit) {
  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
  });

  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || `Request failed: ${res.status}`);
  }

  if (res.status === 204) return null;
  return res.json();
}

// Company

export interface UpdateCompanyPayload {
  name: string;
  email: string;
  phone: string;
  address: string;
}

export function updateCompany(companyId: string,  UpdateCompanyPayload) {
  return apiFetch(`/companies/update/${companyId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

// Settings

export interface UpdateSettingsPayload {
  appointmentInterval: string;
  maxCancellationInterval: number;
}

export function updateSettings(companyId: string,  UpdateSettingsPayload) {
  return apiFetch(`/companies/${companyId}/settings/update`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

// Off Days

export interface OffDayPayload {
  date: string;
  reason: string;
  offDaysType: string;
}

export function createOffDay(companyId: string,  OffDayPayload) {
  return apiFetch(`/companies/${companyId}/settings/off_days/create`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateOffDay(companyId: string, offDayId: string,  OffDayPayload) {
  return apiFetch(`/companies/${companyId}/settings/off_days/update/${offDayId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteOffDay(companyId: string, offDayId: string) {
  return apiFetch(`/companies/${companyId}/settings/off_days/delete/${offDayId}`, {
    method: "DELETE",
  });
}

// Operating Hours

export interface OperatingHoursPayload {
  weekday: string;
  isActive: boolean;
  startTime: string;
  endTime: string;
  lunchStartTime: string | null;
  lunchEndTime: string | null;
}

export function createOperatingHours(companyId: string,  OperatingHoursPayload) {
  return apiFetch(`/companies/${companyId}/settings/operating_hours/create`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateOperatingHours(
  companyId: string,
  operatingHoursId: string,
   OperatingHoursPayload
) {
  return apiFetch(`/companies/${companyId}/settings/operating_hours/update/${operatingHoursId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteOperatingHours(companyId: string, operatingHoursId: string) {
  return apiFetch(`/companies/${companyId}/settings/operating_hours/${operatingHoursId}`, {
    method: "DELETE",
  });
}

// Services

export interface ServicePayload {
  name: string;
  description?: string;
  durationInMinutes: number;
  price: number;
  isActive: boolean;
}

export function createService(companyId: string,  ServicePayload) {
  return apiFetch(`/companies/${companyId}/services/create`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function updateService(companyId: string, serviceId: string,  ServicePayload) {
  return apiFetch(`/companies/${companyId}/services/update/${serviceId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export function deleteService(companyId: string, serviceId: string) {
  return apiFetch(`/companies/${companyId}/services/delete/${serviceId}`, {
    method: "DELETE",
  });
}
