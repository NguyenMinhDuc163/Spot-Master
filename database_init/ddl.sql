

CREATE TABLE `btlltm`.`users` (
                                  `userId` INT NOT NULL AUTO_INCREMENT,
                                  `username` VARCHAR(45) NOT NULL,
                                  `password` VARCHAR(45) NOT NULL,
                                  `score` FLOAT NOT NULL,
                                  `win` INT NOT NULL,
                                  `draw` INT NOT NULL,
                                  `lose` INT NOT NULL,
                                  `avgCompetitor` FLOAT NOT NULL,
                                  `avgTime` FLOAT NOT NULL,
                                  PRIMARY KEY (`userId`));