CREATE DEFINER=`root`@`localhost` PROCEDURE `order_validator`(IN sessionID varchar(50), IN newOrderMD5 varchar(50),  OUT oStatus INT )
BEGIN
declare old_md5 varchar(50);
declare lastOrderTime int(12);
declare currentTimeStamp int(12);
declare order_expiry INT;

select CAST(param_value as UNSIGNED) into order_expiry from settings where param_name='ORDER_EXPIRY';
select order_md5, last_order_time into old_md5, lastOrderTime from CLIENT_SESSION where session_id = sessionID;
select UNIX_TIMESTAMP() into currentTimeStamp;
if( currentTimeStamp - lastOrderTime <= order_expiry) then
if( strcmp(old_md5, newOrderMD5) = 0 ) then
set oStatus = 0;
else
set oStatus = 1;
update CLIENT_SESSION set order_md5 = newOrderMD5, last_order_time = currentTimeStamp where session_id = sessionID;
end if;
else
set oStatus = 1;
update CLIENT_SESSION set order_md5 = newOrderMD5, last_order_time = currentTimeStamp where session_id = sessionID;
end if;
END
