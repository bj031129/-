/*
 Navicat Premium Data Transfer

 Source Server         : mydatabase
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : mail

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 18/11/2024 12:54:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for contact
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact`  (
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `add_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`username`, `contact_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact
-- ----------------------------

-- ----------------------------
-- Table structure for email
-- ----------------------------
DROP TABLE IF EXISTS `email`;
CREATE TABLE `email`  (
  `mid` int NOT NULL,
  `sender_email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `receiver_email` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `send_time` datetime NULL DEFAULT NULL,
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `read` tinyint(1) NULL DEFAULT NULL,
  `deleted` tinyint(1) NULL DEFAULT NULL,
  `tag` tinyint(1) NULL DEFAULT NULL,
  `send` tinyint(1) NULL DEFAULT NULL,
  `annex_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `summary` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `size` double NULL DEFAULT NULL,
  PRIMARY KEY (`mid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email
-- ----------------------------
INSERT INTO `email` VALUES (30927834, 'sbq', 'sbq', '2024-11-17 21:55:17', 'test1', '123', 0, 0, 0, 1, NULL, NULL, 3);
INSERT INTO `email` VALUES (189730489, 'sbq', 'adm', '2024-11-17 19:16:49', '1312', 'faefea', 0, 0, 0, 1, NULL, NULL, 6);
INSERT INTO `email` VALUES (319157956, 'sbq', 'adm', '2024-11-15 16:25:34', '1231', 'fafeae', 0, 0, 0, 1, NULL, NULL, 6);
INSERT INTO `email` VALUES (326095064, 'sbq', 'user1', '2024-11-17 19:43:55', 'sbq', 'afea', 0, 0, 0, 1, NULL, NULL, 4);
INSERT INTO `email` VALUES (347756294, 'adm', 'user1', '2024-11-15 16:29:30', 'tset', 'geaga', 0, 0, 0, 1, '', NULL, 5);
INSERT INTO `email` VALUES (387825976, 'sbq', 'user1', '2024-11-14 17:16:14', 'hello1', 'hello world!', 0, 0, 0, 1, NULL, NULL, 12);
INSERT INTO `email` VALUES (437905851, 'sbq', 'sbq', '2024-11-17 21:47:48', 'test', 'hello', 0, 0, 0, 1, NULL, NULL, 5);
INSERT INTO `email` VALUES (552279658, 'sbq', 'user1', '2024-11-14 17:34:53', 'test1', '123456789', 0, 0, 0, 1, NULL, NULL, 9);
INSERT INTO `email` VALUES (590006831, 'sbq', 'sbq', '2024-11-17 22:00:41', '12', '1212', 0, 0, 0, 1, NULL, NULL, 4);
INSERT INTO `email` VALUES (675315264, 'sbq', 'user1', '2024-11-14 22:20:35', '123', '13142', 0, 0, 0, 1, NULL, NULL, 5);
INSERT INTO `email` VALUES (814752444, 'sbq', 'user1', '2024-11-14 14:30:49', 'test', 'hello\n', 0, 0, 0, 1, NULL, NULL, 6);
INSERT INTO `email` VALUES (892504644, 'adm', 'adm', '2024-11-15 16:29:30', 'tset', 'geaga', 0, 0, 0, 1, '', NULL, 5);
INSERT INTO `email` VALUES (903774770, 'sbq', 'adm', '2024-11-15 16:25:34', '1231', 'fafeae', 0, 0, 0, 1, NULL, NULL, 6);
INSERT INTO `email` VALUES (904458464, 'sbq', 'adm', '2024-11-15 21:41:38', '12', 'afefa', 0, 0, 0, 1, NULL, NULL, 5);

-- ----------------------------
-- Table structure for filter
-- ----------------------------
DROP TABLE IF EXISTS `filter`;
CREATE TABLE `filter`  (
  `fid` int NOT NULL,
  `ip_address` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`fid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter
-- ----------------------------
INSERT INTO `filter` VALUES (953991533, '192.168.31.117');
INSERT INTO `filter` VALUES (998603723, '192.168.31.118');

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `log_id` int NOT NULL,
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` timestamp NULL DEFAULT NULL,
  `operation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `state` tinyint(1) NULL DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of log
-- ----------------------------
INSERT INTO `log` VALUES (117390945, 'adm', '2024-11-15 17:07:00', '关闭POP3服务', 1, NULL);
INSERT INTO `log` VALUES (171558382, 'adm', '2024-11-15 17:07:13', '开启POP3服务', 1, NULL);
INSERT INTO `log` VALUES (182720671, 'adm', '2024-11-15 16:29:30', '群发邮件: <adm@test.com sbq@test.com user1@test.com >', 1, NULL);
INSERT INTO `log` VALUES (217238307, 'adm', '2024-11-15 20:12:10', '恢复账户:<adm>', 1, NULL);
INSERT INTO `log` VALUES (220896207, 'adm', '2024-11-15 16:39:50', '开启SMTP服务', 1, NULL);
INSERT INTO `log` VALUES (241306678, 'adm', '2024-11-15 16:23:57', '恢复账户:<sbq>', 1, NULL);
INSERT INTO `log` VALUES (249942689, 'adm', '2024-11-15 20:11:53', '注销账户:<adm@test.com>', 1, NULL);
INSERT INTO `log` VALUES (294102457, 'adm', '2024-11-15 16:39:38', '关闭SMTP服务', 1, NULL);
INSERT INTO `log` VALUES (296126104, 'adm', '2024-11-15 16:14:24', '取消过滤账户<adm@test.com >', 1, NULL);
INSERT INTO `log` VALUES (330193925, 'adm', '2024-11-15 20:12:34', '修改SMTP服务端口为:25', 1, NULL);
INSERT INTO `log` VALUES (435221246, 'adm', '2024-11-15 16:14:49', '开启SMTP服务', 1, NULL);
INSERT INTO `log` VALUES (469100539, 'adm', '2024-11-15 16:34:20', '修改SMTP服务端口为:26', 1, NULL);
INSERT INTO `log` VALUES (470315173, 'adm', '2024-11-15 13:56:53', '注销账户:<sbq@test.com>', 1, NULL);
INSERT INTO `log` VALUES (471709512, 'adm', '2024-11-15 16:47:59', '修改SMTP服务端口为:26', 1, NULL);
INSERT INTO `log` VALUES (492419114, 'adm', '2024-11-15 21:51:24', '取消过滤账户<sbq@test.com >', 1, NULL);
INSERT INTO `log` VALUES (511435853, 'adm', '2024-11-15 18:01:28', '添加IP黑名单信息<127.0.0.1 >', 1, NULL);
INSERT INTO `log` VALUES (555039955, 'adm', '2024-11-15 16:38:25', '修改SMTP服务端口为:26', 1, NULL);
INSERT INTO `log` VALUES (615574463, 'adm', '2024-11-15 16:36:18', '修改POP3服务端口为:111', 1, NULL);
INSERT INTO `log` VALUES (652203246, 'adm', '2024-11-15 16:14:33', '修改SMTP服务端口为:26', 1, NULL);
INSERT INTO `log` VALUES (660526884, 'adm', '2024-11-15 18:12:26', '删除IP黑名单信息', 1, NULL);
INSERT INTO `log` VALUES (669522629, 'adm', '2024-11-15 17:05:25', '修改POP3服务端口为:111', 1, NULL);
INSERT INTO `log` VALUES (687309237, 'adm', '2024-11-15 16:14:21', '设置过滤账户<sbq@test.com >', 1, NULL);
INSERT INTO `log` VALUES (691154355, 'adm', '2024-11-15 16:14:00', '添加IP黑名单信息<192.168.31.117 >', 1, NULL);
INSERT INTO `log` VALUES (734172009, 'adm', '2024-11-15 16:14:38', '关闭SMTP服务', 1, NULL);
INSERT INTO `log` VALUES (742527841, 'adm', '2024-11-15 16:13:49', '添加IP黑名单信息<192.168.31.118 >', 1, NULL);
INSERT INTO `log` VALUES (753793756, NULL, '2024-11-14 14:30:49', '群发邮件: <user1@test.com >', 1, NULL);
INSERT INTO `log` VALUES (792637180, 'adm', '2024-11-15 16:31:04', '修改SMTP服务端口为:26', 1, NULL);
INSERT INTO `log` VALUES (880699942, 'adm', '2024-11-15 20:12:41', '修改POP3服务端口为:110', 1, NULL);
INSERT INTO `log` VALUES (914415411, 'adm', '2024-11-15 16:41:57', '修改POP3服务端口为:111', 1, NULL);

-- ----------------------------
-- Table structure for server-msg
-- ----------------------------
DROP TABLE IF EXISTS `server-msg`;
CREATE TABLE `server-msg`  (
  `sid` int NOT NULL,
  `server_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `server_ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `smtp_state` tinyint(1) NULL DEFAULT NULL,
  `smtp_port` int NULL DEFAULT NULL,
  `pop3_state` tinyint(1) NULL DEFAULT NULL,
  `pop3_port` int NULL DEFAULT NULL,
  PRIMARY KEY (`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of server-msg
-- ----------------------------
INSERT INTO `server-msg` VALUES (1, 'test.com', '192.168.137.1', 1, 25, 1, 110);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` char(44) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `account_type` int NOT NULL,
  `latest_login_time` timestamp NULL DEFAULT NULL,
  `latest_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mail_box_size` int NULL DEFAULT NULL,
  `avatar_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `logout` tinyint(1) NULL DEFAULT NULL,
  `forbidden` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('adm', '123', NULL, 1, '2024-11-15 21:45:25', '::1', NULL, NULL, 0, 0);
INSERT INTO `user` VALUES ('sbq', '123', NULL, 0, NULL, NULL, 100, NULL, 0, 0);
INSERT INTO `user` VALUES ('ss', '123', NULL, 0, NULL, NULL, 10, NULL, 0, 0);
INSERT INTO `user` VALUES ('user1', '123', '1234567890', 0, '2024-11-13 23:40:08', '10.69.6.40', NULL, NULL, 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;
