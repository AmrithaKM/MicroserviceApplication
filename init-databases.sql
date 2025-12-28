-- Initialize databases for microservices
-- PostgreSQL databases (Product uses MongoDB)
DROP DATABASE IF EXISTS inventory_service;
DROP DATABASE IF EXISTS order_service;

CREATE DATABASE inventory_service;
CREATE DATABASE order_service;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE inventory_service TO postgres;
GRANT ALL PRIVILEGES ON DATABASE order_service TO postgres;

