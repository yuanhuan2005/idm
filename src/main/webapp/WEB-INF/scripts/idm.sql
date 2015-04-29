/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.6.11 : Database - idm
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`idm` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `idm`;

/*Table structure for table `t_idm_accesskey` */

DROP TABLE IF EXISTS `t_idm_accesskey`;

CREATE TABLE `t_idm_accesskey` (
  `accessKeyId` char(64) NOT NULL,
  `secretAccessKey` text,
  `status` char(32) DEFAULT NULL,
  `ownerId` char(64) DEFAULT NULL COMMENT 'userId or accountId',
  `createDate` char(32) DEFAULT NULL,
  PRIMARY KEY (`accessKeyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_accesskey` */

insert  into `t_idm_accesskey`(`accessKeyId`,`secretAccessKey`,`status`,`ownerId`,`createDate`) values ('Ce2EUWzxXCSZJaybdevwaHEMW4Mqlymh','BC1678D8A0A7D0E7F2F39A2775CF1FAAB668FD17403103276B9820EE45515E8209F8A8A03F0718DC759D2B4FE5849D6A7A926104DF67C0215F4FE5F42CFC777B','active','12349a87-be24-4868-ad94-4cae028e8bfe','2014-04-09T10:30:00Z'),('CSkk84RTAXNWuK7QkhlPrfpEkYaqYCEe','ECCCEDF3DCCC46FBFDC53233BF9D92DAA70FE93ED1507F4AA4982B07A699816CD4B64D513BDFA4349F6DB3B8DD0F8A5D7A926104DF67C0215F4FE5F42CFC777B','active','15e453d7-25fc-4d5d-a616-5325055abc01','2014-04-09T10:30:00Z'),('enzBMQrymXjLiph4XZeIR0QyASfAEO2F','8B24175C9D69886FFBD4D9FC53BA3B05B668FD17403103276B9820EE45515E8209F8A8A03F0718DC759D2B4FE5849D6A7A926104DF67C0215F4FE5F42CFC777B','active','7461c1a8-1464-4a2c-8457-ba4009d21826','2014-04-16T10:18:43Z'),('i8jqjWMwE2ONdqzC590tb15cnHwdbiKg','941358E4711B9761471D1475D85FBB7354F9FB56A64859122082EEB2FDD4420600C0430A37D8695B7ABF9A705B0732287A926104DF67C0215F4FE5F42CFC777B','active','15e453d7-25fc-4d5d-a616-5325055abc01','2014-04-16T09:53:52Z'),('jI0BuobD5gJ0njNGdI31C9tZcI9wKYKT','0D0969F9563EB4DFC1FFAB62B25A0F957979B26C9D934946E187B96E46085CAAE966E85AC99DEC2E2B23456EADA4C44C7A926104DF67C0215F4FE5F42CFC777B','active','c6599a87-be24-4868-ad94-4cae028e8bfe','2014-04-16T10:19:56Z'),('puKTNQWAKRJga1XQ3r0OlZFf0bIAN8zj','A43E104EBA6951C2213377DC78B624278ADCBA982199E2F0EF835938BD9A88C6F0CD68EAF413786CA58649ABD39DC5FC7A926104DF67C0215F4FE5F42CFC777B','active','3ce7e30c-d61a-4320-9685-7841e4dd76b1','2014-04-16T10:19:56Z'),('SpkEUWzxXCSZJaybdevwaHEMW4Mqlymh','15960E08A935A0FB117D9D93136ED491B668FD17403103276B9820EE45515E8209F8A8A03F0718DC759D2B4FE5849D6A7A926104DF67C0215F4FE5F42CFC777B','active','8c9482e8-cff4-4c22-beff-dcd754625cb2','2014-04-16T10:18:43Z');

/*Table structure for table `t_idm_group` */

DROP TABLE IF EXISTS `t_idm_group`;

CREATE TABLE `t_idm_group` (
  `groupId` char(64) NOT NULL,
  `groupName` char(64) DEFAULT NULL,
  `accountId` char(64) DEFAULT NULL,
  `description` text,
  `createDate` char(32) DEFAULT NULL,
  PRIMARY KEY (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_group` */

insert  into `t_idm_group`(`groupId`,`groupName`,`accountId`,`description`,`createDate`) values ('980b7a09-6c8b-4927-a904-1e17ba9532e0','RkitGroup001','15e453d7-25fc-4d5d-a616-5325055abc01','group_test_for_rkit','2014-04-09T16:36:53Z');

/*Table structure for table `t_idm_ip_request_logs` */

DROP TABLE IF EXISTS `t_idm_ip_request_logs`;

CREATE TABLE `t_idm_ip_request_logs` (
  `ipaddress` char(32) NOT NULL DEFAULT '',
  `requestTimes` int(16) NOT NULL DEFAULT '0',
  `currentMinute` char(32) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_ip_request_logs` */

/*Table structure for table `t_idm_policy` */

DROP TABLE IF EXISTS `t_idm_policy`;

CREATE TABLE `t_idm_policy` (
  `policyId` char(64) NOT NULL,
  `policyName` char(128) NOT NULL,
  `policyDocument` text NOT NULL,
  `ownerType` char(16) DEFAULT NULL COMMENT 'user/group',
  `ownerId` char(64) DEFAULT NULL COMMENT 'userId/groupId',
  PRIMARY KEY (`policyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_policy` */

insert  into `t_idm_policy`(`policyId`,`policyName`,`policyDocument`,`ownerType`,`ownerId`) values ('2661f1a8-2464-4a2c-8457-ba4009d21847','read','[{\"effect\":\"allow\",\"resource\":\"jobs:*\"}]','user','7461c1a8-1464-4a2c-8457-ba4009d21826');

/*Table structure for table `t_idm_user` */

DROP TABLE IF EXISTS `t_idm_user`;

CREATE TABLE `t_idm_user` (
  `userId` char(64) NOT NULL,
  `userType` char(16) DEFAULT NULL,
  `userName` char(128) DEFAULT NULL,
  `accountId` char(64) DEFAULT NULL,
  `createDate` char(32) DEFAULT NULL,
  `password` text,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_user` */

insert  into `t_idm_user`(`userId`,`userType`,`userName`,`accountId`,`createDate`,`password`) values ('000aeb23-800d-49c3-a38f-86da1da26544','user','Jason','15e453d7-25fc-4d5d-a616-5325055abc01','2014-04-09T14:39:59Z','E49A34E02D1546417BEAB25D3A607654'),('12349a87-be24-4868-ad94-4cae028e8bfe','inner','rkit',NULL,'2014-04-16T10:19:55Z','E49A34E02D1546417BEAB25D3A607654'),('15e453d7-25fc-4d5d-a616-5325055abc01','account','admin','3ce7e30c-d61a-4320-9685-7841e4dd76b1','2014-04-09T10:30:00Z','E49A34E02D1546417BEAB25D3A607654'),('3ce7e30c-d61a-4320-9685-7841e4dd76b1','admin','admin',NULL,'2014-04-09T10:30:00Z','E49A34E02D1546417BEAB25D3A607654'),('7461c1a8-1464-4a2c-8457-ba4009d21826','user','Bob','15e453d7-25fc-4d5d-a616-5325055abc01','2014-04-09T10:30:00Z','E49A34E02D1546417BEAB25D3A607654'),('ae38844d-0733-40ce-b846-e8d79d61871a','user','NewTom','15e453d7-25fc-4d5d-a616-5325055abc01','2014-04-09T10:30:00Z','E49A34E02D1546417BEAB25D3A607654'),('c6599a87-be24-4868-ad94-4cae028e8bfe','account','Bob','3ce7e30c-d61a-4320-9685-7841e4dd76b1','2014-04-16T10:19:55Z','E49A34E02D1546417BEAB25D3A607654');

/*Table structure for table `t_idm_user_group_map` */

DROP TABLE IF EXISTS `t_idm_user_group_map`;

CREATE TABLE `t_idm_user_group_map` (
  `userId` char(64) NOT NULL DEFAULT '',
  `groupId` char(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`userId`,`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_user_group_map` */

insert  into `t_idm_user_group_map`(`userId`,`groupId`) values ('7461c1a8-1464-4a2c-8457-ba4009d21826','980b7a09-6c8b-4927-a904-1e17ba9532e0');

/*Table structure for table `t_idm_user_request_logs` */

DROP TABLE IF EXISTS `t_idm_user_request_logs`;

CREATE TABLE `t_idm_user_request_logs` (
  `userId` char(64) NOT NULL DEFAULT '',
  `requestTimes` int(16) NOT NULL DEFAULT '0',
  `currentMinute` char(32) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_idm_user_request_logs` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
