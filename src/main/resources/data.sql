-- User 1
INSERT INTO `users` (`id`, `version`, `firstName`, `lastName`, `dateCreated`)
VALUES
(`user_sequence`.nextval,
 0,
 'Amy',
 'Adams',
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- User 2
INSERT INTO `users` (`id`, `version`, `firstName`, `lastName`, `dateCreated`)
VALUES
(`user_sequence`.nextval,
 0,
 'Bilbo',
 'Baggins',
 parsedatetime('01-01-2000 00:00:02.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- User 3
INSERT INTO `users`
(`id`, `version`, `firstName`, `lastName`, `dateCreated`)
VALUES
(`user_sequence`.nextval,
 0,
 'Charlie',
 'Chaplin',
 parsedatetime('01-01-2000 00:00:03.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- Account 1
INSERT INTO `accounts`
(`id`, `version`, `accountOwnerId`, `balanceMinor`, `dateCreated`, `dateUpdated`)
VALUES
(`account_sequence`.nextval,
 0,
 1,
 100,
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS'),
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- Account 2
INSERT INTO `accounts`
(`id`, `version`, `accountOwnerId`, `balanceMinor`, `dateCreated`, `dateUpdated`)
VALUES
(`account_sequence`.nextval,
 0,
 2,
 200,
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS'),
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- Account 3
INSERT INTO `accounts`
(`id`, `version`, `accountOwnerId`, `balanceMinor`, `dateCreated`, `dateUpdated`)
VALUES
(`account_sequence`.nextval,
 0,
 3,
 -300,
 parsedatetime('01-01-2000 00:00:03.000', 'dd-MM-yyyy hh:mm:ss.SSS'),
 parsedatetime('01-01-2000 00:00:03.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- Transfer 1
INSERT INTO `transfers`
(`id`, `originAccountId`, `destinationAccountId`, `amountMinor`, `dateCreated`)
VALUES
(`transfer_sequence`.nextval,
 3,
 1,
 100,
 parsedatetime('01-01-2000 00:00:01.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);

-- Transfer 2
INSERT INTO `transfers`
(`id`, `originAccountId`, `destinationAccountId`, `amountMinor`, `dateCreated`)
VALUES
(`transfer_sequence`.nextval,
 3,
 2,
 200,
 parsedatetime('01-01-2000 00:00:02.000', 'dd-MM-yyyy hh:mm:ss.SSS')
);