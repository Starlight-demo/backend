--liquibase formatted sql
--changeset kate:inset-sponsor-06 splitStatements:true endDelimiter:;
INSERT INTO sponsor (full_name,email,password,avatar)
VALUES ('Maksym Khudoliy','zaxaqueiboigreu-5997@gmail.com','$2a$10$A40TjVC8xC1tC7uChck0BOWVxgMKY..7sfG2.YHrjkrG2GLSy4YLW','https://drive.google.com/file/d/14KHHoX72gArjYtbdIeFrE6y3qjiW08YH/view?usp=share_link');
INSERT INTO sponsor (full_name,email,password,avatar)
VALUES ('Alex Lee','faulleureheiyei-5854@gmail.com','$2a$10$A40TjVC8xC1tC7uChck0BOWVxgMKY..7sfG2.YHrjkrG2GLSy4YLW',null);

-- changeset serhii:11
UPDATE sponsor SET status = 'ACTIVE';

-- changeset serhii:12
UPDATE delayed_delete SET deleting_entity_type = 'SPONSOR';
