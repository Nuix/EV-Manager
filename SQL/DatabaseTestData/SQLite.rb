require 'java'
java.sql.DriverManager.registerDriver(org.sqlite.JDBC.new())


# This class provides connectivity to SQLite database
class SQLite < Database
	attr_accessor :file

	# file is the db file to open/create
	def initialize(file,settings={})
		@file = file
		@settings = settings
	end

	# Provides SQLite specific connections
	def create_connection()
		connection_properties = java.util.Properties.new
		@settings.each do |k,v|
			connection_properties[k] = v
		end
		return java.sql.DriverManager.getConnection("jdbc:sqlite:#{@file}",connection_properties)
	end

	# Binds data to prepared statement, SQLite has subtle differences
	# so must implement its own version from that in class Database
	def bind_data(statement,data)
		if !data.nil?
			data.to_enum.with_index(1) do |value,index|
				case value
				when Fixnum, Bignum
					statement.setLong(index,value)
				when Float
					#Ruby floats are double precision
					statement.setDouble(index,value)
				when TrueClass, FalseClass
					statement.setBoolean(index,value)
				when String
					statement.setString(index,value)
				when Java::byte[]
					statement.setBytes(index,value)
				else
					statement.setString(index,value.to_s)
				end
			end
		end
	end
end