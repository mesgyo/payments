CREATE DATABASE  IF NOT EXISTS payments_db;
USE payments_db;

DROP TABLE IF EXISTS payment;

CREATE TABLE payment (
  id integer NOT NULL AUTO_INCREMENT,
  UserID varchar(255) DEFAULT NULL,
  UnixTimestamp long DEFAULT NULL,
  Country varchar(255) DEFAULT NULL,
  Currency varchar(255) DEFAULT NULL,
  AmountInCents integer DEFAULT NULL,
  AmountInEuro double DEFAULT NULL,
  PRIMARY KEY (`id`)
);