-- MySQL dump 10.13  Distrib 8.0.21, for Linux (x86_64)
--
-- Host: localhost    Database: quote
-- ------------------------------------------------------
-- Server version       8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
Warning: A partial dump from a server that has GTIDs will by default include the GTIDs of all transactions, even those that changed suppressed parts of the database. If you don't want to restore GTIDs, pass --set-gtid-purged=OFF. To make a complete dump, pass --all-databases --triggers --routines --events.
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3bd74d13-34be-11eb-b96b-20677ce3b4b4:1-277870';

--
-- Table structure for table `BSECDS_QUOTE`
--

DROP TABLE IF EXISTS `BSECDS_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSECDS_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `LAST_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `AVG_PRICE` decimal(14,4) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,4) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,4) DEFAULT NULL,
  `52_WK_LOW` decimal(14,4) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BSECDS_QUOTE_HISTORY`
--

DROP TABLE IF EXISTS `BSECDS_QUOTE_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSECDS_QUOTE_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `LAST_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `AVG_PRICE` decimal(14,4) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,4) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,4) DEFAULT NULL,
  `52_WK_LOW` decimal(14,4) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BSE_QUOTE`
--

DROP TABLE IF EXISTS `BSE_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSE_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCX_QUOTE`
--

DROP TABLE IF EXISTS `MCX_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MCX_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCX_QUOTE_HISTORY`
--

DROP TABLE IF EXISTS `MCX_QUOTE_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MCX_QUOTE_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NCDEX_QUOTE`
--

DROP TABLE IF EXISTS `NCDEX_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NCDEX_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NCDEX_QUOTE_HISTORY`
--

DROP TABLE IF EXISTS `NCDEX_QUOTE_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NCDEX_QUOTE_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NFO_QUOTE`
--

DROP TABLE IF EXISTS `NFO_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NFO_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NFO_QUOTE_HISTORY`
--

DROP TABLE IF EXISTS `NFO_QUOTE_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NFO_QUOTE_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSECDS_QUOTE`
--

DROP TABLE IF EXISTS `NSECDS_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSECDS_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `LAST_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `AVG_PRICE` decimal(14,4) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,4) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,4) DEFAULT NULL,
  `52_WK_LOW` decimal(14,4) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSECDS_QUOTE_HISTORY`
--

DROP TABLE IF EXISTS `NSECDS_QUOTE_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSECDS_QUOTE_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `EXPIRY_DATE` date DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `LAST_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `AVG_PRICE` decimal(14,4) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,4) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,4) DEFAULT NULL,
  `52_WK_LOW` decimal(14,4) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,4) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSE_QUOTE`
--

DROP TABLE IF EXISTS `NSE_QUOTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSE_QUOTE` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `SYMBOL_NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `SYMBOL_UNIQ_DESC` varchar(50) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `LAST_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `AVG_PRICE` decimal(14,2) DEFAULT NULL,
  `TOTAL_TRADED_VALUE` decimal(20,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `CHANGE` decimal(14,2) DEFAULT NULL,
  `CHANGE_PER` decimal(7,2) DEFAULT NULL,
  `52_WK_HIGH` decimal(14,2) DEFAULT NULL,
  `52_WK_LOW` decimal(14,2) DEFAULT NULL,
  `TOTAL_BUY_QTY` bigint DEFAULT NULL,
  `TOTAL_SELL_QTY` bigint DEFAULT NULL,
  `LAST_TRADED_TIME` timestamp NULL DEFAULT NULL,
  `OPENINTEREST` bigint DEFAULT NULL,
  `LAST_TRADED_VOLUME` bigint DEFAULT NULL,
  `TOP5BIDPRICE` varchar(255) DEFAULT NULL,
  `TOP5ASKPRICE` varchar(255) DEFAULT NULL,
  `TOP5BIDVALUE` varchar(255) DEFAULT NULL,
  `TOP5ASKVALUE` varchar(255) DEFAULT NULL,
  `TOP5BIDNUMBER` varchar(255) DEFAULT NULL,
  `TOP5ASKNUMBER` varchar(255) DEFAULT NULL,
  `PRE_CLOSE_PRICE` varchar(255) DEFAULT NULL,
  `ISIN` varchar(64) NOT NULL,
  `UPPER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LOWER_CIR_LIMIT` decimal(14,2) DEFAULT NULL,
  `LAST_FEED_TIME` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`),
  KEY `isin_id` (`ISIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'quote'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;
/*!50106 DROP EVENT IF EXISTS `DAILY_MAINTENANCE` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8mb4 */ ;;
/*!50003 SET character_set_results = utf8mb4 */ ;;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = '' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`platformwrite`@`localhost`*/ /*!50106 EVENT `DAILY_MAINTENANCE` ON SCHEDULE EVERY 1 DAY STARTS '2020-11-20 00:00:00' ON COMPLETION NOT PRESERVE ENABLE COMMENT 'It will backup all the expiry quote data in the database and deletes records that are older than 60 days' DO BEGIN
call move_expiry_quote_nfo();
call move_expiry_quote_mcx();
call move_expiry_quote_ncdex();
call move_expiry_quote_nsecds();
call move_expiry_quote_bsecds();
call delete_60_days_older_records();
END */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;

--
-- Dumping routines for database 'quote'
--
/*!50003 DROP PROCEDURE IF EXISTS `delete_60_days_older_records` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`platformwrite`@`localhost` PROCEDURE `delete_60_days_older_records`()
BEGIN
delete from quote.NFO_QUOTE_HISTORY where DATE(UPDATED_AT) < CURDATE()-INTERVAL 60 DAY;
delete from quote.MCX_QUOTE_HISTORY where DATE(UPDATED_AT) < CURDATE()-INTERVAL 60 DAY;
delete from quote.NCDEX_QUOTE_HISTORY where DATE(UPDATED_AT) < CURDATE()-INTERVAL 60 DAY;
delete from quote.NSECDS_QUOTE_HISTORY where DATE(UPDATED_AT) < CURDATE()-INTERVAL 60 DAY;
delete from quote.BSECDS_QUOTE_HISTORY where DATE(UPDATED_AT) < CURDATE()-INTERVAL 60 DAY;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_expiry_quote_bsecds` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_expiry_quote_bsecds`()
BEGIN
INSERT IGNORE INTO quote.BSECDS_QUOTE_HISTORY SELECT * FROM BSECDS_QUOTE WHERE EXPIRY_DATE < NOW();
delete from quote.BSECDS_QUOTE WHERE EXPIRY_DATE < NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_expiry_quote_mcx` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_expiry_quote_mcx`()
BEGIN
INSERT IGNORE INTO quote.MCX_QUOTE_HISTORY SELECT * FROM MCX_QUOTE WHERE EXPIRY_DATE < NOW();
delete from quote.MCX_QUOTE WHERE EXPIRY_DATE < NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_expiry_quote_ncdex` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_expiry_quote_ncdex`()
BEGIN
INSERT IGNORE INTO quote.NCDEX_QUOTE_HISTORY SELECT * FROM NCDEX_QUOTE WHERE EXPIRY_DATE < NOW();
delete from quote.NCDEX_QUOTE WHERE EXPIRY_DATE < NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_expiry_quote_nfo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_expiry_quote_nfo`()
BEGIN
INSERT IGNORE INTO quote.NFO_QUOTE_HISTORY SELECT * FROM NFO_QUOTE WHERE EXPIRY_DATE < NOW();
delete from quote.NFO_QUOTE WHERE EXPIRY_DATE < NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_expiry_quote_nsecds` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_expiry_quote_nsecds`()
BEGIN
INSERT IGNORE INTO quote.NSECDS_QUOTE_HISTORY SELECT * FROM NSECDS_QUOTE WHERE EXPIRY_DATE < NOW();
delete from quote.NSECDS_QUOTE WHERE EXPIRY_DATE < NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-28 18:52:42
