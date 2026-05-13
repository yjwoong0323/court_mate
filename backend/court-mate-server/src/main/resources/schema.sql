-- schema.sql

DROP TABLE IF EXISTS court_assignment;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS court;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS `admin`;

-- admin
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- player
CREATE TABLE `player` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `sex` enum('M','W') NOT NULL,
  `level` varchar(10) NOT NULL,
  `is_attended` BIT NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- court
CREATE TABLE `court` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `court_type` enum('ACTIVE','WAITING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- game
CREATE TABLE `game` (
  `id` int NOT NULL AUTO_INCREMENT,
  `court_id` int NOT NULL,
  `status` enum('BEFORE','PLAYING','FINISHED') NOT NULL DEFAULT 'BEFORE',
  `started_at` datetime DEFAULT NULL,
  `ended_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_court_id_idx` (`court_id`),
  CONSTRAINT `FK_court_id` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- court_assignment
CREATE TABLE `court_assignment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `game_id` int NOT NULL,
  `player_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_game_player` (`game_id`,`player_id`),
  KEY `FK_game_id_idx` (`game_id`),
  KEY `FK_player_id_idx` (`player_id`),
  CONSTRAINT `FK_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FK_player_id` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
