-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: lack_of_comment
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `templatesDetectors`
--

DROP TABLE IF EXISTS `templatesDetectors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templatesDetectors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `xid` varchar(50) NOT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `detectorType` int(11) NOT NULL,
  `alarmLevel` int(11) NOT NULL,
  `stateLimit` float DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `durationType` int(11) DEFAULT NULL,
  `binaryState` char(1) DEFAULT NULL,
  `multistateState` int(11) DEFAULT NULL,
  `changeCount` int(11) DEFAULT NULL,
  `alphanumericState` varchar(128) DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `eventDetectorTemplateId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `templatesDetectorsFk1` (`eventDetectorTemplateId`),
  CONSTRAINT `templatesDetectorsFk1` FOREIGN KEY (`eventDetectorTemplateId`) REFERENCES `eventDetectorTemplates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `templatesDetectors`
--

LOCK TABLES `templatesDetectors` WRITE;
/*!40000 ALTER TABLE `templatesDetectors` DISABLE KEYS */;
/*!40000 ALTER TABLE `templatesDetectors` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-10-15 15:01:10
