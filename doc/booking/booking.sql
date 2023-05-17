-- 预约表
CREATE TABLE `appointment` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `user_id` int NOT NULL COMMENT '用户 ID',
                               `service_type_id` int NOT NULL COMMENT '服务类型 ID',
                               `location_id` int NOT NULL COMMENT '地点 ID',
                               `date` date NOT NULL COMMENT '预约日期',
                               `start_time` time NOT NULL COMMENT '开始时间',
                               `end_time` time NOT NULL COMMENT '结束时间',
                               `status` ENUM('pending', 'confirmed', 'canceled') NOT NULL DEFAULT 'pending' COMMENT '预约状态',
                               `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               KEY `fk_user_id` (`user_id`),
                               KEY `fk_service_type_id` (`service_type_id`),
                               KEY `fk_location_id` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

INSERT INTO `appointment` (`user_id`, `service_type_id`, `location_id`, `date`, `start_time`, `end_time`, `status`) VALUES
(1, 2, 3, '2023-05-18', '10:00:00', '11:00:00', 'pending'),
(2, 1, 2, '2023-05-20', '09:00:00', '10:30:00', 'confirmed'),
(3, 3, 1, '2023-05-22', '14:00:00', '15:30:00', 'pending'),
(4, 2, 3, '2023-05-24', '16:00:00', '17:00:00', 'canceled'),
(5, 4, 1, '2023-05-26', '13:00:00', '14:00:00', 'pending'),
(6, 1, 2, '2023-05-28', '11:00:00', '12:30:00', 'confirmed'),
(7, 3, 1, '2023-05-30', '10:00:00', '11:30:00', 'pending'),
(8, 4, 3, '2023-06-01', '08:00:00', '09:00:00', 'confirmed'),
(9, 2, 2, '2023-06-03', '15:00:00', '16:00:00', 'pending'),
(10, 1, 1, '2023-06-05', '12:00:00', '13:00:00', 'canceled');

-- 日程表
CREATE TABLE `schedule` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `user_id` int NOT NULL COMMENT '用户 ID',
                            `appointment_id` int NOT NULL COMMENT '预约 ID',
                            `start_time` datetime NOT NULL COMMENT '开始时间',
                            `end_time` datetime NOT NULL COMMENT '结束时间',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `fk_user_id` (`user_id`),
                            KEY `fk_appointment_id` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日程表';

INSERT INTO `schedule` (`user_id`, `appointment_id`, `start_time`, `end_time`) VALUES
(1, 1, '2023-05-18 10:00:00', '2023-05-18 11:00:00'),
(2, 2, '2023-05-20 09:00:00', '2023-05-20 10:30:00'),
(3, 3, '2023-05-22 14:00:00', '2023-05-22 15:30:00'),
(6, 6, '2023-05-28 11:00:00', '2023-05-28 12:30:00'),
(8, 8, '2023-06-01 08:00:00', '2023-06-01 09:00:00'),
(9, 9, '2023-06-03 15:00:00', '2023-06-03 16:00:00');

-- 服务类型表
CREATE TABLE `service_type` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `name` varchar(50) NOT NULL COMMENT '服务类型名称',
                                `description` varchar(255) DEFAULT '' COMMENT '服务类型描述',
                                `price` decimal(8,2) NOT NULL COMMENT '价格',
                                `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务类型表';

INSERT INTO `service_type` (`name`, `description`, `price`) VALUES
('洗车服务', '包括内外清洗，打蜡抛光等', 100.00),
('美容服务', '包括汽车美容、除味等', 200.00),
('维修服务', '包括汽车机械、电气故障排除等', 300.00),
('改装服务', '包括汽车底盘、外观改装等', 500.00);

-- 地点表
CREATE TABLE `location` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `name` varchar(50) NOT NULL COMMENT '地点名称',
                            `address` varchar(255) NOT NULL COMMENT '详细地址',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地点表';

INSERT INTO `location` (`name`, `address`) VALUES
('东城区修车厂', '北京市东城区xx路xx号'),
('西城区修车厂', '北京市西城区xx路xx号'),
('朝阳区修车厂', '北京市朝阳区xx路xx号'),
('海淀区修车厂', '北京市海淀区xx路xx号');

