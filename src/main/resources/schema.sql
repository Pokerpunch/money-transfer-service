CREATE SEQUENCE `user_sequence` INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT DEFAULT `user_sequence`.nextval PRIMARY KEY,
    `version` BIGINT,
    `firstName` VARCHAR NOT NULL,
    `lastName` VARCHAR NOT NULL,
    `dateCreated` TIMESTAMP NOT NULL
);