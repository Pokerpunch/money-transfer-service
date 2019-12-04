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