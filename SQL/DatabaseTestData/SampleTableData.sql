/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [ID]
      ,[EmployeeID]
      ,[Name]
      ,[Title]
      ,[Department]
      ,[Location]
      ,[RecordCreated]
      ,[RecordLastModified]
  FROM [UserData].[dbo].[UserRecord]
  GO

  SELECT TOP (1000) [ID]
      ,[UserRecordID]
      ,[RecordCreated]
      ,[RecordLastModified]
      ,[Address]
  FROM [UserData].[dbo].[UserAddress]
  GO

  SELECT TOP (1000) [ID]
      ,[UserRecordID]
      ,[RecordCreated]
      ,[PhoneNumber]
  FROM [UserData].[dbo].[UserPhoneNumber]
  GO

  SELECT TOP (1000) [ID]
      ,[UserRecordID]
      ,[RecordCreated]
      ,[SID]
  FROM [UserData].[dbo].[UserSID]
  GO