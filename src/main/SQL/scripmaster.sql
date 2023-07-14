-- MySQL dump 10.13  Distrib 8.0.13, for Linux (x86_64)
--
-- Host: localhost    Database: globecapital
-- ------------------------------------------------------
-- Server version    8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
--
-- Table structure for table `SCRIPMASTER`
--

DROP TABLE IF EXISTS `SCRIPMASTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
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
  `SymbolUniqDesc` varchar(30) DEFAULT NULL,
  `SymbolDetails2` varchar(50) DEFAULT NULL,
  `SymbolDetails1` varchar(50) DEFAULT NULL,
  `companyName` varchar(50) DEFAULT NULL,
  `isFNOExists` varchar(5) DEFAULT 'true',
  PRIMARY KEY (`nTokenSegment`),
  KEY `tokenSegId` (`nTokenSegment`),
  KEY `sISINCode` (`sISINCode`),
  FULLTEXT KEY `sym` (`sSymbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Dumping events for database 'globecapital'
--

--
-- Dumping routines for database 'globecapital'
--
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_equities` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_equities`()
BEGIN
DELETE FROM SCRIPMASTER WHERE nMarketSegmentId IN (1,3);
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-21  7:28:05