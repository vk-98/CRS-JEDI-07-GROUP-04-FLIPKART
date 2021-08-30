/*
MySQL Data Transfer
Source Host: localhost
Source Database: test
Target Host: localhost
Target Database: test
Date: 25-08-2021 12:35:42
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL auto_increment,
  `userId` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `user_id_admin` (`userId`),
  CONSTRAINT `user_id_admin` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` int(11) NOT NULL auto_increment,
  `courseName` varchar(30) NOT NULL,
  `courseDescription` varchar(30) NOT NULL,
  `professorId` int(11),
  `courseFee` double NOT NULL,
  `studentCount` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `professor_id` (`professorId`),
  CONSTRAINT `professor_id` FOREIGN KEY (`professorId`) REFERENCES `professor` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for grade
-- ----------------------------
DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade` (
  `id` int(11) NOT NULL auto_increment,
  `courseId` int(11) NOT NULL,
  `studentId` int(11) NOT NULL,
  `gpa` double NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `course_id_grade` (`courseId`),
  KEY `student_id_grade` (`studentId`),
  CONSTRAINT `course_id_grade` FOREIGN KEY (`courseId`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_id_grade` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL auto_increment,
  `notificationContent` varchar(255) NOT NULL,
  `studentId` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `student_id_notificaton` (`studentId`),
  CONSTRAINT `student_id_notificaton` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for optedcourse
-- ----------------------------
DROP TABLE IF EXISTS `optedcourse`;
CREATE TABLE `optedcourse` (
  `id` int(11) NOT NULL auto_increment,
  `courseId` int(11) NOT NULL,
  `semesterRegistrationId` int(11) NOT NULL,
  `isPrimary` tinyint(1) NOT NULL default 0,
  `isAllotted` tinyint(255) NOT NULL default 0,
  `studentId` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `course_id_optedCourse` (`courseId`),
  KEY `semester_id_optedCourse` (`semesterRegistrationId`),
  CONSTRAINT `course_id_optedCourse` FOREIGN KEY (`courseId`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `semester_id_optedCourse` FOREIGN KEY (`semesterRegistrationId`) REFERENCES `semesterregistration` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_id_optedCourse` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for professor
-- ----------------------------
DROP TABLE IF EXISTS `professor`;
CREATE TABLE `professor` (
  `id` int(11) NOT NULL auto_increment,
  `userId` int(11) NOT NULL,
  `department` varchar(30) NOT NULL,
  `designation` varchar(30) default NULL,
  PRIMARY KEY  (`id`),
  KEY `user_id_professor` (`userId`),
  CONSTRAINT `user_id_professor` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for semesterregistration
-- ----------------------------
DROP TABLE IF EXISTS `semesterregistration`;
CREATE TABLE `semesterregistration` (
  `id` int(11) NOT NULL auto_increment,
  `studentId` int(11) NOT NULL,
  `registrationStatus` tinyint(1) NOT NULL DEFAULT 0,
  `feeStatus` tinyint(1) NOT NULL DEFAULT 0,
  `totalFees` double default NULL,
  PRIMARY KEY  (`id`),
  KEY `student_id_semreg` (`studentId`),
  CONSTRAINT `student_id_semreg` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`) ON DELETE cascade
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(11) NOT NULL auto_increment,
  `userId` int(11) NOT NULL,
  `isApproved` tinyint(1) NOT NULL default 0,
  PRIMARY KEY  (`id`),
  KEY `user_id_student` (`userId`),
  CONSTRAINT `user_id_student` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(30) NOT NULL auto_increment,
  `name` varchar(30) NOT NULL,
  `email` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `phone` varchar(30) default NULL,
  `role` varchar(30) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_email_on_users` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records 
-- ----------------------------

