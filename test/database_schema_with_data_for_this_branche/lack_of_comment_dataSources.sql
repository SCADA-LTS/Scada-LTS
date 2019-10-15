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
-- Table structure for table `dataSources`
--

DROP TABLE IF EXISTS `dataSources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataSources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `xid` varchar(50) NOT NULL,
  `name` varchar(40) NOT NULL,
  `dataSourceType` int(11) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dataSourcesUn1` (`xid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataSources`
--

LOCK TABLES `dataSources` WRITE;
/*!40000 ALTER TABLE `dataSources` DISABLE KEYS */;
INSERT INTO `dataSources` VALUES (1,'DS_731949','VDS1',1,_binary '¨\Ì\0sr\0=com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVOˇˇˇˇˇˇˇˇ\0I\0updatePeriodTypeI\0\rupdatePeriodsxr\0.com.serotonin.mango.vo.dataSource.DataSourceVOˇˇˇˇˇˇˇˇ\0Z\0enabledI\0idL\0alarmLevelst\0Ljava/util/Map;L\0namet\0Ljava/lang/String;L\0statet\0!Lorg/scada_lts/ds/state/IStateDs;L\0xidq\0~\0xpw\0\0\0sr\0java.util.HashMap\⁄¡\√`\—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0\0w\0\0\0\0\0\0\0xsr\00org.scada_lts.ds.state.ImportChangeEnableStateDs/èõ\Ët£}\0\0xpxw\0\0\0\0\0\0\0\0\0\nx'),(2,'DS_853875','VDS2',1,_binary '¨\Ì\0sr\0=com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVOˇˇˇˇˇˇˇˇ\0I\0updatePeriodTypeI\0\rupdatePeriodsxr\0.com.serotonin.mango.vo.dataSource.DataSourceVOˇˇˇˇˇˇˇˇ\0Z\0enabledI\0idL\0alarmLevelst\0Ljava/util/Map;L\0namet\0Ljava/lang/String;L\0statet\0!Lorg/scada_lts/ds/state/IStateDs;L\0xidq\0~\0xpw\0\0\0sr\0java.util.HashMap\⁄¡\√`\—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0\0w\0\0\0\0\0\0\0xsr\00org.scada_lts.ds.state.ImportChangeEnableStateDs/èõ\Ët£}\0\0xpxw\0\0\0\0\0\0\0\0\0\nx');
/*!40000 ALTER TABLE `dataSources` ENABLE KEYS */;
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
