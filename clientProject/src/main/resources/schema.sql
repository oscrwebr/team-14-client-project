create schema if not exists nhs_recruitment;

use nhs_recruitment;

SET FOREIGN_KEY_CHECKS = 0;
drop table if exists communicationLogs;
drop table if exists communicationslogs;
drop table if exists systemLogs;

drop table if exists applicantpreferences;
drop table if exists applicationdetails;


drop table if exists users;
drop table if exists applicants;

drop table if exists deletedApplicants;
drop table if exists deletedUsers;

SET FOREIGN_KEY_CHECKS = 1;



-- User Table
create table if not exists users(
                                    ID          int auto_increment primary key,
                                    username        varchar(50) not null unique,
                                    passwordHashed   varchar(128) not null ,
                                    firstName       varchar(128) not null,
                                    lastName        varchar(128) not null,
                                    role            enum('admin', 'recruiter') not null,
                                    lastLogin       timestamp,
                                    createdAt        timestamp default current_timestamp
) engine = InnoDB;

-- Applicant Table
create table if not exists applicants(
                                         Id             int auto_increment primary key,
                                         firstName       varchar(128) not null ,
                                         lastName        varchar(128) not null ,
                                         location        varchar(100) not null ,
                                         email           varchar(100) not null unique,
                                         phoneNumber     varchar(15),
                                         eventAttended   varchar(100) not null,
                                         skill           text,
                                         createdAt        timestamp default current_timestamp,
                                         updatedAt        timestamp default current_timestamp on update current_timestamp
) engine = InnoDB;

-- Application Details Table
create table if not exists applicationDetails(
                                                 Id           int auto_increment primary key,
                                                 applicationId int,
                                                 currentPosition varchar(100),
                                                 status          enum('External','Internal') default 'External',
                                                 CvPath          longblob,-- Store CV PDF as binary data
                                                 CoverLetterPath longblob,-- Store Cover Letter PDF as binary data
                                                 createdAt        timestamp default current_timestamp,
                                                 updatedAt        timestamp default current_timestamp on update current_timestamp,
                                                 foreign key (applicationId) references applicants(Id) on delete cascade
) engine = InnoDB;

--  Applicant Preferences Table
create table if not exists applicantPreferences
(
    Id          int auto_increment primary key,
    applicationId int not null,
    SubscribeToNewsLetter enum('Yes', 'No') default 'No',
    SubscribeToBulletins enum('Yes', 'No') default 'No',
    SubscribeToJobUpdates enum('Yes', 'No') default 'No',
    foreign key (applicationId) references applicants(Id) on delete cascade
) engine = InnoDB;



-- logging table
create table if not exists communicationLogs(
                                                logId              int auto_increment primary key,
                                                applicantId        int null,
                                                userId         int not null DEFAULT 1,
                                                timestamp       timestamp default current_timestamp,
                                                userType        enum('admin', 'recruiter') not null DEFAULT 'recruiter',
                                                logType        enum('communication', 'detailChange') not null,
                                                communicationType enum('email', 'phone', 'text', 'person') null DEFAULT 'email',
                                                actionTaken enum('emailSent', 'applicantAdded', 'applicantRemoved', 'applicantDetailsChanged', 'note', 'other') not null,
                                                notes           varchar(225),
                                                foreign key (userId) references users(Id) on delete cascade,
                                                foreign key (applicantId) references applicants(Id) on delete cascade
) engine = InnoDB;

-- System Logs Table
create table if not exists systemLogs(
                                         systemLogId              int auto_increment primary key,
                                         userId          int not null,
                                         actionTaken          enum('login', 'logout', 'addedUser', 'removedUser', 'changedRole', 'other'),
                                         timestamp   datetime default current_timestamp
) engine = InnoDB;

DESCRIBE users;

-- Deleted Applicants Table - Temporary table to store deleted applicants for a period of time
-- Currently set to 30 seconds - easier to demonstrate the functionality to client
CREATE TABLE deletedApplicants (
                                   Id int auto_increment primary key,
                                   firstName varchar(128) not null,
                                   lastName varchar(128) not null,
                                   location varchar(100) not null,
                                   email varchar(100) not null unique,
                                   phoneNumber varchar(15),
                                   currentPosition varchar(100),
                                   status enum('External', 'Internal') default 'External',
                                   skill text,
                                   eventAttended varchar(100) not null,
                                   SubscribeToNewsLetter enum('Yes', 'No') default 'No',
                                   SubscribeToBulletins enum('Yes', 'No') default 'No',
                                   SubscribeToJobUpdates enum('Yes', 'No') default 'No',
                                   deletedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) engine = InnoDB;

SET GLOBAL event_scheduler = ON;

CREATE EVENT IF NOT EXISTS deleteApplicants
    ON SCHEDULE EVERY 30 SECOND
    DO
    DELETE FROM deletedApplicants WHERE deletedAt < NOW() - INTERVAL 30 SECOND;

create table if not exists deletedUsers(
                                           ID          int auto_increment primary key,
                                           username        varchar(50) not null unique,
                                           passwordHashed   varchar(128) not null ,
                                           firstName       varchar(128) not null,
                                           lastName        varchar(128) not null,
                                           role            enum('admin', 'recruiter') not null,
                                           lastLogin       timestamp,
                                           createdAt        timestamp default current_timestamp,
                                           deletedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP

) engine = InnoDB;

CREATE EVENT IF NOT EXISTS deleteUsers
    ON SCHEDULE EVERY 30 SECOND
    DO
    DELETE FROM deletedUsers WHERE deletedAt < NOW() - INTERVAL 30 SECOND;