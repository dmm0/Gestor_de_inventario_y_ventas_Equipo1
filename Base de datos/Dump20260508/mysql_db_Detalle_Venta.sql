-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: proyecto-ing-software.cpi6ommsww1k.us-east-2.rds.amazonaws.com    Database: mysql_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `Detalle_Venta`
--

DROP TABLE IF EXISTS `Detalle_Venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Detalle_Venta` (
  `id_detalle_Venta` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `id_venta` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` int NOT NULL,
  PRIMARY KEY (`id_detalle_Venta`),
  KEY `fk_dVenta_producto_idx` (`id_producto`),
  KEY `fk_dventa_venta_idx` (`id_venta`),
  CONSTRAINT `fk_dVenta_producto` FOREIGN KEY (`id_producto`) REFERENCES `Productos` (`id_productos`),
  CONSTRAINT `fk_dventa_venta` FOREIGN KEY (`id_venta`) REFERENCES `Ventas` (`id_venta`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Detalle_Venta`
--

LOCK TABLES `Detalle_Venta` WRITE;
/*!40000 ALTER TABLE `Detalle_Venta` DISABLE KEYS */;
INSERT INTO `Detalle_Venta` VALUES (1,1,2,2,2),(2,2,2,2,4),(3,3,2,2,20),(4,1,3,1,2),(5,2,3,4,4),(6,3,3,5,20),(7,1,4,8,2),(8,2,4,1,4),(9,3,4,1,20),(10,1,5,2,2),(11,2,5,2,4),(12,3,5,2,20),(13,1,6,1,2),(14,4,6,1,5),(15,1,7,5,2),(16,2,7,1,4),(17,4,7,2,5),(18,1,8,3,2),(19,2,8,3,4),(20,4,9,1,5),(21,3,9,1,20),(22,2,9,1,4),(23,1,9,1,2),(24,1,10,1,2),(25,3,11,1,20),(26,3,12,5,20),(27,4,13,8,5),(28,1,14,1,2),(29,2,14,1,4),(30,3,14,1,20),(31,4,14,1,5),(32,2,15,1,4);
/*!40000 ALTER TABLE `Detalle_Venta` ENABLE KEYS */;
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

-- Dump completed on 2026-05-08 17:38:08
