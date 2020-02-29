DROP TABLE IF EXISTS `t_tenant`;
CREATE TABLE `t_tenant`
(
    `id`              bigint(13)                                             NOT NULL COMMENT '资源主键',
    `username`        varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
    `password`        varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
    `sys_id`          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '系统id',
    `organization_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '组织id',
    `user_id`         bigint(13)                                             NOT NULL COMMENT '用户信息id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`              bigint(13)                                             NOT NULL AUTO_INCREMENT COMMENT '资源主键',
    `address`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '地址',
    `telephone`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '电话',
    `sys_id`          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '系统id',
    `organization_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '组织id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;


INSERT INTO `test_user`.`t_tenant`(`id`, `username`, `password`, `sys_id`, `organization_id`, `user_id`) VALUES (1, 'feifei', '123456', 'sysId', 'organizationId', 1);
INSERT INTO `test_user`.`t_user`(`id`, `address`, `telephone`, `sys_id`, `organization_id`) VALUES (1, '湖北武汉', '13587116519', 'sysId', 'organizationId');