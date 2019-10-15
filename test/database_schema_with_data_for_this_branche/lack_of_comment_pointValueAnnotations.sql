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
-- Table structure for table `pointValueAnnotations`
--

DROP TABLE IF EXISTS `pointValueAnnotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pointValueAnnotations` (
  `pointValueId` bigint(20) NOT NULL,
  `textPointValueShort` varchar(128) DEFAULT NULL,
  `textPointValueLong` longtext,
  `sourceType` smallint(6) DEFAULT NULL,
  `sourceId` int(11) DEFAULT NULL,
  KEY `pointValueAnnotationsFk1` (`pointValueId`),
  CONSTRAINT `pointValueAnnotationsFk1` FOREIGN KEY (`pointValueId`) REFERENCES `pointValues` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pointValueAnnotations`
--

LOCK TABLES `pointValueAnnotations` WRITE;
/*!40000 ALTER TABLE `pointValueAnnotations` DISABLE KEYS */;
INSERT INTO `pointValueAnnotations` VALUES (2,'1',NULL,5,1),(3,'2',NULL,5,1),(4,'1.0',NULL,4,1),(7,'3',NULL,1,1),(9,'1',NULL,1,1),(10,'1',NULL,5,1),(35,'7',NULL,1,1),(36,'-1.0',NULL,4,2),(37,'1.0',NULL,4,1),(38,'5.0',NULL,4,2),(40,'3',NULL,1,1),(51,'1',NULL,1,2);
/*!40000 ALTER TABLE `pointValueAnnotations` ENABLE KEYS */;
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
