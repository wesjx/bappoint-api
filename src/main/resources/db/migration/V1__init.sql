CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE settings (
                          id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          appointment_interval integer NOT NULL,
                          max_cancellation_interval integer NOT NULL
);

CREATE TABLE companies (
                           company_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                           slug varchar(255) UNIQUE,
                           stripe_account_id varchar(255),
                           clerk_user_id varchar(255),
                           name varchar(255),
                           email varchar(255),
                           phone varchar(255),
                           address varchar(255),
                           deposit_percentage numeric(5,2),
                           settings_id uuid UNIQUE,
                           CONSTRAINT fk_companies_settings
                               FOREIGN KEY (settings_id) REFERENCES settings(id)
);

CREATE TABLE services (
                          id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          settings_id uuid,
                          name varchar(255) NOT NULL,
                          price numeric(10,2) NOT NULL,
                          duration_minutes integer NOT NULL,
                          is_active boolean NOT NULL,
                          CONSTRAINT fk_services_settings
                              FOREIGN KEY (settings_id) REFERENCES settings(id) ON DELETE CASCADE
);

CREATE TABLE operating_hours (
                                 id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                 is_active boolean NOT NULL DEFAULT true,
                                 start_time time NOT NULL,
                                 end_time time NOT NULL,
                                 lunch_start_time time,
                                 lunch_end_time time,
                                 settings_id uuid,
                                 weekday varchar(20) NOT NULL,
                                 CONSTRAINT fk_operating_hours_settings
                                     FOREIGN KEY (settings_id) REFERENCES settings(id) ON DELETE CASCADE,
                                 CONSTRAINT uq_operating_hours_settings_weekday
                                     UNIQUE (settings_id, weekday)
);

CREATE TABLE off_days (
                          off_days_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                          settings_id uuid NOT NULL,
                          reason varchar(255),
                          date date NOT NULL,
                          off_days_type varchar(30),
                          CONSTRAINT fk_off_days_settings
                              FOREIGN KEY (settings_id) REFERENCES settings(id) ON DELETE CASCADE
);

CREATE TABLE appointments (
                              appointment_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                              company_id uuid NOT NULL,
                              costumer_name varchar(100) NOT NULL,
                              costumer_email varchar(150) NOT NULL,
                              costumer_phone varchar(30) NOT NULL,
                              appointment_date date NOT NULL,
                              start_time timestamp NOT NULL,
                              end_time timestamp NOT NULL,
                              total_amount numeric(10,2) NOT NULL,
                              stripe_session_id varchar(255),
                              appointment_status varchar(30) NOT NULL,
                              created_at timestamp,
                              payment_deadline timestamp,
                              slot_key varchar(200) NOT NULL UNIQUE,
                              CONSTRAINT fk_appointments_company
                                  FOREIGN KEY (company_id) REFERENCES companies(company_id)
);

CREATE TABLE appointment_services (
                                      appointment_id uuid NOT NULL,
                                      service_id uuid NOT NULL,
                                      PRIMARY KEY (appointment_id, service_id),
                                      CONSTRAINT fk_appointment_services_appointment
                                          FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE,
                                      CONSTRAINT fk_appointment_services_service
                                          FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE INDEX idx_services_settings_id ON services(settings_id);
CREATE INDEX idx_operating_hours_settings_id ON operating_hours(settings_id);
CREATE INDEX idx_off_days_settings_id ON off_days(settings_id);
CREATE INDEX idx_appointments_company_id ON appointments(company_id);
CREATE INDEX idx_appointments_appointment_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(appointment_status);
CREATE INDEX idx_appointments_stripe_session_id ON appointments(stripe_session_id);
CREATE INDEX idx_appointment_services_service_id ON appointment_services(service_id);
