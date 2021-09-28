-- --------------------------------------------------------
-- Verkkotietokone:              10.114.32.44
-- Palvelinversio:               5.5.64-MariaDB - MariaDB Server
-- Server OS:                    Linux
-- HeidiSQL Versio:              10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for testdb
CREATE DATABASE IF NOT EXISTS `testdb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `testdb`;

-- Dumping structure for taulu testdb.OrderedPortions
CREATE TABLE IF NOT EXISTS `OrderedPortions` (
  `junction_id` int(11) NOT NULL AUTO_INCREMENT,
  `Order_id` int(11) NOT NULL,
  `Portion_id` int(11) NOT NULL,
  `Portion_details` varchar(50) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`junction_id`),
  KEY `FK_PortionsOrdersJunction_Orders` (`Order_id`),
  KEY `FK_PortionsOrdersJunction_Portions` (`Portion_id`),
  CONSTRAINT `FK_PortionsOrdersJunction_Orders` FOREIGN KEY (`Order_id`) REFERENCES `Orders` (`ID`),
  CONSTRAINT `FK_PortionsOrdersJunction_Portions` FOREIGN KEY (`Portion_id`) REFERENCES `Portions` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COMMENT='Junction table to enable many-to-many relation between Orders and Portions. One order can have multiple portions and a portion can be ordered multiple times.';

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Orders
CREATE TABLE IF NOT EXISTS `Orders` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Order_details` varchar(255) DEFAULT NULL,
  `Table_number` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Table_number` (`Table_number`),
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`Table_number`) REFERENCES `Tables` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Portions
CREATE TABLE IF NOT EXISTS `Portions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL DEFAULT '0',
  `Price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COMMENT='Each row holds a item in the restaurants menu.';

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Reservations
CREATE TABLE IF NOT EXISTS `Reservations` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Client_name` varchar(100) NOT NULL,
  `Client_phone` varchar(50) NOT NULL,
  `Reservation_date` date NOT NULL,
  `Table_number` int(11) NOT NULL,
  `Start_time` time NOT NULL,
  `End_time` time NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Table_number` (`Table_number`),
  CONSTRAINT `Reservations_ibfk_1` FOREIGN KEY (`Table_number`) REFERENCES `Tables` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Tables
CREATE TABLE IF NOT EXISTS `Tables` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Seats` int(11) NOT NULL,
  `Reservation_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Reservation_id` (`Reservation_id`),
  CONSTRAINT `Tables_ibfk_1` FOREIGN KEY (`Reservation_id`) REFERENCES `Reservations` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Users
CREATE TABLE IF NOT EXISTS `Users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Userlevel` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `username_unique` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;



-- --------------------------------------------------------
-- Verkkotietokone:              10.114.32.44
-- Palvelinversio:               5.5.64-MariaDB - MariaDB Server
-- Server OS:                    Linux
-- HeidiSQL Versio:              10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for testdb
CREATE DATABASE IF NOT EXISTS `mydb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `mydb`;

-- Dumping structure for taulu testdb.OrderedPortions
CREATE TABLE IF NOT EXISTS `OrderedPortions` (
  `junction_id` int(11) NOT NULL AUTO_INCREMENT,
  `Order_id` int(11) NOT NULL,
  `Portion_id` int(11) NOT NULL,
  `Portion_details` varchar(50) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`junction_id`),
  KEY `FK_PortionsOrdersJunction_Orders` (`Order_id`),
  KEY `FK_PortionsOrdersJunction_Portions` (`Portion_id`),
  CONSTRAINT `FK_PortionsOrdersJunction_Orders` FOREIGN KEY (`Order_id`) REFERENCES `Orders` (`ID`),
  CONSTRAINT `FK_PortionsOrdersJunction_Portions` FOREIGN KEY (`Portion_id`) REFERENCES `Portions` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COMMENT='Junction table to enable many-to-many relation between Orders and Portions. One order can have multiple portions and a portion can be ordered multiple times.';

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Orders
CREATE TABLE IF NOT EXISTS `Orders` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Order_details` varchar(255) DEFAULT NULL,
  `Table_number` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Table_number` (`Table_number`),
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`Table_number`) REFERENCES `Tables` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Portions
CREATE TABLE IF NOT EXISTS `Portions` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL DEFAULT '0',
  `Price` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 COMMENT='Each row holds a item in the restaurants menu.';

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Reservations
CREATE TABLE IF NOT EXISTS `Reservations` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Client_name` varchar(100) NOT NULL,
  `Client_phone` varchar(50) NOT NULL,
  `Reservation_date` date NOT NULL,
  `Table_number` int(11) NOT NULL,
  `Start_time` time NOT NULL,
  `End_time` time NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Table_number` (`Table_number`),
  CONSTRAINT `Reservations_ibfk_1` FOREIGN KEY (`Table_number`) REFERENCES `Tables` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Tables
CREATE TABLE IF NOT EXISTS `Tables` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Seats` int(11) NOT NULL,
  `Reservation_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Reservation_id` (`Reservation_id`),
  CONSTRAINT `Tables_ibfk_1` FOREIGN KEY (`Reservation_id`) REFERENCES `Reservations` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

-- Dumping structure for taulu testdb.Users
CREATE TABLE IF NOT EXISTS `Users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Userlevel` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `username_unique` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Tietojen vientiä ei oltu valittu.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

