/**
 * TestDB objects.
 */
CREATE DATABASE [TestDB];
GO
USE [TestDB];
GO
-- Enable trustworthy so views in other databases can use this database.
ALTER DATABASE [TestDB] SET TRUSTWORTHY ON;
GO
CREATE LOGIN [tester] WITH PASSWORD=N'P@$$w0rd', DEFAULT_DATABASE=[master], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON
GO
CREATE USER [tester] FOR LOGIN [tester] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [tester]
GO
CREATE SCHEMA [Source]
GO
CREATE SCHEMA [Target]
GO

IF OBJECT_ID('[dbo].[CUST_CLASS_SAT]') IS NOT NULL
	DROP TABLE [dbo].[CUST_CLASS_SAT] 

CREATE TABLE [dbo].[CUST_CLASS_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_REGION] [varchar](100) NULL,
	[CUST_CLASS] [varchar](20) NULL,
	[CUST_RATING] [decimal](3, 1) NULL
)

IF OBJECT_ID('[dbo].[CUST_HUB]') IS NOT NULL
	DROP TABLE [dbo].[CUST_HUB] 

CREATE TABLE [dbo].[CUST_HUB](
	[CUST_ID] [int] NOT NULL,
	[CREATE_DD] [date] NULL
)
GO

IF OBJECT_ID('[dbo].[CUST_SAT]') IS NOT NULL
	DROP TABLE [dbo].[CUST_SAT] 

CREATE TABLE [dbo].[CUST_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_NAME] [varchar](100) NULL,
	[CUST_DOB] [date] NULL,
	[CUST_LANG] [char](5) NULL
)
GO

IF OBJECT_ID('[dbo].[DIM_Product]') IS NOT NULL
	DROP TABLE [dbo].[DIM_Product] 

CREATE TABLE [dbo].[DIM_Product](
	[ProductID] [int] NOT NULL,
	[ProductGroup] [varchar](10) NULL,
	[ProductCategory] [varchar](20) NULL
)
GO

IF OBJECT_ID('[Source].[AB_RH_INS_POLCY]') IS NOT NULL
	DROP TABLE [Source].[AB_RH_INS_POLCY] 

CREATE TABLE [Source].[AB_RH_INS_POLCY](
	[INS_POLCY_SQN] [int] NULL,
	[INS_POLCY_BK] [varchar](10) NULL
)
GO

IF OBJECT_ID('[Source].[CUST_CLASS_SAT]') IS NOT NULL
	DROP TABLE [Source].[CUST_CLASS_SAT]

CREATE TABLE [Source].[CUST_CLASS_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_REGION] [varchar](100) NULL,
	[CUST_CLASS] [varchar](20) NULL,
	[CUST_RATING] [decimal](3, 1) NULL
)
GO

IF OBJECT_ID('[Source].[CUST_HUB]') IS NOT NULL
	DROP TABLE [Source].[CUST_HUB]

CREATE TABLE [Source].[CUST_HUB](
	[CUST_ID] [int] NOT NULL,
	[CREATE_DD] [date] NULL
)
GO

IF OBJECT_ID('[Source].[CUST_SAT]') IS NOT NULL
	DROP TABLE [Source].[CUST_SAT]

CREATE TABLE [Source].[CUST_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_NAME] [varchar](100) NULL,
	[CUST_DOB] [date] NULL,
	[CUST_LANG] [char](5) NULL
)
GO

IF OBJECT_ID('[Source].[Customer]') IS NOT NULL
	DROP TABLE [Source].[Customer]

CREATE TABLE [Source].[Customer](
	[Customer_ID] [bigint] NOT NULL,
	[Customer_Name] [varchar](100) NULL,
	[Country] [varchar](10) NULL,
	[Customer_class] [Char](25) NULL,
	[IsActive] bit NOT NULL default 1,
	PRIMARY KEY CLUSTERED ([Customer_ID] ASC)
)
GO

IF OBJECT_ID('[Target].[Customer]') IS NOT NULL
	DROP TABLE [Target].[Customer]

CREATE TABLE [Target].[Customer](
	[Customer_ID] [bigint] NOT NULL,
	[Customer_Name] [varchar](100) NULL,
	[Country] [varchar](10) NULL,
	PRIMARY KEY CLUSTERED ([Customer_ID] ASC)
)
GO

IF OBJECT_ID('[Source].[Table with strangé character$]') IS NOT NULL
	DROP TABLE [Source].[Table with strangé character$]

CREATE TABLE [Source].[Table with strangé character$](
	[ID] [int] NOT NULL,
	[Fie#ld with \Strange namë] [varchar](100) NULL,
	PRIMARY KEY CLUSTERED ([ID] ASC)
)
GO

IF OBJECT_ID('[Source].[DataTypeTest]') IS NOT NULL
	DROP TABLE [Source].[DataTypeTest]

CREATE TABLE [Source].[DataTypeTest](
	[Test_BigInt] [bigint] NULL,
	[Test_Boolean] BIT NULL,
	[Test_Varchar] [varchar](100) NULL,
	[Test_Char] [Char](25) NULL,
	[Test_Date] [Date] NULL,
	[Test_Decimal] [DECIMAL](23,2) NULL,
	[Test_BigDecimal] [DECIMAL](20,19) NULL,
	[Test_FloatSmall] [Float](24) NULL,
	[Test_FloatBig] [Float](26) NULL,
	[Test_Real] [Real] NULL
)
GO

IF OBJECT_ID('[Source].[plain_table]') IS NOT NULL
	DROP TABLE [Source].plain_table

CREATE TABLE [Source].plain_table(
	TextColumn varchar(100)
)
GO

IF OBJECT_ID('[Source].[singleprefix_table]') IS NOT NULL
	DROP TABLE [Source].[singleprefix_table]

CREATE TABLE [Source].[singleprefix_table](
	TextColumn varchar(100)
)
GO

IF OBJECT_ID('[Source].[table_singlesuffix]') IS NOT NULL
	DROP TABLE [Source].table_singlesuffix

CREATE TABLE [Source].table_singlesuffix(
	TextColumn varchar(100)
)
GO

IF OBJECT_ID('[Source].[parentprefix_childprefix_table]') IS NOT NULL
	DROP TABLE [Source].parentprefix_childprefix_table

CREATE TABLE [Source].parentprefix_childprefix_table(
	TextColumn varchar(100)
)
GO


IF OBJECT_ID('[Source].[table_childsuffix]') IS NOT NULL
	DROP TABLE [Source].table_childsuffix

CREATE TABLE [Source].table_childsuffix(
	TextColumn varchar(100)
)
GO

IF OBJECT_ID('[Source].[childprefix_table]') IS NOT NULL
	DROP TABLE [Source].childprefix_table

CREATE TABLE [Source].childprefix_table(
	TextColumn varchar(100)
)
GO

/**
 * TestOtherDB objects.
 */
CREATE DATABASE [TestOtherDB];
GO
USE [TestOtherDB];
GO
CREATE USER [tester] FOR LOGIN [tester] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [tester]
GO
CREATE VIEW [dbo].[vw_Customer]
AS
SELECT * FROM [TestDB].[source].[Customer];
GO

/**
 * TestDB1 objects.
 */
CREATE DATABASE [TestDB1]
GO
USE [TestDB1];
GO
create table test1
 	(test1field varchar(100));
GO
CREATE USER [tester] FOR LOGIN [tester] WITH DEFAULT_SCHEMA=[dbo]
GO
 ALTER ROLE [db_owner] ADD MEMBER [tester]
 GO

/**
 * TestDB2 objects.
 */
CREATE DATABASE [TestDB2]
GO
USE [TestDB2];
GO
create table test2
 	(test2field varchar(100));
GO
CREATE USER [tester] FOR LOGIN [tester] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [tester]
GO