CREATE USER zephyr_admin WITH PASSWORD 'zephyr_pwd';
CREATE DATABASE zephyr_db OWNER zephyr_admin;
-- connect as user zephyr_admin to ensure the access right of the created table
\c zephyr_db zephyr_admin
DROP TABLE IF EXISTS ORDER_LIST;
CREATE TABLE ORDER_LIST (
    ID SERIAL PRIMARY KEY,
    PRODUCT_NAME VARCHAR(20),
    SIZE VARCHAR(2),
    QUANTITY INTEGER,
    PRICE INTEGER,
    SUB_TOTAL INTEGER,
    STATUS INTEGER,
    ORDER_ID VARCHAR (20)
);