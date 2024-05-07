INSERT INTO reservation_time(start_at) VALUES ('01:00');
INSERT INTO reservation_time(start_at) VALUES ('03:00');
INSERT INTO reservation_time(start_at) VALUES ('05:00');
INSERT INTO reservation_time(start_at) VALUES ('07:00');
INSERT INTO reservation_time(start_at) VALUES ('09:00');

INSERT INTO theme(name, description, thumbnail) VALUES('test1', 'desc1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES('test2', 'desc2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES('test3', 'desc3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES('test4', 'desc4', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');


-- 가장 많이 예약된 테마가 3 -> 2 -> 1 -> 4 순으로 되도록 설정
-- 1번 테마
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '1', '1');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name4', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '5', '1');

-- 2번 테마
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name2', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '2', '2');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name3', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '1', '2');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name4', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '5', '2');

-- 3번 테마
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '1', '3');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '2', '3');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '3', '3');
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '5', '3');

-- 4번 테마
INSERT INTO reservation(name, date, time_id, theme_id) VALUES ('name1', FORMATDATETIME(DATEADD(DAY, -1, CURRENT_DATE), 'yyyy-MM-dd'), '4', '4');
