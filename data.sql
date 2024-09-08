USE spot_master_db;
CREATE database spot_master_db;

CREATE TABLE players (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         username VARCHAR(50) UNIQUE NOT NULL,
                         password VARCHAR(50) NOT NULL,
                         total_score INT DEFAULT 0
);

CREATE TABLE games (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       player_id INT,
                       start_time DATETIME,
                       end_time DATETIME,
                       score INT,
                       time_limit INT,
                       FOREIGN KEY (player_id) REFERENCES players(id)
);


CREATE TABLE images (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        image_path_1 VARCHAR(255) NOT NULL,
                        image_path_2 VARCHAR(255) NOT NULL,
                        description TEXT
);

CREATE TABLE differences (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             image_id INT,
                             x_coordinate INT,
                             y_coordinate INT,
                             FOREIGN KEY (image_id) REFERENCES images(id)
);

CREATE TABLE game_differences (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  game_id INT,
                                  difference_id INT,
                                  found_time DATETIME,
                                  FOREIGN KEY (game_id) REFERENCES games(id),
                                  FOREIGN KEY (difference_id) REFERENCES differences(id)
);
