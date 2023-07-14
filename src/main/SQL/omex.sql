CREATE TABLE `omex_heartbeat` (
  `LAST_UPDATED_TIME` VARCHAR(50) NOT NULL,
  `DATE` DATE NOT NULL,
  PRIMARY KEY (`DATE`));

  


DROP EVENT IF EXISTS DELETE_HEARTBEAT
  
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` EVENT DELETE_HEARTBEAT
ON SCHEDULE EVERY 1 DAY
STARTS `2022-10-12 07:30:00` ON COMPLETION  PRESERVE ENABLE
COMMENT 'deletes omex_heartbeat table everyday'
 DO
      BEGIN
         TRUNCATE TABLE omex_heartbeat;
      END ;;
DELIMITER ;

CREATE TABLE `ORDER_DATA` (
  `user_id` int NOT NULL,
  `is_refresh_required` bit(1) NOT NULL,
  `todays` json DEFAULT NULL,
  `last_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `derivatives` json DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
