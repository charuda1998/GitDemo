-- MySQL dump 10.13  Distrib 8.0.21, for Linux (x86_64)
--
-- Host: localhost    Database: globecapital
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3bd74d13-34be-11eb-b96b-20677ce3b4b4:1-277461';

--
-- Table structure for table `ADVANCE_LOGIN`
--

DROP TABLE IF EXISTS `ADVANCE_LOGIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ADVANCE_LOGIN` (
  `USER_ID` varchar(10) NOT NULL,
  `MPIN` varchar(128) DEFAULT NULL,
  `MPIN_ENABLED` char(1) DEFAULT NULL,
  `FINGERPRINT_ENABLED` char(1) DEFAULT NULL,
  `MPIN_ACTIVE` char(1) DEFAULT NULL,
  `FINGERPRINT_ACTIVE` char(1) DEFAULT NULL,
  `MPIN_FAILURE_COUNT` int DEFAULT '0',
  `APP_ID` varchar(64) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `NOTIFICATION_ACTIVE` char(1) DEFAULT 'Y',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AMO_DETAILS`
--

DROP TABLE IF EXISTS `AMO_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AMO_DETAILS` (
  `EXCHANGE` varchar(10) NOT NULL DEFAULT '',
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `DATE` date NOT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  PRIMARY KEY (`EXCHANGE`,`DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APP_CONFIG`
--

DROP TABLE IF EXISTS `APP_CONFIG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `APP_CONFIG` (
  `CODE` varchar(20) NOT NULL,
  `VALUE` varchar(1000) DEFAULT NULL,
  `ACTION_CODE` varchar(20) NOT NULL,
  `STATUS` varchar(20) NOT NULL,
  `IS_DELETED` char(1) DEFAULT NULL,
  `FORM_FACTOR` varchar(10) DEFAULT NULL,
  `ID` int NOT NULL AUTO_INCREMENT,
  `CREATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `BUILD_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `APP_CONFIG`
--

LOCK TABLES `APP_CONFIG` WRITE;
/*!40000 ALTER TABLE `APP_CONFIG` DISABLE KEYS */;
INSERT INTO `APP_CONFIG` VALUES ('OpenAnAccountLink','https://myglobe.globextranet.com/CLIENTONBOARDing/INDEX.HTML','1','ACT',NULL,NULL,1,'2019-09-13 17:00:48','hybrid-phone'),('MaxWatchlistCount','5','1','ACT',NULL,NULL,2,'2019-10-16 19:44:52','hybrid-phone'),('MaxWatchlistSymCount','50','1','ACT',NULL,NULL,3,'2019-10-16 19:47:02','hybrid-phone'),('TermsAndConditions','https://www.globecapital.com/TermAndCon','1','ACT',NULL,NULL,4,'2019-11-15 16:11:06','hybrid-phone'),('TCPReconnectInterval','1000','1','ACT',NULL,NULL,5,'2019-12-03 10:14:51','hybrid-phone'),('HttpReqTimeout','30000','1','ACT',NULL,NULL,6,'2019-12-16 09:39:13','hybrid-phone'),('PositnUpdateInterval','3000','1','ACT',NULL,NULL,7,'2020-01-06 08:09:04','hybrid-phone'),('MaxMPINAttempt','5','1','ACT',NULL,NULL,8,'2020-01-20 05:43:43','hybrid-phone'),('EquityAndFNO','380','1','ACT',NULL,NULL,9,'2020-03-13 08:58:16','hybrid-phone'),('Currency','480','1','ACT',NULL,NULL,10,'2020-03-13 08:58:34','hybrid-phone'),('Commodity','810','1','ACT',NULL,NULL,11,'2020-03-13 08:58:57','hybrid-phone'),('HelpAndSupportLink','https://globe-plans.web.app/about-app.html','1','ACT',NULL,NULL,12,'2020-07-21 08:12:36','hybrid-phone'),('EqToolTipMsg','Total charges may include SEBI Turnover Fees, Stamp Duty, Security Transaction Tax, Exchange transaction Charges, GST and Other Charges','1','ACT',NULL,NULL,13,'2020-11-25 04:45:52','hybrid-phone'),('ComdtyToolTipMsg','Total charges may include SEBI Turnover Fees, Stamp Duty, Commodity Transaction Tax, Exchange transaction Charges, GST and Other Charges','1','ACT',NULL,NULL,14,'2020-11-25 04:45:39','hybrid-phone'),('TCPConnectTimeout','10000','1','ACT',NULL,NULL,15,'2020-12-15 15:05:56','hybrid-phone'),('WFHlinks','125.16.98.219,180.179.195.18,223.31.33.108','1','ACT',NULL,NULL,16,'2020-12-15 15:05:56','hybrid-phone'),('WFHprt','4507','1','ACT',NULL,NULL,17,'2020-12-15 15:05:59','hybrid-phone');
/*!40000 ALTER TABLE `APP_CONFIG` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Table structure for table `APP_INFO`
--

DROP TABLE IF EXISTS `APP_INFO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `APP_INFO` (
  `APP_ID` varchar(100) NOT NULL,
  `CHANNEL` varchar(20) NOT NULL,
  `APP_NAME` varchar(50) NOT NULL,
  `BUILD` varchar(20) NOT NULL,
  `OS_VENDOR` varchar(50) DEFAULT NULL,
  `OS_NAME` varchar(50) DEFAULT NULL,
  `VENDOR` varchar(50) DEFAULT NULL,
  `IMEI` varchar(50) DEFAULT NULL,
  `MODEL` varchar(64) DEFAULT NULL,
  `SCREEN` varchar(50) DEFAULT NULL,
  `GPS` varchar(50) DEFAULT NULL,
  `IMSI` varchar(50) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `OS_TYPE` varchar(50) DEFAULT NULL,
  `DEVICE_TYPE` varchar(50) DEFAULT NULL,
  `DISPLAY` varchar(50) DEFAULT NULL,
  `KEYBOARD` varchar(50) DEFAULT NULL,
  `CELLULAR` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APP_INFO_TRANS`
--

DROP TABLE IF EXISTS `APP_INFO_TRANS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `APP_INFO_TRANS` (
  `APP_ID` varchar(100) NOT NULL,
  `OS_VERSION` varchar(50) NOT NULL,
  `APP_VERSION` varchar(50) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UUID` varchar(100) DEFAULT NULL,
  UNIQUE KEY `index2` (`APP_ID`,`OS_VERSION`,`APP_VERSION`),
  KEY `FK_APP_INFO_TRANS_APP_INFO` (`APP_ID`),
  CONSTRAINT `FK_APP_INFO_TRANS_APP_INFO` FOREIGN KEY (`APP_ID`) REFERENCES `APP_INFO` (`APP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APP_VERSION`
--

DROP TABLE IF EXISTS `APP_VERSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `APP_VERSION` (
  `APP_VERSION` varchar(50) NOT NULL,
  `CHANNEL` varchar(20) NOT NULL,
  `MANDATORY` char(1) DEFAULT NULL,
  `URL` varchar(200) NOT NULL,
  `RELEASE_NOTES` blob NOT NULL,
  `DEVICE_ID` int NOT NULL,
  `STATUS` varchar(10) NOT NULL,
  `IS_DELETED` char(1) DEFAULT NULL,
  `CREATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ID` int NOT NULL AUTO_INCREMENT,
  `appVersion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_APP_VERSION_1` (`DEVICE_ID`),
  CONSTRAINT `fk_APP_VERSION_1` FOREIGN KEY (`DEVICE_ID`) REFERENCES `DEVICES` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CLIENT_SESSION`
--

DROP TABLE IF EXISTS `CLIENT_SESSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CLIENT_SESSION` (
  `user_id` varchar(10) NOT NULL,
  `session_id` varchar(50) DEFAULT NULL,
  `app_id` varchar(100) NOT NULL,
  `build` varchar(30) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active` int NOT NULL,
  `user_type` varchar(20) DEFAULT NULL,
  `user_info` varchar(1000) DEFAULT NULL,
  `ft_session` varchar(100) DEFAULT NULL,
  `ft_session_id` varchar(200) DEFAULT NULL,
  `j_key` varchar(50) DEFAULT NULL,
  `client_order_no` int DEFAULT NULL,
  `is_2FA_authenticated` varchar(2) DEFAULT NULL,
  `order_md5` varchar(32) DEFAULT NULL,
  `last_order_time` int DEFAULT NULL,
  `last_auth_type` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DEVICES`
--

DROP TABLE IF EXISTS `DEVICES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `DEVICES` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) NOT NULL,
  `OS_VERSION` varchar(20) DEFAULT NULL,
  `TYPE` varchar(20) DEFAULT NULL,
  `CREATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `osVersion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FT_INDICES`
--

DROP TABLE IF EXISTS `FT_INDICES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FT_INDICES` (
  `sSymbol` varchar(50) DEFAULT NULL,
  `nTokenSegment` varchar(50) NOT NULL,
  `sSecurityDesc` varchar(50) DEFAULT NULL,
  `nToken` varchar(50) DEFAULT NULL,
  `nMarketSegmentId` varchar(50) DEFAULT NULL,
  `sExchange` varchar(50) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `indexWeightage` varchar(2) DEFAULT NULL,
  `isIndexforMarket` varchar(2) DEFAULT NULL,
  `segmentType` varchar(20) NOT NULL,
  `isIndexforOverview` varchar(2) DEFAULT NULL,
  `sISINCode` varchar(50) DEFAULT NULL,
  `nPrecision` varchar(50) DEFAULT NULL,
  `nRegularLot` varchar(50) DEFAULT NULL,
  `DecimalLocator` varchar(50) DEFAULT NULL,
  `nSearchSymbolDetails` varchar(50) DEFAULT NULL,
  `sInstrumentName` varchar(50) DEFAULT NULL,
  `ExchangeName` varchar(50) DEFAULT NULL,
  `nPriceTick` varchar(50) DEFAULT NULL,
  `sSeries` varchar(50) DEFAULT NULL,
  `ExpiryDate` varchar(50) DEFAULT NULL,
  `nStrikePrice` varchar(50) DEFAULT NULL,
  `sOptionType` varchar(50) DEFAULT NULL,
  `dispPriceTick` varchar(50) DEFAULT NULL,
  `SymbolDetails1` varchar(50) DEFAULT NULL,
  `SymbolDetails2` varchar(50) DEFAULT NULL,
  `SymbolUniqDesc` varchar(30) DEFAULT NULL,
  `nBasePrice` varchar(50) DEFAULT NULL,
  `companyName` varchar(50) DEFAULT NULL,
  `mappingSymbolUniqDesc` varchar(66) DEFAULT NULL,  
  PRIMARY KEY (`nTokenSegment`,`segmentType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `INDEX`
--

DROP TABLE IF EXISTS `INDEX`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `INDEX` (
  `sSymbol` varchar(50) DEFAULT NULL,
  `nToken` varchar(50) DEFAULT NULL,
  `sExchange` varchar(50) DEFAULT NULL,
  `nMarketSegmentId` varchar(50) DEFAULT NULL,
  `sSecurityDesc` varchar(50) DEFAULT NULL,
  `nTokenSegment` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`nTokenSegment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `JOB_INFO`
--

DROP TABLE IF EXISTS `JOB_INFO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `JOB_INFO` (
  `JOBNAME` varchar(50) NOT NULL,
  `USERID` varchar(50) DEFAULT NULL,
  `PASSWORD` varchar(50) DEFAULT NULL,
  `CONFIG` text,
  `CREATED_AT` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `JOB_INFO`
--

LOCK TABLES `JOB_INFO` WRITE;
/*!40000 ALTER TABLE `JOB_INFO` DISABLE KEYS */;
INSERT INTO `JOB_INFO` VALUES ('FTIndicesDump','TEMPMF4','msf@235','{\"equities\":[\"1\",\"3\"],\"equity_index\":[\"NIFTY\",\"SENSEX\",\"BANKNIFTY\",\"NIFTYMID100\",\"NIFTYSMALL\",\"NIFTY500\",\"INDIAVIX\"],\"equity_all_index\":[\"AUTO\",\"BANKNIFTY\",\"COMMODITIES\",\"CONSUMPTION\",\"ENEGRY\",\"FINSERVICE\",\"FMCG\",\"GROWTH\",\"INDIAVIX\",\"INFRA\",\"MEDIA\",\"METAL\",\"MNC\",\"NIFTY\",\"NIFTY100\",\"NIFTY100LV30\",\"NIFTY200\",\"NIFTY500\",\"NIFTYALPHA50\",\"NIFTYCPSE\",\"NIFTYIT\",\"NIFTYMCAP150\",\"NIFTYMDSL400\",\"NIFTYMID100\",\"NIFTYMID50\",\"NIFTYMIDLQ15\",\"NIFTYNEXT50\",\"NIFTYPSE\",\"NIFTYPVTBANK\",\"NIFTYSCAP250\",\"NIFTYSMALL\",\"PHARMA\",\"PSUBANK\",\"REALITY\",\"SERVICE\",\"VALUE20\",\"ALLCAP\",\"AUTO\",\"BANKEX\",\"BHRT22\",\"BSE100\",\"BSE200\",\"BSE500\",\"BSECD\",\"BSECG\",\"BSEFMC\",\"BSEHC\",\"BSEIPO\",\"BSEIT\",\"BSEMETL\",\"BSEPBI\",\"BSEPSU\",\"BSETECK\",\"CDGS\",\"ENERGY\",\"FIN\",\"INFRA\",\"LCTMCI\",\"LMI250\",\"LRGCAP\",\"MFG\",\"MID150\",\"MIDCAP\",\"MSL400\",\"OILGAS\",\"POWER\",\"REALTY\",\"SENSEX\",\"SML250\",\"SMLCAP\",\"SNSX50\",\"TELCOM\",\"UTILS\"],\"derivative_index\":[\"NIFTY\",\"BANKNIFTY\",\"NIFTYMID100\",\"NIFTYSMALL\",\"NIFTY500\",\"INDIAVIX\",\"SENSEX\"],\"derivative_scrip_index\":[\"NIFTY\",\"BANKNIFTY\"],\"currency_scrip_index\":[\"USDINR\",\"EURINR\",\"JPYINR\",\"GBPINR\"],\"commodity_scrip_index\":[\"GOLD\",\"CRUDEOIL\",\"SILVER\",\"COPPER\",\"COTTON\"]}','2020-06-29 18:22:10','2020-06-29 18:22:10');
/*!40000 ALTER TABLE `JOB_INFO` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


--
-- Table structure for table `SCRIPMASTER`
--

DROP TABLE IF EXISTS `SCRIPMASTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SCRIPMASTER` (
  `sSymbol` varchar(50) DEFAULT NULL,
  `sCreditRating` varchar(50) DEFAULT NULL,
  `nListingDate` varchar(50) DEFAULT NULL,
  `nMarketType` varchar(50) DEFAULT NULL,
  `nExposureMarginAdditional` varchar(50) DEFAULT NULL,
  `nMarginPercentage` varchar(50) DEFAULT NULL,
  `nBookClosureEndDate` varchar(50) DEFAULT NULL,
  `nPriceNum` varchar(50) DEFAULT NULL,
  `cDeleteFlag` varchar(50) DEFAULT NULL,
  `nAuction_MarketAllowed` varchar(50) DEFAULT NULL,
  `nIndexParticipant` varchar(50) DEFAULT NULL,
  `sInstrumentName` varchar(50) DEFAULT NULL,
  `nBookClosureStartDate` varchar(50) DEFAULT NULL,
  `sSeries` varchar(50) DEFAULT NULL,
  `nNRIFlag` varchar(50) DEFAULT NULL,
  `nLastUpdateTime` varchar(50) DEFAULT NULL,
  `nMaxNumberOfActiveContracts` varchar(50) DEFAULT NULL,
  `nAONAllowed` varchar(50) DEFAULT NULL,
  `nDeliveryLongMargin` varchar(50) DEFAULT NULL,
  `nPriceBdAttribute` varchar(50) DEFAULT NULL,
  `nAVMSellMargin` varchar(50) DEFAULT NULL,
  `nSpecialShortMargin` varchar(50) DEFAULT NULL,
  `SPOS` varchar(50) DEFAULT NULL,
  `nExpulsionDate` varchar(50) DEFAULT NULL,
  `nFIIFlag` varchar(50) DEFAULT NULL,
  `nWatchLimit` varchar(50) DEFAULT NULL,
  `nMinimumLot` varchar(50) DEFAULT NULL,
  `nWatchFlag` varchar(50) DEFAULT NULL,
  `nPOS` varchar(50) DEFAULT NULL,
  `nMarginTypeIndicator` varchar(50) DEFAULT NULL,
  `nExerciseStyle` varchar(50) DEFAULT NULL,
  `nRights` varchar(50) DEFAULT NULL,
  `PreOpen` varchar(50) DEFAULT NULL,
  `nMaxSingleTransactionValue` varchar(50) DEFAULT NULL,
  `nFreezePercent` varchar(50) DEFAULT NULL,
  `SpecialPreOpen` varchar(50) DEFAULT NULL,
  `nOpenInterest` varchar(50) DEFAULT NULL,
  `nIssueStartDate` varchar(50) DEFAULT NULL,
  `nMarginMultiplier` varchar(50) DEFAULT NULL,
  `nDetailLastUpdateTime` varchar(50) DEFAULT NULL,
  `sOptionType` varchar(50) DEFAULT NULL,
  `nDeliveryLotQty` varchar(50) DEFAULT NULL,
  `nSpecialLongMargin` varchar(50) DEFAULT NULL,
  `nRegularLot` varchar(50) DEFAULT NULL,
  `sISINCode` varchar(50) DEFAULT NULL,
  `nReadmissionDate` varchar(50) DEFAULT NULL,
  `nHighPriceRange` varchar(50) DEFAULT NULL,
  `nMaxSingleTransactionQty` varchar(50) DEFAULT NULL,
  `nIntrinsicValue` varchar(50) DEFAULT NULL,
  `nExDate` varchar(50) DEFAULT NULL,
  `nNormal_MarketAllowed` varchar(50) DEFAULT NULL,
  `nSpot_SecurityStatus` varchar(50) DEFAULT NULL,
  `sRemarks` varchar(50) DEFAULT NULL,
  `nSpread` varchar(50) DEFAULT NULL,
  `nFIILimit` varchar(50) DEFAULT NULL,
  `nOddLot_SecurityStatus` varchar(50) DEFAULT NULL,
  `nRecordDate` varchar(50) DEFAULT NULL,
  `sAssetInstrument` varchar(50) DEFAULT NULL,
  `nLowPriceRange` varchar(50) DEFAULT NULL,
  `nExRejectionAllowed` varchar(50) DEFAULT NULL,
  `nExerciseStartDate` varchar(50) DEFAULT NULL,
  `ExpiryDate` varchar(50) DEFAULT NULL,
  `nDividend` varchar(50) DEFAULT NULL,
  `nIssueRate` varchar(50) DEFAULT NULL,
  `nPriceDen` varchar(50) DEFAULT NULL,
  `nNoDeliveryEndDate` varchar(50) DEFAULT NULL,
  `nAGM` varchar(50) DEFAULT NULL,
  `nPriceQuotFactor` varchar(50) DEFAULT NULL,
  `sDPSecurityDesc` varchar(50) DEFAULT NULL,
  `nEGM` varchar(50) DEFAULT NULL,
  `nNRILimit` varchar(50) DEFAULT NULL,
  `nOddLot_MarketAllowed` varchar(50) DEFAULT NULL,
  `nPriceBdConfig` varchar(50) DEFAULT NULL,
  `nDynamicLastUpdateTime` varchar(50) DEFAULT NULL,
  `nDeliveryShortMargin` varchar(50) DEFAULT NULL,
  `DecimalLocator` varchar(50) DEFAULT NULL,
  `nWarningPercent` varchar(50) DEFAULT NULL,
  `nBcastFlag` varchar(50) DEFAULT NULL,
  `nAssetToken` varchar(50) DEFAULT NULL,
  `nSettlementMethod` varchar(50) DEFAULT NULL,
  `nStrikePrice` varchar(50) DEFAULT NULL,
  `nToken` varchar(50) DEFAULT NULL,
  `nFaceValue` varchar(50) DEFAULT NULL,
  `sQtyUnit` varchar(50) DEFAULT NULL,
  `nTotalLongMargin` varchar(50) DEFAULT NULL,
  `nTotalShortMargin` varchar(50) DEFAULT NULL,
  `nIssuedCapital` varchar(50) DEFAULT NULL,
  `sPriceQuotUnit` varchar(50) DEFAULT NULL,
  `CallAuction` varchar(50) DEFAULT NULL,
  `nMarketSegmentId` varchar(50) DEFAULT NULL,
  `sAssetName` varchar(50) DEFAULT NULL,
  `nMFAllowed` varchar(50) DEFAULT NULL,
  `nSpot_MarketAllowed` varchar(50) DEFAULT NULL,
  `nBasePrice` varchar(50) DEFAULT NULL,
  `nTenderPeriodIndicator` varchar(50) DEFAULT NULL,
  `nIssuePDate` varchar(50) DEFAULT NULL,
  `SPOSTYPE` varchar(50) DEFAULT NULL,
  `sSecurityDesc` varchar(50) DEFAULT NULL,
  `nPermittedToTrade` varchar(50) DEFAULT NULL,
  `nAVMBuyMargin` varchar(50) DEFAULT NULL,
  `nNormal_SecurityStatus` varchar(50) DEFAULT NULL,
  `nAuction_SecurityStatus` varchar(50) DEFAULT NULL,
  `nExtrinsicValue` varchar(50) DEFAULT NULL,
  `nIssueMaturityDate` varchar(50) DEFAULT NULL,
  `nExAllowed` varchar(50) DEFAULT NULL,
  `nCALevel` varchar(50) DEFAULT NULL,
  `nBonus` varchar(50) DEFAULT NULL,
  `nOldToken` varchar(50) DEFAULT NULL,
  `nTotalValueTraded` varchar(50) DEFAULT NULL,
  `sDeliveryUnit` varchar(50) DEFAULT NULL,
  `nExpiryDate` varchar(50) DEFAULT NULL,
  `nPlAllowed` varchar(50) DEFAULT NULL,
  `nIsAsset` varchar(50) DEFAULT NULL,
  `nInterest` varchar(50) DEFAULT NULL,
  `nExerciseEndDate` varchar(50) DEFAULT NULL,
  `nInstrumentType` varchar(50) DEFAULT NULL,
  `nNoDeliveryStartDate` varchar(50) DEFAULT NULL,
  `exchangeName` varchar(50) DEFAULT NULL,
  `nPrecision` varchar(50) DEFAULT NULL,
  `assetClass` varchar(50) DEFAULT NULL,
  `symbolDetails` varchar(50) DEFAULT NULL,
  `nTokenSegment` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  `expiryForSearch` varchar(40) DEFAULT NULL,
  `instrumentForSearch` varchar(40) DEFAULT NULL,
  `nSearchSymbolDetails` varchar(50) DEFAULT NULL,
  `dispPriceTick` varchar(50) DEFAULT NULL,
  `nPriceTick` varchar(50) DEFAULT NULL,
  `isinSegment` varchar(50) DEFAULT NULL,
  `dispSearch` varchar(50) DEFAULT NULL,
  `CM_co_code` varchar(50) DEFAULT NULL,
  `CM_BSECode` varchar(50) DEFAULT NULL,
  `CM_CategoryName` varchar(50) DEFAULT NULL,
  `CM_BSEGroup` varchar(50) DEFAULT NULL,
  `CM_mcaptype` varchar(50) DEFAULT NULL,
  `CM_SectorCode` varchar(50) DEFAULT NULL,
  `CM_SectorName` varchar(50) DEFAULT NULL,
  `SymbolUniqDesc` varchar(50) DEFAULT NULL,
  `SymbolDetails2` varchar(50) DEFAULT NULL,
  `SymbolDetails1` varchar(50) DEFAULT NULL,
  `CM_SectorFormat` varchar(50) DEFAULT NULL,
  `companyName` varchar(50) DEFAULT NULL,
  `mappingSymbolUniqDesc` varchar(66) DEFAULT NULL,
  PRIMARY KEY (`nTokenSegment`),
  KEY `tokenSegId` (`nTokenSegment`),
  KEY `sISINCode` (`sISINCode`),
  FULLTEXT KEY `sym` (`sSymbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERSION_MASTER`
--

DROP TABLE IF EXISTS `VERSION_MASTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `VERSION_MASTER` (
  `TYPE` varchar(20) NOT NULL,
  `VERSION` int NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `FORM_FACTOR` varchar(5) NOT NULL,
  `BUILD_NAME` varchar(100) DEFAULT NULL,
  UNIQUE KEY `index1` (`TYPE`,`VERSION`,`FORM_FACTOR`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `predefined_watchlist`
--

DROP TABLE IF EXISTS `predefined_watchlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `predefined_watchlist` (
  `indexCode` varchar(10) DEFAULT NULL,
  `indexName` varchar(50) NOT NULL,
  `exchange` varchar(10) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `isWatchlist` varchar(2) DEFAULT NULL,
  `watchlistWeightage` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`indexName`,`exchange`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `predefined_watchlist_symbols`
--

DROP TABLE IF EXISTS `predefined_watchlist_symbols`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `predefined_watchlist_symbols` (
  `indexCode` varchar(10) DEFAULT NULL,
  `indexName` varchar(20) NOT NULL,
  `symbol` varchar(50) DEFAULT NULL,
  `isin` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`indexName`,`isin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settings` (
  `param_name` varchar(30) NOT NULL,
  `param_value` varchar(200) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'globecapital'
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
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`platformwrite`@`localhost`*/ /*!50106 EVENT `DAILY_MAINTENANCE` ON SCHEDULE EVERY 1 DAY STARTS '2020-07-23 05:30:00' ON COMPLETION NOT PRESERVE ENABLE COMMENT 'It will remove previous day AMO timings from the database' DO BEGIN

call delete_previous_day_amo_details();

END */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;

--
-- Dumping routines for database 'globecapital'
--
/*!50003 DROP PROCEDURE IF EXISTS `add_indices` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_indices`(IN symbol varchar(50), IN mrkt varchar(50), IN segment varchar(20), IN weight varchar(2))
BEGIN
DECLARE sym varchar(50);
DECLARE tokenId varchar(50);
DECLARE sec varchar(50);
DECLARE token varchar(50);
DECLARE id varchar(50);
DECLARE exch varchar(50);
DECLARE isin varchar(50);
DECLARE prec varchar(50);
DECLARE lot varchar(50);
DECLARE loc varchar(50);
DECLARE base varchar(50);
DECLARE inst varchar(50);
DECLARE tick varchar(50);
DECLARE dispTick varchar(50);
DECLARE series varchar(50);
DECLARE exp varchar(50);
DECLARE strike varchar(50);
DECLARE opt varchar(50);
DECLARE search varchar(50);
DECLARE uniq varchar(50);
DECLARE det1 varchar(50);
DECLARE det2 varchar(50);
DECLARE compName varchar(50);
SELECT sSymbol, nTokenSegment, sSecurityDesc, nToken, nMarketSegmentId, exchangeName, sISINCode, nPrecision, nRegularLot, DecimalLocator, nBasePrice, sInstrumentName, nPriceTick,
 dispPriceTick, sSeries, ExpiryDate, nStrikePrice, sOptionType, nSearchSymbolDetails, SymbolUniqDesc, SymbolDetails1,
 SymbolDetails2, companyName INTO sym, tokenId, sec, token, id, exch, isin, prec, lot, loc, base, inst, tick, dispTick, series, exp, strike, opt, search, uniq, det1, det2, compName FROM SCRIPMASTER
WHERE sSymbol = symbol AND nMarketSegmentId = mrkt AND sInstrumentName LIKE 'FUT%' ORDER BY nExpiryDate LIMIT 1;
INSERT INTO FT_INDICES (sSymbol, nTokenSegment, sSecurityDesc, nToken, nMarketSegmentId, sExchange, ExchangeName, sISINCode, nPrecision, nRegularLot, DecimalLocator, nBasePrice, sInstrumentName, nPriceTick,
 dispPriceTick, sSeries, ExpiryDate, nStrikePrice, sOptionType, nSearchSymbolDetails, SymbolUniqDesc, SymbolDetails1,
 SymbolDetails2, isIndexforOverview, segmentType, indexWeightage, created_at, companyName)
VALUES( sym, tokenId, sec, token, id, exch, exch, isin, prec, lot, loc, base, inst, tick, dispTick, series, exp, strike, opt, search, uniq, det1, det2, '1', segment, weight,  NOW(), compName)
ON DUPLICATE KEY UPDATE sSymbol=VALUES(sSymbol), sSecurityDesc=VALUES(sSecurityDesc),
nToken=VALUES(nToken), nMarketSegmentId=VALUES(nMarketSegmentId), sExchange=VALUES(sExchange), isIndexforMarket=VALUES(isIndexforMarket),
segmentType=VALUES(segmentType), nPriceTick=VALUES(nPriceTick), dispPriceTick=VALUES(dispPriceTick), sSeries=VALUES(sSeries), ExpiryDate=VALUES(ExpiryDate),
nStrikePrice=VALUES(nStrikePrice), sOptionType=VALUES(sOptionType), nSearchSymbolDetails=VALUES(nSearchSymbolDetails), SymbolUniqDesc=VALUES(SymbolUniqDesc),
SymbolDetails1=VALUES(SymbolDetails1), SymbolDetails2=VALUES(SymbolDetails2), ExchangeName=VALUES(ExchangeName), nPrecision=VALUES(nPrecision), nRegularLot=VALUES(nRegularLot),
DecimalLocator=VALUES(DecimalLocator), nBasePrice=VALUES(nBasePrice), sISINCode=VALUES(sISINCode), indexWeightage=VALUES(indexWeightage),companyName=VALUES(companyName), updated_at=NOW();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `add_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `add_user`(IN iUserId VARCHAR(50),IN iPassword VARCHAR(3000),IN i2FA TINYINT(4),IN iFirstLogin TINYINT(4),
IN iUserStatus VARCHAR(10),IN iMobileNo VARCHAR(15))
BEGIN
    INSERT INTO CLIENT_LOGIN(user_id,password,2fa,login_status,first_time_login,mobile_no)
    VALUES(iUserId,iPassword,i2FA,iUserStatus,iFirstLogin,iMobileNo);

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `client_order_no_retrieval` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`platformwrite`@`localhost` PROCEDURE `client_order_no_retrieval`(IN userID varchar(50), OUT oClientOrderNo INT)
BEGIN

SELECT client_order_no into oClientOrderNo from CLIENT_SESSION WHERE user_id = userID;
UPDATE CLIENT_SESSION SET client_order_no = oClientOrderNo + 1 WHERE user_id = userID;

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_equities` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_equities`(IN mrkt varchar(50))
BEGIN
DELETE FROM SCRIPMASTER WHERE nMarketSegmentId = mrkt;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_expired_contracts` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_expired_contracts`()
BEGIN
DELETE FROM SCRIPMASTER WHERE nMarketSegmentId IN (2,5,7,13,38) AND STR_TO_DATE(ExpiryDate,'%d%b%Y') < CURDATE();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_expired_indices` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_expired_indices`()
BEGIN
DELETE FROM FT_INDICES WHERE nMarketSegmentId IN (2,5,7,13,38) AND STR_TO_DATE(ExpiryDate,'%d%b%Y') < CURDATE();
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_previous_day_amo_details` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_previous_day_amo_details`()
BEGIN
delete from globecapital.AMO_DETAILS where DATE <= CURDATE() - INTERVAL 2 DAY;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `has_version_update` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `has_version_update`(IN d_app_id VARCHAR(50))
begin
  DECLARE d_build, d_app_version, d_device_type VARCHAR(50);

  DECLARE d_os_version, d_channel VARCHAR(50);

  DECLARE db_app_version VARCHAR(50);

  DECLARE db_url VARCHAR(200);

  DECLARE db_release_notes BLOB;

  DECLARE db_mandatory CHAR(1);

  DECLARE db_device_id INT;

  SELECT ai.build,ai.device_type,ai.channel,ait.os_version,ait.app_version
    INTO d_build, d_device_type, d_channel, d_os_version, d_app_version
    FROM APP_INFO ai, APP_INFO_TRANS ait
   WHERE ai.app_id = d_app_id
         AND ai.app_id = ait.app_id
   ORDER BY ait.created_at DESC
   LIMIT 1;

  IF d_build IS NOT NULL THEN
    SELECT app_version,mandatory,url,release_notes,device_id
      INTO db_app_version, db_mandatory, db_url, db_release_notes, db_device_id
      FROM APP_VERSION av,DEVICES d
     WHERE av.channel = d_channel
           AND av.device_id = d.id
           AND d.name = d_build
           AND ( d.os_version = d_os_version
                  OR d.os_version IS NULL )
           AND ( d.type = d_device_type
                  OR d.type IS NULL )
           AND status = 'ACT'
           AND is_deleted IS NULL;

    IF Inet_aton(d_app_version) < Inet_aton(db_app_version) THEN
      IF db_mandatory IS NULL THEN
        SELECT Max(mandatory)
          INTO db_mandatory
          FROM APP_VERSION
         WHERE device_id = db_device_id
               AND Inet_aton(d_app_version) < Inet_aton(app_version)
               AND Inet_aton(db_app_version) > Inet_aton(app_version)
               AND status = 'ACT';
      end IF;

      SELECT 1,db_app_version,db_url,db_release_notes, db_mandatory;
    ELSEIF Inet_aton(d_app_version) > Inet_aton('1.0.60') AND Inet_aton(d_app_version) <= Inet_aton('1.0.82') THEN
	  IF d_channel = 'androidmarket' THEN
		SELECT 1,'1.0.27', 'https://play.google.com/store/apps/details?id=com.globecapital.prod', 'This version of the App you are currently using is no longer supported. Kindly delete and download the latest app from the Play Store', 'true';		
	  ELSE
		SELECT 1,'1.0.27', 'https://apps.apple.com/us/app/globe-trade-pro-share-trading/id1545938530', 'This version of the App you are currently using is no longer supported. Kindly delete and download the latest app from the App Store', 'true';
	  end IF;		
	ELSE
      SELECT 0;
    end IF;
  end IF;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `order_validator` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `order_validator`(IN sessionID varchar(50), IN newOrderMD5 varchar(50),  OUT oStatus INT )
BEGIN
declare old_md5 varchar(50);
declare lastOrderTime int(12);
declare currentTimeStamp int(12);
declare order_expiry INT;

select CAST(param_value as UNSIGNED) into order_expiry from settings where param_name='ORDER_EXPIRY';
select order_md5, last_order_time into old_md5, lastOrderTime from CLIENT_SESSION where session_id = sessionID;
select UNIX_TIMESTAMP() into currentTimeStamp;
if( currentTimeStamp - lastOrderTime <= order_expiry) then
if( strcmp(old_md5, newOrderMD5) = 0 ) then
set oStatus = 0;
else
set oStatus = 1;
update CLIENT_SESSION set order_md5 = newOrderMD5, last_order_time = currentTimeStamp where session_id = sessionID;
end if;
else
set oStatus = 1;
update CLIENT_SESSION set order_md5 = newOrderMD5, last_order_time = currentTimeStamp where session_id = sessionID;
end if;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `session_validation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `session_validation`(IN sessionID varchar(50), OUT oStatus INT, OUT oMsg varchar(30) )
BEGIN
declare lastActive int(12) DEFAULT 0;
declare currentTimeStamp int(12);
declare session_timeout INT;
select CAST(param_value AS UNSIGNED) INTO session_timeout from settings where param_name='session_timeout';
select last_active into lastActive from CLIENT_SESSION where session_id = sessionID;
select UNIX_TIMESTAMP() into currentTimeStamp;
IF( lastActive = 0 ) then
SET oStatus = -1;
SET oMsg = "Invalid sessionID";
ELSEIF( ABS(currentTimeStamp - lastActive) <= session_timeout) then
select user_id, app_id, build, user_type,last_active, user_info, ft_session, ft_session_id,j_key,client_order_no,is_2FA_authenticated from CLIENT_SESSION where session_id = sessionID;

SET oStatus = 0;
ELSE
DELETE from CLIENT_SESSION where session_id = sessionID;
SET oStatus = -2;
SET oMsg = "Session expired";
END IF;
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

-- Dump completed on 2020-12-28 18:47:35