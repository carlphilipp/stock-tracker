-- Copyright 2013 Carl-Philipp Harmant
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 31, 2013 at 10:03 PM
-- Server version: 5.5.31
-- PHP Version: 5.3.10-1ubuntu3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `stock`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE IF NOT EXISTS `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `liquidity` double NOT NULL,
  `del` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Table structure for table `company`
--

CREATE TABLE IF NOT EXISTS `company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `yahooId` varchar(50) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `market` varchar(50) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  `industry` varchar(50) DEFAULT NULL,
  `sector` varchar(50) DEFAULT NULL,
  `quote` double DEFAULT NULL,
  `yesterdayClose` double DEFAULT NULL,
  `changeInPercent` varchar(50) DEFAULT NULL,
  `yearLow` double DEFAULT NULL,
  `yearHigh` double DEFAULT NULL,
  `yield` double DEFAULT NULL,
  `marketCapitalization` varchar(50) DEFAULT NULL,
  `realTime` tinyint(1) NOT NULL,
  `found` tinyint(1) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `yahooId` (`yahooId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=95 ;

-- --------------------------------------------------------

--
-- Table structure for table `currency`
--

CREATE TABLE IF NOT EXISTS `currency` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency1` varchar(50) NOT NULL,
  `currency2` varchar(50) NOT NULL,
  `value` double NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `currency1` (`currency1`,`currency2`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=21 ;

-- --------------------------------------------------------

--
-- Table structure for table `equity`
--

CREATE TABLE IF NOT EXISTS `equity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `portfolioId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `namePersonal` varchar(50) DEFAULT NULL,
  `sectorPersonal` varchar(50) DEFAULT NULL,
  `industryPersonal` varchar(50) DEFAULT NULL,
  `marketCapPersonal` varchar(50) DEFAULT NULL,
  `quantity` double NOT NULL,
  `unitCostPrice` double NOT NULL,
  `yieldPersonal` double DEFAULT NULL,
  `parityPersonal` double DEFAULT NULL,
  `stopLossLocal` double DEFAULT NULL,
  `objectiveLocal` double DEFAULT NULL,
  `yieldFrequency` varchar(50) DEFAULT NULL,
  `yieldMonth` varchar(50) DEFAULT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `portfolioId` (`portfolioId`,`companyId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=97 ;

-- --------------------------------------------------------

--
-- Table structure for table `follow`
--

CREATE TABLE IF NOT EXISTS `follow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `lowerLimit` double DEFAULT NULL,
  `higherLimit` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `companyId` (`companyId`,`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=42 ;

-- --------------------------------------------------------

--
-- Table structure for table `index`
--

CREATE TABLE IF NOT EXISTS `index` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `yahooId` varchar(50) NOT NULL,
  `value` double NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `yahooId` (`yahooId`,`date`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1207 ;

-- --------------------------------------------------------

--
-- Table structure for table `portfolio`
--

CREATE TABLE IF NOT EXISTS `portfolio` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `currency` varchar(3) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- Table structure for table `sharevalue`
--

CREATE TABLE IF NOT EXISTS `sharevalue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `accountId` int(11) NOT NULL,
  `liquidityMovement` double NOT NULL,
  `yield` double NOT NULL,
  `sell` double NOT NULL,
  `buy` double NOT NULL,
  `taxe` double NOT NULL,
  `portfolioValue` double NOT NULL,
  `shareQuantity` double NOT NULL,
  `shareValue` double NOT NULL,
  `monthlyYield` double NOT NULL,
  `commentary` longtext,
  `details` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1129 ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `password` varchar(128) NOT NULL,
  `email` varchar(50) NOT NULL,
  `locale` varchar(50) DEFAULT NULL,
  `timeZone` varchar(50) NOT NULL,
  `updateHourTime` tinyint(1) DEFAULT NULL,
  `updateSendMail` tinyint(1) DEFAULT NULL,
  `datePattern` varchar(50) NOT NULL,
  `allow` tinyint(1) NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `follow`
--
ALTER TABLE `follow`
  ADD CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
