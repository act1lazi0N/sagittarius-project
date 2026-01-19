CREATE DATABASE order_service_db;
CREATE DATABASE inventory_service_db;
CREATE DATABASE payment_service_db;

GRANT ALL PRIVILEGES ON DATABASE order_service_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE inventory_service_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE payment_service_db TO admin;