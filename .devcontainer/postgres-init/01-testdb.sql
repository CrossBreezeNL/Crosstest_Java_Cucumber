/**
 * PostgreSQL equivalent of TestCrossTest/testdb.sql (SQL Server).
 * Runs once when the postgres-data volume is first created, as user "tester" on database "testdb".
 *
 * Schema names are lower case (matching the schema attribute in XTestConfig.xml).
 * Table and column names are quoted to keep their exact case, since the DatabaseConfigs
 * use quoteObjectNames="true" (quoted identifiers are case-sensitive in PostgreSQL).
 */
CREATE SCHEMA IF NOT EXISTS source;
CREATE SCHEMA IF NOT EXISTS target;

-- public schema (equivalent of dbo).
CREATE TABLE public."CUST_CLASS_SAT" (
	"CUST_ID" int NOT NULL,
	"CUST_REGION" varchar(100) NULL,
	"CUST_CLASS" varchar(20) NULL,
	"CUST_RATING" decimal(3, 1) NULL
);

CREATE TABLE public."CUST_HUB" (
	"CUST_ID" int NOT NULL,
	"CREATE_DD" date NULL
);

CREATE TABLE public."CUST_SAT" (
	"CUST_ID" int NOT NULL,
	"CUST_NAME" varchar(100) NULL,
	"CUST_DOB" date NULL,
	"CUST_LANG" char(5) NULL
);

CREATE TABLE public."DIM_Product" (
	"ProductID" int NOT NULL,
	"ProductGroup" varchar(10) NULL,
	"ProductCategory" varchar(20) NULL
);

-- source schema.
CREATE TABLE source."AB_RH_INS_POLCY" (
	"INS_POLCY_SQN" int NULL,
	"INS_POLCY_BK" varchar(10) NULL
);

CREATE TABLE source."CUST_CLASS_SAT" (
	"CUST_ID" int NOT NULL,
	"CUST_REGION" varchar(100) NULL,
	"CUST_CLASS" varchar(20) NULL,
	"CUST_RATING" decimal(3, 1) NULL
);

CREATE TABLE source."CUST_HUB" (
	"CUST_ID" int NOT NULL,
	"CREATE_DD" date NULL
);

CREATE TABLE source."CUST_SAT" (
	"CUST_ID" int NOT NULL,
	"CUST_NAME" varchar(100) NULL,
	"CUST_DOB" date NULL,
	"CUST_LANG" char(5) NULL
);

CREATE TABLE source."Customer" (
	"Customer_ID" bigint NOT NULL PRIMARY KEY,
	"Customer_Name" varchar(100) NULL,
	"Country" varchar(10) NULL,
	"Customer_class" char(25) NULL,
	"IsActive" boolean NOT NULL DEFAULT true
);

CREATE TABLE source."Table with strangé character$" (
	"ID" int NOT NULL PRIMARY KEY,
	"Fie#ld with \Strange namë" varchar(100) NULL
);

-- Tables and columns named with reserved words (in SQL Server, PostgreSQL and Teradata), to test quoteObjectNames.
CREATE TABLE source."ORDER" (
	"CHECK" int NOT NULL,
	"USER" varchar(50) NULL,
	"GROUP" varchar(50) NULL
);

CREATE TABLE source."GROUP" (
	"CHECK" int NOT NULL
);

CREATE TABLE source."DataTypeTest" (
	"Test_BigInt" bigint NULL,
	"Test_Boolean" boolean NULL,
	"Test_Varchar" varchar(100) NULL,
	"Test_Char" char(25) NULL,
	"Test_Date" date NULL,
	"Test_Decimal" decimal(23, 2) NULL,
	"Test_BigDecimal" decimal(20, 19) NULL,
	"Test_FloatSmall" real NULL,
	"Test_FloatBig" double precision NULL,
	"Test_Real" real NULL
);

CREATE TABLE source.plain_table (TextColumn varchar(100));
CREATE TABLE source.singleprefix_table (TextColumn varchar(100));
CREATE TABLE source.table_singlesuffix (TextColumn varchar(100));
CREATE TABLE source.parentprefix_childprefix_table (TextColumn varchar(100));
CREATE TABLE source.table_childsuffix (TextColumn varchar(100));
CREATE TABLE source.childprefix_table (TextColumn varchar(100));

-- target schema.
CREATE TABLE target."Customer" (
	"Customer_ID" bigint NOT NULL PRIMARY KEY,
	"Customer_Name" varchar(100) NULL,
	"Country" varchar(10) NULL
);

/**
 * Additional databases used by the database context tests.
 * (The SQL Server TestOtherDB cross-database view has no PostgreSQL equivalent and is omitted.)
 */
CREATE DATABASE testdb1;
CREATE DATABASE testdb2;

\connect testdb1
CREATE TABLE test1 (test1field varchar(100));

\connect testdb2
CREATE TABLE test2 (test2field varchar(100));
