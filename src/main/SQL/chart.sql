-- MySQL dump 10.13  Distrib 8.0.21, for Linux (x86_64)
--
-- Host: localhost    Database: chart
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3bd74d13-34be-11eb-b96b-20677ce3b4b4:1-278050';

--
-- Table structure for table `BSECDS_CHART`
--

DROP TABLE IF EXISTS `BSECDS_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSECDS_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,4) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,4) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BSECDS_CHART_HISTORY`
--

DROP TABLE IF EXISTS `BSECDS_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSECDS_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,4) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,4) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BSE_CHART`
--

DROP TABLE IF EXISTS `BSE_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSE_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BSE_CHART_HISTORY`
--

DROP TABLE IF EXISTS `BSE_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BSE_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCX_CHART`
--

DROP TABLE IF EXISTS `MCX_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MCX_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCX_CHART_HISTORY`
--

DROP TABLE IF EXISTS `MCX_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MCX_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NCDEX_CHART`
--

DROP TABLE IF EXISTS `NCDEX_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NCDEX_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NCDEX_CHART_HISTORY`
--

DROP TABLE IF EXISTS `NCDEX_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NCDEX_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NFO_CHART`
--

DROP TABLE IF EXISTS `NFO_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NFO_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NFO_CHART_HISTORY`
--

DROP TABLE IF EXISTS `NFO_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NFO_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSECDS_CHART`
--

DROP TABLE IF EXISTS `NSECDS_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSECDS_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,4) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,4) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSECDS_CHART_HISTORY`
--

DROP TABLE IF EXISTS `NSECDS_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSECDS_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,4) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,4) DEFAULT NULL,
  `LOW_PRICE` decimal(14,4) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,4) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,4) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,4) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSE_CHART`
--

DROP TABLE IF EXISTS `NSE_CHART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSE_CHART` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NSE_CHART_HISTORY`
--

DROP TABLE IF EXISTS `NSE_CHART_HISTORY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NSE_CHART_HISTORY` (
  `SYMBOL` varchar(255) NOT NULL DEFAULT '',
  `OPEN_PRICE` decimal(14,2) DEFAULT NULL,
  `HIGH_PRICE` decimal(14,2) DEFAULT NULL,
  `LOW_PRICE` decimal(14,2) DEFAULT NULL,
  `CLOSE_PRICE` decimal(14,2) DEFAULT NULL,
  `VOLUME` bigint DEFAULT NULL,
  `TIME` datetime NOT NULL,
  `TIME_UTC` datetime DEFAULT NULL,
  `VWAP` decimal(14,2) DEFAULT NULL,
  `TOTAL_VOLUME` bigint DEFAULT NULL,
  `DAY_VWAP` decimal(14,2) DEFAULT NULL,
  PRIMARY KEY (`SYMBOL`,`TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'chart'
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
/*!50106 CREATE*/ /*!50117 DEFINER=`platformwrite`@`localhost`*/ /*!50106 EVENT `DAILY_MAINTENANCE` ON SCHEDULE EVERY 1 DAY STARTS '2020-11-20 00:00:00' ON COMPLETION NOT PRESERVE ENABLE COMMENT 'It will backup all the old minute chart data in the database and deletes records that are older than 60 days' DO BEGIN
call move_intraday_chart_nse();
call move_intraday_chart_bse();
call move_intraday_chart_nfo();
call move_intraday_chart_mcx();
call move_intraday_chart_ncdex();
call move_intraday_chart_nsecds();
call move_intraday_chart_bsecds();
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
-- Dumping routines for database 'chart'
--
/*!50003 DROP PROCEDURE IF EXISTS `chart_data` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `chart_data`(IN marketSegID INT, IN symbol_name VARCHAR(255), IN start_time datetime, IN end_time datetime)
BEGIN

IF marketSegID = 1 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM NSE_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 2 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM NFO_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 3 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM BSE_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 5 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM MCX_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 7 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM NCDEX_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 13 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM NSECDS_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
ELSEIF marketSegID = 38 THEN
SELECT OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME FROM BSECDS_CHART WHERE SYMBOL = symbol_name AND TIME >= start_time AND TIME <= end_time;
END IF;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
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
delete from chart.NSE_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.BSE_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.NFO_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.MCX_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.NCDEX_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.NSECDS_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
delete from chart.BSECDS_CHART_HISTORY where DATE(TIME) < CURDATE()-INTERVAL 60 DAY;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_bse` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_bse`()
BEGIN
insert into chart.BSE_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.BSE_CHART);
delete from chart.BSE_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_bsecds` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_bsecds`()
BEGIN
insert into chart.BSECDS_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.BSECDS_CHART);
delete from chart.BSECDS_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_mcx` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_mcx`()
BEGIN
insert into chart.MCX_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.MCX_CHART);
delete from chart.MCX_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_ncdex` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_ncdex`()
BEGIN
insert into chart.NCDEX_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.NCDEX_CHART);
delete from chart.NCDEX_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_nfo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_nfo`()
BEGIN
insert into chart.NFO_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.NFO_CHART);
delete from chart.NFO_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_nse` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_nse`()
BEGIN
insert into chart.NSE_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.NSE_CHART);
delete from chart.NSE_CHART;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_intraday_chart_nsecds` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `move_intraday_chart_nsecds`()
BEGIN
insert into chart.NSECDS_CHART_HISTORY( SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP) (select SYMBOL, OPEN_PRICE, HIGH_PRICE, LOW_PRICE, CLOSE_PRICE, VOLUME, TIME,
TIME_UTC, VWAP, TOTAL_VOLUME, DAY_VWAP from chart.NSECDS_CHART);
delete from chart.NSECDS_CHART;
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

-- Dump completed on 2020-12-28 18:55:02
