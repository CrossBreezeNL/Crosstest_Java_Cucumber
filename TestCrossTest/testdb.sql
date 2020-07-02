USE [TestDB]
GO
/****** Object:  User [tester]    Script Date: 2020-04-16 10:11:04 PM ******/
CREATE USER [tester] FOR LOGIN [tester] WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [tester]
GO
/****** Object:  Schema [Source]    Script Date: 2020-04-16 10:11:04 PM ******/
CREATE SCHEMA [Source]
GO
/****** Object:  Schema [Target]    Script Date: 2020-04-16 10:11:04 PM ******/
CREATE SCHEMA [Target]
GO
/****** Object:  Table [dbo].[CUST_CLASS_SAT]    Script Date: 2020-04-16 10:11:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[dbo].[CUST_CLASS_SAT]') IS NOT NULL
	DROP TABLE [dbo].[CUST_CLASS_SAT] 

CREATE TABLE [dbo].[CUST_CLASS_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_REGION] [varchar](100) NULL,
	[CUST_CLASS] [varchar](20) NULL,
	[CUST_RATING] [decimal](3, 1) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CUST_HUB]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[dbo].[CUST_HUB]') IS NOT NULL
	DROP TABLE [dbo].[CUST_HUB] 

CREATE TABLE [dbo].[CUST_HUB](
	[CUST_ID] [int] NOT NULL,
	[CREATE_DD] [date] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CUST_SAT]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[dbo].[CUST_SAT]') IS NOT NULL
	DROP TABLE [dbo].[CUST_SAT] 

CREATE TABLE [dbo].[CUST_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_NAME] [varchar](100) NULL,
	[CUST_DOB] [date] NULL,
	[CUS_LANG] [char](5) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DIM_Product]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[dbo].[DIM_Product]') IS NOT NULL
	DROP TABLE [dbo].[DIM_Product] 

CREATE TABLE [dbo].[DIM_Product](
	[ProductID] [int] NOT NULL,
	[ProductGroup] [varchar](10) NULL,
	[ProductCategory] [varchar](20) NULL
) ON [PRIMARY]
GO

/****** Object:  Table [Source].[AB_RH_INS_POLCY]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Source].[AB_RH_INS_POLCY]') IS NOT NULL
	DROP TABLE [Source].[AB_RH_INS_POLCY] 

CREATE TABLE [Source].[AB_RH_INS_POLCY](
	[INS_POLCY_SQN] [int] NULL,
	[INS_POLCY_BK] [varchar](10) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [Source].[CUST_CLASS_SAT]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Source].[CUST_CLASS_SAT]') IS NOT NULL
	DROP TABLE [Source].[CUST_CLASS_SAT]

CREATE TABLE [Source].[CUST_CLASS_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_REGION] [varchar](100) NULL,
	[CUST_CLASS] [varchar](20) NULL,
	[CUST_RATING] [decimal](3, 1) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [Source].[CUST_HUB]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Source].[CUST_HUB]') IS NOT NULL
	DROP TABLE [Source].[CUST_HUB]

CREATE TABLE [Source].[CUST_HUB](
	[CUST_ID] [int] NOT NULL,
	[CREATE_DD] [date] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [Source].[CUST_SAT]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Source].[CUST_SAT]') IS NOT NULL
	DROP TABLE [Source].[CUST_SAT]

CREATE TABLE [Source].[CUST_SAT](
	[CUST_ID] [int] NOT NULL,
	[CUST_NAME] [varchar](100) NULL,
	[CUST_DOB] [date] NULL,
	[CUS_LANG] [char](5) NULL
) ON [PRIMARY]
GO
/****** Object:  Table [Source].[Customer]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Source].[Customer]') IS NOT NULL
	DROP TABLE [Source].[Customer]

CREATE TABLE [Source].[Customer](
	[Customer_ID] [bigint] NOT NULL,
	[Customer_Name] [varchar](100) NULL,
	[Country] [varchar](10) NULL,
	[Customer_class] [Char](25) NULL,
	[IsActive] bit NOT NULL default 1,
PRIMARY KEY CLUSTERED 
(
	[Customer_ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [Target].[Customer]    Script Date: 2020-04-16 10:11:05 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

IF OBJECT_ID('[Target].[Customer]') IS NOT NULL
	DROP TABLE [Target].[Customer]

CREATE TABLE [Target].[Customer](
	[Customer_ID] [bigint] NOT NULL,
	[Customer_Name] [varchar](100) NULL,
	[Country] [varchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[Customer_ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

IF OBJECT_ID('[Source].[Table with strangé character$]') IS NOT NULL
	DROP TABLE [Source].[Table with strangé character$]

CREATE TABLE [Source].[Table with strangé character$](
	[ID] [int] NOT NULL,
	[Fie#ld with \Strange namë] [varchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO