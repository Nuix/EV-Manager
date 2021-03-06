/*
* This SQL script was generated using SQL Server Management studio after the tables were designed in that tool.
*/

USE [UserData]
GO

/****** Object:  Table [dbo].[IngestionHistory] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IngestionHistory](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[UserRecordID] [int] NOT NULL,
	[DateIngested] [datetime] NOT NULL,
	[CaseName] [varchar](200) NOT NULL,
	[CaseLocation] [varchar](255) NOT NULL,
 CONSTRAINT [PK__Ingestio__3214EC276DD2750F] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[UserAddress] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserAddress](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[UserRecordID] [int] NOT NULL,
	[RecordCreated] [datetime] NOT NULL,
	[RecordLastModified] [datetime] NULL,
	[Address] [varchar](1000) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[UserPhoneNumber] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserPhoneNumber](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[UserRecordID] [int] NOT NULL,
	[RecordCreated] [datetime] NOT NULL,
	[PhoneNumber] [varchar](30) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[UserRecord] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserRecord](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[EmployeeID] [varchar](100) NOT NULL,
	[Name] [varchar](100) NOT NULL,
	[Title] [varchar](100) NULL,
	[Department] [varchar](100) NULL,
	[Location] [varchar](100) NULL,
	[RecordCreated] [datetime] NOT NULL,
	[RecordLastModified] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[UserSID] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserSID](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[UserRecordID] [int] NOT NULL,
	[RecordCreated] [datetime] NOT NULL,
	[SID] [varchar](190) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Index [IX_IngestionHistory_UserRecordId] ******/
CREATE NONCLUSTERED INDEX [IX_IngestionHistory_UserRecordId] ON [dbo].[IngestionHistory]
(
	[UserRecordID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_UserAddress_UserRecordID] ******/
CREATE NONCLUSTERED INDEX [IX_UserAddress_UserRecordID] ON [dbo].[UserAddress]
(
	[UserRecordID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_UserPhoneNumber_UserRecordID] ******/
CREATE NONCLUSTERED INDEX [IX_UserPhoneNumber_UserRecordID] ON [dbo].[UserPhoneNumber]
(
	[UserRecordID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_UserRecord_Department] ******/
CREATE NONCLUSTERED INDEX [IX_UserRecord_Department] ON [dbo].[UserRecord]
(
	[Department] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_UserRecord_EmployeeID] ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_UserRecord_EmployeeID] ON [dbo].[UserRecord]
(
	[EmployeeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_UserRecord_Location] ******/
CREATE NONCLUSTERED INDEX [IX_UserRecord_Location] ON [dbo].[UserRecord]
(
	[Location] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_UserRecord_Name] ******/
CREATE NONCLUSTERED INDEX [IX_UserRecord_Name] ON [dbo].[UserRecord]
(
	[Name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_UserRecord_Title] ******/
CREATE NONCLUSTERED INDEX [IX_UserRecord_Title] ON [dbo].[UserRecord]
(
	[Title] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_UserSID_SID] ******/
CREATE NONCLUSTERED INDEX [IX_UserSID_SID] ON [dbo].[UserSID]
(
	[UserRecordID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[IngestionHistory] ADD  CONSTRAINT [DF__Ingestion__DateI__10566F31]  DEFAULT (sysdatetime()) FOR [DateIngested]
GO
ALTER TABLE [dbo].[UserAddress] ADD  DEFAULT (sysdatetime()) FOR [RecordCreated]
GO
ALTER TABLE [dbo].[UserPhoneNumber] ADD  DEFAULT (sysdatetime()) FOR [RecordCreated]
GO
ALTER TABLE [dbo].[UserRecord] ADD  DEFAULT (sysdatetime()) FOR [RecordCreated]
GO
ALTER TABLE [dbo].[UserSID] ADD  DEFAULT (sysdatetime()) FOR [RecordCreated]
GO
