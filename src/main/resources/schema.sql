CREATE SEQUENCE `user_sequence` INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT DEFAULT `user_sequence`.nextval PRIMARY KEY,
    `version` BIGINT,
    `firstName` VARCHAR NOT NULL,
    `lastName` VARCHAR NOT NULL,
    `dateCreated` TIMESTAMP NOT NULL
);

CREATE SEQUENCE `account_sequence` INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS `accounts` (
    `id` BIGINT DEFAULT `account_sequence`.nextval PRIMARY KEY,
    `version` BIGINT,
    `accountOwnerId` BIGINT NOT NULL,
    `balanceInMinor` BIGINT NOT NULL,
    `dateCreated` TIMESTAMP NOT NULL,
    `dateUpdated` TIMESTAMP NOT NULL
);

CREATE SEQUENCE `transfer_sequence` INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS `transfers` (
    `id` BIGINT DEFAULT `transfer_sequence`.nextval PRIMARY KEY,
    `originAccountId` BIGINT NOT NULL,
    `destinationAccountId` BIGINT NOT NULL,
    `amountInMinor` BIGINT NOT NULL,
    `dateCreated` TIMESTAMP NOT NULL
);