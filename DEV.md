# Development information

Downloaded oracle JDBC to "./local/ojdbc7-12_1_0_1.jar" which has been excluded from repo because of licencing issues.
ojdbc.jar file has been added in IntelliJ as additional library.
http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html

Creating a test database with docker:

    docker run -d -p 49160:22 -p 49161:1521 -e ORACLE_ALLOW_REMOTE=true wnameless/oracle-xe-11g

connection parameters
            
    String databaseURL = "jdbc:oracle:thin:@localhost:49161:XE";
    String dbUsername = "system";
    String dbPassword = "oracle";

creating a test table

    CREATE TABLE mon_departments(
      ID    NUMBER(10)      NOT NULL,
      NAME  VARCHAR2(60)    NOT NULL
    );
    
    ALTER TABLE mon_departments ADD(
      CONSTRAINT PK_departments PRIMARY KEY (ID)
    );
    
    CREATE SEQUENCE departments_seq START WITH 1;
    
    CREATE OR REPLACE TRIGGER departments_bir
    BEFORE INSERT ON mon_departments
    FOR EACH ROW
    BEGIN
      SELECT  departments_seq.NEXTVAL
      INTO    :new.id
      FROM    dual;
    END;
    /
    
    CREATE TABLE mon_users(
      ID    NUMBER(10)      NOT NULL,
      LNAME VARCHAR2(60)    NOT NULL,
      FNAME VARCHAR2(60),
      DEPARTMENT NUMBER(10)
    );
    
    ALTER TABLE mon_users ADD(
      CONSTRAINT PK_users PRIMARY KEY (ID)
    );
    ALTER TABLE mon_users ADD(
      CONSTRAINT FK_users_dept FOREIGN KEY (DEPARTMENT) REFERENCES mon_departments(ID)
    );
    
    CREATE SEQUENCE users_seq START WITH 1;
    
    CREATE OR REPLACE TRIGGER users_bir
    BEFORE INSERT ON mon_users
    FOR EACH ROW
    BEGIN
      SELECT  users_seq.NEXTVAL
      INTO    :new.id
      FROM    dual;
    END;
    /
    
    INSERT ALL
      INTO "SYSTEM"."MON_DEPARTMENTS" (NAME) VALUES ('HR')
      INTO "SYSTEM"."MON_DEPARTMENTS" (NAME) VALUES ('IT')
      INTO "SYSTEM"."MON_DEPARTMENTS" (NAME) VALUES ('GF')
    SELECT 1 FROM DUAL;
    
    INSERT ALL
      INTO "SYSTEM"."MON_USERS" (LNAME, FNAME, DEPARTMENT) VALUES ('Mustermann', 'Max', '1')
      INTO "SYSTEM"."MON_USERS" (LNAME, FNAME, DEPARTMENT) VALUES ('Musterfrau', 'Monika', '2')
      INTO "SYSTEM"."MON_USERS" (LNAME, FNAME, DEPARTMENT) VALUES ('Lancome', 'Jennifer', '2')
      INTO "SYSTEM"."MON_USERS" (LNAME, FNAME, DEPARTMENT) VALUES ('Maxfactor', 'Heinz', '2')
      INTO "SYSTEM"."MON_USERS" (LNAME, FNAME, DEPARTMENT) VALUES ('Mustermann', 'Dmitri', '3')
    SELECT 1 FROM DUAL;
    
    /*
    CLEAN/REMOVE TABLES
    
    drop table "SYSTEM"."MON_DEPARTMENTS" cascade constraints PURGE;
    drop table "SYSTEM"."MON_USERS" cascade constraints PURGE;
    drop sequence "SYSTEM"."USERS_SEQ";
    drop sequence "SYSTEM"."DEPARTMENTS_SEQ";
    */

    /*
    Example Select STATEMENT
    SELECT COUNT(*) AS NUM_USERS_IT, ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400)) AS TS FROM SYSTEM.MON_USERS USERS JOIN SYSTEM.MON_DEPARTMENTS DEPT ON USERS.DEPARTMENT = DEPT.ID WHERE DEPT.name LIKE 'IT';
    */
        
