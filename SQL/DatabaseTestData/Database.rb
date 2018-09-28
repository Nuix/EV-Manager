require 'java'
java_import java.sql.Statement

# This class wraps a transaction for bulk insertions
class DatabaseInsertBatch
	def initialize(database,sql,max_pending=nil)
		@max_pending = max_pending
		@database = database
		@pending = 0
		@connection = @database.create_connection
		@connection.setAutoCommit(false)
		@statement = @connection.prepareStatement(sql) if !sql.nil?
		@statement_cache = {}
	end

	# This method will commit whatever is present currently
	# in the transaction
	def commit
		@connection.commit
	end

	# Inserts some data against the transaction
	# If @max_pending is not nil, will auto commit if
	# the pending insert count is greater than @max_pending
	def insert(data,sql=nil)
		if sql.nil?
			@database.bind_data(@statement,data)
			@statement.executeUpdate
		else
			statement = @statement_cache[sql]
			if statement.nil?
				statement = @statement_cache[sql] = @connection.prepareStatement(sql)
			end
			@database.bind_data(statement,data)
			statement.executeUpdate
		end
		@pending += 1
		if @max_pending && @pending > @max_pending
			commit
			@pending = 0
		end
	end

	# Ends this transaction and closes connection
	def end
		commit
		@connection.close
	end
end

# This generically represents databases, provides most common
# functionality, not to be instantiated directly, instead create
# instances of classes that derive from this
class Database
	# Create a connection, must be overidden in derived classes
	def create_connection
		raise "Derived class must provide create_connection method"
	end

	# Creates an insert batch class (transaction) against
	# this database object
	def create_insert_batch(sql,max_pending=nil)
		return DatabaseInsertBatch.new(self,sql,max_pending)
	end

	# Creates and closes an insert batch (transaction) for
	# a provided block
	# max_pending dictates how often to auto commit when provided
	def batch_insert(sql,max_pending=nil,&block)
		batch = create_insert_batch(sql,max_pending)
		yield batch
	ensure
		batch.end
	end

	# Exceutes a basic update statement
	# data is bound to prepared statement when provided
	def update(sql,data=nil)
		connection = create_connection
		statement = connection.prepareStatement(sql)
		bind_data(statement,data)
		statement.executeUpdate
	rescue => exc
		raise
	ensure
		connection.close if !connection.nil?
	end

	# Executes an update statement and returns the
	# newly created key, if there was one
	# data is bound to prepared statement when provided
	def insert(sql,data=nil)
		inserted_key = nil
		connection = nil
		connection = create_connection
		statement = connection.prepareStatement(sql,Statement::RETURN_GENERATED_KEYS)
		bind_data(statement,data)
		statement.executeUpdate
		result_set = statement.getGeneratedKeys
		if !result_set.nil? && result_set.next
			insert_key = get_result_column_value(result_set,1)
		end
		return insert_key
	rescue => exc
		raise
	ensure
		connection.close if !connection.nil?
	end

	# Executes a scalar query (single result)
	# data is bound to prepared statement when provided
	def scalar(sql,data=nil)
		connection = create_connection
		statement = connection.prepareStatement(sql)
		bind_data(statement,data)
		result_set = statement.executeQuery
		if !result_set.nil? && result_set.next
			return get_result_column_value(result_set,1)
		end
	rescue => exc
		raise
	ensure
		connection.close if !connection.nil?
	end

	# Runs a select query
	# data is bound to prepared statement when provided
	# hashed, true will return records as hash of COLUMN_NAME => value,
	#  otherwise each record will be an array of values
	# when provided a block will yield each record in turn, without a
	# block will return array of results
	def query(sql,data=nil,hashed=true,&block)
		connection = nil
		connection = create_connection
		statement = connection.prepareStatement(sql,Statement::RETURN_GENERATED_KEYS)
		bind_data(statement,data)
		result_set = statement.executeQuery
		column_metadata = result_set.getMetaData
		column_count = column_metadata.getColumnCount
		column_names = (1..column_count).map{|c|column_metadata.getColumnName(c)}
		records = []
		while result_set.next
			values = []
			(1..column_count).each do |c|
				values << get_result_column_value(result_set,c,column_metadata)
			end
			record = nil
			if !hashed
				record = values
			else
				hashed_values = {}
				column_names.each_with_index do |name,column_index|
					hashed_values[name] = values[column_index]
				end
				record = hashed_values
			end
			if block_given?
				yield record
			else
				records << record
			end
		end
		return records if !block_given?
	rescue => exc
		raise
	ensure
		connection.close if !connection.nil?
	end

	# Helper method for obtaining value from a result set for a given column
	def get_result_column_value(result_set,column_index,column_metadata=nil)
		column_metadata ||= result_set.getMetaData
		value = nil
		case column_metadata.getColumnTypeName(column_index).downcase
		when "integer", "numeric"
			value = result_set.getLong(column_index)
		when "float"
			value = result_set.getFloat(column_index)
		when "double"
			value = result_set.getDouble(column_index)
		when "blob", "binary"
			value = result_set.getBytes(column_index).to_s
		else
			value = result_set.getString(column_index)
		end
		return value
	end

	# Binds data to a prepared statement
	def bind_data(statement,data)
		if !data.nil?
			data.to_enum.with_index(1) do |value,index|
				if value.nil?
					statement.setNull(index,java.sql.Types::NULL)
					next
				end

				case value
				when Fixnum
					statement.setInt(index,value)
				when Bignum
					statement.setLong(index,value)
				when Float
					statement.setDouble(index,value)
				when TrueClass, FalseClass
					statement.setBoolean(index,value)
				when Time
					statement.setTimestamp(index,java.sql.Timestamp.new(value.to_i*1000))
				when Java::byte[]
					statement.setBytes(index,value)
				when java.sql.Date
					statement.setDate(index,value)
				when java.sql.Timestamp
					statement.setTimestamp(index,value)
				when String
					#ASCII-8BIT encoded String is essentially Ruby byte array
					if value.encoding.name == "ASCII-8BIT"
						statement.setBytes(index,value.to_java_bytes)
					else
						statement.setString(index,value)
					end
				else
					statement.setString(index,value.to_s)
				end
			end
		end
	end
end
