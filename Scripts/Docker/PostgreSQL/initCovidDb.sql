/*
 * Terminating all the connections
 */

SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'coviditaly';

DROP DATABASE IF EXISTS COVIDITALY;


/*
 * Create the database
 */
CREATE DATABASE COVIDITALY;

/*
 * Select the Schema
 */
\c coviditaly;

/*
 * Table that contains the list of natial data
 */
CREATE TABLE NATIONAL_DATA (
    DATE_DATA                   DATE                        NOT NULL PRIMARY KEY,
    NEW_INFECTIONS              INTEGER         DEFAULT 0   NOT NULL,
    NEW_TESTS                   INTEGER         DEFAULT 0   NOT NULL,
    NEW_CASUALTIES              INTEGER         DEFAULT 0   NOT NULL,
    NEW_HOSPITALIZED            INTEGER         DEFAULT 0   NOT NULL,
    NEW_INTENSIVE_THERAPY       INTEGER         DEFAULT 0   NOT NULL,
    NEW_RECOVERED               INTEGER         DEFAULT 0   NOT NULL,
    INFECTION_PERC              DECIMAL( 7, 2)  DEFAULT 0   NOT NULL,
    CASUALTIES_PERC             DECIMAL( 7, 2)  DEFAULT 0   NOT NULL
);

CREATE TABLE REGIONAL_DATA (
    DATE_DATA                   DATE                        NOT NULL,
    REGION_CODE                 VARCHAR(3)      DEFAULT ''  NOT NULL,
    NEW_INFECTIONS              INTEGER         DEFAULT 0   NOT NULL,
    NEW_TESTS                   INTEGER         DEFAULT 0   NOT NULL,
    NEW_CASUALTIES              INTEGER         DEFAULT 0   NOT NULL,
    NEW_HOSPITALIZED            INTEGER         DEFAULT 0   NOT NULL,
    NEW_INTENSIVE_THERAPY       INTEGER         DEFAULT 0   NOT NULL,
    NEW_RECOVERED               INTEGER         DEFAULT 0   NOT NULL,
    INFECTION_PERC              DECIMAL( 7, 2)  DEFAULT 0   NOT NULL,
    CASUALTIES_PERC             DECIMAL( 7, 2)  DEFAULT 0   NOT NULL,
    PRIMARY KEY (DATE_DATA, REGION_CODE)
);

CREATE TABLE PROVINCE_DATA (
    DATE_DATA                   DATE                        NOT NULL,
    REGION_CODE                 VARCHAR(3)      DEFAULT ''  NOT NULL,
    PROVINCE_CODE               VARCHAR(3)      DEFAULT ''  NOT NULL,
    REGION_DESC                 VARCHAR(100)    DEFAULT ''  NOT NULL,
    DESCRIPTION                 VARCHAR(100)    DEFAULT ''  NOT NULL,
    PROVINCE_SHORT              VARCHAR(2)      DEFAULT ''  NOT NULL,
    NEW_INFECTIONS              INTEGER         DEFAULT 0   NOT NULL,
    PRIMARY KEY(DATE_DATA, REGION_CODE, PROVINCE_CODE)
);

CREATE TABLE VACCINI_CONSEGNE (
    REGION_CODE                 VARCHAR(3)      DEFAULT ''  NOT NULL,
    DATE_DATA                   DATE                        NOT NULL,
    SUPPLIER                    VARCHAR(100)    DEFAULT ''  NOT NULL,
    DOSES_DELIVERED             INTEGER         DEFAULT 0   NOT NULL,
    PRIMARY KEY(REGION_CODE, DATE_DATA, SUPPLIER)
);

CREATE TABLE SOMMINISTRAZIONI_VACCINI (
    REGION_CODE                 VARCHAR(3)      DEFAULT ''  NOT NULL,
    DATE_DATA                   DATE                        NOT NULL,
    SUPPLIER                    VARCHAR(100)    DEFAULT ''  NOT NULL,
    AGE_RANGE                   VARCHAR(10)     DEFAULT ''  NOT NULL,
    MEN_COUNTER                 INTEGER         DEFAULT 0   NOT NULL,
    WOMEN_COUNTER               INTEGER         DEFAULT 0   NOT NULL,
    NHS_PEOPLE_COUNTER          INTEGER         DEFAULT 0   NOT NULL,
    NON_NHS_PEOPLE_COUNTER      INTEGER         DEFAULT 0   NOT NULL,
    NURSING_HOME_COUNTER        INTEGER         DEFAULT 0   NOT NULL,
    AGE_60_69_COUNTER           INTEGER         DEFAULT 0   NOT NULL,
    AGE_70_79_COUNTER           INTEGER         DEFAULT 0   NOT NULL,
    OVER_80_COUNTER             INTEGER         DEFAULT 0   NOT NULL,
    PUBLIC_ORDER_COUNTER        INTEGER         DEFAULT 0   NOT NULL,
    SCHOOL_STAFF_COUNTER        INTEGER         DEFAULT 0   NOT NULL,
    FRAGILE_PEOPLE_COUNTER      INTEGER         DEFAULT 0   NOT NULL,
    OTHER_PEOPLE_COUNTER        INTEGER         DEFAULT 0   NOT NULL,
    FIRST_DOSE_COUNTER          INTEGER         DEFAULT 0   NOT NULL,
    SECOND_DOSE_COUNTER	        INTEGER         DEFAULT 0   NOT NULL,
    MONO_DOSE_COUNTER           INTEGER         DEFAULT 0   NOT NULL,
    PRIMARY KEY(REGION_CODE, DATE_DATA, SUPPLIER, AGE_RANGE)
);

CREATE TABLE ITALIAN_POPULATION (
    YEAR                        NUMERIC( 4, 0)  DEFAULT 0   NOT NULL,
    AGE                         NUMERIC( 3, 0)  DEFAULT 0   NOT NULL,
    GENDER                      VARCHAR(10)     DEFAULT ''  NOT NULL,
    COUNTER                     INTEGER         DEFAULT 0   NOT NULL,
    PRIMARY KEY(YEAR, AGE, GENDER)
);