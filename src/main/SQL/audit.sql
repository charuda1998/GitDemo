-- MySQL dump 10.13  Distrib 8.0.21, for Linux (x86_64)
--
-- Host: localhost    Database: audit
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3bd74d13-34be-11eb-b96b-20677ce3b4b4:1-278177';

--
-- Table structure for table `AUDIT_TRANSACTIONS`
--

DROP TABLE IF EXISTS `AUDIT_TRANSACTIONS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AUDIT_TRANSACTIONS` (
  `MSG_ID` varchar(64) NOT NULL,
  `SVC_GROUP` varchar(64) DEFAULT NULL,
  `SVC_NAME` varchar(64) DEFAULT NULL,
  `SVC_VERSION` varchar(64) DEFAULT NULL,
  `INFOID` varchar(64) DEFAULT NULL,
  `INFO_MSG` varchar(300) DEFAULT NULL,
  `APP_ID` varchar(100) DEFAULT NULL,
  `USERNAME` varchar(64) DEFAULT NULL,
  `USER_TYPE` varchar(30) DEFAULT NULL,
  `API_TIME` varchar(10) DEFAULT NULL,
  `REQ_TIME` timestamp NULL DEFAULT NULL,
  `RES_TIME` timestamp NULL DEFAULT NULL,
  `SRC_IP` varchar(30) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`MSG_ID`),
  KEY `APP_ID` (`APP_ID`),
  KEY `REQ_TIME` (`REQ_TIME`),
  KEY `SVC_GROUP` (`SVC_GROUP`),
  KEY `SVC_NAME` (`SVC_NAME`),
  KEY `SVC_VERSION` (`SVC_VERSION`),
  KEY `INFOID` (`INFOID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'audit'
--

--
-- Dumping routines for database 'audit'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-28 18:56:35
