CREATE PROCEDURE `client_order_no_retrieval`(IN userID varchar(50), OUT oClientOrderNo INT)
BEGIN

SELECT client_order_no into oClientOrderNo from CLIENT_SESSION WHERE user_id = userID;
UPDATE CLIENT_SESSION SET client_order_no = oClientOrderNo + 1 WHERE user_id = userID;

END
