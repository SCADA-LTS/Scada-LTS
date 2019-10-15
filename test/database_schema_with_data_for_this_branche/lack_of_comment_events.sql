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
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeId` int(11) NOT NULL,
  `typeRef1` int(11) NOT NULL,
  `typeRef2` int(11) NOT NULL,
  `activeTs` bigint(20) NOT NULL,
  `rtnApplicable` char(1) NOT NULL,
  `rtnTs` bigint(20) DEFAULT NULL,
  `rtnCause` int(11) DEFAULT NULL,
  `alarmLevel` int(11) NOT NULL,
  `message` longtext,
  `ackTs` bigint(20) DEFAULT NULL,
  `ackUserId` int(11) DEFAULT NULL,
  `alternateAckSource` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `eventsFk1` (`ackUserId`),
  CONSTRAINT `eventsFk1` FOREIGN KEY (`ackUserId`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,4,1,0,1571142201213,'N',0,0,1,'event.system.startup|',0,NULL,NULL),(2,4,4,1,1571142221124,'Y',1571143095773,1,1,'event.login|admin|',0,NULL,NULL),(3,4,3,0,1571142221124,'N',0,0,0,'event.alarmMaxIncreased|[common.alarmLevel.none|][common.alarmLevel.info|]',0,NULL,NULL),(4,4,9,1,1571142251305,'Y',1571142251484,1,2,'event.pointLink.sourceUnavailable|',0,NULL,NULL),(5,4,3,0,1571142251305,'N',0,0,0,'event.alarmMaxIncreased|[common.alarmLevel.info|][common.alarmLevel.urgent|]',0,NULL,NULL),(6,4,9,2,1571142251383,'Y',1571142251494,1,2,'event.pointLink.sourceUnavailable|',0,NULL,NULL),(7,4,3,0,1571142251494,'N',0,0,0,'event.alarmMaxDecreased|[common.alarmLevel.urgent|][common.alarmLevel.info|]',0,NULL,NULL),(8,4,9,2,1571142251507,'Y',1571142427143,1,2,'event.pointLink.targetUnavailable|',0,NULL,NULL),(9,4,3,0,1571142251507,'N',0,0,0,'event.alarmMaxIncreased|[common.alarmLevel.info|][common.alarmLevel.urgent|]',0,NULL,NULL),(10,4,9,2,1571142251507,'Y',1571142427143,1,2,'common.default|ReferenceError: \"spurce\" is not defined. (<cmd>#1)|',0,NULL,NULL),(11,4,3,0,1571142427143,'N',0,0,0,'event.alarmMaxDecreased|[common.alarmLevel.urgent|][common.alarmLevel.info|]',0,NULL,NULL),(12,4,4,2,1571143095752,'Y',1571144422437,1,1,'event.login|mateusz|',0,NULL,NULL),(13,4,3,0,1571144422437,'N',0,0,0,'event.alarmMaxDecreased|[common.alarmLevel.info|][common.alarmLevel.none|]',0,NULL,NULL),(14,4,2,0,1571144422461,'N',0,0,1,'event.system.shutdown|',0,NULL,NULL);
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
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
