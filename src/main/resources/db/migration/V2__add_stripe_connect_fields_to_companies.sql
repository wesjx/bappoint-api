ALTER TABLE companies
    ADD COLUMN payment_setup_status varchar(30) NOT NULL DEFAULT 'NOT_CONNECTED',
    ADD COLUMN stripe_connected_at timestamp with time zone,
    ADD COLUMN stripe_connection_error text;
