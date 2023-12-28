--liquibase formatted sql
--changeset sasha:inset-kudos-07
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,1,2,'2001-09-14 22:11:12', 50);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,2,3,'2002-09-14 22:11:12', 150);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,3,4,'2003-09-14 22:11:12', 30);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,1,2,'2004-09-14 22:11:12', 40);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,1,2,'2005-09-14 22:11:12', 70);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,1,2,'2006-09-14 22:11:12', 60);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,1,2,'2007-09-14 22:11:12', 10);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,1,2,'2008-09-14 22:11:12', 5);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,1,2,'2009-09-14 22:11:12', 56);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,1,2,'2010-09-14 22:11:12', 12);

-- changeset sasha:9
DELETE FROM kudos;

INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,1,1,'2001-09-14 22:11:12', 50);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,2,1,'2002-09-14 22:11:12', 150);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (1,3,2,'2003-09-14 22:11:12', 30);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,1,1,'2004-09-14 22:11:12', 40);
INSERT INTO kudos (sponsor_id,proof_id,follower_id,create_data, count_kudos)
VALUES (2,2,1,'2005-09-14 22:11:12', 70);
