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
select user_id, app_id, build, user_type,last_active, user_info, ft_session, ft_session_id,j_key,client_order_no,is_2FA_authenticated from CLIENT_SESSION where session_id = sessionID;

SET oStatus = 0;
ELSE
DELETE from CLIENT_SESSION where session_id = sessionID;
SET oStatus = -2;
SET oMsg = "Session expired";
END IF;
END