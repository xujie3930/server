/*
Navicat MySQL Data Transfer

Source Server         : 183.3.221.239
Source Server Version : 50721
Source Host           : 183.3.221.239:5810
Source Database       : demo

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2020-11-06 16:25:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES ('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '初始化密码 123456');
INSERT INTO `sys_config` VALUES ('3', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '深色主题theme-dark，浅色主题theme-light');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `dept_code` varchar(20) DEFAULT NULL COMMENT '机构部门编号',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `site_code` varchar(20) DEFAULT '' COMMENT '所属网点',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `mobile` varchar(11) DEFAULT '' COMMENT '手机号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(500) DEFAULT '' COMMENT '地址',
  `dept_function` varchar(500) DEFAULT '' COMMENT '部门职能',
  `people_max_num` int(10) DEFAULT NULL COMMENT '最大编制人数',
  `people_min_num` int(10) DEFAULT NULL COMMENT '最低编制',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `remark` varchar(2000) DEFAULT '' COMMENT '备注',
  `create_by` varchar(255) DEFAULT '' COMMENT '创建ID',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(255) DEFAULT '' COMMENT '修改者id',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('100', '1000', '0', '0', '敏思达科技', '0', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('101', '1010', '100', '0,100', '深圳总公司', '1', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('102', '1020', '100', '0,100', '广州分公司', '2', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('103', '1030', '101', '0,100,101', '研发部门', '1', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('104', '1040', '101', '0,100,101', '市场部门', '2', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('105', '1050', '101', '0,100,101', '测试部门', '3', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('106', '1060', '101', '0,100,101', '财务部门', '4', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('107', '1070', '101', '0,100,101', '运维部门', '5', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('108', '1080', '102', '0,100,102', '市场部门', '1', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:17');
INSERT INTO `sys_dept` VALUES ('109', '1090', '102', '0,100,102', '财务部门', '2', '', '敏思达', '', '15888888888', '', '', '', null, null, '0', '0', '', '', 'admin', '2018-03-16 11:33:00', '', 'ry', '2020-11-06 08:20:18');

-- ----------------------------
-- Table structure for sys_dept_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_post`;
CREATE TABLE `sys_dept_post` (
  `dept_code` varchar(20) NOT NULL COMMENT '部门编号',
  `post_code` varchar(20) NOT NULL COMMENT '岗位编号',
  `post_name` varchar(50) DEFAULT NULL COMMENT '岗位名称',
  PRIMARY KEY (`dept_code`,`post_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机构部门与岗位关联表';

-- ----------------------------
-- Records of sys_dept_post
-- ----------------------------
INSERT INTO `sys_dept_post` VALUES ('2', '2', '研发');
INSERT INTO `sys_dept_post` VALUES ('2', '3', '测试');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1', '1', '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '性别男');
INSERT INTO `sys_dict_data` VALUES ('2', '2', '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '性别女');
INSERT INTO `sys_dict_data` VALUES ('3', '3', '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '性别未知');
INSERT INTO `sys_dict_data` VALUES ('4', '1', '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '显示菜单');
INSERT INTO `sys_dict_data` VALUES ('5', '2', '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES ('6', '1', '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data` VALUES ('7', '2', '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '停用状态');
INSERT INTO `sys_dict_data` VALUES ('8', '1', '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data` VALUES ('9', '2', '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '停用状态');
INSERT INTO `sys_dict_data` VALUES ('10', '1', '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '默认分组');
INSERT INTO `sys_dict_data` VALUES ('11', '2', '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统分组');
INSERT INTO `sys_dict_data` VALUES ('12', '1', '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统默认是');
INSERT INTO `sys_dict_data` VALUES ('13', '2', '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统默认否');
INSERT INTO `sys_dict_data` VALUES ('14', '1', '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '通知');
INSERT INTO `sys_dict_data` VALUES ('15', '2', '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '公告');
INSERT INTO `sys_dict_data` VALUES ('16', '1', '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data` VALUES ('17', '2', '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '关闭状态');
INSERT INTO `sys_dict_data` VALUES ('18', '1', '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '新增操作');
INSERT INTO `sys_dict_data` VALUES ('19', '2', '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '修改操作');
INSERT INTO `sys_dict_data` VALUES ('20', '3', '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '删除操作');
INSERT INTO `sys_dict_data` VALUES ('21', '4', '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '授权操作');
INSERT INTO `sys_dict_data` VALUES ('22', '5', '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '导出操作');
INSERT INTO `sys_dict_data` VALUES ('23', '6', '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '导入操作');
INSERT INTO `sys_dict_data` VALUES ('24', '7', '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '强退操作');
INSERT INTO `sys_dict_data` VALUES ('25', '8', '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '生成操作');
INSERT INTO `sys_dict_data` VALUES ('26', '9', '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '清空操作');
INSERT INTO `sys_dict_data` VALUES ('27', '1', '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '正常状态');
INSERT INTO `sys_dict_data` VALUES ('28', '2', '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '停用状态');
INSERT INTO `sys_dict_data` VALUES ('29', '1', '总部', '1', 'sys_site_rank', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '网点机构级别总部');
INSERT INTO `sys_dict_data` VALUES ('30', '2', '中心', '2', 'sys_site_rank', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '网点机构级别中心');
INSERT INTO `sys_dict_data` VALUES ('31', '3', '网点', '3', 'sys_site_rank', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '网点机构级别网点');
INSERT INTO `sys_dict_data` VALUES ('32', '4', '代理', '4', 'sys_site_rank', '', '', 'N', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '网点机构级别代理');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1', '用户性别', 'sys_user_sex', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '用户性别列表');
INSERT INTO `sys_dict_type` VALUES ('2', '菜单状态', 'sys_show_hide', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES ('3', '系统开关', 'sys_normal_disable', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统开关列表');
INSERT INTO `sys_dict_type` VALUES ('4', '任务状态', 'sys_job_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '任务状态列表');
INSERT INTO `sys_dict_type` VALUES ('5', '任务分组', 'sys_job_group', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '任务分组列表');
INSERT INTO `sys_dict_type` VALUES ('6', '系统是否', 'sys_yes_no', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '系统是否列表');
INSERT INTO `sys_dict_type` VALUES ('7', '通知类型', 'sys_notice_type', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '通知类型列表');
INSERT INTO `sys_dict_type` VALUES ('8', '通知状态', 'sys_notice_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '通知状态列表');
INSERT INTO `sys_dict_type` VALUES ('9', '操作类型', 'sys_oper_type', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '操作类型列表');
INSERT INTO `sys_dict_type` VALUES ('10', '系统状态', 'sys_common_status', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '登录状态列表');
INSERT INTO `sys_dict_type` VALUES ('11', '网点机构级别', 'sys_site_rank', '0', 'admin', '2020-06-16 10:09:19', 'ry', '2020-06-16 10:09:30', '网点机构级别列表');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1', '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_job` VALUES ('2', '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_job` VALUES ('3', '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务调度日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_lanres
-- ----------------------------
DROP TABLE IF EXISTS `sys_lanres`;
CREATE TABLE `sys_lanres` (
  `ID` varchar(32) NOT NULL,
  `code` varchar(50) DEFAULT NULL COMMENT '编号',
  `strid` varchar(300) NOT NULL COMMENT '简体中文',
  `lan1` varchar(300) DEFAULT NULL COMMENT '语言1',
  `lan2` varchar(300) DEFAULT NULL COMMENT '语言2',
  `lan3` varchar(300) DEFAULT NULL COMMENT '语言3',
  `lan4` varchar(300) DEFAULT NULL COMMENT '语言4',
  `lan5` varchar(300) DEFAULT NULL COMMENT '语言5',
  `VISIBLE` int(1) DEFAULT '1' COMMENT '是否可见',
  `GROUPTYPE` int(1) DEFAULT NULL COMMENT '分组类型',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_by_name` varchar(200) DEFAULT NULL COMMENT '创建人名称',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '最后修改人',
  `update_by_name` varchar(200) DEFAULT NULL COMMENT '最后修改人名称',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `deleted_status` varchar(1) DEFAULT '1' COMMENT '删除状态：0删除 1未删除',
  `parm1` varchar(50) DEFAULT NULL COMMENT '预留字段',
  `parm2` varchar(50) DEFAULT NULL COMMENT '预留字段',
  `parm3` varchar(50) DEFAULT NULL COMMENT '预留字段',
  `parm4` varchar(50) DEFAULT NULL COMMENT '预留字段',
  `parm5` varchar(50) DEFAULT NULL COMMENT '预留字段',
  `app` varchar(10) DEFAULT NULL COMMENT 'app标识',
  PRIMARY KEY (`ID`,`strid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='多语言配置表';

-- ----------------------------
-- Records of sys_lanres
-- ----------------------------
INSERT INTO `sys_lanres` VALUES ('009a7630c5a565d9e78c0f04f4f2aa2e', '9f943b76', '登录地址', 'login address', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('013629d5b816ce30823ca141505a9934', 'cbb65e1e', '手机号码', 'mobile', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('01b218d921d69d800d28fe6292621496', 'f9d2570c', '审核日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('01b8e17ccf58ec3b404f980c00e53ab0', '8dec0f59', '请输入初始密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('02a3eef8a954f0275ee78c9ecb975042', '807b3a52', 'Notice of arrival', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('02b12aaac33424e57c2ef3d1d35caab8', 'c4455365', '所属客户名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0336da1bc4fefddc85f730d18e2af9d0', '2620bd73', '请输入地区编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0365e37f1999345c005199fd9d026f69', 'df307c91', '请选择显示排序', 'please select show order', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0381db58b351900ec35ddac6920ea754', 'e32b5228', '产品名称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('03b9e3b4763e59f318f98dd6a8cd777f', '5b9e135f', '录单日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('03fd2ae6486dadf4bbf33fec73384d0a', 'd7007c9b', '有效时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0401a73675265b4537e1fcb4ce282700', '08134320', '请输入费用类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04548496d64c61d3eef4c9d1fe7db5f0', '5cf20ba3', '问题件回仓', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04aad2736da497274119576fbdeb60fa', '66d6ab90', '登录异常', 'abnormalLogin', null, null, null, null, '1', null, null, null, '2020-11-05 18:37:03', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04b23683d38d5132140a451734a416e6', '1b0758ed', '订单下单成功', 'Order placed successfully', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04b6ad65d952766dda34e625a31f6048', 'ff18bd7a', '体积高', 'High volume', 'إنه كبير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04de4f829e3338d41d3775022ca6dab6', '3b86eba6', '选择业务员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('04e3ef1ff0b0048ce5ca0650162281ab', 'a285284d', '请输入旧密码', 'pleaseEnterYourOldPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:59', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('05945091f7e5f4c52bc3e809a624ea2f', '930a61b7', '操作成功', 'operateSuccess', null, null, null, null, '1', null, null, null, '2020-11-05 19:01:56', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('05bc59a07588d76b2717b0937a441c35', '6a7c3298', '轨迹', 'guide', '_w2324', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0662201a8224526421a749dec5c4ae00', '5d86f259', '发件确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('067d9c16f2c940f1d60dc162727a6c7f', '0c4968f6', '长', 'long', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('06826db9faff17f7952d2cd15f36e658', 'ca225318', '暂无预警消息', 'noEarlyWarningMessage', null, null, null, null, '1', null, null, null, '2020-11-05 19:30:12', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('07da01174a0f4088be78539eb4a6e988', '2674', '运单状态', 'waybillStatus', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('082964543b02401d9149a2211ac75335', '4792', '请输入子单号', 'subWaybillNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0867be1d2fa93c0e3d6592d650ed30f9', '3a4f749f', '中文', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('08977726de873fb4e97399171bf06f32', '5fa99326', '寄件详细地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('08cfefa296ac9f6968f75f9e2b172690', '858f1cfb', '公告消息', 'announcementMessage', null, null, null, null, '1', null, null, null, '2020-11-05 19:29:47', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('08e53abf8a037e1d0417c533249554fc', '3b4ae6ed', '合计', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0948ce78d53ee4163faa6e5e98f8570f', 'a5f0d491', '库内查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0977ebe41bd44a21886adda4f2e06100', '9180', '服务方式', 'serviceMode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('09bbc1c528de3550cf41daa8f8108f7e', '159f886c', '【】Jeddah派件员:测试1026123456正在为您派件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('09c4837b2dca841c66d3ad6943878c6f', '0403adf5', '发件扫描', 'Sending scanning', 'سندینگ اسکنینگ', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0a09a42af2ec456c4a8dea873dcca4b0', '914ccce4', '打印机连接失败', 'printerConnectionFailed', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:38', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0a11219cbf74f26648b35225af5aefe1', '2c56bf36', '留仓件类型选择', 'stayWarehousePieceTypeSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:48', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0a590dfeed3100de4309a2436543841f', 'f7068843', '重新调派人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0b8432276225455f8dcf96eafe506759', '2498', '代收货款', 'cod', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0bfad5e1017850a876c62dff0a37637d', '8c507736', '请输入地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0c3a3f85063127938f1f53db729df703', 'd87fa33d', '所属城市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0c4ac37b4d517c72f7cfac47afd1b66c', '782069e2', '客户员工资料', 'customer employee information', 'معلومات موظف العميل', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0c686ed91c97407884848b0b059b7e6a', '7163', '寄件人电话', 'senderPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0c722bef16c65b546629343ac0327df6', '3345d2cc', '收件详细地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0c8c0aa4fdd6035be2510767bb547e09', '1bf462a5', '网点到件通知', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0cf941fa27e7eed44ada432b20710505', 'ff62d76a', '扫描员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0cff485b9438433181e71d73ffcb1c6b', '649', '件数', 'pieces', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0d078f50eb9fdd7e4210166a4a27118f', '39b854ed', '记住密码', 'rememberPassword', null, null, null, null, '1', null, null, null, '2020-11-05 18:35:27', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0d2fb6a908fd50a8fffac94b6c239061', 'a5c1bb68', '打印子单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0d5b009edf6768823a826afa685a34b8', '86e79a76', '审核', 'audit', 'audit', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0d78757fbbd940a19a8ca37ed6009ecc', '3094', '请输入计费重量', 'chargeableWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0d99c87269084e8f817c36d72d18c80a', '7b84842e', '管理方式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0dbcef47ddfb2d3a86f9aae0d9ec47fe', '29f86d24', '客户报价', 'Customer quotation', 'اقتباس العميل', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0dc44b1197b6b09f9f1353ba005a93e9', '35159047', '晚班', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0e429e822a37ea512e4e33f2c567323a', '59d85d85', '扫描件已经存在', 'scanPieceAlreadyExists', null, null, null, null, '1', null, null, null, '2020-11-05 19:28:28', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0f864213e71c39320ae1d119ead9bfe1', '627925b8', '中班', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0fcd307ce148d72995b9bb00a31d441a', '7b39e327', '员工资料', 'Staff profile', 'الملف الشخصي للموظفين', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('0fd8dfe4644fc0d78263fc7bd4184358', '8932043c', '收件人省', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1', '1001', '登录', 'log in', 'تسجيل الدخول على الفور', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('10', '1010', '网点资料', 'Network information', 'معلومات الشبكة', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('100a621ed2cb56d593c21e38b6b3ca9b', '5b737155', '代收限额', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('100cbd4f6e2b2eed1979919ab70d9735', '6d6b45ec', '新增主类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1023927199e4850e3f5adbeac2361e57', '886ff709', '请选择留仓件类型', 'pleaseSelectTheStayWarehousePieceType', null, null, null, null, '1', null, null, null, '2020-11-05 19:13:23', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('11', '1011', 'APP菜单设置', 'APP System Management', 'إعدادات قائمة APP', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('114bdc24987b2254a16883954b99e7ca', 'ffc6da9e', '订单管理', 'Order Manager', 'إدارة الطلبات', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('115224c8eeec341aacd777ab0784a6b8', '926d5be5', '目的地点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('12', '1012', 'APP角色权限', 'APP Role Permissions', 'أذونات دور APP', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('126d25122bb58d0beefc5f571ff81c4e', 'a6bdfea3', '英文描述', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('12e57a2d6498b4a6e21f37d76cce3bd0', '34bea78d', '现金收款管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:50', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('13', '1013', '语言设置', 'Language Settings', 'اعدادات اللغة', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1336ee4415832a0e35ee1f0f774846b3', 'f3e0bece', '请选择问题类型', 'pleaseSelectTypeOfQuestion', null, null, null, null, '1', null, null, null, '2020-11-05 18:46:56', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('138e52b2e27345d78983914626d1cec6', '4908', '结算重量', 'balanceWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('13a7f19ae2f541069c56be708b722513', '6725', '体积重量', 'volumeWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('13ad7ddc64114b3c9b6dd3643fefa9ce', '8477', '收件邮编', 'receiverPostcode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('13ec3ee72b1c6b58bb623501cf620fc3', 'eefffc21', '货单到件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('14', '1014', '日志管理', 'Log management', 'إدارة السجل', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('142309496da1ccbbecd54c538e87f6d9', '25f86e8e', '没有文件', 'noFiles', null, null, null, null, '1', null, null, null, '2020-11-05 18:34:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('143490cece1e45ae816af6edf3dc5a5a', '7951', '请输入寄件地址', 'senderAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('15', '1015', '登录日志', 'Login log', 'سجل الدخول', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('15bc0680fb3ad6c35383631a18bc4436', '3de34857', '地区编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('16', '1016', '操作日志', 'Operation log', 'سجل العمليات', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('16238ad67e0b68c03c9aeebb57c5bd5d', '12e619b3', '派送中', 'Delivery in progress', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('163b07e6eb6c020a29528cadb0949f7a', 'bf3b3271', '发件', 'sendPiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:09:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1688e775fe34926a9edcdb83baf88b67', '48f6ce41', '清关状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('16cc72d341d9e49f4df84fe54e33b5eb', '7a5fde12', '客户信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('17', '1017', '新增', 'Add', 'أضف', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('170fe086c8a1b8edc94aa49c6844c911', '19f385f7', '登录状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('172fc4a62e2c1429901b29ab62c8b2b5', 'ae6768ca', '正在登录', 'isLogin', null, null, null, null, '1', null, null, null, '2020-11-05 18:37:12', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('174d160b142e32086b61afabb4056c2d', 'd559c24e', '创建人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1755ccb95773f1b1896baf017dfb50bb', '9c8a6ba4', '分拨中心标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('176aa40bda00307b5600e8410441eed2', '03de6bbb', '地区名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('18', '1018', '查询', 'Inquire', 'استعلام', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('183eeab1dec24635b09c0e05a6af1e09', '1184', '寄件地址', 'senderAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('18646b0e5b88437696a80dea7b01c3a3', '8512', '收件手机', 'receiverTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1894780571f1a6844fd615f3cbb58c98', '6aef0496', '已接单', 'alreadyReceptionOrder', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:18:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('189b06379e464724aef1f3dfe951e237', '7391', '订单编号', 'orderNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('18b470c346880a2a2e534a0eafa7db59', '60bce9b7', '寄件区', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('18e9769ee5f504839f06ddf505244214', '4975bf20', '组件demo', 'Component demo', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('18ea104ac4dead12a2ab568dfdcb21a0', '78b9c1d9', '	\r\n进位舍2', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('19', '1019', '清空', 'Empty', 'فارغة', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:52', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1905a47af53fb1531bc3db16bda1a6fd', '1f527256', '未收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('19140554d35561c7122c230b9c8ee10d', 'af0bd63b', '请输入客户', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('195aebecc0331e2c70f205961d2893d2', 'fb4e36ff', 'GFS 业务系统', 'GFS EXPRESS SYSTEM', 'GFS نظام العمل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('19a355dfd8cf42e696ba4b9014474f91', '8954', '寄件手机', 'senderTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1a33f80a232f4f1d306ba0e6633380b0', '60ab2aa9', '分组类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1a68280baa8dea6d195d19c61ca08e04', '9632351b', '留仓说明', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1a6c8803e55d4beeadf3f7642f658440', '1339', '物品类别', 'itemCategoryCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1a942a8809b77c6ce5a34e13cc400dff', '7facf5ce', '请输入网点编号', 'pleaseEnterDotNumber', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:34:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1a9f7385b599a310e52518f13a08c9fe', 'f529413b', '待上传数量', 'awaitUploadNumber', null, null, null, null, '1', null, null, null, '2020-11-05 18:49:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1ab7318c0db1785c6130a6923784a31e', '44866707', '收件详情', 'receivePieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:02:24', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1adb83bbb689b7634b6945faf91ad12d', '3f9b2780', '取件网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1b2ebd42ce3d3902d3da018c138c959f', 'c4ed773b', '主机', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1b586c0ed7c9aa117237285472f9e44d', '000ab30c', '选择模板', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1b9cc34ae850764d278048676214e587', '6efa1698', '返回参数', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1b9dc5de69287901d98a17768107c701', '5292b943', '录入部门', 'Entry department', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1bbda49a0eb6425a81426753e5d922f0', '9210', '请输入代收货款币别', 'codCurrencyCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1bcf97f093ab20926332bef8d2ff1b21', '81bc6fdf', '所属国家', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1bd706729de10567408b36134c67de52', 'a0737a55', '保存', 'save', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1bf0ac0d8b635d10e038df97ec9fab47', '854d54bc', '请输入模板类型', 'Please enter the template type', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1c02c6107009df786e9bf76da63f85d5', '828e55fa', '请输入短信内容', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1c23237a0c9833a88c418c703c2ac86b', 'd7920601', '当日问题件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:54', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1c2c03f144c40652839e1ec6b26800d6', 'c138607e', '到件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1c73b35b9231afca383bf53b0f2183c2', 'a22cdc81', '客服管理', 'Customer service management', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1ca2c2d7a793f3d74c04f0864e0b9222', '6c60ac65', '请输入编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1cfa721efa91d208ea1b01b04710cae8', '90f11543', '有效终止时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1d985ed338bb7ce92a1bbe4a80d995e7', 'd6cb36c5', '中心处理内容', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1da113f53967786516e84ab12bf416e4', 'e69b1c10', '退件中', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1dbe13840de26282b4b010c2e8f8582e', 'b0f5da90', 'SA', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:55', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1dbf296f3abe4fb1b0dd4d12644c78c3', '9261', '请输入寄件人电话', 'senderPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1de572f17609e766a4e6163ec1d2d282', '45cd8e39', '调度业务员', 'dispatch salesman', 'مراسل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1e03641e279490b9ae6def7a6f0395e4', 'a678f063', '当日留仓数量', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1e1659b6b900494ad31b2b0d0fbb5ce6', '336d53ea', '请输入有效时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1e356b11af822d5fe3e23ef07484240f', '6ea485db', '订单编号', 'order no', 'رقم الطلب', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1e87d4b726decaef50aa46fb50128d90', '16598695', '扫描清单报表', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1ec16ef40a00486ab3933b2cd20d8c72', '6404', '月结账号', 'monthAccount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1ed3cd9cb8bc71ce980fb33a3abc2299', '4dbd8425', '签收凭证', 'receiptVoucher', null, null, null, null, '1', null, null, null, '2020-11-05 18:42:36', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1f51a3fd9f554f7ba854ca1f35c93f56', '7396', '收件公司', 'receiverCompany', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1f99e9f1cacef810eed6d62182ea3d04', '3f170a47', '角色编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('1fef6a5e69d03b99353f4917e98cf710', '0e8eeba5', '员工编号', 'employee ID', 'هوية الموظف', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2', '1002', '用户名', 'username', 'اسم المستخدم', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('20', '1020', '确定', 'Determine', 'تحديد', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:56', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('207252c977979e4070bb75baae25e29f', '4efe82f7', '展开', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('208086abf57355a215ba0c42a07cfef7', '8ee3b93d', '打印预览', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('208b39785ff2fe440cff23df9e23704a', '65cc3e76', '主类别维护', 'Main category maintenance', 'فئة الصيانة الرئيسية', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('21', '1021', '取消', 'Cancel', 'إلغاء', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('21143372211e496497d0569a56607acf', '3342', '取件员', 'pickerName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('216f66c430d32b2f1876469d45a5073c', '7043ab26', '请输入备注', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('218f4192c49e82e0065233a81e1cc62a', 'ba4e251a', '清关', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('22', '1022', '删除', 'Delete', 'حذف', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('222af21d5a8c767a65189cf4ee415e30', '2ca26d6e', '账户管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('223263abd7c0fc6d7fbd89f539908c6e', 'cadb954a', 'Notice of arrival at outlets', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('228860b46a422289e8b11b8a04e58941', 'f50ed6dc', '请选择上级菜单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('22dc9e861ac4b33c1e5c7e768ee1a2e2', '15613fb8', '收件录单', 'receiptRecordingOrder', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:09:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('22ea8c6e351866e12f1412aed8332e16', '3caa67f1', '月结账单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('23', '1023', '菜单名称', 'Menu name', 'اسم القائمة', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('23012b3ebc21e6980c9da18b86e84c57', '959639bc', '请输入类别名称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('231c2581207a87b05cd31b10026d06ba', 'b72e73e4', '取件员编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('236f0ebb7b1c4828a2ef7b953775d510', '4d906e33', '站点选择', 'siteSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:07:51', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('23965dbb33985335dc7024a017e9e299', '24572a8e', '目的地资料', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('24', '1024', '菜单状态', 'Menu status', 'حالة القائمة', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:57', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('242455aa19b7436f0fbc60015a43bbd5', 'bc06822d', '旧密码和新密码相同', 'OldPasswordIsSameAsNewPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:26:39', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('247a1e68fad2be1c0c586172e1bc9e89', '58d7df1a', '成功', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('248fbc6e596942473513f55d1458d259', '9910695c', '服务产品', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('24ba17f401b02c18ab799229d5de0341', '47b8545f', '中心处理内容', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('25', '1025', '权限标识', 'Authority ID', 'معرف الهيئة', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('25071f4dfc87a8879dcb512dbb73feca', '2d6dcc0b', '运单编号不能为空', 'waybillNumberCannotBeEmpty', null, null, null, null, '1', null, null, null, '2020-11-05 18:42:46', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('251623cbdc3c68efee52a5003f74a560', '8acd96a0', '拨打失败', 'callFailure', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:12', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('256415dfccaf8df4d814148045e9b87e', '2fffd9f4', '选择日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('25c82e0a49cc460751cdf72bf7d6e353', 'cc44f47c', '查看', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('26', '1026', '路由名称', 'Route name', 'اسم الطريق', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('26079e609b4e42f680dbbda64e95c9fc', '5810', '请输入收件邮编', 'receiverPostcode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2627f05f175068ab827ca6ad65d80980', '86cd154a', '业务员*', 'salesman*', null, null, null, null, '1', null, null, null, '2020-11-05 19:13:37', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2689bdb49ec497cfcbe330d8229a2962', 'c6229fff', '上传部分失败', 'uploadPortionFailure', null, null, null, null, '1', null, null, null, '2020-11-05 18:56:15', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('27', '1027', '类型', 'Type', 'انواع من', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('271c84c7d0ce7c13a4f4833a0b8ff9c7', '4d0e44f6', '问题件查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('272569ae043fe2350333d7d1d8c909c7', '471fba9b', '保存失败', 'saveFail', null, null, null, null, '1', null, null, null, '2020-11-05 18:47:15', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('273797dffbabcb4f80f2f74e25d8aa55', 'ac336679', '员工姓名(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('273e0bc1bb51019f31c5cfeec2a48824', '348b503b', '到件', 'arrivePiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('27447d326b5f4dc993f5ef8c9384cdb6', '3065', '所属网点', 'belongsOutlets', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('27607e14053a5f8c1827fdc4fa6f9d4d', '372bfff2', '录入人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2789cff9cf2e816b235ba5d593285131', '32e32de8', '是否可见', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('279a0cd76eea4d2983f33e9456a7de8b', '6868', '寄件日期', 'shippingDate', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('27ebe3bfbefd7494b3f3d0a883e147b1', 'bb620888', '请假', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('28', '1028', '图标', 'icon', 'أيقونة', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('28142fb2546f727310456bbe4cf49a3b', '9a5ec3b5', '基础数据', 'basic data', 'الرجاء إدخال الحساب', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('28378d8d31bf4e0f3d3e1a92ec5c5bf1', '5a8953a8', '排序编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('283c9758ca06f4bd97914912db43710a', 'a9f29bf1', '货单到件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('28cdff0509e0a0ea602e4b38f2a250f2', 'bdffd3ef', 'token为空', 'tokenIsEmpty', null, null, null, null, '1', null, null, null, '2020-11-05 18:36:39', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('29', '1029', '排序', 'Sort', 'فرز', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('29069c295aa92696d518d7ae9fad44bf', '473af779', '编辑菜单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('291c578de43840e9931ca0033f9599e9', '3154', '请选择寄件日期', 'shippingDate', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('29e9048bf18c64d737a3b8bcfb017bc5', '7f659d9b', '收起', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:51:59', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2a32516a134c6b8cb39e03fce471fa1d', 'd0ad765c', '短信类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:00', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2a80bfa7803cf0c314648106902240a2', 'cc0426a5', '推荐', 'recommend', null, null, null, null, '1', null, null, null, '2020-11-05 18:51:29', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2b5f2879646e92aa9b53b0afe48bf70c', '92f2c1c8', '运单编号错误', 'orderNumberError', null, null, null, null, '1', null, null, null, '2020-11-05 18:46:37', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2be8cbd2b91f1b75cedbe37eb6704efb', '38cbff8c', '项目序列号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:00', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2c0c16d2c31bd743069c80ea4271c14d', '5fdf2a8a', '寄件城市', 'sendCity', null, null, null, null, '1', null, null, null, '2020-11-05 19:14:49', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2c75666bb6bcd9c06c238aba619c4ab3', 'df7e415a', '连接成功', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:00', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2c960f58f5d8028a2ab41cb54f1abacb', 'a4cced30', '维护人', 'Registrant', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-09-29 18:36:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2ca2a66f602be943aefc35bc5e938cf0', 'e0890832', '体积重量', 'volume weight', 'الحجم الوزن', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:01', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2d2f8af65dad42dd8c42e073b9b0f4e7', '4627', '标准运费', 'normFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:01', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2de73033e7f6ff78bcec0b29c2d001df', 'd76f84bc', '到付标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:01', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2e44cea2682a4af0321a5c70240c144d', 'b9aa9cbb', '本币币别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:01', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2e918b8306316ba339a432182100bece', 'cb33ee2a', '请选择所属财务中心', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2f115a2ae55f5bb6487e63cd6dff917b', 'a9df3d64', '香港件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('2f33803a79d29759db39638a091ddef3', 'a7db01b5', '产品类型(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3', '1003', '密码', 'password', 'كلمه السر', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30', '1030', '状态', 'status', 'الحالة', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3011f0041600e6c1bdb59490e412d844', '93aeea19', '请输入字典排序', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:02', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30381bc5444745b29115cb3c3d08872e', '8619', '中心审核日期 ', 'centerAuditTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30df05ec30ec791beedffa12510cd410', 'f1d77e07', '签收人*', 'signForPeople*', null, null, null, null, '1', null, null, null, '2020-11-05 21:46:35', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30e3d92e76474fdab65d08308d4baa02', '2608', '退件标识', 'returnFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30ea03a2def45e008f890d19adb5c200', '4f325e81', '台湾件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30f38dccb688ca755b83f1e661be10ed', '6ed18ee9', '测试', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30f3ee67b02c45bccd4f34a469429d99', '49c9aca5', '客户编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('30f40fa9905e37b01146e1fdfaef86c0', '1cbdf13e', '请选择菜单状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('31', '1031', '机构级别', 'Institutional level', 'المستوى المؤسسي', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('315b27996289b41a38b9862dc864ca7b', 'b2c13c95', '电子产品', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('315b657e27297a40a28a6bc6b09149ad', '35c10b9d', '问题件描述', 'problemArticleDescribe', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:38:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3176694acaf83bfb7341378ce2205b61', 'cf948725', '中心到件通知', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('31c0b42e4c8f495abba5630c9dbf7218', '1474', '快件类型', 'expressType', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('31e1fe386424aa2d6d31857a78cceab9', '96eab103', '选择目的地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('32', '1032', '创建时间', 'Creation time', 'وقت الابتكار', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('321d387fe59449139d544d887748693e', '4776', '寄件日期', 'shippingDate', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3223641a5482bf761773d2a0e4d1d339', 'f846e602', '短信内容', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('325a643eae50fafd07824b1d678b373f', 'f283f3c9', '收件人电话：', 'recipientTelephone：', null, null, null, null, '1', null, null, null, '2020-11-05 19:24:00', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('32cfd27b74e946f984e7884aa2527e66', '885', '重算标识', 'backrollFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('32d888c144ab4f9ad83cd74addcde477', '77a8661f', '初始密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('33', '1033', '操作', 'Operating', 'التشغيل', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('34', '1034', '新增菜单', 'New menu', 'قائمة جديدة', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('346d45d41b3e778cecef2d20c241fe5f', '54c0ab41', '重新调派原因', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:04', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('349bde5b211a7e4a0627039ebbdccacf', '93f96b11', 'GFS 大客户平台', 'GFS EXPRESS VIP PLATFORM', 'GFS منصة العميل الرئيسية', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('349f0bf2b4a86b798c13fa97aa24e17f', '6f4650bf', '请选择目的地', 'pleaseSelectDestination', null, null, null, null, '1', null, null, null, '2020-11-05 19:11:38', null, null, '2020-11-05 19:11:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('34e7c1f44864431589d02b3d9ace9a1a', '6869', '锁定人', 'lockPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('34eab2917a25588b7fabd57219d6ac47', 'fbbf3378', '反审时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('35', '1035', '上级菜单', 'Upper menu', 'القائمة العلوية', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('350b25e9174f4d0481af46d5e2579656', '674', '打印标识', 'printFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3540732572bb50b806ccdbce33f81868', 'e4f18009', '派送范围', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('35499d33b76944fba759695619c5e448', '6095', '请输入件数', 'pieces', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3549bd86215443dfa4d9eb0b1b9c97ac', 'da127ddd', '请选择派件员', 'pleaseSelectDeliveryman', null, null, null, null, '1', null, null, null, '2020-11-05 19:12:51', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('356ed7b5232ff08e41eeb96fc95cab21', 'e891228e', '请输入子类别名称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3576eb23674383178da516ce073abdd6', '7fdb2a0f', '表格', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('35b6ca7ffb2356e95842ac6d38ec1687', '4780bfcb', '电子商务件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('36', '1036', '菜单类型', 'Menu type', 'نوع القائمة', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('363a05f734a522ab8522d11cc8053361', '0a0c38e9', '系统模块', 'System module', 'وحدة النظام', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('36a8c7369f1811151b458d0bea9babb2', '1db265a5', '查询结果', 'searchResult', null, null, null, null, '1', null, null, null, '2020-11-05 18:59:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37', '1037', '目录', 'List', 'جدول المحتويات', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('374be4e7a96c537c1b7d4a1c2ce0151d', '40a9389b', '调度时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3751b0589a0347d0bb28badaa0d545d8', '9818', '生鲜条码', 'freshBarCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37647970e40aa2d0050a90ff14451429', 'ed07f1a9', '运单发短信', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3777c4da0f8defb7cb8e729524c58900', '4d02211f', '派件详情', 'deliveryDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:03:07', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37966c10ba083e9b911161bb39a77330', '454c9a06', '已上传数量', 'uploadCount', null, null, null, null, '1', null, null, null, '2020-11-05 18:49:22', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37b0d52017221c0b545e4cacac9a2469', '50f8de93', '月结账单管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37ce0c4bf298cabd86c9794521fdf226', 'f3dd4a19', '短信管理', 'SMS management', 'إدارة الرسائل القصيرة', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37f3ed407a87495c918e9940f67fa5e7', '7934', '中心审核标识', 'centerAuditFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('37f40b6db6fc9dd004653bf373473bda', 'f52f069a', '欧元', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('38', '1038', '菜单', 'Memu', 'قائمة طعام', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3815c2069e97e2dbdac603519699aacc', 'e20aff3c', '请输入类别名称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('38467524b2a12197553a4c15e9836e2d', '1bed92e9', '请选择扫描件', 'pleaseSelectScanPiece', null, null, null, null, '1', null, null, null, '2020-11-05 18:55:28', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('384d982330fab7eeaa998f7943daa674', '300503bd', '文件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('38bab229d657f06f0cfe0e951996cb24', 'a7bd6d76', '扫描明细', 'scanDetail', null, null, null, null, '1', null, null, null, '2020-11-05 18:46:04', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('38cf609c88f44b63bb1ffa30a49462a6', '8582', '修改标识', 'updateFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('39', '1039', '按钮', 'Button', 'زر', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3920b6268b35b94589e7720566c13ac1', '2b060f6a', '仓库管理', 'Warehouse management', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3997bfaacd7d45437c50f4db6e9c9c1b', '394c15b7', '产品名称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('39d89277e5b1c84afb3375559d01c05e', 'ab74c8a0', '中心', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3a0f24e6ffea466a90dbf200a50fcbee', '8383', '签收时间', 'signerTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3adf70f9ff0ab97f17dce7b6b7fc33e5', 'e97f8e15', '关闭所有', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3b03f07d406db26e206e608f478b1dbe', '2007c0b9', '请输入客户全称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3bd0b490df4b9a08f291723bc02292a6', '6c0efc4f', '请输入员工编号', 'pleaseEnterEmployeeNumber', null, null, null, null, '1', null, null, null, '2020-11-05 18:34:52', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3c359813ac34fe3d65bb45a31471aaf0', '7b50631d', '寄件人地址：', 'senderAddress：', null, null, null, null, '1', null, null, null, '2020-11-05 19:23:25', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3ca3b1ae7434ae020698d5c73f13f147', '37624895', '订单导入', 'Import order', 'Import order', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:06', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3cfc55306b1a409f84838c671e5839c4', '7541', '物品类别', 'itemCategoryName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3d13158b0514a4a84674ace582ce56bd', 'b73072f7', '登录密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3d6637d843b5484ecf09d57f541067dc', '5c1f4929', '收件人：', 'ecipients：', null, null, null, null, '1', null, null, null, '2020-11-05 19:23:49', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3d8851357959d138db19aecc7e68f6ac', '2ac4cf32', '员工在职状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3d8f446516ef4073b9792b69cfa027fb', '602', '请输入寄件邮编', 'senderNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3d9296471e72e047222d7fbec2a7f545', 'bc4dd887', '收件区', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3dc03a34ed39a66e20c4b10166f9ad88', 'c7ad6ba0', '登录日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3e080620daaac07075df124a3f45e5b5', '16de54b8', '所属财务中心', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3e5954dc664a86ed0cf1b62407c63b36', '531f7a86', '请输入所属网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3e7da233a341abc1637e681d310a0db4', 'd8368de6', '库位维护', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3e809e40cc0be28781b74d2ecb90b86e', '7a19c0a3', '选择网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3e9e449b0f92ca0fab00bd9d75a71e30', 'e913eaa9', '扫描记录查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3ee2fd19016e19ae63368f92dd0688d6', 'e84e5de0', '请输入产品类型(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3f0f5f6f5d774f28915b07ee1ea74049', '59191dc5', '已签收', 'Signed in', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3f367d9ba2e70a60d799a2211abdd45b', '19b6404d', '修改', 'edit', 'تعديل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3f43ff136c9648ecafe29faa204ecd28', '1086', '目的网点', 'destSiteCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('3f8f44d9d30cdacf25cf60ce9260e437', '488e07c7', '已签收已回款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4', '1004', '验证码', 'verification code', 'رمز التحقق', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('40', '1040', '显示排序', 'Display sort', 'نوع العرض', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('404b26bbf49f00268b4a98437b0e28b5', '4ad03893', '操作时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4058ba2fcb41c46072acbcb0f8aac144', '1234ecb0', '到付标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('407e3989608c47cc927cd74b8aeb2e7d', '7416', '录入方式', 'entryMode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('40ad27dffa75df9e9fcab03b2862f337', '3d286762', '快件跟踪查询', 'Express inquiry', 'Express inquiry', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('40b4d7042f779c1265b44d48974f613b', '40936c15', '客服', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('41', '1041', '目录名称', 'Directory name', 'اسم الدليل', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('41b0ed5cc1af2ab5cd365a0a0609b427', '09ba422a', '首页', 'home page', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('41e181266597ba8d59f96b44e3b130b6', '647910f3', '下单时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('42', '1042', '目录图标', 'Directory icon', 'رمز الدليل', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('420931216f7a45bdbb2b4f5f521a3be9', '7960', '转入单号', 'shiftToNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('426fd680613b3ab484e5be2b5279b2de', 'aee444f2', '人民币', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4297df6c0c384dd6c7aabc0a27583517', 'ce07328d', '证件姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('42bac190b68fda722319fca315502292', '224d8c8c', '打印主单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('43', '1043', '路由名称', 'Route name', 'اسم الطريق', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4300ae7f506d4de4308de94cf8cf7cb4', '8eedf454', '营业网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('43df7a18ad155bfca35f8d89f5c7dbb2', 'cbdda7a8', '请输入寄件人详细地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('44', '1044', '组件路径', 'Component path', 'مسار المكون', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('447c3a696471a6b75c7df8ace935befe', '1d20e0fe', '签收人类型', 'signPersonType', null, null, null, null, '1', null, null, null, '2020-11-05 18:39:36', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('44ac3791176e3993f44d108fbe05ac2a', '82416f96', '请选择显示状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('44ee69ba2ad3710d9295a07b1504dc55', 'd0137d08', '生鲜递件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('45', '1045', '机构级别', 'Institutional level', 'المستوى المؤسسي', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('45318d8cc99b974a06ab42407128ba84', 'fc9f9ae0', '票量', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('453e9bdcc88788e46ddbe87d8043d101', '7772837f', '异常查看', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('46', '1046', '菜单状态', 'Menu status', 'حالة القائمة', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4663c9b2bc2b42e9a7bb488f750c8fe0', '9540', '寄付运费', 'senderFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('466832b4e34df433ee3dc0836fec66dc', '6043d99f', '用户类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('468810509732b591ccdee18a0ac8f159', '9dbc8309', '计费信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('47', '1047', '显示状态', 'Display state', 'حالة العرض', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('47552ad154fb6fa9537c4056a20368bf', '91e5c5f9', '子类别名称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('47dfe6f434865c6ce72b661e82264389', '4d670bb9', '登记网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('48', '1048', '菜单图标', 'Menu icon', 'رمز القائمة', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('48340d258cb751c3319ec0f5257099b3', 'e133975b', '清空日志', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('48481ddea6ebb31bf91cf7acc355789f', '2d88241f', '收件人国家', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('485198c8f0fd40af9d7771a1cb935ab0', '5686', '录入网点', 'entryOutlets', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('48e7238076fa4f499f951653b201f453', '6981', '币别', 'currencyType', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('49', '1049', '功能名称', 'function name', 'اسم وظيفة', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4915f9e4a4eaaca5fea8194c0ab64c31', '3f0e4770', '上次扫描数据', 'Last scan data', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('491b6174055d73697aec57eb4db18ad1', '31d113e4', '问题件出仓', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4956e5f32f3a4d5e88003f474d32b9b2', '22b8338a', '目的地区域：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('497715812568c01bca00e1a06caf0901', 'dd93bc50', '网点审核时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('49adadc8e4c393fcadffae45314fa15a', '4689e1d0', '发件确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('49f5db0adce945de9b5a040c75769ad2', '145', '退件时间', 'returnTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:10', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4a21928922d106cdb0817ae526fcd3ac', '2b77ec46', '待取件', 'stayFetchPiece', null, null, null, null, '1', null, null, null, '2020-11-05 19:20:49', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4a4c4446558f4b1cc4d44c4cec62a36a', 'c5d208c0', '客户全称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4a546625fb5ef1039c5b782340b6862d', '4cd243d6', '请选择快件类型', 'pleaseSelectTheExpressDeliveryType', null, null, null, null, '1', null, null, null, '2020-11-05 19:12:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4a549378e1bd4f63bacde133dd7a8735', '533', '代收款币别', 'codCurrency', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4a99587972394282906aa0f8041c19a4', '4146', '寄件邮编', 'senderNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4ab664a83675edd46a4ec2b222bfc515', '76fa845c', '菜单编辑', 'menuEditor', null, null, null, null, '1', null, null, null, '2020-11-05 18:49:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4ac44407d33cad09c8a32444e2070bbb', 'd1eaf8ce', 'GFS express', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4accf3b4d94b8ac5f6e680742506d55c', 'da5ae2e3', '请输入关键字', 'inputKeyword', null, null, null, null, '1', null, null, null, '2020-11-05 18:51:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4adeaf9e7a2bdf0640587f0518e002b8', '08cedc6f', '货款金额', '99999', '99999', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b17a8eea78c95689c23d29194101900', '11b6d130', '打印122', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b2718f319ab4193a1d7d71c49405071', '4879', '收件人', 'receiver', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b2cac04491d45fe83c08f0f9e9a1d64', '4680', '纯运费', 'cleanFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b41ae9f9f98689e7bf98b596dd3adb6', '5ff8e9a2', '员工选择', 'employeesSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:07:12', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b55b90a1712220fb3d734d22cc605c7', '1427b9c9', '请选择客户名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b748859e21a46646951713bcc7a3473', '2877d1c1', 'COD账单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b7686697b0bf1d905b2a63d92191832', '7a64daeb', '登记网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4b84e5d9172a57ea18b86ad0f1ea9589', '90f8eb5f', '请选择类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4c39e8c1a2a844b7184c5ae8f4c125bc', 'af6e1e53', '问题件扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4c49d1b8cac482b32a498fa1508d0674', '2179c77c', '网点运单排名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4c5078203d5f4eb7d0f18341d49aa9a4', 'fbe83a90', '关键字', 'Keyword', 'الكلمة الرئيسية', null, null, null, '1', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4c63df911f1e572c87544043a3b68597', '7f3d6102', '客户全称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4cd7390d66d6d31ceda91e8e2b334002', '6dae99a0', '批量签收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4d0d1e0ce17411994314ade8d53b1432', 'e6ac6d27', '所属客户：', 'belongsToCustomer：', null, null, null, null, '1', null, null, null, '2020-11-05 19:22:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4d28af51c83b81c9a9fc6d5bd8dd5fa4', '4c2d4c09', '回款登记时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4d2cc912db67bd24444f8af135a7d2de', '0e90797f', '操作信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4dcd520b08bbf57f6d93feb9d66925ba', 'af9e4412', '客户全称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4e20e9bb4f94b9bdf35ac467547c7c50', '91434705', '【】快件已签收,签收人:', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4e4c5d846fd45f7f4cb41e238fb8e975', '31e355d5', '跟踪记录', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4e538bbc88405dc4f3e10db8b62a3f5a', 'e92078e3', '客户', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4e8137e2523797b71574292c0237866f', 'a003f2ca', '留仓件详情', 'keepWarehousePieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:03:27', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4e9345cb039f67ee5a1da28ce77b28b4', '16768be4', '新增客户员工管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4eaa53911011235e8b3b5f25f363528a', '8f4fbeb3', '回款日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4f3e1f4ad8c98c667ed57cbb629ef6ca', '97fa79f9', '到件通知', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4f4384b6a9d7b58db4c16df956308b0f', 'adc327d3', '问题件描述', 'problemArticleDescribe', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:38:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4f65c08238b42458b48271e9de48d5be', '52cc04d9', '强退', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4f8806f683e7248f2e7d03b934213c96', '25f563cb', '中心调派时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4fdb16ae015465556d08939dadc60e43', 'dbdead0f', '订单类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4feafc0f965cc15a09f4b4a3b0d36e2e', 'fc299e3a', '中心已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:12', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('4ffb57f6425e2a0d8aa7771f2b834868', '2d7cc1a6', '员工姓名(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5', '1005', '立即登录', 'log in immediately', 'تسجيل الدخول على الفور', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('50', '1050', '请输入账号', 'please enter account', 'الرجاء إدخال الحساب', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('50535442073f084e56905b2c940b700c', 'f0dd52e5', '派件交单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('508142907c8bf19c7a1fe0fcc5f3aac1', '1e0f5965', 'AWB到货重量（提单）', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('50b3c6da1f89432c4f6d8c946f45f94b', '72a33d20', '扫描件查询', 'scanPieceQuery', null, null, null, null, '1', null, null, null, '2020-11-05 19:24:20', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('51', '1051', '请输入密码', 'Please enter password', 'الرجاء إدخال كلمة المرور', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('511ea57c2cfcafa7326fe2ecfdcefb92', '3eafa52f', '沙特里亚', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('52', '1052', '请输入验证码', 'please enter verification code', 'الرجاء إدخال رمز التحقق', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5206c635d094a5247e915668ade76a7d', 'c58d1b3d', '运输中', 'In the transport', 'جاري النقل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('520a7b9fe6a744ff987e1b9662230eaf', '2894', '财务核销', 'financeVerify', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:13', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('527f2a93d430c6306995fb8214353467', '2659841b', '编号', 'serialNumber', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:05:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('52842cdf467dc0d263173b5928baaf16', 'd1ebc7d4', '起航时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('52c270f37658def8633174afed467148', 'f52c3c97', '正常', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('52ebe6c6319388c7f48da9e8337951b6', '529e1e50', '逐票录单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('531f5c7c9fc13d280b41896257e9b228', 'a4baf22c', '反审', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('539146e281cf14fdff54b596bb405f89', '81d9f827', '登记人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('53bbc8cd1848724b64ae38429f1cd9de', '80d918b3', '调派网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5407c32e030b2f65fd208c9a5ad381a0', 'a06749df', '寄件网点', 'send site', 'نقطة إرسال البريد', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('541b99ea90ed1ef5da6a1bcc3a2d4773', 'a25f22ce', '扫描网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('546ffb02d0aaa03cba80a3faec00e850', 'e6179ada', '入录时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('54c5cd920e924d3f843342ce8c912072', '9898', '保价费', 'insuredAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('54c6ffaf20c1e315b656a959059e9fdd', '7d6955aa', '已保存', 'hasBeenSaved', null, null, null, null, '1', null, null, null, '2020-11-05 18:43:16', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5531e989a09345fb9b7f731a9ed629bf', '6362', '寄件承包区', 'null', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('55325101ec5648c8cc65cc8a1dfef420', '8d95ca10', '收件扫描', 'receiptPieceScan', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:58', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('55611aa4da7013e6949b6020e5d8f465', 'cee8cdd7', '【】已到达Jeddah', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('556249a524a60400949c6e9a25ba30b0', '276199c5', '品名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5594c3774fad43ad804df42ea86062b0', '9642', '核销日期', 'verif yD ate', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('55b89ba7c6cc30fea57376f86be4605a', 'a5bedafd', '运单编号', 'Waybill', '', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('55e86fd18d9f63f1d0139178d8af4901', '4cd0a094', '当日票量', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('56365f6c97ecb9159e880234f2c08313', '0e020976', '巴枪使用密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('56821228dfbf917ae82d0685a7aae002', '687a0923', '子类别维护', 'Subcategory maintenance', 'صيانة الفئة الفرعية', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('56986d262551666d04856c8ab7d3676b', 'bace80a1', '计算公式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('56bbd73f96dfffebf7b191d55f3a3cd0', 'e7d40649', '到付', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('56cba55f086d3793b3bf46c5a6f4f599', '223b832c', '合计', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('570c850afd8b9bbdf5d161d314794944', 'ddbfde1c', '运费计算', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('57a44f577f92ba4ac5f7bbee23fd6caa', '5170e662', '签收日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('580a778229a9a199c5dadfdb774dd610', '3b4bd6ef', '暂无客户数据', 'noCustomerDataAtPresent', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:42', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58955bbe65b34810b887efa892769c5e', '112', '网点审核日期', 'auditTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:15', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58a095d49ae84f5a9ba5b1742fb64506', '5088', '寄件公司', 'senderCompany', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58b4a387b870354c9bb954e4404f3233', '71a9711e', '正在后台上传中', 'uploadIn', null, null, null, null, '1', null, null, null, '2020-11-05 18:45:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58bc58f725052d0c9fb0f654ccd447dd', '1e0e93f3', '结算方式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58ca6c5e84b80af859685847993d0cbb', 'f43345ee', '物品类型', 'Item type', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('58e6fffff29e449daf54d4dc9e61400d', '5061', '请选择付款方式', 'payMethodCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5960b8cbaacd23f6591bdec4c82acc2e', '1c3a1d6f', '时间 :', 'time:', null, null, null, null, '1', null, null, null, '2020-11-05 18:59:42', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('599653f54f71569c8f6417c95951af56', '45be5c32', '扫描清单表报', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('59c26d341e85497c93694d74efb5101e', 'e80f31ec', '请选择上一站', 'pleaseSelectUpStation', null, null, null, null, '1', null, null, null, '2020-11-05 19:12:36', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5a04d2d019b65700ab2febef4792f239', '6f97a9aa', '代收货款限额', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5a720d4a2b43777996e06bb7a517ef99', '29277570', '登记时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5abf72e7b07de68eb867768a367618a3', '2badeab0', '证件省', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5ad4dfe96eb4b6a23d2980fba5f73454', '00d29b3e', '寄件市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5ad8f1f126dcf2329449a62502845634', '8b617152', '操作地点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:16', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5bcb508e6bb3097978afb5005e6edd66', 'b7d9e442', '傻白甜', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5bdd33248a995f6a3a363ef953136619', 'cde0c6ce', '发送人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5c4ae11d691199e0c7badcc82adb5a85', '9e2b7e57', '重量', 'weight', 'الوزن', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5c701895e250783843c71ecf91f03933', '3d0510eb', '高', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5cf7391c72c74a24bba3cb5ec90b57e3', '2175', '物品名称', 'itemCategoryName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5cf85026c57a647a4b639ace2799bd71', '5317a7d0', '扫描标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d2fd0e01ee5d5719553b9578aa33443', '0122e1b2', '【Riyadh】Riyadh(LYD00105)已揽收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d340ecff037e6e8f955b972169d468f', '3c9f09b2', '业务员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d378151c0be7fdf7e217597954419e0', '1bc678a4', '问题件信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d3b0834b18300dfa5f788276f730fcd', '69ef1fe4', '请选择下单日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d5eef72b4bb729750fafb31cb7e156a', '2215da38', '珠三角件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d748847176d20fe395b9bed903a6cdf', '7a6aea98', '清除失败', 'clearFailed', null, null, null, null, '1', null, null, null, '2020-11-05 19:00:48', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5d97742cd1a446888bc395ec36f186d0', '671', '寄件人', 'sender', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5db86d6130340b9308fb6f6005361815', '69b5e36e', '获取用户信息失败', 'failedToObtainUserInformation', null, null, null, null, '1', null, null, null, '2020-11-05 18:36:53', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5ddba6939ad894bf033bc9f2eadfc69a', '52e6f46d', '消息详情', 'messageDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:29:55', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5e167ef242fd22e3133ca85ce8030d71', '7dcec4d3', '请输入备注说明', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5e234cefdab04c8ba003e3732ee24e04', '7225', '修改人', 'updateByName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5e352bfabdd042f0d774e421ce3812bd', '7af0990c', '付款确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5eaad2bb8254edd6601d19449b871961', 'a403f0d9', '中心已调派', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('5f99e21cf6fa22d49f0f549ad01ab9f6', '83663045', '物品类别选择', 'goodsCategorySelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:22', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6', '1006', '系统管理', 'System Management', 'ادارة النظام', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('601074e102954cf6ae527196a0ec6c99', '8756', '代收货款', 'cod', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('602f0ea1b4b347584af52e09c3c2faf9', 'bfc22909', '财务管理', 'financial management', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6051aeda29d4896713c55bcefb19057d', 'a0c5d817', '联系收货人', 'contactConsignee', null, null, null, null, '1', null, null, null, '2020-11-05 19:24:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6094e20f18204b70f0ef4fd06d99a68a', '7de6cce0', '请选择分组类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('60bf43b29665a13f529822f21fc2bb70', '88358b41', '签收管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('60cfc3e5b7571968ddddf5d99e5a71b1', '9eca4bc3', '文件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('60d000db2b615202cc8f1f7e649d5a3e', '6c15ece8', '电子商务件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('611820b3ef4cbafeca7eab3bda140964', '8259f575', '请选择所属网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('61a4a88781f806e02c37d442d424bd5e', '5470a158', '今日', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('61b2b2564886a79e3ce2b3028c512ec1', '3c804267', '编辑角色', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:18', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('61d1afb4cab2af0cda7da63ff0c3fdc9', '680ce597', '中心调派网点', 'Central dispatch outlets', 'إعادة توزيع نقاط الاتصال في المركز', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('61d863928fc9a86a9824fba636a2ebb4', 'a380fa21', '请输入员工姓名(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('61f771fd9da163e750898c9a99237ea2', '1117d6f3', '快件信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('620995304b5653456149f2b23324f9c4', 'c321144a', '派件', 'deliveryPiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('622ddf7dab87b8fda4feadd2b17d1b9b', '9387a0ea', '上传', 'upload', null, null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('62f5d00b5ef8481f931a1cea822b8817', '709', '退件费', 'returnFee', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('62fb9eccb8dcdaf4ec1e74046276a757', 'd4e520ff', '已签收未回款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('63d8399927ab6e147ab6b84fd126227b', '80131b7a', '日志编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('641d0ca22a03a76ee8028edb1f1f7ced', 'a59a8a34', '联系电话', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('642bdccd33ec40048b50801ef3209382', '5725', '运单状态更新时间', 'waybillStatusUpTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('644c6dee3989dfe116a60e7cb675f5ab', '1fa49b13', '证件类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('645bd0507bd45c92f1fc61df74636c07', 'c8b2496d', '转运单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('648b94fa764f69e67eb57ac0d009fa17', '3c8ad64d', '下一站*', 'nextStation*', null, null, null, null, '1', null, null, null, '2020-11-05 19:06:26', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('64aea8584fec55dd9e88e3a263fa5242', 'c00e9f6a', '请选择所属城市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('64cf7c5c3cd127bf3c458128cf902e7d', 'ea34ae26', '票量趋势', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('64eb206a035e183cc8ffef7f82297cb1', '492b4e92', '留仓件管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6501dae82ca3a53ebd5aec2f46b0e9fc', '84f41257', '寄件人国家', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:19', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6538252316c14a297dd8dd2d31a28c9c', '895a2db4', '子类别编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('654da40d583146b5919bd89f970fc139', '4045', '锁定日期', 'lockDate', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6554c8bcaacde238d51dceb6643e8338', '70a4ca04', '到件详情', 'arrivePieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:02:58', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('65599cb92d2cd02444a6b8d605a45927', '1bc0c910', '删除日志', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6583fc87c765dd96fa9e4d5def51bdea', 'fdd475fa', '所属业务员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('666fce29fa55aed9f6991607442a4fa3', 'd2fbc5aa', '业务员姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('66a85b5e3fbf74b6caa009d3e526be67', 'fa9282e2', '快件类型选择', 'ExpressTypeSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:10', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('66a9e9afba0fd48caae4f6e5bed50d3f', '43d9804c', '体积高', '', '', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('66e6d1e89fc414b1f32499019ee3545c', 'fc3ab3cf', '测试002', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('67023fe4e63654bef4b30868e1ca09d5', '253aa7de', '请输入英文', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6705edecc31586e7554d2178ae580c9c', 'a537ed35', '主类目', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('676c1f7cff0d77c08b4251efee477d0b', 'f68dda68', '请输入手机号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('68697bac030d8e250054410a018d38a8', 'b82c596c', '上一站', 'Last stop', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('68a8686f728fd55e3a651dcc85132393', 'e77e6b09', '默认发件地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('68c81116c666e9911d746ffba104c260', '9661f91f', '登记人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('68d2b2421d7ca86c2480ab5c4b4f4508', 'cfb4e835', '审核人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('68edb384196736a5e677a8843e0fdb2a', '1b6480e2', '请输入阿拉伯', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('690a473a9968fcb90741815fe8d3f459', '209a7aef', '测试', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('695f402270d26aa12e313bc09813dd59', 'e28d7e1d', '城市选择', 'citySelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:17:50', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('69e50e6dbccf3e88c23ced906d4a4f65', '8650e692', '请输入运单编号', 'Please enter waybill number', 'Please enter waybill number', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:20', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6a43eb328da984ef071d16172e84fb30', 'e56a1fb1', '重量(KG)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6a94edb34d2f9488ce13ed24fe8057de', '19bd3e33', '隐藏', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6a99f1090c87456bbfd86c17170f470e', '4a144551', '分错件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6af23d8275669d8e3e4c4b85efb6b405', '5b126e27', '快件类型*', 'ExpressType*', null, null, null, null, '1', null, null, null, '2020-11-05 19:07:37', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6afb4c9aada9c211030f54f0d41a0872', 'ef7f85e4', '启用', 'Open', 'ممكن', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6b09e6e2c46ce855efadd9db33d5b4a7', '8852a2de', '电话', 'phone', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:19:53', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6b5ea660f48672f5cfa4311896627bf1', '1ac9cbff', '权限设置', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6b6849f03ba46921e0022733f442bc3b', '075cff9c', '单位 代码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6b891eb44f8c0fc954ac204f29924e43', '44866962', '请输入员工姓名(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6b8e7f1ca51cd0bf8299dd85d3055506', '680474ad', '收件地址：', 'recipientsAddress：', null, null, null, null, '1', null, null, null, '2020-11-05 19:24:11', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6bfb7b0047888c57d27b20d7a098fa9e', '0102aa5d', '车次号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6c11cf183d37cf30c59cc0c178baed41', 'd8eb06eb', '打印122', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6c28a1373fc0440692ec66e9b5f0b071', '3921', '请输入应收运费', 'freightReceivable', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6c80c874cf73a558a1551a837a61aaf5', '7d167387', '清关费结算', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6c985e3729553b8b01637b27d5f1b0db', '16dcc68b', '请选择收件城市', 'pleaseSelectRecipientCity', null, null, null, null, '1', null, null, null, '2020-11-05 19:17:38', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6cd9de36c2fe11517fce22c8fa5dc111', 'b374e028', '上一站*', 'upStation*', null, null, null, null, '1', null, null, null, '2020-11-05 19:12:26', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6d3f9bc9e174f41dca9f8dcf5234a4c7', '9b5416a4', '问题件', 'Questions', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:21', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6d4c73e020aec92bddb945211d372f59', '5cc90228', '取件员姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6d571b22ce48e548d02f142a7bbb5c9c', '06c514a1', '营业网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6ea93876942157705d10137fe896b0cd', '86bae9cb', '问题件出仓', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6ecdcfa904cc7959ea1cad7767446363', '00cebaed', '核销时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6edb2752282de677229932393948f568', '83c03cac', '列表table', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6f5152c147005b4ad6bf3d6d9c78ee37', 'a9e945d1', '已揽收', 'Received', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6f694e5bdfee98daccad659cec31c6a0', '9af8b9a4', '反审人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('6fa94f002aec2733cbdf31e1e6f9446a', 'e88a9e22', '自动化总控平台', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7', '1007', '菜单设置', 'Menu Settings', 'إعدادات القائمة', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('70141cb00d5d139bb7965e5df26be53f', 'b264b48b', '留仓件扫描', 'stayWarehousePieceScan', null, null, null, null, '1', null, null, null, '2020-11-05 19:11:21', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7091380d4edae99d35a9743e2c82c1ff', 'd6dd3c03', '当日签收率', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('70d8afe70ae3e4bc672eafc568498a8a', '69888593', '寄件人：', 'sender：', null, null, null, null, '1', null, null, null, '2020-11-05 19:22:43', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('70df562ab4b251641aab8f48fed715af', 'b401a719', '请输入地区名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('70ec2a6e3ba9fb61ab000e2557fbf23e', '425830e3', '关于我们', 'aboutUs', null, null, null, null, '1', null, null, null, '2020-11-05 18:50:29', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7120850d47c44c5196e933e86f308c39', '6853', '子单号', 'subWaybillNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('713cc0f5994812ffa9cbb10888d5e769', 'fa64b8fb', '客户选择', 'customerSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:33', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('718587063bde03f4dd1df708e1d8ed37', 'e2cff8db', '新增目的地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7192e8aee5c3438b9916401f83e82368', '1215', '优惠金额', 'couponAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('71bd98b373ef4cd1ab43f8180f84c5f8', '6879', '实际重量', 'actualWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('71d514eac4102d4a2a9d3f87fb213818', '6fdbffb7', '请求方式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('71d55d600d1503b2098629ffc74e88f4', '0e570781', '预警消息', 'earlyWarningMessage', null, null, null, null, '1', null, null, null, '2020-11-05 19:29:38', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('71e328d534174d1045cc81b87a41648c', '7ce6bb2d', '此操作将永久删除该数据, 是否继续?', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7228967c6014cac63445bffe345beca3', '468b4aaa', '扫描上传', 'scanningUpload', null, null, null, null, '1', null, null, null, '2020-11-05 18:49:43', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('726e45eee44918b7eada433c1abd7f0b', '800fbddd', '请选择机构级别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('72d75c56e3d7404b9c83989ee9d138b6', '7970', '取件员', 'pickerNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:22', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('72e99ab1795a93e950f6b46adf9c7cfb', 'c1e40b09', '暂无轨迹信息', 'No track information', 'No track information', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7343503bfff020fde377047b2f8f843d', 'b506a46a', '项目', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('736678b4a7ad4c5a867b4285c1f955ff', '36f5d31a', '福建件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7375ec7dfe9577ee914fe9dfa687523f', 'd040b860', '货款状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('73b2df70fe9706116483003e596705d4', '14b0f43b', '请输入巴枪使用密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('73e0e3b8a4c1853d9913cde2cdd5bc47', 'f60d2c47', '异常', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('73f9fac097dc5bf285c6882aa5032219', '2b56d966', '请输入子类别名称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('741298be2c86c8ea1486d65e06c6db10', '2025eae2', '绑定APP角色', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('741574e592e54a75a6a5837f9536145b', '4250', '子单类型', 'subWaybillType', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('749477092f8e06e87bd372de9bd594ff', 'bdf65f99', '大件配送', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('74992b5115d57e62611cb080befda9c3', '3ef405fa', '派件费', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('74d27dd990c94c90a871b866da815183', '6304', '备注', 'remark', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('74f40a7dae91a3f822be1cd90924c62a', '76b22ca2', '订单信息', 'orderInformation', null, null, null, null, '1', null, null, null, '2020-11-05 19:21:20', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('75223856fd28366e4383f9707763403b', '4b43315a', '项目', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7560907cb62310c53dbff2b333e2e456', 'ea3f2eb7', '客户姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('75ba99cea5567adce0e8c65046a831f4', 'ab02e854', '用户名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('75d24a4942716f1e91136fabfd539906', '1be38c33', '主类别名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('766cd5f6344b6a9486560eecec3acb96', 'ebdac521', '查看公式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:23', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('76d2567d6c4fac5dbae8664f104c22ca', 'de62e8e7', '关键字维护', 'Keyword maintenance', 'صيانة الكلمات الرئيسية', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('77ba74aa3104ec8f6336c1b0e5d9aa39', '9295c294', '记录编号', 'Record number', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('781cd9e418d6f334224b2416edb54053', '31ee0ac1', '收件城市', 'consigneeCity', null, null, null, null, '1', null, null, null, '2020-11-05 19:15:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('78542146de3204514f6bb6753568aaff', '1184afc5', '车辆管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7869ae0616d1cf6702087abc7f41132b', '3b235f77', '员工姓名(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('788920b4afc2a5601abe8f3b9ccb19b4', 'ca3d0ced', '派件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('794ce9d284fe89037fba5a5b9a398e4e', '4ae97a07', '产品编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('79613c424b89fc08c912dde37ec2f258', '6444c485', '公告管理', 'Announcement management', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('798d8b50ba26771944cd81a80f0ba04f', 'cb91f8b4', '寄件人地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('79a0e59ea7994f6e687da1710d3faa3d', '0134c462', '目的地编号：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('79ba72d1ae7c46bbb689f8a924fa09e0', '7636', '物品信息', 'itemCategoryName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('79dd9d4d9e965bc6b5bd7ddd47769636', 'c1acec28', '打印子单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('79e577d0b6accccae622b222f452d47d', 'c3bed543', '消息', 'message', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:29:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7a26f54d0d6ffe3aeda93dfdf2ddabe6', 'fbcfe432', '邮箱', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7a5c8f60ad5c74bddc9107d8a1dffe97', 'df8a659e', '字典排序', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:24', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7a640db995b472e722d39940b1061fb0', '0f0335e8', '请输入单号生成前缀', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7afed2e90987300cb734c06138892d99', '5f9b2b8a', '产品类型(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7b0cb614197a242bb07a5456e380545b', 'a74a56e0', '客户资料', 'Customer profile', 'ملف الزائر', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7b7681a1c20aa3a6fac5f26984e10113', '7ff97d47', '绑定PC角色', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7c0c0105587935a14cfb049b9e30517a', '90efdba0', '到件确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7c403c88e789d41e88ab3519d223acfb', '6948b42c', '网点审核标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7c6b843b6e6ccb20f984cfb87c5f1fbf', 'b33480ad', '加载中，请稍后', 'inLoadingPleaseWait', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:34:07', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7c78973fbbc4f6204c04f5da75640544', '093a46d6', '派件网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7cc3a5c4e2bb3227f03948841128d0de', '5ed9671c', '目的地选择', 'destinationSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:06:45', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7cf0b0ad594536bb9101e7030e24aaa7', '007adfad', '领峰供应链集成管理平台', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7cf196346c649fb7d604f17937416a96', 'a7d85b17', '下单日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7cfd88acbe294d6e8abf3223da5d2e7c', '4780', '派送要求', 'deliveryAsk', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7d15194c29a0a8e6a6fc64e6b5478578', '9d3fecf2', '新增运单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7d3d0d461f186cca64168ec6a02dd8b7', 'e1376a7f', '模板类型', 'Template type', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-09-29 18:36:03', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7d52f9ce31259382867f754c4f823048', '6f0bff99', '请输入姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7d7476434471b78d59f2c81d7ff73e57', 'd4a2af5b', '月结收款管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7df0b7234f9aded274c307d27d75fbef', '22b1ec7a', '手机号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7e079b0cdc4deaadc5e98bf420e2bdba', '2f1f9c95', '总部', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7e67add363c4f19fe0c88e06e8a35b25', '829515b1', '请输入客户名称', 'pleaseEnterCustomerName', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:49', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7e75eec099ff5702a0dc2310a26890e2', 'e462b227', '订单调度', 'order scheduling', 'إرسال الطلبات', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7e906b0804a0464f9b92b62c42daf27a', '1602', '请选择物品类型', 'itemCategoryCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7e927f15f4ce4672ae82e1d773673729', '5733', '报价类别', 'makeOffersType', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7f20902f83124f171b285d1daa6b5fce', '9e0a7b47', '访问编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7f53102bb74d8f7accc7bb4dd26374ca', '03be5a7b', '重量段', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7f5f044f63091e1f76027659670e76db', '3899bc51', '签收单交单扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7f7c6843291de369ddcd2ff909fd35cd', '811fa886', '操作员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7f8d20a3f3983d5a1d83413bd2eb798b', '0022e2c3', '短信统计', 'SMS statistics', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('7fb8fce9404d6888e433b513c0298e2f', '3248500f', '有效起始时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8', '1008', '角色权限', 'Role Permissions', 'أذونات الدور', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('800957c3c0ce238b8c52adbd8598283b', '12da4e7a', '扫描员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8011ac4dc42573ac3dc4d6a8b2e9d280', '99bda391', '派站已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('80825ed92cff4ea5930d9d6cff1afe84', '3083', '中心审核人', 'centerAuditPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('81067066cc4d0c44be65c5147a566358', '2708af53', '航班号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('81184821ee7bf2614d3be174d8838bb1', '90848195', '设备管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('811caa8681094a4190a4e0b06d636198', '6362', '回单编号', 'null', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8138165628d3453da227a69e1064b774', '9028', '运单状态更新人', 'waybillStatusUpName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('815ad9969dfd4b60127634f7b9140cf2', '09cd3f1d', '显示', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('81a60418b086452b8763c417a4e21b58', 'f796e2e5', '清空数据', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('81a7562bc68d197f27ab2171df71ff57', '6cd86e70', '拒单', 'refuseOrder', null, null, null, null, '1', null, null, null, '2020-11-05 19:24:47', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('825458da6ef87d82935820707476aaf4', '1b1a2711', '手机', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('82625c23eb86f9a66716f7e3d205af04', '54c75ff4', '子类别名称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('827557c0ecf64d85b91452b13450e5e4', '3600', '件数', 'pieces', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('829982cd21d74f70b0e68e9592d5c118', '7562', '核销人', 'verifyPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('82b8bb663ffbfc4f350b251ece79e112', 'f7664047', '货款监控', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('82f957633ba95a30c6c5c144a89d5f0b', '6fe29c21', '问题件信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('82febc116455ef1dd4970aad775b312b', '2edf7706', '运单管理', 'wayBill manager', 'إدارة حافظة الشحن', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8303445a335c26338785961ffbdbd872', '28c234b7', '派方收款管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('83236b173aa73e947bf7a011558e61db', '86bce02b', '派件或收件员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('83307fea803ddd2a738a132aab2b619a', '4da816ae', '弹窗表单dialogForm', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('83a0d6fce39d1070c8dcb11a4af120fc', '00a1a268', '产品名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('83a47d82512aa7344bc2ebf89e788eca', '57f2a3fa', '修改客户报价', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('83f5358cf661c6442999379ecf2958e1', '1261f4c5', '员工管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('845fc97f0dd534d5bcb8da9d1e7c9f81', '4967f8b7', '新增客户报价', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('84b5b421a73e4b399928d498b671d90f', '1649', '请输入物品信息', 'itemCategoryName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('855513ba5e8cc04cc40e423a5e2e6ae1', 'aebefb55', '原密码', 'Old password', 'Old password', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('85c23f7c1931996fda6dd0917908753f', '0e68180a', '寄件人邮编', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('864ac359dd1cd4914b0b4f2324cf00b2', '17c852cc', '选择发送人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8679cdfa15f2536f98a5915ad40b4ead', '2e383e2c', '备注说明', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8690f9b33f399ea1732c9a604a9e2504', '17a2ceca', '打印机已连接', 'printerAlreadyConnected', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:28', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('86bba461a0fe8eac218aa3bba3c4effc', 'f9517999', '本次扫描数据', 'Scanning data', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('87bc90d3ec6e76ade1cf13ca27c66c46', '62e2ed8c', '退件扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('87f78861ccc747f9b22f890bed5f1ef6', '3393', '请输入目的网点', 'destSiteCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('881864f3919b4651a7f4a8cd4c6622fb', '104', '请输入总运费', 'totalFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('881d5d3628b1362cd2c684ecf10d9fa5', '4d0c9c7c', '【】Riyadh派件员:测试13522221111正在为您派件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:27', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('883dfd971ffbc00844131ac2f7911426', 'e50304ab', '调度', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8893a73bbf4fd4a49eb980e8fee73941', 'a11a13ef', '即时配送asd', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('88b63288aa88aa604d1efbb5c057036c', '77aef33d', '宽', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('88c3775cebd14e4dfa774e0535027a58', '4426dc89', '条件筛选Form', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('88ce94b2ca01b6fd4018b999be25ae90', '51317cbd', '请输入类别名称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8924ff4db89e1e3f6d438113a276883b', 'f9068b11', '请输入手机号码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('897e56bf9d0d05ae1d578fa7774bcb2b', '40039b1d', '对账管理', 'Reconciliation management', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('898f2c5bda7b427396d2f3fe5c7d332c', '2600', '分拨中心', 'distributionCenter', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('899d5f5d51bd4430c82350d92df665ca', 'a2ace346', '主类别编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('89a09bec4c44f882bd61f91f59fcac6e', '3dd44318', '账单管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('89d794f988a63a070aa5c5de289ca95a', 'f5ff6007', '问题件管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8a5404c0302c3a7f31c8e4fb0af1d7ec', '7084acf8', '收款确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8ae9ef2139b760bb8d30fa5cbcf70675', 'f5b90487', '发表公告', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8b293fd74acd1d5ff659668194441a4c', '61a27d07', '现金收款统计', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8b73aefb914eb7830e511b446741ce73', '9246aefb', '扫描管理', 'Scan management', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8bd58f41e381d15ab83ce6448d449b93', '5f2c85e3', '短信发送', 'send a text message', 'ارسل رسالة نصية', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8c24fef1a9973e22d88ff1b2923bfcdd', '34a71498', '运单信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8c32d0a465fc46b2a786144bf6a937f4', '4812', '请输入寄件人', 'sender', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8c454535caecdb9d994961dd8f8f1b62', 'd92d5934', '在职状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8cbd73aad0e6c2381c0a3eef679cda7a', '7585ca90', '寄收件信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8d2996134b90391f1b7c2478ac1d4bed', 'f2750e78', '产品类型选择', 'productTypeSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:32', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8d779a4f5b8ab179428745d3345b1072', 'c01f8f6b', '客户已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8e05a69e123bf34f92c0fd5e53b50e4f', '902fbbe9', '确认收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8e66a4ab8a4bd520ab72ea4e827cddee', '17a839ac', '月结账单生成', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:28', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8ea89b0bd6ba90b2e6d0e25d8a622943', '4547b1a4', '其它', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8ec25af8e422769d25a420ddc428eee9', '5c9f1c3c', '收件人地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8ed0d0d8934d99cfb17bfd3ecca19cab', '4a366dd5', '签收日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8ee952d689b64f4d9338b5aa5ec13545', '52280c32', '请输入客户全称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8eea71fdef766ddc266d46904d2f4fcf', 'ee91f63c', '删除扫描', 'deleteScan', null, null, null, null, '1', null, null, null, '2020-11-05 19:03:57', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8f3e2e6285743c92aadc45525b57c8e3', '05a82d55', '扫描网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8f68af970cdf2d3f8ce36ae65c46a058', 'f53de4d0', '操作人员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8f705a8cec068c5f930606a0ca52a5be', '1f63ca41', '请输入', 'Please enter', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8fa82036fafc3c4b998099442622992a', '5d577cbf', '请输入收件人详细地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('8fdfbce34c093da052822ff6f8980980', 'a750c52c', '证件市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9', '1009', '用户设置', 'User Settings', 'إعدادات المستخدم', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9003ca61a12c23de1e2275b70d865d6d', '54313216', '是否启用', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('901f679b2a0502394691de057882d82a', 'd5dd05a8', '表格字段管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('902b25568fcb8e0deb130f370a0abf44', 'd727aa10', '客户订单号', 'Customer order Number', 'رقم طلب العميل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('906e12d1b5ae1a1c65998429778884ca', 'f5c4dd2e', '卸货时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('907c19dbf25451dbd1e03621ecb1e019', 'fbf355bd', '输入关键字进行过滤', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('911605483de615c25dc85fb85d9f0a44', '6bf9860d', '班次选择', 'shiftSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:02', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('916af5c7e9eade8eb8e6064b34ad68d7', '2a2fa6b8', '备注：', 'remark：', null, null, null, null, '1', null, null, null, '2020-11-05 19:23:37', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9179b8eed648ecd6e4ed58a1301d97c4', '5f6eabeb', '接口信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('91c02a10d5d46d6ec5e4733726c7d206', '8f134046', '请求地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('91dff036d5a07c98c0e2172cd2ea0fbe', '9c26ab85', '二派订单查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('927cbee5b64ff29c40cb78a9a398351e', 'fb19344f', '客户编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('92b2ccc2f96dc1138b04c1a3c8b64578', '4565d5ee', '到件称重扫描', 'Arrival scanning', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('92d613ab0249f7c101494c2f62fcbede', '5613a4d2', '扫描类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('92ecf45d8d084a30a693e2ec5aa554fb', '80', '应收运费', 'freightReceivable', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('92ed78d5b7beed753701a8d9f61171f8', '034281d1', '请求参数', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('933c8ba95f15cb7c77716b0b2da110be', '9373fbcf', '新增产品类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('935420f0093a205f641d694f0d67b948', 'ebc1a52b', '请同意权限才可以拨打电话哦', 'pleaseAllowPermissionToMakeAPhoneCall', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:03', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('936fed31a72b1b1973fc7fcc23d85562', '22e5a27d', '请输入路由名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9411f62268f51a0a1264981303eaf545', 'ddd7821c', '上传失败', 'uploadFail', null, null, null, null, '1', null, null, null, '2020-11-05 18:56:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('945677c13e3ebc8c152d20b431414cb3', '39fd548e', '车次号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9473bbd5045f4aceaf3755a2cc64877d', '9950', '录单员', 'entryPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('949f2eb21b684e3db27dd867153f137a', '2818', '计费重量', 'chargeableWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('94bbcdee34529a3ad95db6da090865b0', '6d1425c9', '所属城市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('94ee9fda7e39923daa3948d089a963fe', '078360a7', '英文', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9592f7068738e3b7c230c3d02d4161be', '5bf58cb4', '修改密码', 'Change Password', 'Change Password', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('95cd43cf17afe6464bda47e82df23898', 'cc6d3fc7', '运单编号*', 'waybillNumber*', null, null, null, null, '1', null, null, null, '2020-11-05 21:38:44', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('95df6c864f1a6a52366833e7381abfa7', '2b1bff16', '航班到达时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('962aea49bc4ef1d5f88caf80481bad1d', '374c0855', '请输入中文', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('962e6faed7a649369dd1837b2e5a4073', '4591', '请输入收件人电话', 'receiverPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('964a5d957afa6ba5234f7260d0eb5b43', 'f2a8396a', '编辑', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('968f65b8070065068624c6bcb1175efa', '83425e10', '类别编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9703e05c594e2373f65484167bc37850', '68832108', '导入日志', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9799268371e438bdf5cd0bcde8f29e8a', '184ceb63', '大字错', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('97a4ee3b1c76c50a30c681f483d379db', 'cd84796c', 'FID', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('97bb1a4785e1e1da745f8e55d6203773', '4fc3f15d', '登录地点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('97e4e7d7908379dbc77e8a861f7de4a0', 'dba1b56b', '内部用户', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('97ee1d8d03e5870d39d80cf64a709b91', '3fd5649a', '请选择本币币别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9805796d4db85a7e57937193aee27415', '98b2e39e', '上传', 'upload', null, null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('98225f1b48519e082e6ba4a474b299ef', '9ca6360d', '关键字名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9958a9636ec2c790020de858334a09b3', 'cef1bfd8', '性别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('99ac527b6b5142f2ba05eeadc00bde1a', '7021', '寄件地址', 'senderAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('99d8ce53de2e59b26e7c38f27db8707f', 'af2a6ea2', '负责人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('99e503d32ee32ffcfdf7af5f9cdcfc3a', 'e97b1200', '请选择寄件网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9a0377990405271ab11e03c2dcc401ed', 'e48804fc', '是否退出当前登录?', 'whetherLogOutCurrentLogin？', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:21', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9a04bd16171771d5610e41cdbdc76ae0', '10f4694f', '模板下载', 'Download template', 'Download template', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9a095dd8636c6f49a7d5f9e7cbc689e1', 'a7e277d8', '上一站或下一站', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9a3a8b99780f45488ef91bf79720276c', '7771', '到付款', 'payToSource', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9aae66d8447fb0d46c88760c71f8499c', '1a046ead', '基本信息', 'Basic Information', 'معلومات اساسية', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9ad4a55fe90cd57fab2169b404d6b6e6', '41f3ce6a', '上传列表', 'uploadList', null, null, null, null, '1', null, null, null, '2020-11-05 18:44:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9b187329ea84e0fb2ea4bb2ce8a0a6d4', '0f5b65ce', '收件省', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:31', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9be932aa5920805ceea48d4f2252ed50', '363cd767', '导入', 'Import', 'Import', null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9cb8e6affe4dbc806591185adc22b77c', 'd51dc62e', '当日应收货款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d2596b1344f80212ea70640f7f924d5', '235f9fbd', '体积宽', 'Volume wide', 'عرض الحجم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d4541633e47c2e13c39348c8260c1dc', 'f0510f46', '确认收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d51a64481b69c657e0978d68e103a12', 'e9b7e95a', '至', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d735144d8ffc8db75f2dc58ae4ba2fa', '2f8e1b4e', '测试数据01', 'ceshi01', 'ceshi025', null, null, null, '0', '2', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d7d3bc292ac2234709031c7da02a56b', '60ec8cf1', '请输入正确的联系电话', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9d86c16a14877cd3ff4902bb4752b4f1', '95eebe89', '网点编号', 'Outlet number', 'رقم المنفذ', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9dfebc7740bec6bfbf83a708385d65a3', '5c94d28f', '珠三角件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9e4c40f5b1b5d9163add17fe8e7ca8ea', '710d2d6a', '货款通知', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9e741d33c30b62d7963ce8817bd63b8e', '2bef0c91', '单号生成前缀', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9e868e623f2db7e4a70c432a6dcd726f', '2a628bf6', '正在检查更新,请稍后..', 'inCheckingForUpdates..', null, null, null, null, '1', null, null, null, '2020-11-05 19:28:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9f54f66fd6226b2648493128da302aa0', 'b11c601b', '留仓件', 'stayWarehousePiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:01', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9f6d6a610f698d9daf6e9450372f8956', '6dbf9c70', '重量(KG)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9f9acffc2291c9463d2bcba2b1a5fc4a', '4859ec8b', '权限', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9fb2ad6e318fc1b601decd0d5a509450', '3e21d4e1', '结束日期', 'end time', 'إنه كبير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9fc85f9850ba33a9e1e39e545cf2cbda', 'ab6ce01e', '标签tab', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9feab2de41d275f23dff920a23ec04d3', '98392ff5', '派站已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('9ff0ec3d52a94ec8cb14e719691b9c4e', 'be2e53f3', '管理标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a01cd3fe016b56ab8325e9b6dd3539b4', '10cf545f', '绑定PC角色', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a0b9f8e41b3a841c5a1eed96d30510cf', '6c505044', '目的地编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a0bb53a222b9299088263dd04c939932', '06a4b0e2', '美元', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a0c2a01e68c6d6bfc64c14198ca81322', 'f369f4ea', '完成', 'complete', null, null, null, null, '1', null, null, null, '2020-11-05 19:03:48', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a104754f13ece9225e6d536c93b0ba25', '9c922c50', '收起', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a10d24f5e01dfc3cd1c347205a9e7e84', '1ff82580', '录入', 'entry', 'audit', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a1435ad6d26b156fe68f237008cc4a83', '8651258b', '试算', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a1d8d7af546ceea035863a5a2bf62278', '815f014c', '选择主类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a1f98e4fd645ac348dbe88528e5c428f', 'e279bebe', '阿拉伯', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a208e3c8c731c2363aa150da1ee0c367', '5dcc30b6', '快件查询', 'Express inquiry', 'Express inquiry', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a239015029b4845da0bd7b81e5b26762', '255c877d', '请输入确认新密码', 'pleaseConfirmNewPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:26:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a249ae1c30d72bf9d9f295fe5385e9e7', '11bc0ab5', '申报价值', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a297ab22b3c3a4358277371679e134b2', 'efce9249', '即取即派', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a3365619cb4b4c72aa9bb0cd5531445f', '2884', '物品价值', 'goodsValue', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a338cc47b4fc4be821c4a2498427417e', 'b7871b71', '审核时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a3b195f1af3c801fedb3dcf42ad33277', '0aa227b5', '收款确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a42949612ef0f5b7403d3db1602fe68e', 'baf5e221', '单独清关', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a488e2e8edde441c865a1ef8fdb7aba0', '8159', '其他费用', 'additionalCharge', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a48d064f4128647e08108e1f46e50a13', '5d378e18', '全部添加', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a49883dc05b07b67c56a966e0abcdcf4', '23a40804', '运单审核', 'waybill audit', 'فحص الشحنة', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a4c1c34eff3b3340d395f37812eda3bf', '40e273a0', '请输入收件人名称', 'pleaseEnterRecipientName', null, null, null, null, '1', null, null, null, '2020-11-05 19:17:25', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a537b83104d8a3d71e9a156f05b7042a', 'bb5fdd89', '装车扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a5ecb87afabeb0ee11645685cea30e66', '53f24746', '刷新页面', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a6181a964269cd00d15e3b529aede69c', '5dfccf01', '转第三方', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a6642f4052ff61a7d73db6651c20483c', 'bcb9125d', '问题件回仓', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a69ecf5a89a6357a6e2f86de8921f246', 'b2736b70', '请选择业务员', 'PleaseSelectSalesman', null, null, null, null, '1', null, null, null, '2020-11-05 19:13:00', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a6bf3324ca8c37a74b92e6341d89382c', 'c16f3908', '跟踪信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a6f716ca29d6418384481ea2bf3d2dab', 'e7d20260', '未收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a7283297aced4fd390c430d27ce411be', '7665', '打印人', 'printOne', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a783e6eeda51c1328dadc3b475fd2ac3', '7858f04a', '产品类型(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a78dfe45323b4e7780932885165567b3', '5699', '请输入代收货款', 'cod', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a7a3793bb0c4401487c9f5931693dbce', '2530', '请输入重量', 'actualWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a8d3bff7c6783e38d8e3bbf3180c74fd', 'f5c21e0b', '运费', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a98d859af1e2fe9dc0a8eaf6f98369f4', '5233cdb7', '重新调派', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a998def9c8791ebf3d4fbd9bcc54cf18', '41814caf', '收件人区县', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:33', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('a9ce91593dfa1d8a2c0d953b21eca4db', 'baacbd8f', '请选择目的地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aa049859fbe2c666199a7eeb47f14e23', '69b72289', '请选择发送人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aa6565be9ad4eb0a6bf5319421115a96', 'e807a576', '联系地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aa6d43098f1c6f7c05aa857a55383154', '16c3469f', '发件称重扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aab182c677e780bc757142c30ff94594', '130af147', '当日运单货款金额', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aad716813c13246b5c4a0a9a8a51e4d9', 'b3c61d76', '请输入产品类型(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('aae1ad5b597b294e23eafcf382f40a57', 'bf49f3e2', '收件市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ab7004ce3dc143b019df63dd857fe15a', '8c95fc28', '加载中，请稍后', 'inLoadingPleaseWait', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:34:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ab9a5ad0861b581f334b40e39cccb680', '75167729', '主类别编码：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('abb079cfa64dffe29d79793b707292de', '1f49f811', '批量打印', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ac0ae589acf50ed86eb621c69e6cebfe', 'd564fced', '请选择班次', 'pleaseSelectShift', null, null, null, null, '1', null, null, null, '2020-11-05 19:12:15', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ac3be4c14a29cf99200fd2d0e84a6f40', '1c93f17e', '未处理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ac55cef90541701d239ec20adf60c911', '877b28ad', '下一站', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('acbfcb2323f3bb324c27500147ddb4d9', '4bbf3cd2', '打印列表', 'printList', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('acd6d9383b4d3b1f64a502787b2edfe6', '720ff789', '请输入计费模式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ad72dfa61175301ae213c90ab942f09d', 'c68c2415', '月结', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ad766de735f3dabea2cd242bd57fdcc7', '91f53e45', '清单编号：', 'Bill No', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:34', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ae36b56087abe1e2fdd74c4b083ebe99', '1490b864', '清关服务', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ae801b946f63a2302a12ef7972f413ef', '702b25b9', '网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('af66b7bb2f9ee0232ab2418ebaf3fe16', 'f8d985bc', '操作类型', 'operationType', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 18:49:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b05c0a55e79be2e40ded9102e0ed1207', 'e7db4fbc', '加收费用', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b0b74f20f89774aded534f3a6f149485', 'a370317d', '接口数据监控', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b129d31f51a684b97353c1bb953ffc67', 'd8e4260d', '描述 :', 'describe:', null, null, null, null, '1', null, null, null, '2020-11-05 18:59:57', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b13af055df398ab9ff200e37cae57412', 'acfbfd4b', '上一站或下一站', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b14f520679769bccc4a6ee15d924fafb', 'b063ed22', '请选择管理方式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b15554091bcb2248ae46ebfde06f8ebe', 'cb6e845e', '待派件', 'stayDeliveryPiece', null, null, null, null, '1', null, null, null, '2020-11-05 19:20:58', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b1916a385e7f2ba94eb8ad65ed1b3bf1', '63ece105', '体积重', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b1951a97294f40c6b5c45f80fe7016af', '8511', '请输入标准运费', 'normFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b27684bf4721bc9c9904ff48f94dc76d', 'f410e4db', '运单号码错误或为空', 'waybillNumberErrorOrBeEmpty', null, null, null, null, '1', null, null, null, '2020-11-05 18:58:05', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b2ce8ffce35e99f9d27a73120ff6e674', 'f5a0d931', '请选择城市', 'pleaseSelectCity', null, null, null, null, '1', null, null, null, '2020-11-05 19:17:59', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b2d4efbdab96ef0871031a7313c97a54', 'e38cb503', '基础信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b3978e76997d615649cdb12966b443e2', '0b3c2123', '派件交单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b3e8e84811bf850492347ae02b96b99e', 'ce3f0d9c', '实时监控', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b45b4c8646b85e375d6b56dd9381c536', '2f99ede7', '子类别编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b49c750ad1664d00b25d0230cb5c1539', '3350', '录单时间', 'recordTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b52f9727a95c9de4a48c338fce78ceb1', '1888272b', '证件号码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b545bdabd7104caa87d407530bce1b2a', '2455', '寄件客户', 'sendCusCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b54c245d7c422df7f8569543becad8ff', '40996717', '运单编号已经存在', 'waybillNumberAlreadyExists', null, null, null, null, '1', null, null, null, '2020-11-05 18:42:56', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b562b3f9d397436f83725ba8a1feaa49', '7993d4b0', '暂无公告消息', 'noAnnouncementMessage', null, null, null, null, '1', null, null, null, '2020-11-05 19:30:03', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b59d15ee5dfcabb22336ddb5c1cfe43b', 'e1532c55', '展开字段', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b5b17d0ab8ff461db26aa25c2ebc4d47', '233bf73c', '确认新密码', 'confirmNewPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:43', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b5b6911fa698f0b757077465ae739d74', '8b570659', '港币', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b5bf7b43955e1a96376ad28295cc1cca', '9bea9729', 'sad', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b64f4561bf8d9d28ab98d020fb1b9d87', '9c5da4b2', '复制新增', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b65fa81d47f944998e08fbd30aa63e0b', '7449', '班次', 'shift', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b6a9c286f4f9140730ba0251019af779', '14208d80', '关闭其他', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:35', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b6ae1ba2d660b2ab7252650078f9bd63', 'd968bee2', '员工编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b6bf4fbd76570dc9d709b905c45b6ed4', '0fc6403b', '模板内容', 'Template content', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-09-29 18:36:26', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b70cdfc5997f6af5f0d9a1c941e26c39', 'ab2ff941', '类别名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b70f90e718c140c29a0ad9899d5aca5b', '3498', '请输入收件地址', 'receiverAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b73e1e019e19d9e1d036d576cb777bb9', '2166bf05', '已取消', 'alreadyCancel', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:18:37', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b76347e509955b370e68fe27e47be471', 'ae57cd8e', '订单信息：', 'rderInformation:', null, null, null, null, '1', null, null, null, '2020-11-05 19:21:34', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b7653b3f1a53461081ca5bc9287bf7e5', '1095', '收件地址', 'receiverAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b7743cb9e88f59e36a67c1dc8aebfeca', '831bf3dd', '请选择营业网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b77be1dd65e08bd49af3397a0189d122', '1478bcca', '请输入正确的手机号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b7926a2610054dffb453be4e953adb46', '5408', '签收人', 'signerUserName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b7af28f3a236464ab5ba8f734fdd1204', '3674', '请输入其他费用', 'additionalCharge', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b8d61f56f0e1498231f7eb9baae1a707', 'cffeb3b2', '中心调派人', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b8f79cc2643ce24e6d1f6480b2d7aca1', 'a8dd7b47', '导出失败', 'exportFailure', null, null, null, null, '1', null, null, null, '2020-11-05 19:05:10', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b97b914b36f65df973f7ae06ed24bea4', '7142c947', '到件确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b99e3e745bf548158082d76a4922c12c', '3121', '请选择产品类型', 'productTypeCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('b9c9f53a5ea34074978f354dd2a106bd', '4427', '请输入优惠金额', 'couponAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba041534efed0c23b5500942092c9871', '8a20646c', '到件扫描', 'arrivePieceScan', null, null, null, null, '1', null, null, null, '2020-11-05 19:11:13', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba0c6b948eaa032be7ed8db65cf3666b', '0910c387', '异常信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba29fae45e55dadcd58457e01e30e0e5', '4d2d15b1', '手机发短信', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba3d5fbfca3d76cbc994b6095a2ded66', '3f55ff77', '台湾件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba64f4330f07167fb1f6b69e2b2a77d2', '8b8af996', '新增模板', 'New template', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba732e268a354e98d22cf46f9740ff67', '723f62c8', '打印主单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ba73794b55f4443593614c4d26e5565f', '3214', '产品类型', 'ProductType', 'هاتف المستلم', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('baab2d180965f9bb7abfcdcd16ff9628', 'bd58f5e4', '清单编号：', 'Bill No', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:36', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bab388991918168c03fa4a19ea656535', '706836b3', 'lable', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bab782931edf37a9d35011820ff5f973', 'a903d38f', '地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bb0f9b403bbf85f4c32200d0ddd553eb', '3a9ebc37', '请选择客户', 'pleaseSelectCustomer', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:14:25', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bb7fc1cff9fcab5df34f47c30bb8fe3a', '17da3f89', '签收人不能为空', 'signatoryCannotBeEmpty', null, null, null, null, '1', null, null, null, '2020-11-05 18:43:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bb8f73adef5d2e87ca6b431bab77a1cb', '0a48c534', '子类别名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bbda6ad024d9d55daed2cfd8780cb895', '8064ddae', '退出登录', 'Log out', 'Log out', null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bc7be8942f7e4079bc02393f77b638bc', '4694', '退件标识人', 'returnPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bcc321b5d5a51974998df11121a85c8f', '353ef2bc', '查询失败', 'queryFail', null, null, null, null, '1', null, null, null, '2020-11-05 19:20:16', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bcd1d07e801433c6568a147511457434', '6550a5da', '客户已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:38', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bd162e69f6da0c36b31d03a8e5aa2798', '693e94bd', '货款核销', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bd2ac4225702ba7bcf9f8f9b6d43a478', '00f27e21', '到件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bd4a74a433a845a38f0a490764221fe3', '5653', '提成金额', 'commissionAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bd7ea28bcc5fa36731c4ee3b67815e38', '75710d72', '上级网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bdaaa8d96b5548fe85e57495c0535bdc', '63ef6847', '对接管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bdd00003cab1af54c8adb5e980af3cd2', 'd6e42681', '密码错误', 'wrongPassword', null, null, null, null, '1', null, null, null, '2020-11-05 18:36:24', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bdd8908f3c22aa80c0a68ecaf5823c4d', '80367d07', '删除失败', 'deleteFailed', null, null, null, null, '1', null, null, null, '2020-11-05 19:01:15', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bddf2345e7d94a4594ffcaf9061fac34', '3114', '大字', 'large', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('be0b34b7dbf7e13dc173c081a35692d3', 'e0117f7a', '用户昵称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('be704e3d0af3fa8bae6e7dd1c76ffed9', '887fc0f8', '所属岗位', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bead724049c89081e496a2cf7e30734b', '4f224dc7', '本年', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('befa4ecc69464304b9aec52a383d4c89', '6596', '修改时间', 'updateTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf0ad90f8e954e05a1016daf55436e9c', '7538', '打印时间', 'printTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf2b778c43daba633d4170d011ffbc6e', '8c804b2e', '本周', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf3e7e1dcbb344d684cd5268cbe63206', '2511', '收件地址', 'receiverAddress', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf492051088d49108802a6614454a763', '3547', '目的地', 'bourn', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf746bc6b966ea239309170e4554e2a2', 'd1e7eb8e', '本月', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf7935c34ab5e058ab64709a36b31233', 'e73bae0d', '离职', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('bf7ae53a9238b0997ae3fe0fa836c38f', 'aa1ce0f1', '审核网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c000b88b4b0e141aca497f7a572c9ab7', '4f5a8e18', '请选择寄件网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c00ffd4ed3cecffeab34aa3fdcd2f56e', '1479ab7e', '录入日期', 'Date of entry', 'تاريخ الدخول', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c031584dcf607fa05d0e2c867f3e3169', '80db629e', '全屏', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c089ea55224c64985f93c9817b86cf4f', '001023ed', '类别名称(阿拉伯)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c0a58b39a5960402cfd1e58fe6b4857e', '5cbfbbe1', '分拣码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c0d15e79f6c9d8cfb267b2440e532383', '92ddee18', '提交', 'submit', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:51', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c0dbdc4bf957fdd2f7c280f54c91b5b5', '60e8996c', '快件派送', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c103d959a872e3cb4c13b5809e147800', '970e2b33', '图片地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c109e767c775a2f00dfb039db71f5913', '23528ef4', '上传时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c16a7e19809fecdfeb6c22880edf2a24', 'bd585af1', '信息中心', 'Information Center', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c21cac508d53cb688d1d36d792ffce05', '31969b52', '群发消息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:39', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c2266546f9c74ff6b7a441e320334c48', '934', '请输入保价费', 'insuredAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c23c4d1e1dcf2c0342b0035c8c5862c0', '42049b2c', '费用类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c240670f1dc0b17b9290bec5910da825', '42355555', '发件', 'sendPiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:09:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c287f644bb1c62640f4b258e1da7c857', '62e91d3f', '请输入客户简称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c29635b8176821f8547cd56894d081b9', '55c4a111', '派件', 'deliveryPiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:30', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c2e9bbecfb01acd3c11c9f80583f80d7', 'fa3371f0', '操作员', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c2eafff7a624c4551374fd8023cb0239', 'a356fdd0', '到达日期', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3283053a664246c3be8cfc60b8a98c0', '6aa47040', '大字错', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3320dc18ca7c0d7ff2dd1cff1f219a4', 'd9e6d4b0', '滞留件2', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c36cdf4c884ac258b7b4594d7be46785', '1bfdaeab', '未找到扫描件', 'notFoundScanPiece', null, null, null, null, '1', null, null, null, '2020-11-05 19:20:26', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3baa61bc1ba873e78a322601351a8eb', 'a3fca830', '已调度', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3bd51c825fb61c2526feed26c68005b', '6833b964', '版本说明', 'versionsExplain', null, null, null, null, '1', null, null, null, '2020-11-05 19:28:34', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3c01056aec8c34eac3b7cbaa55a9e61', '4a7f888e', '打印', 'Printing', null, null, null, null, '1', '3', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c3d56c8830b696513c9e8b9fed9baa65', 'c4ce9f7d', '暂无数据', 'Temporarily no data', 'عدد لا يحصى من الوثائق', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c480c685edc89ccd018d76f85705f47f', 'a64e0bb2', '请输入客户编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c4bbb92e35694c4b8b970f7cf372568e', '4682', '寄件人手机', 'senderTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c4c8eb6d3b9a5b0685e14a32c417afbb', '5c80ca55', '短信通知收件人', 'SMS notification recipient', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c52724572ebf13ffd18968b4ca2e0c43', '0a15e635', '请输入单号', 'Please enter the tracking number', 'إنه كبير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c55affeee86dcfe38e8f65d4b0028cfc', '3a5dcb81', '开发中', 'inDevelopment', null, null, null, null, '1', null, null, null, '2020-11-05 19:06:09', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c58b36f17e048a7bde0bfed354a0fa4c', '13e7a3a3', '用户账号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c5d20ba483b663a8945c24ee0147a018', '728112ff', '请输入用户名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c5d38421ef836cc4725e4e18143dcc43', 'fa3ce802', '快件跟踪', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c5d53d05da1a21c530e27e9c5d52df1b', 'f2d1ee6f', '下单时间：', 'orderTime：', null, null, null, null, '1', null, null, null, '2020-11-05 19:22:14', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c5dc04af18ad84a2cdf97f87572d9d47', 'cd193f3f', '请选择导出的数据', 'pleaseSelectDataToExport', null, null, null, null, '1', null, null, null, '2020-11-05 19:01:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c63b5a91c152c40ca9bf429aae47af4a', 'b18ea45c', '地区名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c6754d5022b54342eca2826946f0b4e2', 'fd515605', '维护日期', 'Date of registration', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-09-29 18:37:17', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c68346c672ce13bcf8fcdd869734b237', '6bb0a797', '正在保存', 'inSave', null, null, null, null, '1', null, null, null, '2020-11-05 18:47:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c696ab9837f22d2fba246d962ae96b09', 'a25ce943', '类别名称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c6bbde1687204debe7a2e6588ab4a472', '506fa30b', '产品名称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c6f6963f329b9e3ecbf79598b8ec1237', 'f10b1e23', '告警类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c76a4b59a56983608ff0bb3c05756b09', '68416c1c', '请输入重量段', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:40', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c79d7c4f057a4df89b6a4c8c8eeaefdc', '8414', '请输入物品价值', 'goodsValue', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c7a2cce96eb3bafef07f9a992776ee72', 'aaeeed84', '客户简称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c7bcf1c417fc4f1fbf56ef1a02a6c0f8', '5115', '直退标识', 'abcReturnFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c81f6ba03acf0d4e4e39bb568f931dbe', '705096b5', '语言选择', 'languageChoice', null, null, null, null, '1', null, null, null, '2020-11-05 18:35:55', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c836fcfd11ce3773dbdf44c2965fce0f', '7fbb41f2', '客户名称', 'customer name', 'اسم العميل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c84b3e7cd5ba8f562aaa3911c5ccade9', 'e99a6519', '预警中心', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c860a9696e8cad77b7ef8ce6d400b008', '3c96778f', '计费模式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c8a3fa2961b6ab41e4700264566f5cd6', 'f4700bcd', '货单发件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c933eeb208f858b81022f5c5c192042e', 'a2044eda', '设备服务商', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c97315271d96c0ab7bca1156d576022b', 'b1458efb', '接收手机号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c9d5b3d61d5966e7c04c5f30c29b7b84', '311797c2', '请输入默认发件地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c9e716036df15b9ae5a72eb354f3da55', '3db228c9', '发送标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('c9e72f478cf6c8a3543bd2d186914761', '1b502d63', '正在修改中..', 'inModification..', null, null, null, null, '1', null, null, null, '2020-11-05 19:28:08', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ca05aee6b2d44eeeb00d673ddbe0aa74', '8378', '中心审核时间', 'centerAuditTime', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ca3ba97a438843c0273d68d0ecd57aed', '52eec9d3', '福建件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ca641280bf9df2e1bd925053e761c8c2', 'ad34ce13', '请选择下一站', 'pleaseChooseTheNextStop', null, null, null, null, '1', null, null, null, '2020-11-05 19:11:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ca988056e6ed380a6bc6121497d70caa', 'b73dc47e', '批量删除', 'batch deletion', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cb292b8731eb52255913c46fde3f30e8', '97669e98', '客服', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cb324fbbd72afcd878a0f7009d9334ef', 'd4151a7f', '展开字段', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cbc327c6cead7760c71994606c76d9eb', 'ed3256ad', '开始日期', 'start time', '', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cbddadc99d9aa317b7e559bc22c4591d', '209ce4f1', '签收状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cc84bb4a972603f1cf4ed12135386dec', '8ce6f5cc', '收件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cca8abc50b56bde6a4daaed42677a9f0', 'aa4f06e3', '阿拉伯描述', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:41', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ccde6227b55990ee78768a81f7aca837', '39ed6ae3', '生鲜递件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cce6107cccbe6d9ba228c0b1d775e268', 'eb67a992', '请输入主类别名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cd131df995c71b54aabe20fa7f1e5e2c', '28375564', '代收限额', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cd2895f0755fed8931a828396ade41c4', '1c154659', '寄件人省', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cd48faf64a76fd98bee4302051891547', '9f681431', '请输入名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cd73fa2ff44da7d1d5ed205152372735', '59ddc83e', '重复新密码', 'repeat New password', 'repeat New password', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cdf1d1eac21074b484caa818842cd096', '734f79e3', '设备类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ce35ec5a9a6083116b12e5bfddb0769f', '0c51596b', '收件录单', 'receiptRecordingOrder', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:09:32', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ce3ac2a05955eecfe88b3aa9e9877205', '3368a0a9', '版本更新', 'versionUpdating', null, null, null, null, '1', null, null, null, '2020-11-05 18:50:20', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ce9036d8905bdbcef2649c04fbd0cf77', '975c1450', '分错件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ceb8090f9a271fde557b8171f75a3bc0', 'ca6d5467', '新增客户资料', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ceddc14d82edb5c0170063bc4f01e1bf', 'f38c1acf', '派件扫描', 'Dispatch scanning', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cf20bc008820341d5cd5beb1f98d51a7', '24165faa', '中心已付款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cf3ce79ed8c929769c1ce74c1ac793c4', '153bdfd8', '已结束', 'haveFinished', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:19:08', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cf55bf93207ff3e65d6328c85600a8c5', '131fafc0', '收件人信息', 'consigneeInfo', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:11', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cfac82ad1b650425536bce664b9e2a86', '07c40702', '到件', 'arrivePiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cfc42333ca1963672ccfb78439e3ed4c', '47c7ebd3', '证件区', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('cfdfec0e0def1af5792064b303ff6f61', '925510f8', '新密码和确认密码不同', 'newPasswordAndConfirmNewPasswordDifferent', null, null, null, null, '1', null, null, null, '2020-11-05 19:26:48', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d0753a8ca23b11823595f8db6ca1f049', 'a056216d', '跟踪记录', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d10bfbe81aaa88d8fd3495c3c1091d0a', '134f0f6d', '付款确认', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d117e016f78a19118f23590d59c7118d', '9eec7881', '寄件', 'send', 'إنه كبير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d1243f4ab10585208f70f7598be76bd1', '24bdaca7', '正在清除', 'clearIn', null, null, null, null, '1', null, null, null, '2020-11-05 19:00:27', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d1484fb4d20647a4a7c3ab6f390a2ba2', '9821', '审核标识', 'auditFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d1b03624dbdec9e7fe37d4a1ae1b4683', '7502348d', '转第三方扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d1fc228b1fd574a31f58f5a759c95296', '2291a95a', '允许到付', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d1fff931c88681771c55d9ae473744e5', 'cd80405c', '货单发件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d209f04026df1fc25b282d437a8bbaf7', '20ea7ac7', '打印机已断开', 'printerAlreadyDisconnected', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:47', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d26c9d5cd57a723357e72b4149133c95', '517d7039', '	\r\n进位舍1', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d286ef028c3667c612364aa0cb0c94d9', 'a0340d7d', '订单来源', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d2d58507c02d8d85a70987a14984acf1', '98532f6b', '请输入新密码', 'pleaseEnterNewPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:26:09', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d312216e159944fc8c01ed252b1a9043', '2a85a0b1', '未签收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d337fb1f874a6a906e69759d23fa3193', 'b62f2955', '开始上传', 'startUpload', null, null, null, null, '1', null, null, null, '2020-11-05 19:05:46', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d36ecf36f5b82afcfb4d484ebe1064df', '47fca57c', '发件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d3ab67b1d3243d7e0c738979488c48bf', '79f51983', '滞留件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:42', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d3c6e7fd628dbf74695fb74a422980d2', '805527a2', '未审核', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d3db34782ef8ae9e4025bdad0667a2f3', '30e8b788', '收件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d51a77f366e950e4642171c0f4b80738', 'f1f8d227', '停用', 'Stop', 'تعطيل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d55b4c63c8b13697f058542b2999baff', '5342c57a', '货款手续费', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d59cd785ee31f153f255303bebc6419a', '49f3e962', '序号', 'No.', 'رقم تسلسلي', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d5d72b88e15089180588698262bf81b2', 'ffacb756', '到付转月结', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d5de4f8048fd22fc5f1e00df4b85fd13', '474bc143', '名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d5f785e55e836c9d5df5a048719fca0b', '98a9c1bb', '旧密码', 'oldPassword', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d61102adacb2fd7409cef760dd7e7fa0', 'f22d3ce1', '派送费结算', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d649e42fe23ecfd9b1affe2a6e169286', 'cb6c6147', '类别名称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d6641e67eb5c758b761f379dca576fa3', '94aa8bc3', '客户姓名：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d6a711ff71aebd0d341c783dd31595eb', '0c2f21d4', '请输入计算公式', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d72c383d7793e74662d59dca6052f603', '71cbd41e', '修改多语言', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d73273e2a5821623421923abae41ca39', '451f4710', '请输入角色名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d749bd094d8293921230cba077b5f58e', '550b30b5', 'VIP平台登录密码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d7836e475fc1d2e2c668e8cfd418a061', '01657f92', '多语言查漏', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d78501eb45ed973780655e76b7721501', '05ed14fa', '操作日期', 'Operation date', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d7c1c3ade1414d8f8a1a32adf1aff4ce', '3641', '锁定标识', 'lockFlag', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d8143f47288053dc991dde59ca58ac63', '3244d9ad', '关闭当前', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d8a85b6998389f0f9db152f3e8fa5651', '155e2c00', '中转费', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d912b04f05daf3af4d315fa2d0e65b4a', '9d677b0b', '地区编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d95d991856886c503c0d07112133c283', 'bc1e196c', '寄件人区县', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d982a0e6fa0b9f4b299321eb598229d7', '2281b5f1', '取消调度', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d9e83685906bbc78c242d2404acaa3c5', 'a7e71152', '微信ID', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('d9f257c9c65537ff95132f659672153c', 'a944bd97', '子类别名称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dab182f89ac4334d7fe5a83827fd74f2', '23c15123', '已派件', 'alreadyDeliveryPiece', null, null, null, null, '1', null, null, null, '2020-11-05 19:21:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dae614ad36e70aecf9fa21cbbd67e47d', '59f760be', '寄件人电话：', 'senderTelephone：', null, null, null, null, '1', null, null, null, '2020-11-05 19:22:50', null, null, '2020-11-05 19:23:09', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('db3bddf5cb4b4557a3088656529438ad', '4635', '物流方式', 'logisticsMode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dbc32a845ce34e6be4b44293f2d89476', 'ee7a5d4c', '操作中', 'inTheOperation', null, null, null, null, '1', null, null, null, '2020-11-05 18:32:38', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dbca6991392249e483cdb57d9804fe2c', '110', '回单标识', 'theReceiptId', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dbdab8ad495ba3b1e11f50e5c2105c4e', 'caaeb845', '签收详情', 'signPieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:02:14', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dbe21de82b2a6db6edb7b3e696067f65', 'ce59b184', '中心已付款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dbecaf0c667c536072e7b1f08e307af7', '7a5c9131', '寄件人市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dc08ce6d8fc4816ff6eccf5dfdfd983a', '01d91cbf', '请输入联系电话', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dc782af635ebc1585dbcf9756fd6e76c', 'eef2ce67', '寄付', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:43', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dc8f9da34cd6d0efb48929dae101c2a8', 'ed7bf900', '在职状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dceeebe12cc544c198185cbcc480c7de', '4204', '客户名称', 'sendCusName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dd0ef5e0f5778755ed8afa4fdd871cf6', 'ffd6a8f6', '操作方法', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dd9877dec1584a81abd41bdfb7a897f7', '1328', '子单号', 'subWaybillNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dd9e601ed3574d8b78c12b747e512c02', 'f86afdf3', '操作提示', 'operatingHints', null, null, null, null, '1', null, null, null, '2020-11-05 19:03:40', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dd9ec72a900941fabb8335f64b4a9efd', '8256', '寄件人邮箱', 'senderEmail', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dda391c637b44b10839f684c76183b18', '6857', '请输入体积重', 'volumeWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ddad689e65b04a64bd6fa403ccdbce14', '7935', '付款方式', 'payMethod', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ddf8d698ebb8f8383310898128da70b5', 'ebeb87cc', '请输入地区名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dea0fe486e5447038c1d6e77cd4a7550', '7796', '收件电话', 'receiverPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('deeced294f15da7f18465245858f921c', 'c1d194fb', '回款登记网点', 'tttttt', 'tttttttt', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('def066bb62344a8fa084c25f5f7b398c', '1747', '保价金额', 'insuredAmount', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('df1351f72330f17c11ec93b808924a96', '82615cc6', '签收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('df18fa5b0c6ef57406bfc1be19e7e251', '3be39e47', '寄件信息', 'mailingInfo', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:00', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('df28263dcab46d7250bcb8a5a0bf1f49', 'e0714a82', '取件时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('df837d88dc7f018c5c4156d761071788', '08757dbf', '新增子类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('df9855dba5321be6e54c23db6b4e823f', 'cd861307', '反审网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dfaa1ec4e4cb4b2cd5c5f8f3a703f9d0', '13e328a5', '新增员工资料', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dfe68f401a0d9f823a954104a37698d2', '400b5b6a', '请输入主类别编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('dff17c188282bc200da3d9f7ad262b14', '39f9690a', '中心已收款', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e0334356dcf95d38924c1377418b17a9', '0ff3ce3d', '操作日志详情', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e03abbb3a1c67a3de19dcd74678c1045', '573b5293', '请输入寄件人名称', 'pleaseEnterSenderSName', null, null, null, null, '1', null, null, null, '2020-11-05 19:17:17', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e09af8016e6af18c0ea82fede27f8f69', '80f8f4de', '录入时间', 'Entry time', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e0a7aafa2fad2f3c00a7d4f5279b5d9f', '9307b1af', '我的', 'mine', null, null, null, null, '1', null, null, null, '2020-11-05 18:37:31', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e0b909e3ce7ca99b6fbe1dac5001f208', '3d1ac6d6', '派件网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e10fcb326ad0ee87371ab6735b1b369e', '5852ec37', '寄件省', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e1547b8f77754eda873f89abb8759886', '6978', '寄件电话', 'senderPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e17a27d6efb9499597ba717ace79bc1c', '1789', '派件员', 'deliveryman', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e22e93b675e596c837dc95074c4ae414', '148a3ff7', '请选择关键字', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e23118824ad817a3b938cea2305c9397', 'eb4b66d7', '员工编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e23ac757ebf2b2e63bdcc4ee90adfeb1', '8ef36045', '发件详情', 'sendPieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:02:49', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e23c37b657a4333bc51fc5c66657136f', '43f3f8f1', '请输入分组类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:44', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e2aad4541a3195e1c362d7c251ff8617', '9123fe06', '网点编码：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e3445aec48787732b3e4c001a5e69256', '9069ffbe', '保存成功', 'saveSucceed', null, null, null, null, '1', null, null, null, '2020-11-05 18:47:24', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e35b6cad1cfefd3aafefc1a9a8d78d85', 'd185e4b7', '请选择删除的数据', 'pleaseSelectDataToDelete', null, null, null, null, '1', null, null, null, '2020-11-05 19:00:58', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e38430d5fcc3b99762b3b2637855afac', 'd58b7c90', '留仓扫描', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e399ff39afdcca2841d542bb8fb7ea3f', '98ccc22a', '正在删除', 'deleting', null, null, null, null, '1', null, null, null, '2020-11-05 19:00:11', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e3ac25179f2055cd3fb84a8a6e6ccb2b', 'ad7a0705', '请输入员工姓名(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e3c084313eeb56edf7d3fa2900abe8e1', '42d149f3', '请输入子类别名称(中)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e42a2c843a7e4f6a92d52990b322c04b', '5251', '支付类型', 'paymentType', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e42d9ff7e2ec69e6a776bacb17ebf78d', 'ab164cf6', '新密码', 'New password', 'New password', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e430dad06fbfa84a3bd977e7d52f39b2', '75514f5f', '中心代收管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e445379be9d934f1c7a5e5abfe73fe26', '872324ea', '首页显示', 'homePageShow', null, null, null, null, '1', null, null, null, '2020-11-05 18:39:46', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e45dddf3c299251fc55ca90fef9e65ac', '7ce8ef6a', '主类别编码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e493ea85aee670b08b99f17da475ef57', 'ed4f2d92', '请选择', 'please select', 'إنه كبير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e4cc5ee7d88f464eae5d659986117206', '3202', '用户编号', 'userNo', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e4f01101b74a20bbdd0323306200d49d', '2c731025', '工号', 'workNumber', null, null, null, null, '1', null, null, null, '2020-11-05 18:41:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e4f65a088fa507260207d3244e2b7688', 'bfa2dff7', '修改子类别', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e5687b7812184275a38ec771b098481c', '9121', '总运费', 'totalFreight', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e56ac42476a8d3e91bb8845f75a305d2', 'd10cc64d', '重新调派网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e57bd59aba41303bd148d68515e26b72', 'bb78a41f', '订单状态', 'order status', 'حالة الطلب', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e5c59618fb2ee352e85315fda40f7e0e', 'a5e54437', '请输入网点名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e60b42d26fbe68788d6c891bba190567', 'a68cf1bc', '新增目的地', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e619ef05da8b53fcd4b1d1a691f16f09', 'c6015922', '问题件详情', 'issuePieceDetails', null, null, null, null, '1', null, null, null, '2020-11-05 19:02:06', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e670b474675046cde9d516fadccb2e7b', '4f2834fe', '现金收款', 'Cash collection', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e695b8c1019db800fb76a8929a3133bd', '9bd2f40d', '清关扫描', 'Customs clearance scanning', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e6a278b3b3e31634a4d9ff541acfa4c2', '4ca9cbde', '核销标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e6c3698a73bc119dcab84b6790b8fd95', '74fbe361', '签收件', 'signForPiece', null, null, null, null, '1', null, null, null, '2020-11-05 18:37:55', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e6fef1eb7e0c4d33928b3ef426f5fe95', '1969', '尺寸(CM)', 'size', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e730ea3d8251da12f6cb485a2657ce21', '7ece1be1', '国家城市维护', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e73e9ad7c3ee1db8119374c347eb9f24', 'e7f9ffd5', '新增短信模板', 'Add SMS template', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e78d5909ccb563e8a0583ca766640d03', '2dd19ed0', '正在刷新语言', 'inRefreshLanguage', null, null, null, null, '1', null, null, null, '2020-11-06 15:18:53', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e7d0e2592017f989e7abf1f8b8faa7a4', 'afb42926', '保费查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e7fc15b3665eb14d93622793c56a6ca5', 'e92401fc', '登录时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:45', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e80954545ae3b459240e1c98c6f52245', '8203d114', 'ssda', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e80a55ebadda2a8856da2c4272712c2f', 'bd1532c8', '发送时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e89e2a65015e4fd69c03244970b2d434', '8656', '代收货款币别', 'codCurrencyCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e8e8665553b497506dc43355c44b9114', '689ef70d', '输入查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e8e8c211523e908e5b3ac62d93c0186e', 'c2b515c6', '目的地区域', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e910bc739368478890130a24e391a9da', '9037', '寄件人', 'sender', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e94448c195f6d0a6ca040617d95243b2', '8ecff867', '运单号查询', 'waybillNumberQuery', null, null, null, null, '1', null, null, null, '2020-11-05 19:05:54', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e95d0abe925b18fe74a54a97b701c60e', '1ca75baa', '主类别名称：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e98dd3c4b2609fa0c3f744603ea6223f', 'acc83e9d', '寄件人信息', 'senderInformation', null, null, null, null, '1', null, null, null, '2020-11-05 19:16:19', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e994828c7510299e3cb653d5e8ee1e1c', 'c2e0638c', '员工编号错误', 'staffSerialNumberError', null, null, null, null, '1', null, null, null, '2020-11-05 18:36:10', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('e9cf50e48448342c51f0a61625da35c8', '79da4227', '导出', 'export', 'تصدير', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ea4d8800b6faf5f046a8b04b0b76a18b', 'e281b4fe', '网点派件通知', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ea76891cd834569dc3a8e1698bb3ff2c', '5f506be8', '货款查询', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ea8959c7bc4ce2945e7a11868cac0fb2', '77e4f4b0', '请选择物品类别', 'pleaseSelectTheGoodsCategory', null, null, null, null, '1', null, null, null, '2020-11-05 19:11:58', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eace715548ecc043de33a5943470ebb8', 'fbebeb06', '晚班', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eaf520856cd14c56ab8f9f55cb7d9ac9', '5614', '目的网点', 'destSiteName', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eb2014e6f69221567791de045345dcb1', 'd55e997d', '待上传总数', 'noTotalNumberOfUpload', null, null, null, null, '1', null, null, null, '2020-11-05 18:47:56', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eb30323b41bb8d92b35e9dc6df890d1e', '7325a653', '疑难件', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eb5908f14005c8f93c6033742a4ddc9b', '9793bda5', '入录部门', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ebb4d44969c0ca63ab7b77323c4b11a8', '3a98c966', '操作系统', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ec4f46e4cc4146d591dffd2091926b9c', '9706', '请输入收件人手机', 'receiverTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ecb7923e7eb8717da327b3ac43c5d08d', 'aa5fb763', '中班', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ed0458620b83dd2f2147d2593b402d50', '88b83f5a', '车牌号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ed1992b47adb4491ad7afed89446c4f2', '1497', '收件人手机', 'receiverTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ed2110daf9873453ab78778547eb6a17', '717059c1', '版本号', 'versionNumber', null, null, null, null, '1', null, null, null, '2020-11-05 19:28:40', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ed8f2b36af83929733dd5f279d5ad645', '88006fc4', '登录信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('edc9b3c0aa3a3bd6d39fa1991f3588f9', '863e41e1', '车牌号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ede2f9112c1980c3a59ef491d52529af', 'd4e24b3b', '允许代收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ee17a293509647ada3c3a327ce9f32e5', '5062', '业务类别', 'businessClass', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ee6585a9e73e93d8e9e763a4c109f6cc', 'bb763d65', '问题件类型', 'Problem type', 'نوع المشكلة', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:46', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ee97c48643c25ec8d7b419ecb3bce546', '49d47483', '证件地址', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('eecc32d65b0cdbacc1c7447d2c20ae05', 'b8cb167f', '留仓件', 'stayWarehousePiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:10:05', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ef2bc4020acacd0cc2fb7dba58bee2a8', '4fc810df', '失败原因', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ef2d0ec18a35f4639f6f1fb6ea4f3211', '4ef29f7d', '角色名称', 'Role name', 'Role name', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ef4dea3612ddec82985f0290d84d4134', 'c6e37d04', '收件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ef61d5ff060ba0e4837e77d67ac200f3', '8a7a0be8', '收件人邮编', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ef860d55e888f209a6bd3b28f2c6ec59', '7c76f119', '请输入客户全称(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f00627b65a65d8ebef60a7942ef021bc', 'ea292041', '您确定删除嘛？', 'areYouSureYouWantToDeleteIt?', null, null, null, null, '1', null, null, null, '2020-11-05 18:52:31', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f05bc6c5ffe71c2c2cd25c37f678f2a5', '539df243', '授权', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f062c124b0208aa556761b2347c8e752', '4b8c18c3', '已取件', 'alreadyFetchPiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:18:29', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f09609a4680073340d6db918c9da77ca', 'b5c8e54f', '上传成功', 'uploadSuccess', null, null, null, null, '1', null, null, null, '2020-11-05 18:55:39', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f0e8d3a2993144213ceff71c0bf54679', 'c4430de5', '浏览器', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f11ce04b710e73c40486676561e87afd', '3ab49f92', '请选择营业网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f14d2d743ff4c6fd30b6589db82d6da9', 'df05f433', '请输入内容', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f156d487825546e49655dab1fd86a8b1', '8393', '请输入收件人', 'receiver', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f19aacba9b457ad82b2ced950a7d6470', '4ad9aed8', '请输入地区编号', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f1abb37d6b80112c0762263c6d1256bc', 'b9bbd6ed', '网点名称', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f1de7016d4b3be9fa031f629a44913e8', '700e123f', '操作状态', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f21fc467852f43d29ff870695753ba01', '4284', '收件人邮箱', 'receiverEmail', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2367785f41d92b005fc2b7eb323d501', '6478be94', '问题件类型*', 'problemArticleType*', null, null, null, null, '1', null, null, null, '2020-11-05 21:39:48', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f27d8e1d4ac6427c97a16b274a76c87d', 'a4fb8e4f', '扫描雇员', 'Scan employees', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2804c5b09a96d0453926958ef942b36', '8bd5bfbb', '运单信息', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2b9d41acaf9be6731de7bcdb1ecfd78', 'bb7be7fa', '生成代码', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2ba76b55d36c7c726060249919f3c3e', 'a3ba72ef', '选择网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2d31444ec9bcde9645c6c887068bd3c', 'c50326cc', '发送网点', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2ecd6c9885797b5b2002c21b4362e93', '58ec517b', '密码修改成功', 'passwordChangedSuccessfully', null, null, null, null, '1', null, null, null, '2020-11-05 19:27:55', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f2f5c7bffdb5a3488301b8fc4bd47201', '3e20fc8c', '字段管理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:47', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f340d255b89af9c094797a666c992185', '781ccc0b', '登录失败', 'loginFailure', null, null, null, null, '1', null, null, null, '2020-11-05 19:05:33', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f37e7c9da4b659395c69fb5b3095e6f3', '55d8fa00', '请选择签收人', 'pleaseSelectSignPeople', null, null, null, null, '1', null, null, null, '2020-11-05 18:46:47', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f3de28619d24150dcbaa7e5f202816ef', '316da836', '代理', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f3f3f7e72c6dadd360e7212a25d5c126', 'e46ce428', '短信模板', 'SMS template', 'قالب SMS', null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f3faf4e0099353f3a911f809d9d69fd0', 'b1d45bed', '请输入产品类型(英)', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f4788d80191f4b0db413f3c8ff1307e5', '2706', '派件承包区', 'deliveryContract', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f47a63bd612ee647d5b0e689a416bed4', 'baa39fe7', '搜索', 'search', 'search', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f485fca608cd1494077f331bf73012a8', '966a1d8f', '已收件', 'alreadyReceivePiece', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:18:51', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f4fc07ff6c6d2c4b2e737287f8a45206', 'e2093638', '客户编号：', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f527977a545b2eac091e41b3660e0878', '0542b4f5', '个人用户', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f5419f8e2ae6b508a394c5c0a05782b3', '73d82cae', '签收标识', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f5b87b23357140288dde5c395fe02479', '8223', '请输入寄件人手机', 'senderTel', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f67c41f7af3c73e28fd0f61c65cf6f24', '7eacfed5', '登记时间', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f6aae284230fb4144123adb5772201c3', '5526fd29', '上次扫描数据', 'Last scan data', null, null, null, null, '1', '4', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f6d06f6e351856d4bb7a55199917b4aa', 'cb25cf4a', '已下单', 'PlaceAnOrder', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 19:18:11', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f6df40115516081d4bb7ccf25e6d8a72', '645ccfb9', '收件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f7754e908cb9f09772096c060c737d6a', '162da554', '请签名', 'pleaseSignYourName', null, null, null, null, '1', null, null, null, '2020-11-05 18:43:25', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f7af5dc9992c83243d550f51821cda6f', 'f35fa3f3', '脱机登录', 'offlineLogin', null, null, null, null, '1', null, null, null, '2020-11-05 18:35:15', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f7cf2573e1a9c2be2e622b089586bc1b', 'fe2ea65e', '订单号：', 'orderNumber:', null, null, null, null, '1', null, null, null, '2020-11-05 19:21:56', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f7d659768ebcf5b66a1303c5964f00c3', '4567c568', '请填写拒单原因', 'pleaseFillReasonRejection', null, null, null, null, '1', null, null, null, '2020-11-05 19:25:03', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f836bcc53772554f9cc4070e4ddf4b00', 'f8ccde53', '签收', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f8c4f77d2654896779f62b8b08217081', '165f3fe9', '财务中心', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f90c4e13188902d3a21b4d40de45602d', '38fcdcda', '请输入产品类型', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f91ffeb9e893642a753b2defed3ae62b', '7d3e4759', '下单成功', 'checkout success', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f945e5071c4d7ae7c9701e6ba0264fc2', 'a2016ca3', '在职', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f96a4aabf802f1898be665786890be8b', '9f1011ad', '扫描时间', 'scan time', null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f9a393c623c9dc3de95498517207a700', '86b63d64', '体积长', 'Volume long', 'إنه طويل', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f9c346e77ce52353d7c8dea8be34d4fc', 'f38f5474', '签收录入', 'Sign in entry', null, null, null, null, '1', '1', null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('f9c85f3f3890d23f5e2ee81515547e8a', 'd5b04656', '角色', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fa3b54d2b97774cebed0149a7a29de76', '0fa0b128', 'AWB到货数量（提单）', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fa8a81e7196ee6986382b9b6919f3852', '7f449399', '收件人市', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fac65efa080b65a24c0c202a215f0a00', 'c0a07d2a', '派件清单', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:48', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fae790808f704054a4ad3e298466be45', '5048', '收件人电话', 'receiverPhone', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fb3f7f99e9eb5982214747861072dbd9', '52076b38', 'Delivery notice', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fbaaf4b6042240a3bb81632405c0aa21', '9216', '付款方式', 'payMethodCode', 'هاتف المستلم', null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fbea8549ff743e89537c6a3befd21953', 'ca877401', '最新扫描单号', 'latestScanCode', null, null, null, null, '1', null, null, null, '2020-11-05 18:46:12', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fbf187bbd69fe91108be9a49e41799ae', '32e26eb9', '付款方式选择', 'paymentMethodSelection', null, null, null, null, '1', null, null, null, '2020-11-05 19:08:40', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fbfe3a7f4fc08a690e808fa99296f28e', 'd4a78cd2', '姓名', null, null, null, null, null, '1', null, null, null, '2020-09-29 18:35:30', null, null, '2020-11-05 14:52:49', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fc94838c50d60ce1c5a0eaa478acf16c', 'c605e279', '选择文件', 'Select file', 'Select file', null, null, null, '1', null, null, null, '2020-11-02 15:00:17', null, null, '2020-11-02 15:02:14', '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fca92d6a3e7a47cc8f93ae1560f5fef0', '8110', '网点审核人', 'auditPerson', 'هاتف المستلم', null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fd3887846e9481a825c838d4967921dd', '08c3c82a', '已审核', null, null, null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fd7cee29660ceb8c24178723f35e7240', '7f0de474', '转二派', null, null, null, null, null, '1', null, null, null, '2020-11-02 15:00:16', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fd836abf4eef4d1d89f7102f01004699', '5982', '计费重量', 'chargeableWeight', 'هاتف المستلم', null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fdab2187598f14a9f47211595728f1e4', '3020c002', '关键字编号', null, null, null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fddd8b725d744c0df0fb5c622ba3b1f1', 'a149d30d', '收件人手机', 'Recipient cell phone', 'هاتف المستلم', null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('fee913774ae9f823c0e45aabdc63b246', '1fd0f06b', '运输方式', null, null, null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ff3c111a57654f56bf3c660ea117507e', '14e5808c', '点击刷新', null, null, null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ffa06acacbb5e0c8008b15668c647f99', '0180b3c3', '【Riyadh】已离开Riyadh,发往Jeddah', null, null, null, null, null, '1', null, null, null, '2020-10-30 22:26:08', null, null, null, '1', null, null, null, null, null, null);
INSERT INTO `sys_lanres` VALUES ('ffaec33cb3af43528eaadbf43b3a53ac', '3498', '收件人', 'receiver', 'هاتف المستلم', null, null, null, '1', null, null, null, null, null, null, null, '1', null, null, null, null, null, null);

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(50) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='系统访问记录';

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES ('1', 'admin', '127.0.0.1', '深圳', 'ie 7', 'Win7', '0', '登录成功', '2020-07-28 17:58:27');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `type` int(2) NOT NULL DEFAULT '1' COMMENT '权限类型：1-PC，2-APP，3-VIP',
  `site_rank_code` varchar(20) DEFAULT '4' COMMENT '网点级别：数据字典维护',
  `site_rank_name` varchar(50) DEFAULT '' COMMENT '网点机构级别名称',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `parent_name` varchar(50) DEFAULT NULL COMMENT '父节点名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`,`remark`)
) ENGINE=InnoDB AUTO_INCREMENT=2355 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('2010', '1', '002001', '总部', '系统管理', '0', '主类目', '17', 'system', null, '1', 'M', '0', '0', '', 'icon-shezhi', '', '2020-07-20 16:54:42', 'admin', '2020-10-23 10:34:22', '');
INSERT INTO `sys_menu` VALUES ('2011', '1', '002001', '总部', '菜单设置', '2010', '系统管理', '1', 'systemPcMenu', 'system/pcMenu/index', '1', 'C', '0', '0', 'system:menu:list', null, '', '2020-07-20 16:59:45', 'admin', '2020-09-23 10:47:04', '');
INSERT INTO `sys_menu` VALUES ('2012', '1', '002001', '总部', '角色权限', '2010', '系统管理', '2', 'systemPcRoles', 'system/pcRoles/index', '1', 'M', '0', '0', 'system:role:list', null, '', '2020-07-20 17:00:32', 'admin', '2020-09-23 10:51:16', '');
INSERT INTO `sys_menu` VALUES ('2013', '1', '002001', '总部', '用户设置', '2010', '系统管理', '3', 'systemPcUsers', 'system/pcUsers/index', '1', 'C', '0', '0', 'system:user:list', null, '', '2020-07-20 17:01:17', 'admin', '2020-09-23 10:51:26', '');
INSERT INTO `sys_menu` VALUES ('2014', '1', '002001', '总部', '网点资料', '2010', '系统管理', '4', 'systemWebsite', 'system/website/index', '1', 'C', '0', '0', 'system:site:list', null, '', '2020-07-20 17:02:01', 'admin', '2020-09-23 10:51:34', '');
INSERT INTO `sys_menu` VALUES ('2015', '1', '002001', '总部', 'APP菜单设置', '2010', '系统管理', '5', 'systemAppMenu', 'system/appMenu/index', '1', 'C', '0', '0', 'system:appMenu:list', null, '', null, 'admin', '2020-09-23 10:51:42', '');
INSERT INTO `sys_menu` VALUES ('2016', '1', '002001', '总部', 'APP角色权限', '2010', '系统管理', '6', 'systemAppRoles', 'system/appRoles/index', '1', 'C', '0', '0', 'system:appRole:list', null, '', '2020-07-21 15:16:46', 'admin', '2020-09-23 10:51:55', '');
INSERT INTO `sys_menu` VALUES ('2019', '1', '002001', '总部', '扫描管理', '0', '主类目', '4', 'scan', null, '1', 'M', '0', '0', '', 'icon-yundanfenpei', 'admin', '2020-07-22 15:33:58', 'admin', '2020-08-10 16:24:42', '');
INSERT INTO `sys_menu` VALUES ('2020', '1', '002001', '总部', '收件扫描', '2019', '扫描', '1', 'address', 'scan/addressee/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 15:50:14', 'admin', '2020-07-31 17:33:01', '');
INSERT INTO `sys_menu` VALUES ('2021', '1', '002001', '总部', '发件扫描', '2019', '扫描', '2', 'send', 'scan/send/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:40:26', 'admin', '2020-07-22 17:41:13', '');
INSERT INTO `sys_menu` VALUES ('2022', '1', '002001', '总部', '发件称重扫描', '2019', '扫描', '3', 'SendWeh', 'scan/SendWeh/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:42:14', 'admin', '2020-07-22 17:41:16', '');
INSERT INTO `sys_menu` VALUES ('2023', '1', '002001', '总部', '到件称重扫描', '2019', '扫描', '4', 'ToWeh', 'scan/ToWeh/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:43:06', 'admin', '2020-07-22 17:41:19', '');
INSERT INTO `sys_menu` VALUES ('2024', '1', '002001', '总部', '派件扫描', '2019', '扫描', '5', 'delivery', 'scan/delivery/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:44:34', 'admin', '2020-07-22 17:41:21', '');
INSERT INTO `sys_menu` VALUES ('2025', '1', '002001', '总部', '留仓扫描', '2019', '扫描', '6', 'KeepWarehouse', 'scan/KeepWarehouse/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:45:13', 'admin', '2020-07-22 17:41:25', '');
INSERT INTO `sys_menu` VALUES ('2026', '1', '002001', '总部', '问题件扫描', '2019', '扫描', '7', 'problemSh', 'scan/problemSh/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:45:55', 'admin', '2020-07-22 17:41:29', '');
INSERT INTO `sys_menu` VALUES ('2027', '1', '002001', '总部', '签收单交单扫描', '2019', '扫描', '8', 'SignFor', 'scan/SignFor/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:47:54', 'admin', '2020-07-22 17:41:33', '');
INSERT INTO `sys_menu` VALUES ('2028', '1', '002001', '总部', '扫描记录查询', '2019', '扫描', '11', 'searchRecord', 'scan/searchRecord/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-22 16:50:06', 'admin', '2020-08-18 14:24:06', '');
INSERT INTO `sys_menu` VALUES ('2029', '1', '002001', '总部', '签收管理', '2019', '扫描', '10', 'deliveryReceipt', 'scan/deliveryReceipt/index', '1', 'M', '0', '1', '', null, 'admin', '2020-07-22 16:52:14', 'admin', '2020-08-12 10:51:43', '');
INSERT INTO `sys_menu` VALUES ('2033', '1', '002001', '总部', '运单管理', '0', '主类目', '3', 'wayBill', '', '1', 'M', '0', '0', '', 'icon-quanburenwu', 'admin', '2020-07-23 09:49:57', 'admin', '2020-10-22 11:21:07', '');
INSERT INTO `sys_menu` VALUES ('2034', '1', '002001', '总部', '运单管理', '2033', '运单', '1', 'wayBillManageIndex', 'wayBill/wayBillManage/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 10:53:24', 'admin', '2020-07-23 11:05:29', '');
INSERT INTO `sys_menu` VALUES ('2035', '1', '002001', '总部', '运单审核', '2033', '运单', '2', 'wayBillAuditIndex', 'wayBill/wayBillAudit/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 10:57:03', 'admin', '2020-07-23 11:49:33', '');
INSERT INTO `sys_menu` VALUES ('2037', '1', '002001', '总部', '基础数据', '0', '主类目', '1', 'mdm', ' ', '1', 'M', '0', '0', '', 'icon-dongtai-miaobian', 'admin', '2020-07-23 11:05:23', 'admin', '2020-10-22 11:21:04', '');
INSERT INTO `sys_menu` VALUES ('2038', '1', '002001', '总部', '关键字维护', '2037', '基础数据', '1', 'keywordWorkbench', 'mdm/keyword/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:07:13', 'admin', '2020-09-14 17:34:05', '');
INSERT INTO `sys_menu` VALUES ('2039', '1', '002001', '总部', '产品类型', '2037', '基础数据', '2', 'productTypeWorkbench', 'mdm/productType/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:09:09', 'admin', '2020-09-14 17:34:55', '');
INSERT INTO `sys_menu` VALUES ('2040', '1', '002001', '总部', '员工资料', '2037', '基础数据', '3', 'userInfoWorkbench', 'mdm/userInfo/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:09:50', 'admin', '2020-09-14 17:35:08', '');
INSERT INTO `sys_menu` VALUES ('2041', '1', '002001', '总部', '客户资料', '2037', '基础数据', '4', 'clientInfoWorkbench', 'mdm/clientInfo/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:10:27', 'admin', '2020-09-14 17:35:18', '');
INSERT INTO `sys_menu` VALUES ('2042', '1', '002001', '总部', '主类别维护', '2037', '基础数据', '6', 'mainCategory', 'mdm/lookUp/mainCategory', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:11:17', 'admin', '2020-09-14 17:35:28', '');
INSERT INTO `sys_menu` VALUES ('2044', '1', '002001', '总部', '子类别维护', '2037', '基础数据', '7', 'subtype', 'mdm/lookUp/subtype', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:11:58', 'admin', '2020-10-22 18:00:25', '');
INSERT INTO `sys_menu` VALUES ('2045', '1', '002001', '总部', '客户报价', '2037', '基础数据', '8', 'customerQuote', 'mdm/customerQuote/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-23 11:12:35', 'admin', '2020-08-20 16:06:03', '');
INSERT INTO `sys_menu` VALUES ('2046', '1', '002001', '总部', '日志管理', '2010', '系统管理', '8', 'systemLogManage', 'system/logManage/index', '1', 'M', '0', '0', '', null, 'admin', '2020-07-27 14:55:24', 'admin', '2020-09-23 10:52:13', '');
INSERT INTO `sys_menu` VALUES ('2047', '1', '002001', '总部', '登录日志', '2046', '日志管理', '1', 'systemLogManageLoginLog', 'system/logManage/loginLog', '1', 'C', '0', '0', 'system:loginLog:list', null, 'admin', '2020-07-27 15:02:28', 'admin', '2020-09-23 11:21:12', '');
INSERT INTO `sys_menu` VALUES ('2048', '1', '002001', '总部', '操作日志', '2046', '日志管理', '2', 'systemLogManageOprLog', 'system/logManage/oprLog', '1', 'C', '0', '0', 'system:oprLog:list', null, 'admin', '2020-07-27 15:02:57', 'admin', '2020-09-23 11:21:20', '');
INSERT INTO `sys_menu` VALUES ('2049', '2', '002001', '总部', '系统管理', '0', '主类目', '4', 'system', '', '1', 'M', '1', '1', '', 'w-set', '', '2020-07-20 16:54:42', 'admin', '2020-10-19 10:41:47', '');
INSERT INTO `sys_menu` VALUES ('2051', '2', '002001', '总部', '用户管理', '2049', '系统管理', '1', 'scheduling', null, '1', 'C', '1', '1', '', null, 'admin', '2020-07-27 15:45:56', 'admin', '2020-10-19 10:41:37', '');
INSERT INTO `sys_menu` VALUES ('2052', '1', '002001', '总部', '签收查询', '2029', '签收管理', '0', 'index', 'scan/deliveryReceipt/index', '1', 'C', '0', '0', '', null, 'admin', '2020-07-31 17:26:58', 'admin', '2020-07-31 17:31:14', '');
INSERT INTO `sys_menu` VALUES ('2053', '1', '002001', '总部', '签收新增', '2029', '签收管理', '0', 'add', 'scan/deliveryReceipt/add', '1', 'C', '0', '0', null, null, 'admin', '2020-07-31 17:32:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('2054', '1', '002001', '总部', '菜单详情', '2011', '菜单设置', '0', '', null, '1', 'F', '0', '0', 'system:menu:query', null, 'admin', '2020-08-05 11:06:12', 'admin', '2020-08-24 17:16:13', '');
INSERT INTO `sys_menu` VALUES ('2055', '1', '002001', '总部', '菜单新增', '2011', '菜单设置', '0', '', null, '1', 'F', '0', '0', 'system:menu:add', null, 'admin', '2020-08-05 11:06:40', '', null, '');
INSERT INTO `sys_menu` VALUES ('2056', '1', '002001', '总部', '菜单编辑', '2011', '菜单设置', '0', '', null, '1', 'F', '0', '0', 'system:menu:edit', null, 'admin', '2020-08-05 11:07:17', '', null, '');
INSERT INTO `sys_menu` VALUES ('2057', '1', '002001', '总部', '菜单删除', '2011', '菜单设置', '0', '', null, '1', 'F', '0', '0', 'system:menu:delete', null, 'admin', '2020-08-05 11:07:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('2058', '1', '002001', '总部', '查询详情', '2012', '角色权限', '0', '', null, '1', 'F', '0', '0', 'system:role:query', null, 'admin', '2020-08-05 14:41:11', 'admin', '2020-08-24 17:16:25', '');
INSERT INTO `sys_menu` VALUES ('2059', '1', '002001', '总部', '新增角色', '2012', '角色权限', '0', '', null, '1', 'F', '0', '0', 'system:role:add', null, 'admin', '2020-08-05 14:41:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2060', '1', '002001', '总部', '编辑角色', '2012', '角色权限', '0', '', null, '1', 'F', '0', '0', 'system:role:edit', null, 'admin', '2020-08-05 14:41:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('2061', '1', '002001', '总部', '删除角色', '2012', '角色权限', '0', '', null, '1', 'F', '0', '0', 'system:role:delete', null, 'admin', '2020-08-05 14:41:46', '', null, '');
INSERT INTO `sys_menu` VALUES ('2062', '1', '002001', '总部', '绑定权限', '2012', '角色权限', '0', '', null, '1', 'F', '0', '0', 'system:role:right', null, 'admin', '2020-08-05 14:42:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2063', '1', '002001', '总部', '查询用户详情', '2013', '用户设置', '0', '', null, '1', 'F', '0', '0', 'system:user:query', null, 'admin', '2020-08-05 14:43:18', 'admin', '2020-08-24 17:16:42', '');
INSERT INTO `sys_menu` VALUES ('2064', '1', '002001', '总部', '绑定PC角色', '2013', '用户设置', '0', '', null, '1', 'F', '0', '0', 'system:user:bindPc', null, 'admin', '2020-08-05 14:43:30', '', null, '');
INSERT INTO `sys_menu` VALUES ('2065', '1', '002001', '总部', '绑定APP角色', '2013', '用户设置', '0', '', null, '1', 'F', '0', '0', 'system:user:bindApp', null, 'admin', '2020-08-05 14:43:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('2066', '1', '002001', '总部', '查询网点详情', '2014', '网点资料', '0', '', null, '1', 'F', '0', '0', 'system:site:query', null, 'admin', '2020-08-05 14:43:58', 'admin', '2020-08-24 17:16:49', '');
INSERT INTO `sys_menu` VALUES ('2067', '1', '002001', '总部', '新增网点', '2014', '网点资料', '0', '', null, '1', 'F', '0', '0', 'system:site:add', null, 'admin', '2020-08-05 14:44:11', '', null, '');
INSERT INTO `sys_menu` VALUES ('2068', '1', '002001', '总部', '编辑网点', '2014', '网点资料', '0', '', null, '1', 'F', '0', '0', 'system:site:edit', null, 'admin', '2020-08-05 14:44:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('2069', '1', '002001', '总部', '删除网点', '2014', '网点资料', '0', '', null, '1', 'F', '0', '0', 'system:site:delete', null, 'admin', '2020-08-05 14:44:29', '', null, '');
INSERT INTO `sys_menu` VALUES ('2075', '1', '002001', '总部', '新增角色', '2016', 'APP角色权限', '0', '', null, '1', 'F', '0', '0', 'system:appRole:add', null, 'admin', '2020-08-05 14:46:47', '', null, '');
INSERT INTO `sys_menu` VALUES ('2076', '1', '002001', '总部', '编辑角色', '2016', 'APP角色权限', '0', '', null, '1', 'F', '0', '0', 'system:appRole:edit', null, 'admin', '2020-08-05 14:46:57', '', null, '');
INSERT INTO `sys_menu` VALUES ('2077', '1', '002001', '总部', '删除角色', '2016', 'APP角色权限', '0', '', null, '1', 'F', '0', '0', 'system:appRole:delete', null, 'admin', '2020-08-05 14:47:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2078', '1', '002001', '总部', '绑定权限', '2016', 'APP角色权限', '0', '', null, '1', 'F', '0', '0', 'system:appRole:right', null, 'admin', '2020-08-05 14:47:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('2079', '1', '002001', '总部', '查询日志', '2047', '登录日志', '0', '', null, '1', 'F', '0', '0', 'system:loginLog:query', null, 'admin', '2020-08-05 14:48:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2080', '1', '002001', '总部', '删除日志', '2047', '登录日志', '0', '', null, '1', 'F', '0', '0', 'system:loginLog:delete', null, 'admin', '2020-08-05 14:48:11', '', null, '');
INSERT INTO `sys_menu` VALUES ('2081', '1', '002001', '总部', '清空日志', '2047', '登录日志', '0', '', null, '1', 'F', '0', '0', 'system:loginLog:clean', null, 'admin', '2020-08-05 14:48:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('2082', '1', '002001', '总部', '导出日志', '2047', '登录日志', '0', '', null, '1', 'F', '0', '0', 'system:loginLog:export', null, 'admin', '2020-08-05 14:48:33', '', null, '');
INSERT INTO `sys_menu` VALUES ('2083', '1', '002001', '总部', '查询日志', '2048', '操作日志', '0', '', null, '1', 'F', '0', '0', 'system:oprLog:query', null, 'admin', '2020-08-05 14:49:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('2084', '1', '002001', '总部', '删除日志', '2048', '操作日志', '0', '', null, '1', 'F', '0', '0', 'system:oprLog:delete', null, 'admin', '2020-08-05 14:49:11', '', null, '');
INSERT INTO `sys_menu` VALUES ('2085', '1', '002001', '总部', '清空日志', '2048', '操作日志', '0', '', null, '1', 'F', '0', '0', 'system:oprLog:clean', null, 'admin', '2020-08-05 14:49:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('2086', '1', '002001', '总部', '导出日志', '2048', '操作日志', '0', '', null, '1', 'F', '0', '0', 'system:oprLog:export', null, 'admin', '2020-08-05 14:49:29', '', null, '');
INSERT INTO `sys_menu` VALUES ('2087', '1', '002001', '总部', '查看详情', '2048', '操作日志', '0', '', null, '1', 'F', '0', '0', 'system:oprLog:detail', null, 'admin', '2020-08-05 14:49:39', '', null, '');
INSERT INTO `sys_menu` VALUES ('2088', '3', '002001', '', '编辑用户', '2091', '用户设置', '1', '', null, '1', 'F', '0', '0', 'system:user:edit', null, 'admin', '2020-08-05 16:31:47', '', null, '');
INSERT INTO `sys_menu` VALUES ('2089', '3', '002001', '总部', '菜单设置', '2188', '系统管理', '1', 'systemPcMenu', 'system/pcMenu/index', '1', 'C', '0', '0', 'system:menu:list', null, '', '2020-07-20 16:59:45', 'vipadmin', '2020-09-23 11:08:16', '');
INSERT INTO `sys_menu` VALUES ('2090', '3', '002001', '总部', '角色权限', '2188', '系统管理', '2', 'systemPcRoles', 'system/pcRoles/index', '1', 'C', '0', '0', 'system:role:list', null, '', '2020-07-20 17:00:32', 'vipadmin', '2020-09-23 11:08:23', '');
INSERT INTO `sys_menu` VALUES ('2091', '3', '002001', '总部', '用户设置', '2188', '系统管理', '3', 'systemPcUsers', 'system/pcUsers/index', '1', 'C', '0', '0', 'system:user:list', null, '', '2020-07-20 17:01:17', 'vipadmin', '2020-09-23 11:08:32', '');
INSERT INTO `sys_menu` VALUES ('2092', '3', '002001', '总部', '日志管理', '2188', '系统管理', '5', 'systemLogManage', 'system/logManage/index', '1', 'M', '0', '0', '', null, 'admin', '2020-07-27 14:55:24', 'vipadmin', '2020-09-23 11:08:48', '');
INSERT INTO `sys_menu` VALUES ('2099', '1', '002001', '总部', '订单管理', '0', '主类目', '2', 'scheduling', null, '1', 'M', '0', '0', '', 'icon-yundanguanli1', 'admin', '2020-08-10 16:08:51', 'admin', '2020-10-22 11:21:06', '');
INSERT INTO `sys_menu` VALUES ('2100', '1', '002001', '总部', '订单调度', '2099', '订单管理', '2', 'dispatch', 'scheduling/dispatch/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-10 16:14:10', 'admin', '2020-08-20 16:08:10', '');
INSERT INTO `sys_menu` VALUES ('2101', '1', '002001', '总部', '中心调派网点', '2099', '订单管理', '1', 'centralDeployment', 'scheduling/centralDeployment/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-10 16:14:14', 'admin', '2020-08-20 16:08:01', '');
INSERT INTO `sys_menu` VALUES ('2102', '1', '002001', '总部', '语言设置', '2010', '系统管理', '7', 'systemLang', 'system/lang/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-10 17:41:31', 'admin', '2020-09-23 10:52:04', '');
INSERT INTO `sys_menu` VALUES ('2188', '3', '002001', '总部', '系统管理', '0', '主类目', '6', 'system', '', '1', 'M', '0', '0', '', 'icon-shezhi', '', '2020-07-20 16:54:42', 'vipadmin', '2020-08-13 13:35:00', '');
INSERT INTO `sys_menu` VALUES ('2193', '3', '002001', '', '语言设置', '2188', '系统管理', '4', 'lang', 'system/lang/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-10 17:41:31', 'admin', '2020-08-11 11:11:25', '');
INSERT INTO `sys_menu` VALUES ('2194', '1', '002001', '总部', '客服管理', '0', '主类目', '6', 'customerService', null, '1', 'M', '0', '0', '', 'icon-gerenxinxi', 'admin', '2020-08-12 11:42:51', 'admin', '2020-08-13 22:12:44', '');
INSERT INTO `sys_menu` VALUES ('2195', '1', '002001', '总部', '问题件管理', '2194', '客服管理', '1', 'problemManage', 'customerService/problemManage/index', '1', 'C', '0', '1', '', null, 'admin', '2020-08-12 11:47:56', 'admin', '2020-11-05 16:32:20', '');
INSERT INTO `sys_menu` VALUES ('2196', '1', '002001', '网点', '签收录入', '0', '主类目', '7', 'signInput', null, '1', 'M', '0', '0', '', 'icon-shoucang', 'admin', '2020-08-12 11:49:55', 'admin', '2020-08-12 14:27:11', '');
INSERT INTO `sys_menu` VALUES ('2197', '1', '002001', '网点', '签收管理', '2196', '签收录入', '0', 'signManage', null, '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 11:52:02', 'admin', '2020-08-12 14:32:23', '');
INSERT INTO `sys_menu` VALUES ('2198', '1', '002001', '网点', '代收货款', '0', '主类目', '9', 'cod', null, '1', 'M', '0', '0', '', 'icon-qian-renminbi', 'admin', '2020-08-12 11:54:20', 'admin', '2020-08-12 14:27:19', '');
INSERT INTO `sys_menu` VALUES ('2199', '1', '002001', '网点', '派方收款管理', '2198', '代收货款', '0', 'sendRceivable', 'cod/sendRceivable/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 11:55:40', 'admin', '2020-08-14 10:27:32', '');
INSERT INTO `sys_menu` VALUES ('2200', '1', '002001', '中心', '中心代收管理', '2198', '代收货款', '0', 'centerCollecting', 'cod/centerCollecting/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 11:56:25', 'admin', '2020-08-14 19:59:08', '');
INSERT INTO `sys_menu` VALUES ('2201', '1', '002001', '网点', '货款核销', '2198', '代收货款', '0', 'paymentVerification', 'cod/paymentVerification/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 12:00:20', 'admin', '2020-08-14 19:57:03', '');
INSERT INTO `sys_menu` VALUES ('2203', '1', '002001', '网点', '货款查询', '2198', '代收货款', '0', 'paymentQuery', 'cod/paymentQuery/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 13:56:46', 'admin', '2020-08-14 09:35:44', '');
INSERT INTO `sys_menu` VALUES ('2204', '1', '002001', '网点', '货款监控', '2198', '代收货款', '0', 'paymentMonitor', 'cod/paymentMonitor/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 13:57:08', 'admin', '2020-08-14 10:51:15', '');
INSERT INTO `sys_menu` VALUES ('2205', '1', '002003', '网点', '现金收款', '0', '主类目', '10', 'cashManager', null, '1', 'M', '0', '0', '', 'icon-qian-renminbi', 'admin', '2020-08-12 13:58:14', '2007001', '2020-09-22 10:31:37', '');
INSERT INTO `sys_menu` VALUES ('2206', '1', '002001', '总部', '现金收款管理', '2205', '现金收款管理', '0', 'cashReceipts', 'cashManager/cashReceipts/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 13:58:49', 'admin', '2020-11-02 16:16:09', '');
INSERT INTO `sys_menu` VALUES ('2207', '1', '002001', '总部', '现金收款统计', '2205', '现金收款管理', '0', 'cashStatistics', 'cashManager/cashStatistics/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:00:51', 'admin', '2020-10-30 16:46:26', '');
INSERT INTO `sys_menu` VALUES ('2208', '1', '002001', '网点', '财务管理', '0', '主类目', '12', '财务管理', null, '1', 'M', '0', '0', '', 'icon-qian-renminbi', 'admin', '2020-08-12 14:05:17', 'admin', '2020-08-12 14:30:50', '');
INSERT INTO `sys_menu` VALUES ('2209', '1', '002001', '网点', '派送费结算', '2208', '财务管理', '0', '派送费结算', null, '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:05:46', 'admin', '2020-08-12 14:33:12', '');
INSERT INTO `sys_menu` VALUES ('2210', '1', '002001', '总部', '短信管理', '0', '主类目', '13', 'smsManagement', null, '1', 'M', '0', '0', '', 'icon-xiaoxi', 'admin', '2020-08-12 14:07:32', 'admin', '2020-08-21 18:30:51', '');
INSERT INTO `sys_menu` VALUES ('2211', '1', '002001', '总部', '短信发送', '2210', '短信管理', '0', 'sendMessge', 'smsManagement/sendMessge/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:08:30', 'admin', '2020-09-10 15:15:20', '');
INSERT INTO `sys_menu` VALUES ('2212', '1', '002001', '总部', '短信管理', '2210', '短信管理', '2', 'sms', 'smsManagement/sms/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:08:54', 'admin', '2020-08-21 18:23:13', '');
INSERT INTO `sys_menu` VALUES ('2213', '1', '002001', '总部', '短信统计', '2210', '短信管理', '0', 'smsStatistics', 'smsManagement/smsStatistics/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:09:14', 'admin', '2020-10-29 11:04:26', '');
INSERT INTO `sys_menu` VALUES ('2214', '1', '002001', '网点', '公告管理', '0', '主类目', '14', '公告管理', null, '1', 'M', '0', '0', '', 'icon-gonggao-miaobian', 'admin', '2020-08-12 14:10:37', 'admin', '2020-08-12 14:37:25', '');
INSERT INTO `sys_menu` VALUES ('2215', '1', '002001', '总部', '发表公告', '2214', '公告管理', '0', '发表公告', null, '1', 'C', '0', '0', '', 'icon-gonggao-miaobian', 'admin', '2020-08-12 14:11:24', 'admin', '2020-09-27 13:51:32', '');
INSERT INTO `sys_menu` VALUES ('2216', '1', '002001', '网点', '公告管理', '2214', '公告管理', '0', '公告管理', null, '1', 'C', '0', '0', null, null, 'admin', '2020-08-12 14:11:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2217', '1', '002001', '总部', '转第三方扫描', '2019', '扫描管理', '9', 'cod', 'scan/cod', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:13:52', 'admin', '2020-09-14 10:36:50', '');
INSERT INTO `sys_menu` VALUES ('2218', '1', '002001', '中心', '转第三方', '2033', '运单管理', '3', '转第三方', null, '1', 'C', '0', '1', '', null, 'admin', '2020-08-12 14:15:30', 'JYD001001', '2020-09-25 14:48:49', '');
INSERT INTO `sys_menu` VALUES ('2219', '1', '002001', '网点', '清关费结算', '2208', '财务管理', '2', '清关费结算', null, '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:16:29', 'admin', '2020-08-12 14:33:18', '');
INSERT INTO `sys_menu` VALUES ('2220', '1', '002001', '总部', '清关', '0', '主类目', '5', 'clearanceScan', '', '1', 'M', '0', '0', '', 'icon-yundanfenpei', 'admin', '2020-08-12 14:18:24', 'admin', '2020-10-16 15:32:54', '');
INSERT INTO `sys_menu` VALUES ('2221', '1', '002001', '总部', '装车扫描', '2220', '清关扫描', '0', 'car', 'clearanceScan/car', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:18:58', 'admin', '2020-09-17 22:37:26', '');
INSERT INTO `sys_menu` VALUES ('2222', '1', '002001', '总部', '清关扫描', '2220', '清关扫描', '1', 'clearance', 'clearanceScan/clearance', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:20:11', 'admin', '2020-09-18 09:53:51', '');
INSERT INTO `sys_menu` VALUES ('2223', '1', '002001', '网点', '批量签收', '2220', '清关扫描', '3', '批量签收', null, '1', 'C', '0', '1', '', null, 'admin', '2020-08-12 14:21:10', 'admin', '2020-10-16 13:42:27', '');
INSERT INTO `sys_menu` VALUES ('2224', '1', '002001', '总部', '仓库管理', '0', '主类目', '15', 'warehouse', null, '1', 'M', '0', '0', '', 'icon-moxing-miaobian', 'admin', '2020-08-12 14:28:15', 'admin', '2020-11-05 11:32:37', '');
INSERT INTO `sys_menu` VALUES ('2225', '1', '002001', '总部', '库位维护', '2224', '仓库管理', '0', 'cuneWorkbench', 'warehouse/cune/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 14:28:45', 'admin', '2020-09-18 16:51:27', '');
INSERT INTO `sys_menu` VALUES ('2226', '1', '002001', '总部', '查询菜单详情', '2015', 'APP菜单设置', '1', '', null, '1', 'F', '0', '0', 'system:appMenu:query', null, 'admin', '2020-08-12 17:33:10', 'admin', '2020-08-24 17:17:09', '');
INSERT INTO `sys_menu` VALUES ('2227', '1', '002001', '总部', '菜单新增', '2015', 'APP菜单设置', '2', '', null, '1', 'F', '0', '0', 'system:appMenu:add', null, 'admin', '2020-08-12 17:33:28', 'admin', '2020-08-12 17:34:10', '');
INSERT INTO `sys_menu` VALUES ('2228', '1', '002001', '总部', '菜单编辑', '2015', 'APP菜单设置', '3', '', null, '1', 'F', '0', '0', 'system:appMenu:edit', null, 'admin', '2020-08-12 17:33:43', 'admin', '2020-08-12 17:34:15', '');
INSERT INTO `sys_menu` VALUES ('2229', '1', '002001', '总部', '菜单删除', '2015', 'APP菜单设置', '4', '', null, '1', 'F', '0', '0', 'system:appMenu:delete', null, 'admin', '2020-08-12 17:34:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2230', '3', '002001', '总部', '订单管理', '0', '主类目', '1', 'scheduling', null, '1', 'M', '0', '0', '', 'icon-dongtai-miaobian', 'vipadmin', '2020-08-12 17:39:44', 'vipadmin', '2020-08-13 14:03:26', '');
INSERT INTO `sys_menu` VALUES ('2231', '3', '002001', '总部', '二派订单查询', '2230', '订单管理', '2', 'twoFactions', 'order/twoFactions/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-12 17:40:22', 'JCEX', '2020-10-29 18:55:01', '');
INSERT INTO `sys_menu` VALUES ('2232', '3', '002001', '总部', '异常查看', '0', '主类目', '2', 'problem', null, '1', 'M', '0', '0', '', 'icon-jiagou', 'vipadmin', '2020-08-13 09:55:52', 'vipadmin', '2020-10-27 10:52:44', '');
INSERT INTO `sys_menu` VALUES ('2233', '3', '002001', '总部', '问题件查询', '2232', '异常查看', '0', 'problemQuery', 'problem/problemQuery', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 09:56:21', 'vipadmin', '2020-10-27 10:52:47', '');
INSERT INTO `sys_menu` VALUES ('2234', '3', '002001', '总部', '账单管理', '0', '主类目', '3', 'billManage', null, '1', 'M', '0', '0', '', 'icon-youhuiquan', 'vipadmin', '2020-08-13 09:57:25', 'vipadmin', '2020-08-13 10:03:44', '');
INSERT INTO `sys_menu` VALUES ('2235', '3', '002001', '总部', '月结账单', '2234', '账单管理', '0', 'billManageMonthly', 'reconciliation/monthly/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 09:58:02', 'vipadmin', '2020-09-23 11:09:26', '');
INSERT INTO `sys_menu` VALUES ('2236', '3', '002001', '总部', 'COD账单', '2234', '账单管理', '0', 'billManageCod', 'billManage/cod/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 09:58:17', 'vipadmin', '2020-08-19 21:03:37', '');
INSERT INTO `sys_menu` VALUES ('2237', '3', '002001', '总部', '快件查询', '0', '主类目', '4', 'trace', null, '1', 'M', '0', '0', '', 'icon-xiaohuoche-pika', 'vipadmin', '2020-08-13 10:01:49', 'vipadmin', '2020-08-13 10:03:53', '');
INSERT INTO `sys_menu` VALUES ('2238', '3', '002001', '总部', '快件跟踪查询', '2237', '快件查询', '0', 'traceQuery', 'trace/traceQuery/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 10:02:06', 'vipadmin', '2020-08-20 13:32:57', '');
INSERT INTO `sys_menu` VALUES ('2239', '3', '002001', '总部', '账户管理', '0', '主类目', '5', 'setting', null, '1', 'M', '0', '0', '', 'icon-woderenwu', 'vipadmin', '2020-08-13 10:02:34', 'vipadmin', '2020-08-20 14:21:14', '');
INSERT INTO `sys_menu` VALUES ('2240', '3', '002001', '总部', '员工管理', '2239', '账户管理', '0', 'accountManagementIndex', 'setting/accountManagement/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 10:02:48', 'vipadmin', '2020-08-20 14:21:38', '');
INSERT INTO `sys_menu` VALUES ('2241', '3', '002001', '总部', '订单管理', '2230', '订单管理', '1', 'orderManage', 'order/orderManage/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-13 10:05:04', 'vipadmin', '2020-08-13 14:30:54', '');
INSERT INTO `sys_menu` VALUES ('2242', '3', '002001', '总部', '登录日志', '2092', '日志管理', '1', 'systemLogManageLoginLog', 'system/logManage/loginLog', '1', 'C', '0', '0', '', null, 'admin', '2020-07-27 15:02:28', 'vipadmin', '2020-09-23 11:20:53', '');
INSERT INTO `sys_menu` VALUES ('2243', '3', '002001', '总部', '操作日志', '2092', '日志管理', '2', 'systemLogManageOprLog', 'system/logManage/oprLog', '1', 'C', '0', '0', '', null, 'admin', '2020-07-27 15:02:57', 'vipadmin', '2020-09-23 11:20:30', '');
INSERT INTO `sys_menu` VALUES ('2244', '3', '002001', '', '菜单查询', '2089', '菜单设置', '1', '', null, '1', 'F', '0', '0', 'system:menu:query', null, 'admin', '2020-08-05 11:06:12', 'admin', '2020-08-07 16:20:44', '');
INSERT INTO `sys_menu` VALUES ('2245', '3', '002001', '', '菜单新增', '2089', '菜单设置', '2', '', null, '1', 'F', '0', '0', 'system:menu:add', null, 'admin', '2020-08-05 11:06:40', '', null, '');
INSERT INTO `sys_menu` VALUES ('2246', '3', '002001', '', '菜单编辑', '2089', '菜单设置', '3', '', null, '1', 'F', '0', '0', 'system:menu:edit', null, 'admin', '2020-08-05 11:07:17', '', null, '');
INSERT INTO `sys_menu` VALUES ('2247', '3', '002001', '', '菜单删除', '2089', '菜单设置', '4', '', null, '1', 'F', '0', '0', 'system:menu:delete', null, 'admin', '2020-08-05 11:07:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('2248', '3', '002001', '', '查询角色', '2090', '角色权限', '1', '', null, '1', 'F', '0', '0', 'system:role:query', null, 'admin', '2020-08-05 14:41:11', '', null, '');
INSERT INTO `sys_menu` VALUES ('2249', '3', '002001', '', '新增角色', '2090', '角色权限', '2', '', null, '1', 'F', '0', '0', 'system:role:add', null, 'admin', '2020-08-05 14:41:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2250', '3', '002001', '', '编辑角色', '2090', '角色权限', '3', '', null, '1', 'F', '0', '0', 'system:role:edit', null, 'admin', '2020-08-05 14:41:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('2251', '3', '002001', '', '删除角色', '2090', '角色权限', '4', '', null, '1', 'F', '0', '0', 'system:role:delete', null, 'admin', '2020-08-05 14:41:46', '', null, '');
INSERT INTO `sys_menu` VALUES ('2252', '3', '002001', '', '绑定权限', '2090', '角色权限', '5', '', null, '1', 'F', '0', '0', 'system:role:right', null, 'admin', '2020-08-05 14:42:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2253', '3', '002001', '', '查询用户', '2091', '用户设置', '2', '', null, '1', 'F', '0', '0', 'system:user:query', null, 'admin', '2020-08-05 14:43:18', '', null, '');
INSERT INTO `sys_menu` VALUES ('2254', '3', '002001', '', '绑定PC角色', '2091', '用户设置', '3', '', null, '1', 'F', '0', '0', 'system:user:bindPc', null, 'admin', '2020-08-05 14:43:30', '', null, '');
INSERT INTO `sys_menu` VALUES ('2255', '3', '002001', '', '绑定APP角色', '2091', '用户设置', '4', '', null, '1', 'F', '0', '0', 'system:user:bindApp', null, 'admin', '2020-08-05 14:43:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('2282', '1', '002001', '总部', '快件跟踪', '2194', '客服管理', '2', 'waybillTrajectory', 'customerService/waybillTrajectory/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-17 10:07:10', 'admin', '2020-08-17 19:51:45', '');
INSERT INTO `sys_menu` VALUES ('2283', '1', '002001', '总部', '问题件类型', '2037', '基础数据', '9', 'problem', 'mdm/problem/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-17 16:10:22', 'admin', '2020-08-20 16:07:18', '');
INSERT INTO `sys_menu` VALUES ('2284', '1', '002001', '总部', '信息中心', '0', '主类目', '16', 'message', '', '1', 'M', '0', '0', '', 'icon-xiaoxi', 'admin', '2020-08-18 17:23:42', 'admin', '2020-09-12 19:24:39', '');
INSERT INTO `sys_menu` VALUES ('2285', '1', '002001', '总部', '消息', '2284', '信息', '0', 'message', 'message/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-18 17:24:36', 'admin', '2020-08-18 17:25:44', '');
INSERT INTO `sys_menu` VALUES ('2286', '1', '002001', '总部', '客户员工资料', '2037', '基础数据', '5', 'clientEmpWorkbench', 'mdm/clientEmp/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-20 14:03:55', 'admin', '2020-08-20 16:07:07', '');
INSERT INTO `sys_menu` VALUES ('2288', '3', '002003', '网点', '保费查询', '2234', '账单管理', '0', 'premium', 'billManage/premium/index', '1', 'C', '0', '0', '', null, 'vipadmin', '2020-08-21 13:38:02', 'vipadmin', '2020-09-27 10:56:46', '');
INSERT INTO `sys_menu` VALUES ('2289', '1', '002001', '总部', '短信模板', '2210', '短信管理', '4', 'smsTep', 'smsManagement/smsTep/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-09-01 10:03:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('2290', '1', '002001', '总部', '多语言查漏', '2010', '系统管理', '9', 'systemLackLang', 'system/lackLang/index', '1', 'C', '0', '0', '', '', 'admin', '2020-09-12 10:53:53', 'admin', '2020-09-23 10:52:22', '');
INSERT INTO `sys_menu` VALUES ('2291', '3', '002001', '总部', '多语言查漏', '2188', '系统管理', '6', 'systemLackLang', 'system/lackLang/index', '1', 'C', '0', '0', '', '#', 'vipadmin', '2020-09-15 14:50:43', 'vipadmin', '2020-09-23 11:08:57', '');
INSERT INTO `sys_menu` VALUES ('2292', '1', '002001', '总部', '库内查询', '2224', '仓库管理', '1', 'locationWorkbench', 'warehouse/location/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-16 15:04:45', 'admin', '2020-09-18 16:51:40', '');
INSERT INTO `sys_menu` VALUES ('2293', '1', '002003', '网点', '退件扫描', '2019', '扫描管理', '2', 'Return', 'clearanceScan/Return', '1', 'M', '0', '0', '', '#', 'admin', '2020-09-18 09:54:50', 'admin', '2020-10-13 20:06:17', '');
INSERT INTO `sys_menu` VALUES ('2294', '1', '002001', '总部', '对账管理', '0', '主类目', '11', 'reconciliation', null, '1', 'M', '0', '0', '', 'icon-qian-renminbi', 'admin', '2020-09-18 10:01:51', 'admin', '2020-09-18 10:03:21', '');
INSERT INTO `sys_menu` VALUES ('2295', '1', '002001', '总部', '月结账单生成', '2294', '对账管理', '1', 'reconciliationMonthlyBill', 'reconciliation/monthlyBill/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-18 10:02:35', 'admin', '2020-09-23 10:58:10', '');
INSERT INTO `sys_menu` VALUES ('2296', '1', '002001', '总部', '月结账单管理', '2294', '对账管理', '2', 'reconciliationMonthlyBillManagement', 'reconciliation/monthlyBillManagement/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-18 10:03:03', 'admin', '2020-09-23 10:58:22', '');
INSERT INTO `sys_menu` VALUES ('2297', '1', '002001', '总部', '问题件', '0', '主类目', '8', 'problem', null, '1', 'M', '0', '0', '', 'icon-xiaoxi', 'admin', '2020-09-18 10:19:42', 'admin', '2020-09-18 11:20:35', '');
INSERT INTO `sys_menu` VALUES ('2298', '1', '002001', '总部', '留仓件管理', '2194', '客服管理', '0', 'RetManagement', 'problem/RetManagement/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-18 10:20:56', 'LYD00105', '2020-10-29 11:16:43', '');
INSERT INTO `sys_menu` VALUES ('2299', '1', '002001', '总部', '车辆管理', '2037', '基础数据', '10', 'vehicle', 'mdm/vehicle/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-09-18 10:49:57', '', null, '');
INSERT INTO `sys_menu` VALUES ('2300', '1', '002001', '总部', '扫描清单报表', '2019', '扫描管理', '12', 'reportForm', 'scan/reportForm/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-22 09:37:08', 'LYD00105', '2020-10-28 11:54:29', '');
INSERT INTO `sys_menu` VALUES ('2301', '1', '002001', '总部', '目的地资料', '2037', '基础数据', '11', 'destinationInfoWorkbench', 'mdm/destinationInfo/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-09-22 10:04:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2302', '1', '002001', '总部', '月结收款管理', '2294', '对账管理', '3', 'reconciliationMonthlyReceivePaymentManagement', 'reconciliation/monthlyReceivePaymentManagement/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-09-22 14:41:22', 'admin', '2020-09-23 10:58:33', '');
INSERT INTO `sys_menu` VALUES ('2304', '1', '002001', '总部', '清关', '2220', '清关', '0', 'customsClearance', 'clearanceScan/customsClearance', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-16 15:55:37', '', null, '');
INSERT INTO `sys_menu` VALUES ('2305', '2', '002001', '总部', '签收', '2037', '基础数据', '0', 'ui.scan_piece.sign.SignScanPieceActivity', null, '1', 'C', '0', '0', null, 'icon-dongtai-miaobian', 'admin', '2020-10-17 17:31:07', '', null, '');
INSERT INTO `sys_menu` VALUES ('2308', '2', '002001', '总部', '订单操作', '0', '主类目', '0', '订单操作', null, '1', 'M', '0', '0', '', '#', 'admin', '2020-10-17 17:45:12', 'admin', '2020-10-19 10:24:29', '');
INSERT INTO `sys_menu` VALUES ('2309', '2', '002001', '总部', '签收', '2308', '订单操作', '0', 'ui.SignScanPieceActivity', 'ui.SignScanPieceActivity', '1', 'C', '0', '0', '', 'http://183.3.221.229:22261/bas/bas/file/user/20201103/3082079443410871400.png', 'admin', '2020-10-17 17:45:47', 'admin', '2020-11-04 09:58:09', '');
INSERT INTO `sys_menu` VALUES ('2310', '2', '002001', '总部', '问题件', '2308', '订单操作', '0', 'ui.IssueScanPieceActivity', null, '1', 'C', '1', '0', '', 'icon-yundanguanli', 'admin', '2020-10-17 17:46:52', 'admin', '2020-11-04 11:30:53', '');
INSERT INTO `sys_menu` VALUES ('2311', '2', '002001', '总部', '收件', '2308', '订单操作', '0', 'ui.ReceiptPieceActivity', null, '1', 'C', '0', '0', '', 'icon-dahuoche--', 'admin', '2020-10-17 17:54:58', 'admin', '2020-11-04 11:33:02', '');
INSERT INTO `sys_menu` VALUES ('2312', '2', '002001', '总部', '订单管理', '0', '主类目', '0', '订单管理', null, '1', 'M', '0', '0', null, '#', 'admin', '2020-10-19 10:28:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2313', '2', '002001', '总部', '待取件', '2312', '订单管理', '0', 'ui.StayFetchPieceActivity', 'ui.StayFetchPieceActivity', '1', 'C', '0', '0', '', 'icon-gerenxinxi', 'admin', '2020-10-19 10:29:31', 'admin', '2020-11-04 09:50:31', '');
INSERT INTO `sys_menu` VALUES ('2314', '2', '002001', '总部', '待派件', '2313', '待取件', '0', '22', null, '1', 'C', '0', '0', null, 'icon-huoche', 'admin', '2020-10-19 10:30:04', '', null, '');
INSERT INTO `sys_menu` VALUES ('2315', '2', '002001', '总部', '待派件', '2312', '订单管理', '0', 'ui.StayDeliveryPieceActivity', 'ui.StayDeliveryPieceActivity', '1', 'C', '0', '0', '', 'icon-xiaohuoche-pika', 'admin', '2020-10-19 10:30:33', 'admin', '2020-11-04 09:50:40', '');
INSERT INTO `sys_menu` VALUES ('2316', '1', '4', '', '国家城市维护', '2037', '基础数据', '12', 'countryCityDistrict', 'mdm/provinceCityDistrict/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-19 10:30:37', '', null, '');
INSERT INTO `sys_menu` VALUES ('2317', '2', '002001', '总部', '已派件', '2312', '订单管理', '0', 'ui.AlreadyDeliveryPieceActivity', 'ui.AlreadyDeliveryPieceActivity', '1', 'C', '0', '0', '', 'icon-huoche', 'admin', '2020-10-19 10:30:52', 'admin', '2020-11-04 09:50:50', '');
INSERT INTO `sys_menu` VALUES ('2318', '2', '002001', '总部', '发件', '2308', '订单操作', '0', 'ui.SendPieceActivity', null, '1', 'C', '0', '0', '', 'icon-dahuoche--', 'admin', '2020-10-19 10:35:23', 'admin', '2020-11-04 15:18:22', '');
INSERT INTO `sys_menu` VALUES ('2319', '2', '002001', '总部', '到件', '2308', '订单操作', '0', 'ui.ArrivePieceActivity', null, '1', 'C', '0', '0', '', 'icon-yundanfahuotongji', 'admin', '2020-10-19 10:35:45', 'admin', '2020-11-04 09:49:52', '');
INSERT INTO `sys_menu` VALUES ('2320', '2', '002001', '总部', '派件', '2308', '订单操作', '0', 'ui.DeliveryPieceActivity', null, '1', 'C', '0', '0', '', 'icon-dahuoche--', 'admin', '2020-10-19 10:36:10', 'admin', '2020-11-04 11:27:02', '');
INSERT INTO `sys_menu` VALUES ('2321', '2', '002001', '总部', '留仓件', '2308', '订单操作', '0', 'ui.StayWarehousePieceActivity', null, '1', 'C', '0', '0', '', 'icon-moxing-miaobian', 'admin', '2020-10-19 10:36:38', 'admin', '2020-11-04 11:32:54', '');
INSERT INTO `sys_menu` VALUES ('2322', '2', '002001', '总部', '录单', '2308', '订单操作', '0', 'ui.RecordingOrderActivity', null, '1', 'C', '0', '0', '', 'icon-quanburenwu', 'admin', '2020-10-19 10:36:58', 'admin', '2020-11-04 09:50:19', '');
INSERT INTO `sys_menu` VALUES ('2323', '2', '002001', '总部', '操作', '0', '主类目', '0', '999', null, '1', 'M', '0', '0', null, 'icon-dingdan-dingdantiaodu', 'admin', '2020-10-19 10:37:27', '', null, '');
INSERT INTO `sys_menu` VALUES ('2324', '2', '002001', '总部', '上传', '2323', '操作', '0', 'ui.ScanPieceUploadActivity', '上传', '1', 'C', '0', '0', '', 'icon-ic_image_upload', 'admin', '2020-10-19 10:38:17', 'admin', '2020-11-04 09:51:00', '');
INSERT INTO `sys_menu` VALUES ('2325', '2', '002001', '总部', '快件跟踪', '2323', '操作', '0', 'ui.ExpressMailTrackingActivity', 'ui.ExpressMailTrackingActivity', '1', 'C', '0', '0', '', 'icon-xiaohuoche-pika', 'admin', '2020-10-19 10:40:03', 'admin', '2020-11-04 09:51:10', '');
INSERT INTO `sys_menu` VALUES ('2326', '2', '002001', '总部', '扫描查询', '2323', '操作', '0', 'ui.ScanQueryActivity', 'ui.ScanQueryActivity', '1', 'C', '0', '0', '', 'icon-jiagou', 'admin', '2020-10-19 10:40:29', 'admin', '2020-11-04 09:51:20', '');
INSERT INTO `sys_menu` VALUES ('2327', '2', '002001', '总部', '联系人', '2323', '操作', '0', 'system_linkman', 'system_linkman', '1', 'C', '0', '0', '', 'icon-woderenwu', 'admin', '2020-10-19 10:40:44', 'admin', '2020-11-04 09:59:33', '');
INSERT INTO `sys_menu` VALUES ('2328', '1', '002001', '总部', '菜单新增', '2040', '员工资料', '0', '', null, '1', 'F', '0', '0', 'bas:basemployees:add', '#', 'admin', '2020-10-22 18:56:56', '', null, '');
INSERT INTO `sys_menu` VALUES ('2329', '1', '002001', '总部', '自动化总控平台', '0', '主类目', '18', 'autoControl', null, '1', 'M', '0', '0', '', 'icon-gengduo-', 'admin', '2020-10-23 10:33:50', 'admin', '2020-10-23 10:34:39', '');
INSERT INTO `sys_menu` VALUES ('2333', '1', '002001', '总部', '设备管理', '2329', '自动化总控平台', '2', 'equipmentMent', 'autoControl/equipmentMent/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-10-23 11:49:23', 'admin', '2020-10-23 14:17:55', '');
INSERT INTO `sys_menu` VALUES ('2334', '1', '002001', '总部', '基础信息', '2329', '自动化总控平台', '5', 'mdm', 'autoControl/mdm/index', '1', 'M', '0', '0', '', '#', 'admin', '2020-10-23 13:36:08', 'admin', '2020-10-23 13:54:29', '');
INSERT INTO `sys_menu` VALUES ('2335', '1', '002001', '总部', '告警类型', '2334', '基础信息', '2', 'alarmType', 'autoControl/mdm/alarmType/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-10-23 13:47:49', 'admin', '2020-10-23 13:51:01', '');
INSERT INTO `sys_menu` VALUES ('2337', '1', '002001', '总部', '设备服务商', '2334', '基础信息', '3', 'equipment', 'autoControl/mdm/equipment/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-10-23 13:48:57', 'admin', '2020-10-23 13:50:54', '');
INSERT INTO `sys_menu` VALUES ('2338', '1', '002001', '总部', '设备类型', '2334', '基础信息', '1', 'equipmentType', 'autoControl/mdm/equipmentType/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-23 14:15:07', '', null, '');
INSERT INTO `sys_menu` VALUES ('2339', '1', '002001', '总部', '预警中心', '2329', '自动化总控平台', '4', 'warningHealth', 'autoControl/warningHealth/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-23 14:18:27', '', null, '');
INSERT INTO `sys_menu` VALUES ('2340', '1', '002001', '总部', '对接管理', '2329', '自动化总控平台', '6', 'docking', 'autoControl/docking/index', '1', 'M', '0', '0', null, '#', 'admin', '2020-10-23 14:37:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('2341', '1', '002001', '总部', '接口信息', '2340', '对接管理', '1', 'interfaceInfo', 'autoControl/docking/interfaceInfo/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-23 14:38:32', '', null, '');
INSERT INTO `sys_menu` VALUES ('2342', '1', '002001', '总部', '接口数据监控', '2340', '对接管理', '2', 'interfaceMonitoring', 'autoControl/docking/interfaceMonitoring/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-23 15:58:29', '', null, '');
INSERT INTO `sys_menu` VALUES ('2343', '1', '002001', '总部', '实时监控', '2329', '自动化总控平台', '1', 'monitor', 'autoControl/monitor/index', '1', 'C', '0', '0', null, '#', 'admin', '2020-10-26 10:32:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2344', '1', '002001', '总部', '现金收款登记', '2205', '现金收款', '0', 'cashRegistration', 'cashManager/cashRegistration/index', '1', 'C', '0', '0', '', '#', 'admin', '2020-10-31 10:50:14', 'admin', '2020-11-02 16:16:15', '');
INSERT INTO `sys_menu` VALUES ('2345', '1', '002001', '网点', '问题件管理', '2297', '问题件', '1', 'problemManage', 'problem/problemManage/index', '1', 'C', '0', '0', '', null, 'admin', '2020-08-12 13:58:49', 'admin', '2020-08-24 17:13:20', '');
INSERT INTO `sys_menu` VALUES ('2346', '1', '002001', '总部', '问题件处理', '2297', '问题件', '2', 'ProblemHandling', 'problem/ProblemHandling', '1', 'C', '0', '0', '', null, 'admin', '2020-08-24 16:08:06', 'admin', '2020-08-24 16:14:09', '');
INSERT INTO `sys_menu` VALUES ('2347', '1', '002001', '总部', '滞留件管理', '2297', '问题件', '3', 'RetManagement', 'problem/RetManagement', '1', 'C', '0', '0', null, '#', 'admin', '2020-08-25 15:02:28', '', null, '');
INSERT INTO `sys_menu` VALUES ('2348', '1', '002001', '总部', '再派件管理', '2297', '问题件', '4', 'dispatchRecord', 'problem/dispatchRecord', '1', 'C', '0', '1', '', '#', 'admin', '2020-08-27 09:54:24', 'admin', '2020-11-03 14:38:57', '');
INSERT INTO `sys_menu` VALUES ('2349', '1', '002001', '总部', '中心问题件审核', '2297', '问题件', '5', 'problemExamine', 'problem/problemExamine', '1', 'C', '0', '0', null, '#', 'admin', '2020-08-27 17:14:57', '', null, '');
INSERT INTO `sys_menu` VALUES ('2350', '1', '002001', '总部', '大客户问题件处理', '2297', '问题件', '6', 'problemBigCustomers', 'problem/problemBigCustomers', '1', 'C', '0', '1', '', '#', 'admin', '2020-08-28 16:02:18', 'admin', '2020-11-03 14:38:57', '');
INSERT INTO `sys_menu` VALUES ('2351', '1', '002001', '总部', '我登记的回复', '2345', '问题件管理', '1', 'reply', '/problem/problemManage/reply', '1', 'C', '0', '0', '', '#', 'admin', '2020-10-23 11:07:31', 'admin', '2020-10-23 11:18:54', '');
INSERT INTO `sys_menu` VALUES ('2352', '2', '002001', '总部', 'ssss', '0', '主类目', '0', 'ddsd', null, '1', 'C', '0', '0', null, 'http://183.3.221.229:22261/bas/bas/file/user/20201103/3078719856663241385.jpg', 'admin', '2020-11-04 09:52:38', '', null, '');
INSERT INTO `sys_menu` VALUES ('2354', '1', '002001', '总部', '问题件回复处理', '2297', '问题件', '7', 'ProblemHandlingReply', 'problem/ProblemHandling/component/reply', '1', 'M', '1', '0', '', '#', 'admin', '2020-11-04 11:25:58', 'admin', '2020-11-05 09:32:45', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` varchar(2000) DEFAULT NULL COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1', '温馨提醒：2018-07-01 敏思达新版本发布啦', '2', '新版本内容', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '管理员');
INSERT INTO `sys_notice` VALUES ('2', '维护通知：2018-07-01 敏思达系统凌晨维护', '1', '维护内容', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '管理员');

-- ----------------------------
-- Table structure for sys_oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_client_details`;
CREATE TABLE `sys_oauth_client_details` (
  `client_id` varchar(255) NOT NULL COMMENT '客户端唯一标识',
  `resource_ids` varchar(255) DEFAULT NULL COMMENT '资源ID标识',
  `client_secret` varchar(255) NOT NULL COMMENT '客户端安全码',
  `scope` varchar(255) NOT NULL COMMENT '客户端授权范围',
  `authorized_grant_types` varchar(255) NOT NULL COMMENT '客户端授权类型',
  `web_server_redirect_uri` varchar(255) DEFAULT NULL COMMENT '服务器回调地址',
  `authorities` varchar(255) DEFAULT NULL COMMENT '访问资源所需权限',
  `access_token_validity` int(11) DEFAULT NULL COMMENT '设定客户端的access_token的有效时间值（秒）',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT '设定客户端的refresh_token的有效时间值（秒）',
  `additional_information` varchar(4096) DEFAULT NULL COMMENT '预留字段',
  `autoapprove` tinyint(4) DEFAULT NULL COMMENT '是否登录时跳过授权（默认false）',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户端配置表';

-- ----------------------------
-- Records of sys_oauth_client_details
-- ----------------------------
INSERT INTO `sys_oauth_client_details` VALUES ('app', null, '$2a$10$y2hKeELx.z3Sbz.kjQ4wmuiIsv5ZSbUQ1ov4BwFH6ccirP8Knp1uq', 'server', 'password,refresh_token', null, null, '36000000', '7200', null, null);
INSERT INTO `sys_oauth_client_details` VALUES ('szmsd', '', '$2a$10$y2hKeELx.z3Sbz.kjQ4wmuiIsv5ZSbUQ1ov4BwFH6ccirP8Knp1uq', 'server', 'password,client_credentials,refresh_token', '', null, '36000000', '7200', null, null);
INSERT INTO `sys_oauth_client_details` VALUES ('web', '', '$2a$10$y2hKeELx.z3Sbz.kjQ4wmuiIsv5ZSbUQ1ov4BwFH6ccirP8Knp1uq', 'server', 'password,refresh_token', '', null, '36000000', '7200', null, null);

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(50) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`oper_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31860 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('1', 'ceo', '董事长', '1', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_post` VALUES ('2', 'se', '项目经理', '2', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_post` VALUES ('3', 'hr', '人力资源', '3', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');
INSERT INTO `sys_post` VALUES ('4', 'user', '普通员工', '4', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `type` int(1) NOT NULL DEFAULT '1' COMMENT '角色类型：1-PC，2-APP，3-VIP',
  `site_rank_code` varchar(20) DEFAULT NULL COMMENT '网点级别：数据字典维护-code',
  `site_rank_name` varchar(200) DEFAULT NULL COMMENT '网点机构级别：总部，中心，网点，代理',
  `role_key` varchar(100) NOT NULL DEFAULT 'common' COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8 COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '管理员', '1', '1020', '总部', 'admin', '1', '1', '0', '0', 'admin', '2018-03-16 11:33:00', 'ry', '2018-03-16 11:33:00', '管理员');
INSERT INTO `sys_role` VALUES ('2', '普通角色', '1', '1022', '网点', 'common', '2', '2', '0', '0', 'admin', '2018-03-16 11:33:00', 'admin', '2020-10-16 17:16:51', '普通角色');
INSERT INTO `sys_role` VALUES ('105', 'ssss', '1', '1021', '中心', 'common', '3', '1', '1', '2', '', '2020-07-18 11:10:36', 'admin', '2020-07-23 13:50:00', null);
INSERT INTO `sys_role` VALUES ('106', '222', '1', '1021', '中心', 'common', '5', '1', '0', '2', 'admin', '2020-07-23 13:55:24', '', null, null);
INSERT INTO `sys_role` VALUES ('107', '3344', '1', '1021', '中心', 'common', '6', '1', '0', '2', 'admin', '2020-07-23 13:56:45', '', null, null);
INSERT INTO `sys_role` VALUES ('108', '5566', '1', '1021', '中心', 'common', '9', '1', '0', '2', 'admin', '2020-07-23 14:12:18', '', null, null);
INSERT INTO `sys_role` VALUES ('109', '操作员', '2', '002003', '网点', 'common', '1', '1', '0', '0', 'admin', '2020-07-27 15:39:54', 'admin', '2020-09-27 14:10:48', null);
INSERT INTO `sys_role` VALUES ('110', 'aaa', '1', '1020', '总部', 'common', '4', '1', '0', '2', 'admin', '2020-08-01 14:40:32', '', null, null);
INSERT INTO `sys_role` VALUES ('111', '操作工人', '1', '002001', '总部', 'common', '5', '1', '0', '0', 'admin', '2020-08-06 09:52:31', 'admin', '2020-09-27 14:09:10', null);
INSERT INTO `sys_role` VALUES ('112', '扫描员', '1', '002002', '中心', 'common', '4', '1', '0', '0', 'admin', '2020-08-10 09:57:03', 'admin', '2020-09-27 13:58:24', null);
INSERT INTO `sys_role` VALUES ('113', '4445', '1', '1022', '网点', 'common', '1', '1', '0', '2', 'admin', '2020-08-10 19:39:01', 'admin', '2020-08-10 19:39:08', null);
INSERT INTO `sys_role` VALUES ('114', '初始化角色', '3', '1020', '总部', 'common', '1', '1', '0', '0', 'vipadmin', '2020-08-19 10:28:07', '', null, null);
INSERT INTO `sys_role` VALUES ('115', '财务人员', '1', '1020', '总部', 'common', '1', '1', '0', '0', 'admin', '2020-08-19 11:16:17', 'admin', '2020-10-27 11:04:31', null);
INSERT INTO `sys_role` VALUES ('116', '大客户', '1', '002001', '总部', 'common', '3', '1', '0', '0', 'admin', '2020-09-10 16:53:51', 'admin', '2020-09-27 13:53:44', null);
INSERT INTO `sys_role` VALUES ('117', '网点管理员', '1', '002003', '网点', 'common', '1', '1', '0', '0', 'admin', '2020-09-11 14:21:14', 'admin', '2020-10-16 17:16:23', null);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_code` varchar(20) NOT NULL COMMENT '部门ID',
  `post_code` varchar(255) NOT NULL COMMENT '岗位编号',
  `site_rank` varchar(20) DEFAULT '' COMMENT '网点级别：数据字典维护',
  PRIMARY KEY (`role_id`,`dept_code`,`post_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES ('2', '100', '22', '44');
INSERT INTO `sys_role_dept` VALUES ('2', '101', '22', '44');
INSERT INTO `sys_role_dept` VALUES ('2', '105', '22', '44');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '2010');
INSERT INTO `sys_role_menu` VALUES ('1', '2011');
INSERT INTO `sys_role_menu` VALUES ('1', '2012');
INSERT INTO `sys_role_menu` VALUES ('1', '2013');
INSERT INTO `sys_role_menu` VALUES ('1', '2014');
INSERT INTO `sys_role_menu` VALUES ('1', '2015');
INSERT INTO `sys_role_menu` VALUES ('1', '2016');
INSERT INTO `sys_role_menu` VALUES ('1', '2019');
INSERT INTO `sys_role_menu` VALUES ('1', '2020');
INSERT INTO `sys_role_menu` VALUES ('1', '2021');
INSERT INTO `sys_role_menu` VALUES ('1', '2022');
INSERT INTO `sys_role_menu` VALUES ('1', '2023');
INSERT INTO `sys_role_menu` VALUES ('1', '2024');
INSERT INTO `sys_role_menu` VALUES ('1', '2025');
INSERT INTO `sys_role_menu` VALUES ('1', '2026');
INSERT INTO `sys_role_menu` VALUES ('1', '2027');
INSERT INTO `sys_role_menu` VALUES ('1', '2028');
INSERT INTO `sys_role_menu` VALUES ('1', '2029');
INSERT INTO `sys_role_menu` VALUES ('1', '2033');
INSERT INTO `sys_role_menu` VALUES ('1', '2034');
INSERT INTO `sys_role_menu` VALUES ('1', '2035');
INSERT INTO `sys_role_menu` VALUES ('1', '2037');
INSERT INTO `sys_role_menu` VALUES ('1', '2038');
INSERT INTO `sys_role_menu` VALUES ('1', '2039');
INSERT INTO `sys_role_menu` VALUES ('1', '2040');
INSERT INTO `sys_role_menu` VALUES ('1', '2041');
INSERT INTO `sys_role_menu` VALUES ('1', '2042');
INSERT INTO `sys_role_menu` VALUES ('1', '2044');
INSERT INTO `sys_role_menu` VALUES ('1', '2045');
INSERT INTO `sys_role_menu` VALUES ('1', '2046');
INSERT INTO `sys_role_menu` VALUES ('1', '2047');
INSERT INTO `sys_role_menu` VALUES ('1', '2048');
INSERT INTO `sys_role_menu` VALUES ('1', '2052');
INSERT INTO `sys_role_menu` VALUES ('1', '2053');
INSERT INTO `sys_role_menu` VALUES ('1', '2054');
INSERT INTO `sys_role_menu` VALUES ('1', '2055');
INSERT INTO `sys_role_menu` VALUES ('1', '2056');
INSERT INTO `sys_role_menu` VALUES ('1', '2057');
INSERT INTO `sys_role_menu` VALUES ('1', '2058');
INSERT INTO `sys_role_menu` VALUES ('1', '2059');
INSERT INTO `sys_role_menu` VALUES ('1', '2060');
INSERT INTO `sys_role_menu` VALUES ('1', '2061');
INSERT INTO `sys_role_menu` VALUES ('1', '2062');
INSERT INTO `sys_role_menu` VALUES ('1', '2063');
INSERT INTO `sys_role_menu` VALUES ('1', '2064');
INSERT INTO `sys_role_menu` VALUES ('1', '2065');
INSERT INTO `sys_role_menu` VALUES ('1', '2066');
INSERT INTO `sys_role_menu` VALUES ('1', '2067');
INSERT INTO `sys_role_menu` VALUES ('1', '2068');
INSERT INTO `sys_role_menu` VALUES ('1', '2069');
INSERT INTO `sys_role_menu` VALUES ('1', '2075');
INSERT INTO `sys_role_menu` VALUES ('1', '2076');
INSERT INTO `sys_role_menu` VALUES ('1', '2077');
INSERT INTO `sys_role_menu` VALUES ('1', '2078');
INSERT INTO `sys_role_menu` VALUES ('1', '2079');
INSERT INTO `sys_role_menu` VALUES ('1', '2080');
INSERT INTO `sys_role_menu` VALUES ('1', '2081');
INSERT INTO `sys_role_menu` VALUES ('1', '2082');
INSERT INTO `sys_role_menu` VALUES ('1', '2083');
INSERT INTO `sys_role_menu` VALUES ('1', '2084');
INSERT INTO `sys_role_menu` VALUES ('1', '2085');
INSERT INTO `sys_role_menu` VALUES ('1', '2086');
INSERT INTO `sys_role_menu` VALUES ('1', '2087');
INSERT INTO `sys_role_menu` VALUES ('1', '2099');
INSERT INTO `sys_role_menu` VALUES ('1', '2100');
INSERT INTO `sys_role_menu` VALUES ('1', '2101');
INSERT INTO `sys_role_menu` VALUES ('1', '2102');
INSERT INTO `sys_role_menu` VALUES ('1', '2194');
INSERT INTO `sys_role_menu` VALUES ('1', '2195');
INSERT INTO `sys_role_menu` VALUES ('1', '2196');
INSERT INTO `sys_role_menu` VALUES ('1', '2197');
INSERT INTO `sys_role_menu` VALUES ('1', '2198');
INSERT INTO `sys_role_menu` VALUES ('1', '2199');
INSERT INTO `sys_role_menu` VALUES ('1', '2200');
INSERT INTO `sys_role_menu` VALUES ('1', '2201');
INSERT INTO `sys_role_menu` VALUES ('1', '2203');
INSERT INTO `sys_role_menu` VALUES ('1', '2204');
INSERT INTO `sys_role_menu` VALUES ('1', '2205');
INSERT INTO `sys_role_menu` VALUES ('1', '2206');
INSERT INTO `sys_role_menu` VALUES ('1', '2207');
INSERT INTO `sys_role_menu` VALUES ('1', '2208');
INSERT INTO `sys_role_menu` VALUES ('1', '2209');
INSERT INTO `sys_role_menu` VALUES ('1', '2210');
INSERT INTO `sys_role_menu` VALUES ('1', '2211');
INSERT INTO `sys_role_menu` VALUES ('1', '2212');
INSERT INTO `sys_role_menu` VALUES ('1', '2213');
INSERT INTO `sys_role_menu` VALUES ('1', '2214');
INSERT INTO `sys_role_menu` VALUES ('1', '2215');
INSERT INTO `sys_role_menu` VALUES ('1', '2216');
INSERT INTO `sys_role_menu` VALUES ('1', '2217');
INSERT INTO `sys_role_menu` VALUES ('1', '2218');
INSERT INTO `sys_role_menu` VALUES ('1', '2219');
INSERT INTO `sys_role_menu` VALUES ('1', '2220');
INSERT INTO `sys_role_menu` VALUES ('1', '2221');
INSERT INTO `sys_role_menu` VALUES ('1', '2222');
INSERT INTO `sys_role_menu` VALUES ('1', '2223');
INSERT INTO `sys_role_menu` VALUES ('1', '2224');
INSERT INTO `sys_role_menu` VALUES ('1', '2225');
INSERT INTO `sys_role_menu` VALUES ('1', '2226');
INSERT INTO `sys_role_menu` VALUES ('1', '2227');
INSERT INTO `sys_role_menu` VALUES ('1', '2228');
INSERT INTO `sys_role_menu` VALUES ('1', '2229');
INSERT INTO `sys_role_menu` VALUES ('1', '2282');
INSERT INTO `sys_role_menu` VALUES ('1', '2283');
INSERT INTO `sys_role_menu` VALUES ('1', '2284');
INSERT INTO `sys_role_menu` VALUES ('1', '2285');
INSERT INTO `sys_role_menu` VALUES ('1', '2286');
INSERT INTO `sys_role_menu` VALUES ('1', '2289');
INSERT INTO `sys_role_menu` VALUES ('1', '2290');
INSERT INTO `sys_role_menu` VALUES ('1', '2328');
INSERT INTO `sys_role_menu` VALUES ('1', '2329');
INSERT INTO `sys_role_menu` VALUES ('1', '2331');
INSERT INTO `sys_role_menu` VALUES ('1', '2332');
INSERT INTO `sys_role_menu` VALUES ('1', '2333');
INSERT INTO `sys_role_menu` VALUES ('2', '2010');
INSERT INTO `sys_role_menu` VALUES ('2', '2011');
INSERT INTO `sys_role_menu` VALUES ('2', '2013');
INSERT INTO `sys_role_menu` VALUES ('2', '2014');
INSERT INTO `sys_role_menu` VALUES ('2', '2037');
INSERT INTO `sys_role_menu` VALUES ('2', '2038');
INSERT INTO `sys_role_menu` VALUES ('2', '2039');
INSERT INTO `sys_role_menu` VALUES ('2', '2040');
INSERT INTO `sys_role_menu` VALUES ('2', '2041');
INSERT INTO `sys_role_menu` VALUES ('2', '2042');
INSERT INTO `sys_role_menu` VALUES ('2', '2044');
INSERT INTO `sys_role_menu` VALUES ('2', '2045');
INSERT INTO `sys_role_menu` VALUES ('2', '2046');
INSERT INTO `sys_role_menu` VALUES ('2', '2047');
INSERT INTO `sys_role_menu` VALUES ('2', '2048');
INSERT INTO `sys_role_menu` VALUES ('2', '2054');
INSERT INTO `sys_role_menu` VALUES ('2', '2056');
INSERT INTO `sys_role_menu` VALUES ('2', '2057');
INSERT INTO `sys_role_menu` VALUES ('2', '2063');
INSERT INTO `sys_role_menu` VALUES ('2', '2064');
INSERT INTO `sys_role_menu` VALUES ('2', '2066');
INSERT INTO `sys_role_menu` VALUES ('2', '2067');
INSERT INTO `sys_role_menu` VALUES ('2', '2068');
INSERT INTO `sys_role_menu` VALUES ('2', '2069');
INSERT INTO `sys_role_menu` VALUES ('2', '2079');
INSERT INTO `sys_role_menu` VALUES ('2', '2080');
INSERT INTO `sys_role_menu` VALUES ('2', '2081');
INSERT INTO `sys_role_menu` VALUES ('2', '2082');
INSERT INTO `sys_role_menu` VALUES ('2', '2083');
INSERT INTO `sys_role_menu` VALUES ('2', '2084');
INSERT INTO `sys_role_menu` VALUES ('2', '2085');
INSERT INTO `sys_role_menu` VALUES ('2', '2086');
INSERT INTO `sys_role_menu` VALUES ('2', '2087');
INSERT INTO `sys_role_menu` VALUES ('2', '2283');
INSERT INTO `sys_role_menu` VALUES ('2', '2286');
INSERT INTO `sys_role_menu` VALUES ('2', '2299');
INSERT INTO `sys_role_menu` VALUES ('2', '2301');
INSERT INTO `sys_role_menu` VALUES ('102', '1001');
INSERT INTO `sys_role_menu` VALUES ('102', '1002');
INSERT INTO `sys_role_menu` VALUES ('103', '110');
INSERT INTO `sys_role_menu` VALUES ('103', '1046');
INSERT INTO `sys_role_menu` VALUES ('103', '1047');
INSERT INTO `sys_role_menu` VALUES ('103', '1049');
INSERT INTO `sys_role_menu` VALUES ('103', '1050');
INSERT INTO `sys_role_menu` VALUES ('103', '1051');
INSERT INTO `sys_role_menu` VALUES ('103', '1052');
INSERT INTO `sys_role_menu` VALUES ('103', '1053');
INSERT INTO `sys_role_menu` VALUES ('103', '1054');
INSERT INTO `sys_role_menu` VALUES ('105', '109');
INSERT INTO `sys_role_menu` VALUES ('105', '110');
INSERT INTO `sys_role_menu` VALUES ('105', '1046');
INSERT INTO `sys_role_menu` VALUES ('105', '1047');
INSERT INTO `sys_role_menu` VALUES ('105', '1048');
INSERT INTO `sys_role_menu` VALUES ('105', '1049');
INSERT INTO `sys_role_menu` VALUES ('105', '1050');
INSERT INTO `sys_role_menu` VALUES ('105', '1051');
INSERT INTO `sys_role_menu` VALUES ('105', '1052');
INSERT INTO `sys_role_menu` VALUES ('105', '1053');
INSERT INTO `sys_role_menu` VALUES ('105', '1054');
INSERT INTO `sys_role_menu` VALUES ('105', '2002');
INSERT INTO `sys_role_menu` VALUES ('105', '2007');
INSERT INTO `sys_role_menu` VALUES ('109', '2049');
INSERT INTO `sys_role_menu` VALUES ('109', '2051');
INSERT INTO `sys_role_menu` VALUES ('109', '2308');
INSERT INTO `sys_role_menu` VALUES ('109', '2309');
INSERT INTO `sys_role_menu` VALUES ('109', '2310');
INSERT INTO `sys_role_menu` VALUES ('109', '2311');
INSERT INTO `sys_role_menu` VALUES ('109', '2312');
INSERT INTO `sys_role_menu` VALUES ('109', '2313');
INSERT INTO `sys_role_menu` VALUES ('109', '2314');
INSERT INTO `sys_role_menu` VALUES ('109', '2315');
INSERT INTO `sys_role_menu` VALUES ('109', '2317');
INSERT INTO `sys_role_menu` VALUES ('109', '2318');
INSERT INTO `sys_role_menu` VALUES ('109', '2319');
INSERT INTO `sys_role_menu` VALUES ('109', '2320');
INSERT INTO `sys_role_menu` VALUES ('109', '2321');
INSERT INTO `sys_role_menu` VALUES ('109', '2322');
INSERT INTO `sys_role_menu` VALUES ('109', '2323');
INSERT INTO `sys_role_menu` VALUES ('109', '2324');
INSERT INTO `sys_role_menu` VALUES ('109', '2325');
INSERT INTO `sys_role_menu` VALUES ('109', '2326');
INSERT INTO `sys_role_menu` VALUES ('109', '2327');
INSERT INTO `sys_role_menu` VALUES ('111', '2037');
INSERT INTO `sys_role_menu` VALUES ('111', '2038');
INSERT INTO `sys_role_menu` VALUES ('111', '2039');
INSERT INTO `sys_role_menu` VALUES ('111', '2040');
INSERT INTO `sys_role_menu` VALUES ('111', '2041');
INSERT INTO `sys_role_menu` VALUES ('111', '2042');
INSERT INTO `sys_role_menu` VALUES ('111', '2044');
INSERT INTO `sys_role_menu` VALUES ('111', '2045');
INSERT INTO `sys_role_menu` VALUES ('111', '2046');
INSERT INTO `sys_role_menu` VALUES ('111', '2047');
INSERT INTO `sys_role_menu` VALUES ('111', '2048');
INSERT INTO `sys_role_menu` VALUES ('111', '2054');
INSERT INTO `sys_role_menu` VALUES ('111', '2056');
INSERT INTO `sys_role_menu` VALUES ('111', '2057');
INSERT INTO `sys_role_menu` VALUES ('111', '2079');
INSERT INTO `sys_role_menu` VALUES ('111', '2080');
INSERT INTO `sys_role_menu` VALUES ('111', '2081');
INSERT INTO `sys_role_menu` VALUES ('111', '2082');
INSERT INTO `sys_role_menu` VALUES ('111', '2083');
INSERT INTO `sys_role_menu` VALUES ('111', '2084');
INSERT INTO `sys_role_menu` VALUES ('111', '2085');
INSERT INTO `sys_role_menu` VALUES ('111', '2086');
INSERT INTO `sys_role_menu` VALUES ('111', '2087');
INSERT INTO `sys_role_menu` VALUES ('112', '2010');
INSERT INTO `sys_role_menu` VALUES ('112', '2011');
INSERT INTO `sys_role_menu` VALUES ('112', '2012');
INSERT INTO `sys_role_menu` VALUES ('112', '2013');
INSERT INTO `sys_role_menu` VALUES ('112', '2014');
INSERT INTO `sys_role_menu` VALUES ('112', '2015');
INSERT INTO `sys_role_menu` VALUES ('112', '2016');
INSERT INTO `sys_role_menu` VALUES ('112', '2046');
INSERT INTO `sys_role_menu` VALUES ('112', '2047');
INSERT INTO `sys_role_menu` VALUES ('112', '2048');
INSERT INTO `sys_role_menu` VALUES ('112', '2054');
INSERT INTO `sys_role_menu` VALUES ('112', '2055');
INSERT INTO `sys_role_menu` VALUES ('112', '2056');
INSERT INTO `sys_role_menu` VALUES ('112', '2057');
INSERT INTO `sys_role_menu` VALUES ('112', '2058');
INSERT INTO `sys_role_menu` VALUES ('112', '2059');
INSERT INTO `sys_role_menu` VALUES ('112', '2060');
INSERT INTO `sys_role_menu` VALUES ('112', '2061');
INSERT INTO `sys_role_menu` VALUES ('112', '2062');
INSERT INTO `sys_role_menu` VALUES ('112', '2063');
INSERT INTO `sys_role_menu` VALUES ('112', '2064');
INSERT INTO `sys_role_menu` VALUES ('112', '2065');
INSERT INTO `sys_role_menu` VALUES ('112', '2066');
INSERT INTO `sys_role_menu` VALUES ('112', '2067');
INSERT INTO `sys_role_menu` VALUES ('112', '2068');
INSERT INTO `sys_role_menu` VALUES ('112', '2069');
INSERT INTO `sys_role_menu` VALUES ('112', '2075');
INSERT INTO `sys_role_menu` VALUES ('112', '2076');
INSERT INTO `sys_role_menu` VALUES ('112', '2077');
INSERT INTO `sys_role_menu` VALUES ('112', '2078');
INSERT INTO `sys_role_menu` VALUES ('112', '2079');
INSERT INTO `sys_role_menu` VALUES ('112', '2080');
INSERT INTO `sys_role_menu` VALUES ('112', '2081');
INSERT INTO `sys_role_menu` VALUES ('112', '2082');
INSERT INTO `sys_role_menu` VALUES ('112', '2083');
INSERT INTO `sys_role_menu` VALUES ('112', '2084');
INSERT INTO `sys_role_menu` VALUES ('112', '2085');
INSERT INTO `sys_role_menu` VALUES ('112', '2086');
INSERT INTO `sys_role_menu` VALUES ('112', '2087');
INSERT INTO `sys_role_menu` VALUES ('112', '2102');
INSERT INTO `sys_role_menu` VALUES ('112', '2208');
INSERT INTO `sys_role_menu` VALUES ('112', '2209');
INSERT INTO `sys_role_menu` VALUES ('112', '2219');
INSERT INTO `sys_role_menu` VALUES ('112', '2226');
INSERT INTO `sys_role_menu` VALUES ('112', '2227');
INSERT INTO `sys_role_menu` VALUES ('112', '2228');
INSERT INTO `sys_role_menu` VALUES ('112', '2229');
INSERT INTO `sys_role_menu` VALUES ('114', '2088');
INSERT INTO `sys_role_menu` VALUES ('114', '2089');
INSERT INTO `sys_role_menu` VALUES ('114', '2090');
INSERT INTO `sys_role_menu` VALUES ('114', '2091');
INSERT INTO `sys_role_menu` VALUES ('114', '2092');
INSERT INTO `sys_role_menu` VALUES ('114', '2188');
INSERT INTO `sys_role_menu` VALUES ('114', '2193');
INSERT INTO `sys_role_menu` VALUES ('114', '2230');
INSERT INTO `sys_role_menu` VALUES ('114', '2231');
INSERT INTO `sys_role_menu` VALUES ('114', '2232');
INSERT INTO `sys_role_menu` VALUES ('114', '2233');
INSERT INTO `sys_role_menu` VALUES ('114', '2234');
INSERT INTO `sys_role_menu` VALUES ('114', '2235');
INSERT INTO `sys_role_menu` VALUES ('114', '2236');
INSERT INTO `sys_role_menu` VALUES ('114', '2237');
INSERT INTO `sys_role_menu` VALUES ('114', '2238');
INSERT INTO `sys_role_menu` VALUES ('114', '2239');
INSERT INTO `sys_role_menu` VALUES ('114', '2240');
INSERT INTO `sys_role_menu` VALUES ('114', '2241');
INSERT INTO `sys_role_menu` VALUES ('114', '2242');
INSERT INTO `sys_role_menu` VALUES ('114', '2243');
INSERT INTO `sys_role_menu` VALUES ('114', '2244');
INSERT INTO `sys_role_menu` VALUES ('114', '2245');
INSERT INTO `sys_role_menu` VALUES ('114', '2246');
INSERT INTO `sys_role_menu` VALUES ('114', '2247');
INSERT INTO `sys_role_menu` VALUES ('114', '2248');
INSERT INTO `sys_role_menu` VALUES ('114', '2249');
INSERT INTO `sys_role_menu` VALUES ('114', '2250');
INSERT INTO `sys_role_menu` VALUES ('114', '2251');
INSERT INTO `sys_role_menu` VALUES ('114', '2252');
INSERT INTO `sys_role_menu` VALUES ('114', '2253');
INSERT INTO `sys_role_menu` VALUES ('114', '2254');
INSERT INTO `sys_role_menu` VALUES ('114', '2255');
INSERT INTO `sys_role_menu` VALUES ('114', '2288');
INSERT INTO `sys_role_menu` VALUES ('116', '2010');
INSERT INTO `sys_role_menu` VALUES ('116', '2011');
INSERT INTO `sys_role_menu` VALUES ('116', '2012');
INSERT INTO `sys_role_menu` VALUES ('116', '2013');
INSERT INTO `sys_role_menu` VALUES ('116', '2014');
INSERT INTO `sys_role_menu` VALUES ('116', '2015');
INSERT INTO `sys_role_menu` VALUES ('116', '2016');
INSERT INTO `sys_role_menu` VALUES ('116', '2019');
INSERT INTO `sys_role_menu` VALUES ('116', '2020');
INSERT INTO `sys_role_menu` VALUES ('116', '2021');
INSERT INTO `sys_role_menu` VALUES ('116', '2022');
INSERT INTO `sys_role_menu` VALUES ('116', '2023');
INSERT INTO `sys_role_menu` VALUES ('116', '2024');
INSERT INTO `sys_role_menu` VALUES ('116', '2025');
INSERT INTO `sys_role_menu` VALUES ('116', '2026');
INSERT INTO `sys_role_menu` VALUES ('116', '2027');
INSERT INTO `sys_role_menu` VALUES ('116', '2028');
INSERT INTO `sys_role_menu` VALUES ('116', '2029');
INSERT INTO `sys_role_menu` VALUES ('116', '2033');
INSERT INTO `sys_role_menu` VALUES ('116', '2034');
INSERT INTO `sys_role_menu` VALUES ('116', '2035');
INSERT INTO `sys_role_menu` VALUES ('116', '2037');
INSERT INTO `sys_role_menu` VALUES ('116', '2038');
INSERT INTO `sys_role_menu` VALUES ('116', '2039');
INSERT INTO `sys_role_menu` VALUES ('116', '2040');
INSERT INTO `sys_role_menu` VALUES ('116', '2041');
INSERT INTO `sys_role_menu` VALUES ('116', '2042');
INSERT INTO `sys_role_menu` VALUES ('116', '2044');
INSERT INTO `sys_role_menu` VALUES ('116', '2045');
INSERT INTO `sys_role_menu` VALUES ('116', '2046');
INSERT INTO `sys_role_menu` VALUES ('116', '2047');
INSERT INTO `sys_role_menu` VALUES ('116', '2048');
INSERT INTO `sys_role_menu` VALUES ('116', '2052');
INSERT INTO `sys_role_menu` VALUES ('116', '2053');
INSERT INTO `sys_role_menu` VALUES ('116', '2054');
INSERT INTO `sys_role_menu` VALUES ('116', '2055');
INSERT INTO `sys_role_menu` VALUES ('116', '2056');
INSERT INTO `sys_role_menu` VALUES ('116', '2057');
INSERT INTO `sys_role_menu` VALUES ('116', '2058');
INSERT INTO `sys_role_menu` VALUES ('116', '2059');
INSERT INTO `sys_role_menu` VALUES ('116', '2060');
INSERT INTO `sys_role_menu` VALUES ('116', '2061');
INSERT INTO `sys_role_menu` VALUES ('116', '2062');
INSERT INTO `sys_role_menu` VALUES ('116', '2063');
INSERT INTO `sys_role_menu` VALUES ('116', '2064');
INSERT INTO `sys_role_menu` VALUES ('116', '2065');
INSERT INTO `sys_role_menu` VALUES ('116', '2066');
INSERT INTO `sys_role_menu` VALUES ('116', '2067');
INSERT INTO `sys_role_menu` VALUES ('116', '2068');
INSERT INTO `sys_role_menu` VALUES ('116', '2069');
INSERT INTO `sys_role_menu` VALUES ('116', '2075');
INSERT INTO `sys_role_menu` VALUES ('116', '2076');
INSERT INTO `sys_role_menu` VALUES ('116', '2077');
INSERT INTO `sys_role_menu` VALUES ('116', '2078');
INSERT INTO `sys_role_menu` VALUES ('116', '2079');
INSERT INTO `sys_role_menu` VALUES ('116', '2080');
INSERT INTO `sys_role_menu` VALUES ('116', '2081');
INSERT INTO `sys_role_menu` VALUES ('116', '2082');
INSERT INTO `sys_role_menu` VALUES ('116', '2083');
INSERT INTO `sys_role_menu` VALUES ('116', '2084');
INSERT INTO `sys_role_menu` VALUES ('116', '2085');
INSERT INTO `sys_role_menu` VALUES ('116', '2086');
INSERT INTO `sys_role_menu` VALUES ('116', '2087');
INSERT INTO `sys_role_menu` VALUES ('116', '2099');
INSERT INTO `sys_role_menu` VALUES ('116', '2100');
INSERT INTO `sys_role_menu` VALUES ('116', '2101');
INSERT INTO `sys_role_menu` VALUES ('116', '2102');
INSERT INTO `sys_role_menu` VALUES ('116', '2194');
INSERT INTO `sys_role_menu` VALUES ('116', '2195');
INSERT INTO `sys_role_menu` VALUES ('116', '2196');
INSERT INTO `sys_role_menu` VALUES ('116', '2197');
INSERT INTO `sys_role_menu` VALUES ('116', '2198');
INSERT INTO `sys_role_menu` VALUES ('116', '2199');
INSERT INTO `sys_role_menu` VALUES ('116', '2200');
INSERT INTO `sys_role_menu` VALUES ('116', '2201');
INSERT INTO `sys_role_menu` VALUES ('116', '2203');
INSERT INTO `sys_role_menu` VALUES ('116', '2204');
INSERT INTO `sys_role_menu` VALUES ('116', '2205');
INSERT INTO `sys_role_menu` VALUES ('116', '2206');
INSERT INTO `sys_role_menu` VALUES ('116', '2207');
INSERT INTO `sys_role_menu` VALUES ('116', '2208');
INSERT INTO `sys_role_menu` VALUES ('116', '2209');
INSERT INTO `sys_role_menu` VALUES ('116', '2210');
INSERT INTO `sys_role_menu` VALUES ('116', '2211');
INSERT INTO `sys_role_menu` VALUES ('116', '2212');
INSERT INTO `sys_role_menu` VALUES ('116', '2213');
INSERT INTO `sys_role_menu` VALUES ('116', '2214');
INSERT INTO `sys_role_menu` VALUES ('116', '2215');
INSERT INTO `sys_role_menu` VALUES ('116', '2216');
INSERT INTO `sys_role_menu` VALUES ('116', '2217');
INSERT INTO `sys_role_menu` VALUES ('116', '2218');
INSERT INTO `sys_role_menu` VALUES ('116', '2219');
INSERT INTO `sys_role_menu` VALUES ('116', '2220');
INSERT INTO `sys_role_menu` VALUES ('116', '2221');
INSERT INTO `sys_role_menu` VALUES ('116', '2222');
INSERT INTO `sys_role_menu` VALUES ('116', '2223');
INSERT INTO `sys_role_menu` VALUES ('116', '2224');
INSERT INTO `sys_role_menu` VALUES ('116', '2225');
INSERT INTO `sys_role_menu` VALUES ('116', '2226');
INSERT INTO `sys_role_menu` VALUES ('116', '2227');
INSERT INTO `sys_role_menu` VALUES ('116', '2228');
INSERT INTO `sys_role_menu` VALUES ('116', '2229');
INSERT INTO `sys_role_menu` VALUES ('116', '2282');
INSERT INTO `sys_role_menu` VALUES ('116', '2283');
INSERT INTO `sys_role_menu` VALUES ('116', '2284');
INSERT INTO `sys_role_menu` VALUES ('116', '2285');
INSERT INTO `sys_role_menu` VALUES ('116', '2286');
INSERT INTO `sys_role_menu` VALUES ('116', '2289');
INSERT INTO `sys_role_menu` VALUES ('117', '2010');
INSERT INTO `sys_role_menu` VALUES ('117', '2012');
INSERT INTO `sys_role_menu` VALUES ('117', '2013');
INSERT INTO `sys_role_menu` VALUES ('117', '2014');
INSERT INTO `sys_role_menu` VALUES ('117', '2016');
INSERT INTO `sys_role_menu` VALUES ('117', '2019');
INSERT INTO `sys_role_menu` VALUES ('117', '2020');
INSERT INTO `sys_role_menu` VALUES ('117', '2021');
INSERT INTO `sys_role_menu` VALUES ('117', '2022');
INSERT INTO `sys_role_menu` VALUES ('117', '2023');
INSERT INTO `sys_role_menu` VALUES ('117', '2024');
INSERT INTO `sys_role_menu` VALUES ('117', '2025');
INSERT INTO `sys_role_menu` VALUES ('117', '2026');
INSERT INTO `sys_role_menu` VALUES ('117', '2027');
INSERT INTO `sys_role_menu` VALUES ('117', '2028');
INSERT INTO `sys_role_menu` VALUES ('117', '2029');
INSERT INTO `sys_role_menu` VALUES ('117', '2033');
INSERT INTO `sys_role_menu` VALUES ('117', '2034');
INSERT INTO `sys_role_menu` VALUES ('117', '2035');
INSERT INTO `sys_role_menu` VALUES ('117', '2037');
INSERT INTO `sys_role_menu` VALUES ('117', '2038');
INSERT INTO `sys_role_menu` VALUES ('117', '2039');
INSERT INTO `sys_role_menu` VALUES ('117', '2040');
INSERT INTO `sys_role_menu` VALUES ('117', '2041');
INSERT INTO `sys_role_menu` VALUES ('117', '2042');
INSERT INTO `sys_role_menu` VALUES ('117', '2044');
INSERT INTO `sys_role_menu` VALUES ('117', '2045');
INSERT INTO `sys_role_menu` VALUES ('117', '2052');
INSERT INTO `sys_role_menu` VALUES ('117', '2053');
INSERT INTO `sys_role_menu` VALUES ('117', '2058');
INSERT INTO `sys_role_menu` VALUES ('117', '2059');
INSERT INTO `sys_role_menu` VALUES ('117', '2060');
INSERT INTO `sys_role_menu` VALUES ('117', '2061');
INSERT INTO `sys_role_menu` VALUES ('117', '2062');
INSERT INTO `sys_role_menu` VALUES ('117', '2063');
INSERT INTO `sys_role_menu` VALUES ('117', '2064');
INSERT INTO `sys_role_menu` VALUES ('117', '2065');
INSERT INTO `sys_role_menu` VALUES ('117', '2066');
INSERT INTO `sys_role_menu` VALUES ('117', '2067');
INSERT INTO `sys_role_menu` VALUES ('117', '2068');
INSERT INTO `sys_role_menu` VALUES ('117', '2069');
INSERT INTO `sys_role_menu` VALUES ('117', '2075');
INSERT INTO `sys_role_menu` VALUES ('117', '2076');
INSERT INTO `sys_role_menu` VALUES ('117', '2077');
INSERT INTO `sys_role_menu` VALUES ('117', '2078');
INSERT INTO `sys_role_menu` VALUES ('117', '2099');
INSERT INTO `sys_role_menu` VALUES ('117', '2100');
INSERT INTO `sys_role_menu` VALUES ('117', '2101');
INSERT INTO `sys_role_menu` VALUES ('117', '2102');
INSERT INTO `sys_role_menu` VALUES ('117', '2194');
INSERT INTO `sys_role_menu` VALUES ('117', '2195');
INSERT INTO `sys_role_menu` VALUES ('117', '2196');
INSERT INTO `sys_role_menu` VALUES ('117', '2197');
INSERT INTO `sys_role_menu` VALUES ('117', '2198');
INSERT INTO `sys_role_menu` VALUES ('117', '2199');
INSERT INTO `sys_role_menu` VALUES ('117', '2200');
INSERT INTO `sys_role_menu` VALUES ('117', '2201');
INSERT INTO `sys_role_menu` VALUES ('117', '2203');
INSERT INTO `sys_role_menu` VALUES ('117', '2204');
INSERT INTO `sys_role_menu` VALUES ('117', '2205');
INSERT INTO `sys_role_menu` VALUES ('117', '2206');
INSERT INTO `sys_role_menu` VALUES ('117', '2207');
INSERT INTO `sys_role_menu` VALUES ('117', '2208');
INSERT INTO `sys_role_menu` VALUES ('117', '2209');
INSERT INTO `sys_role_menu` VALUES ('117', '2210');
INSERT INTO `sys_role_menu` VALUES ('117', '2211');
INSERT INTO `sys_role_menu` VALUES ('117', '2212');
INSERT INTO `sys_role_menu` VALUES ('117', '2213');
INSERT INTO `sys_role_menu` VALUES ('117', '2214');
INSERT INTO `sys_role_menu` VALUES ('117', '2215');
INSERT INTO `sys_role_menu` VALUES ('117', '2216');
INSERT INTO `sys_role_menu` VALUES ('117', '2217');
INSERT INTO `sys_role_menu` VALUES ('117', '2219');
INSERT INTO `sys_role_menu` VALUES ('117', '2220');
INSERT INTO `sys_role_menu` VALUES ('117', '2221');
INSERT INTO `sys_role_menu` VALUES ('117', '2222');
INSERT INTO `sys_role_menu` VALUES ('117', '2223');
INSERT INTO `sys_role_menu` VALUES ('117', '2224');
INSERT INTO `sys_role_menu` VALUES ('117', '2225');
INSERT INTO `sys_role_menu` VALUES ('117', '2282');
INSERT INTO `sys_role_menu` VALUES ('117', '2284');
INSERT INTO `sys_role_menu` VALUES ('117', '2285');
INSERT INTO `sys_role_menu` VALUES ('117', '2286');
INSERT INTO `sys_role_menu` VALUES ('117', '2289');
INSERT INTO `sys_role_menu` VALUES ('117', '2290');
INSERT INTO `sys_role_menu` VALUES ('117', '2292');
INSERT INTO `sys_role_menu` VALUES ('117', '2293');
INSERT INTO `sys_role_menu` VALUES ('117', '2294');
INSERT INTO `sys_role_menu` VALUES ('117', '2295');
INSERT INTO `sys_role_menu` VALUES ('117', '2296');
INSERT INTO `sys_role_menu` VALUES ('117', '2297');
INSERT INTO `sys_role_menu` VALUES ('117', '2298');
INSERT INTO `sys_role_menu` VALUES ('117', '2300');
INSERT INTO `sys_role_menu` VALUES ('117', '2301');
INSERT INTO `sys_role_menu` VALUES ('117', '2302');
INSERT INTO `sys_role_menu` VALUES ('117', '2304');
INSERT INTO `sys_role_menu` VALUES ('117', '2328');
INSERT INTO `sys_role_menu` VALUES ('117', '2329');
INSERT INTO `sys_role_menu` VALUES ('117', '2333');
INSERT INTO `sys_role_menu` VALUES ('117', '2334');
INSERT INTO `sys_role_menu` VALUES ('117', '2335');
INSERT INTO `sys_role_menu` VALUES ('117', '2337');
INSERT INTO `sys_role_menu` VALUES ('117', '2338');
INSERT INTO `sys_role_menu` VALUES ('117', '2339');
INSERT INTO `sys_role_menu` VALUES ('117', '2340');
INSERT INTO `sys_role_menu` VALUES ('117', '2341');
INSERT INTO `sys_role_menu` VALUES ('117', '2342');
INSERT INTO `sys_role_menu` VALUES ('117', '2343');
INSERT INTO `sys_role_menu` VALUES ('117', '2345');
INSERT INTO `sys_role_menu` VALUES ('117', '2346');
INSERT INTO `sys_role_menu` VALUES ('117', '2347');
INSERT INTO `sys_role_menu` VALUES ('117', '2348');
INSERT INTO `sys_role_menu` VALUES ('117', '2349');
INSERT INTO `sys_role_menu` VALUES ('117', '2351');

-- ----------------------------
-- Table structure for sys_site
-- ----------------------------
DROP TABLE IF EXISTS `sys_site`;
CREATE TABLE `sys_site` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '网点id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `parent_name` varchar(200) DEFAULT '' COMMENT '父级网点名称',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `site_code` varchar(50) NOT NULL DEFAULT '' COMMENT '网点编号',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `site_name_chinese` varchar(200) NOT NULL DEFAULT '' COMMENT '网点名称-中文',
  `site_name_arabic` varchar(255) DEFAULT '' COMMENT '网点名称-阿拉伯',
  `site_name_english` varchar(255) DEFAULT '' COMMENT '网点名称-英语',
  `site_rank_code` varchar(20) DEFAULT '' COMMENT '机构网点级别 数据字典编号',
  `site_rank_name` varchar(50) DEFAULT '' COMMENT '机构网点级别名称',
  `cod_currency` varchar(10) DEFAULT '' COMMENT '代收货款币种',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `mobile` varchar(11) DEFAULT '' COMMENT '手机',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `type_code` varchar(30) DEFAULT '' COMMENT '类型:数据字典type',
  `type_name` varchar(50) DEFAULT NULL COMMENT '类型：数据字典',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `send_addr` varchar(200) DEFAULT '' COMMENT '默认发件地',
  `country` varchar(50) DEFAULT '' COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT '' COMMENT '城市',
  `area` varchar(50) DEFAULT '' COMMENT '区域',
  `street` varchar(200) DEFAULT '' COMMENT '街道',
  `address` varchar(500) DEFAULT '' COMMENT '详细地址',
  `keyword_code` varchar(20) DEFAULT NULL COMMENT '关键字表code',
  `keyword_name` varchar(200) DEFAULT NULL COMMENT '关键字表name',
  `region` varchar(50) DEFAULT '' COMMENT '所属地区',
  `fin_center_site_code` varchar(255) DEFAULT '' COMMENT '所属财务中心:网点编号',
  `fin_center_site_name` varchar(255) DEFAULT '' COMMENT '所属财务中心:网点名称',
  `fin_center_flag` char(1) DEFAULT '0' COMMENT '财务中心标识：0-没有，1-有',
  `allocate_center_flag` char(1) DEFAULT '0' COMMENT '分拨重虚拟标识：0-无，1-有',
  `reach_pay_flag` char(1) DEFAULT '0' COMMENT '是否到付：0-否，1-是',
  `loan_flag` char(1) DEFAULT '0' COMMENT '允许代缴：0-不允许，1-允许',
  `loan_payment_quota` decimal(10,2) DEFAULT NULL COMMENT '代收贷款限额',
  `supervisor_mode_code` varchar(30) DEFAULT '' COMMENT '管理方式:数据字典',
  `supervisor_mode_name` varchar(200) DEFAULT '' COMMENT '管理方式:数据字典',
  `delivery_scope` varchar(200) DEFAULT '' COMMENT '派送范围',
  `remark` varchar(2000) DEFAULT '' COMMENT '备注',
  `sorting_code` varchar(200) DEFAULT '' COMMENT '分拣码',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '网点状态（0正常 1停用）',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(32) DEFAULT '' COMMENT '创建ID',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(32) DEFAULT '' COMMENT '修改者id',
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='网点管理';

-- ----------------------------
-- Records of sys_site
-- ----------------------------
INSERT INTO `sys_site` VALUES ('0', '-1', '', '', '0000', '0', 'headquarters', '', '', '002001', '总部', '005001', null, '', null, '001001', '中心', null, 'headquarters', '', null, '', '', '', '', null, null, '', '0000', 'headquarters', '1', '1', '0', '0', null, '020002', '直营', '', '', '', '1', '0', '', '', '2020-08-20 18:46:18', '1', 'admin', '2020-11-06 08:23:55');
INSERT INTO `sys_site` VALUES ('41', '0', 'headquarters', '', 'Riyadh', '0', 'Riyadh', '', '', '002002', '中心', '008002', '', '', '', '001001', '中心', '', 'Riyadh', 'SA', '', 'R', '', '', '', '', '', '', '0000', 'headquarters', '', '1', '', '', null, '020002', '直营', '', '', '', '1', '0', '1', 'admin', '2020-10-16 16:57:09', '1', 'admin', '2020-11-02 17:35:30');
INSERT INTO `sys_site` VALUES ('45', '0', 'headquarters', '', 'TEST', '0', 'TEST(Cannot be deleted)', '', '', '002003', '网点', '008002', '', '', '', '001002', '网点', '', 'TEST', 'SA', '', 'Riyadh', '', '', '', '', '', '', '0000', 'headquarters', '', '', '', '', null, '020002', '直营', '', '', '', '1', '0', '1', 'admin', '2020-10-31 11:38:11', '', '', null);
INSERT INTO `sys_site` VALUES ('46', '0', 'headquarters', '', 'Jeddah', '0', 'Jeddah', '', '', '002003', '网点', '008002', '', '', '', '001002', '网点', '', 'Jeddah', 'SA', '', 'Jeddah', '', '', '', '', '', '', '0000', 'headquarters', '', '', '', '', null, '020002', '直营', '', '', '', '1', '0', '288', 'Riyadh001', '2020-11-03 11:40:18', '', '', null);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `site_code` varchar(50) NOT NULL DEFAULT '' COMMENT '网点编号',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01VIP用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `spear_password` varchar(100) DEFAULT '' COMMENT '巴枪使用密码',
  `service_product` varchar(200) DEFAULT '' COMMENT '服务产品',
  `openid` varchar(32) DEFAULT '' COMMENT '用户的唯一标识',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(50) DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建id',
  `create_by_name` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL,
  `update_by_name` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `site_name` varchar(200) DEFAULT NULL COMMENT '网点名称',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '0000', 'admin', '敏思达E3', '00', '', '15666666666', '1', '', '$2a$10$dv9yIrQo87yFF/4nFVq0U.fx8oEZqwasN16mobqs8qD7c3hTLm2ci', '$2a$10$N.6O3r0HTAqUCaylo.bfluKGMEDxY0fryIje0BzDesXVwjH2i5YF6', '', '', '0', '0', '127.0.0.1', '2018-03-16 11:33:00', null, 'admin', '2018-03-16 11:33:00', null, 'admin', '2020-10-30 20:45:27', 'E3管理员', null);
INSERT INTO `sys_user` VALUES ('2', '0000', 'vipadmin', '敏思达VIP', '01', '', '', '1', '', '$2a$10$UHA3aKEu.1RIh7yvo1KAOOEzp8UxzC7cgeAUDqoJmHkq1tgTuJmPm', '', '', '', '0', '0', '127.0.0.1', '2018-03-16 11:33:00', null, 'admin', '2018-03-16 11:33:00', null, 'vipadmin', '2020-10-14 14:56:42', '大客户管理员', null);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES ('1', '1');
INSERT INTO `sys_user_post` VALUES ('2', '2');
INSERT INTO `sys_user_post` VALUES ('104', '33');
INSERT INTO `sys_user_post` VALUES ('107', '33');
INSERT INTO `sys_user_post` VALUES ('108', '33');
INSERT INTO `sys_user_post` VALUES ('110', '33');
INSERT INTO `sys_user_post` VALUES ('138', '1031');
INSERT INTO `sys_user_post` VALUES ('138', '1035');
INSERT INTO `sys_user_post` VALUES ('138', '1036');
INSERT INTO `sys_user_post` VALUES ('139', '1031');
INSERT INTO `sys_user_post` VALUES ('139', '1032');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
