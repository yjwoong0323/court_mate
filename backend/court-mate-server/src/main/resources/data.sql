-- data.sql

-- admin
INSERT INTO admin (name, password) VALUES ('이재웅', '9874');

-- player
INSERT INTO player (name, sex, level) VALUES ('이재웅', 'M', 'A');
INSERT INTO player (name, sex, level) VALUES ('이다윤', 'W', 'S');
INSERT INTO player (name, sex, level) VALUES ('김범근', 'M', 'A');
INSERT INTO player (name, sex, level) VALUES ('이승주', 'W', 'A');
INSERT INTO player (name, sex, level) VALUES ('허시원', 'M', 'A');
INSERT INTO player (name, sex, level) VALUES ('나예림', 'W', 'A');
INSERT INTO player (name, sex, level) VALUES ('안종식', 'M', 'A');
INSERT INTO player (name, sex, level) VALUES ('장하연', 'W', 'A');

-- court
INSERT INTO court (name, court_type) VALUES ('1', 'ACTIVE');
INSERT INTO court (name, court_type) VALUES ('2', 'ACTIVE');
INSERT INTO court (name, court_type) VALUES ('3', 'ACTIVE');
INSERT INTO court (name, court_type) VALUES ('4', 'ACTIVE');
INSERT INTO court (name, court_type) VALUES ('W1', 'WAITING');
INSERT INTO court (name, court_type) VALUES ('W2', 'WAITING');

select * from player;

commit;