require 'java'

# This class provides connectivity to SQL Server databases
class SQLServer < Database
	attr_accessor :location
	attr_accessor :port
	attr_accessor :instance_name
	attr_accessor :database
	attr_accessor :user
	attr_accessor :pass

	# settings hash contains various connection values
	# rough guess defaults are provided
	# nil values will typically exclude option from connection string
	def initialize(settings={})
		defaults = {
			:location => "localhost",
			:port => nil,
			:instance_name => nil,
			:database => nil,
			:domain => nil,
			:user => nil,
			:pass => nil,
		}
		settings = defaults.merge(settings)
		@location = settings[:location]
		@port = settings[:port]
		@instance_name = settings[:instance_name]
		@database = settings[:database]
		@domain = settings[:domain]
		@user = settings[:user]
		@pass = settings[:pass]
	end

	# Creates a SQL server specific connection
	def create_connection
		connection_string = "jdbc:jtds:sqlserver://"+location
		connection_string += ":#{@port}" if @port
		connection_string += "/#{@database}" if @database
		connection_string += ";instance=#{@instance_name}" if @instance_name
		connection_string += ";domain=#{@domain}" if @domain
		connection_string += ";user=#{@user}" if @user
		connection_string += ";password=#{@pass}" if @pass
		connection_string += ";zeroDateTimeBehavior=convertToNull"
		#puts "DEBUG - Connection String: #{connection_string}"
		connection = java.sql.DriverManager.getConnection(connection_string)
		return connection
	end
end
