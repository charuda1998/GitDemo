CREATE TABLE `CLIENT_SESSION` (
  `user_id` varchar(10) NOT NULL,
  `session_id` varchar(50) DEFAULT NULL,
  `app_id` varchar(100) NOT NULL,
  `build` varchar(30) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active` int(12) NOT NULL,
  `user_type` varchar(20) DEFAULT NULL,
  `user_info` varchar(100) DEFAULT NULL,
  `ft_session_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

 CREATE TABLE `settings` (
  `param_name` varchar(30) NOT NULL,
  `param_value` varchar(200) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DELIMITER #
DROP PROCEDURE IF EXISTS `session_validation`;
CREATE DEFINER=`root`@`localhost` PROCEDURE `session_validation`(IN sessionID varchar(50), OUT oStatus INT, OUT oMsg varchar(30) )
BEGIN
declare lastActive int(12) DEFAULT 0;
declare currentTimeStamp int(12);
declare session_timeout INT;
select CAST(param_value AS UNSIGNED) INTO session_timeout from settings where param_name='session_timeout';
select last_active into lastActive from CLIENT_SESSION where session_id = sessionID;
select UNIX_TIMESTAMP() into currentTimeStamp;
IF( lastActive = 0 ) then
SET oStatus = -1;
SET oMsg = "Invalid sessionID";
ELSEIF( ABS(currentTimeStamp - lastActive) <= session_timeout) then
select user_id, app_id, build, user_type,last_active, user_info,ft_session_id from CLIENT_SESSION where session_id = sessionID;

SET oStatus = 0;
ELSE
DELETE from CLIENT_SESSION where session_id = sessionID;
SET oStatus = -2;
SET oMsg = "Session expired";
END IF;
END #
DELIMITER ;

