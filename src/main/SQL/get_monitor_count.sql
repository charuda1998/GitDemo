CREATE PROCEDURE `get_monitor_count`(IN iService varchar(100), IN iSuccess INT(1), IN   iFailure INT(1),
                                                    OUT  oSuccess INT(10), OUT  oFailure INT(10))
BEGIN

DECLARE  vSuccess   INT(10);
DECLARE  vFailure   INT(10);

SELECT success,failure INTO vSuccess,vFailure FROM PASSIVE_MONITOR WHERE
service_name = iService;
IF FOUND_ROWS() > 0
 THEN
     SET oSuccess = vSuccess + iSuccess;
     SET oFailure = vFailure + iFailure;

     UPDATE PASSIVE_MONITOR SET success = oSuccess, failure = oFailure WHERE
     service_name = iService;
ELSE
    INSERT INTO PASSIVE_MONITOR VALUES(iService, iSuccess, iFailure);
     SET oSuccess = iSuccess;
     SET oFailure = iFailure;
END IF;
END