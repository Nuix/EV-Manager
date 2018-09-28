$target_user_count = 10_000

# Local Express Instance
connection_settings = {
	:location => "localhost",
	:port => nil,
	:instance_name => "SQLEXPRESS",
	:database => "UserData",
	:domain => nil,
	:user => nil,
	:pass => nil,
}

$script_directory = File.dirname(__FILE__)
load File.join($script_directory,"Database.rb")
load File.join($script_directory,"SQLite.rb")
load File.join($script_directory,"SQLServer.rb")

class FakePerson
	@@names = File.read(File.join($script_directory,"NameData.txt")).split("\n")
	@@title_prefixes = ["","Manager","Director","Chief Executive Officer"]
	@@titles = ["Information Technology","Sales","Product Development","Information Management","Janitorial Services",
		"Shipping","Receiving"]
	@@locations = ["Northern Office","Southern Office","Western Office","Eastern Office","Canada Office","Mexico Office","China Office","UK Office"]
	@@departments = ["Department 1","Department 2","Department 3","Department 4","Pre Sales","Post Sales","Covert Ops"]
	@@domains = ["CompanyXyz.com","CompanyXyz.biz","Company-XYZ.com","Company_Xyz.com"]

	def random_location
		last_location = @@locations.size - 1
		return @@locations[rand(0..last_location)]
	end

	def random_department
		last_department = @@departments.size - 1
		return @@departments[rand(0..last_department)]
	end

	def random_title
		last_prefix = @@title_prefixes.size - 1
		last_title = @@titles.size - 1
		
		prefix = @@title_prefixes[rand(0..last_prefix)]
		title = @@titles[rand(0..last_title)]

		if !prefix.empty?
			return "#{prefix} of #{title}"
		else
			return title
		end
	end

	def random_name
		last = @@names.size - 1
		return @@names[rand(0..last)].capitalize
	end

	def random_domain
		last_domain = @@domains.size - 1
		return @@domains[rand(0..last_domain)]
	end

	def random_phone_number
		return "#{rand(1..9)}-#{rand(100..999)}-#{rand(100..999)}-#{rand(1000..9999)}"
	end

	def random_sid
		#Example: S-1-5-21-1004336348-1177238915-682003330-512
		return "S-1-5-21-#{rand(1000000000..9999999999)}-#{rand(1000000000..9999999999)}-#{rand(100000000..999999999)}-#{rand(100..999)}"
	end

	def random_date(after=nil)
		year = 1000 * 60 * 60 * 24 * 365
		if after.nil?
			datemillis = rand(year*30..year*46)
			return java.sql.Timestamp.new(datemillis)
		else
			datemillis = after.getTime + rand(year*1..year*5)
			return java.sql.Timestamp.new(datemillis)
		end
	end

	attr_accessor :unique_id
	attr_accessor :title
	attr_accessor :location
	attr_accessor :department
	attr_accessor :first_name
	attr_accessor :last_name
	attr_accessor :emails
	attr_accessor :database_id
	attr_accessor :phone_numbers
	attr_accessor :hired_date
	attr_accessor :escalation_date
	attr_accessor :terminated_date
	attr_accessor :sids

	def initialize
		@first_name = random_name
		@last_name = random_name
		@title = random_title
		@location = random_location
		@department = random_department
		@hired_date = random_date

		if rand(0..100) > 75
			@escalation_date = random_date
		end

		if rand(0..100) > 75
			@terminated_date = random_date
		end

		@emails = []
		@emails << "#{@first_name}.#{@last_name}@#{random_domain}"
		@emails << "#{@first_name}-#{@last_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@first_name}_#{@last_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@first_name[0]}.#{@last_name}#{random_domain}" if rand(0..100) > 75
		@emails << "#{@first_name[0]}-#{@last_name}#{random_domain}" if rand(0..100) > 75
		@emails << "#{@first_name[0]}_#{@last_name}#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name}.#{@first_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name}-#{@first_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name}_#{@first_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name[0]}.#{@first_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name[0]}-#{@first_name}@#{random_domain}" if rand(0..100) > 75
		@emails << "#{@last_name[0]}_#{@first_name}@#{random_domain}" if rand(0..100) > 75

		@phone_numbers = []
		rand(0..4).times do
			@phone_numbers << random_phone_number
		end

		@sids = []
		rand(1..4).times do
			@sids << random_sid
		end
	end
end

db = SQLServer.new(connection_settings)

db.update("TRUNCATE TABLE UserRecord")
db.update("TRUNCATE TABLE UserAddress")
db.update("TRUNCATE TABLE UserPhoneNumber")
db.update("TRUNCATE TABLE UserSID")

next_unique_id = 10000000

last_progress = Time.now

people_records = {}

puts "Generating fake people..."
$target_user_count.times do |i|
	if (Time.now - last_progress) > 1
		puts i
		last_progress = Time.now
	end

	next_unique_id += 1
	person = FakePerson.new
	person.unique_id = next_unique_id
	person.unique_id = "#{person.unique_id}"
	people_records[person.unique_id] = person
end

db.batch_insert("INSERT INTO UserRecord (EmployeeID,Name,Title,Department,Location) VALUES (?,?,?,?,?)",1000) do |batch|
	people_records.each do |unique_id,person|
		if (Time.now - last_progress) > 1
			puts unique_id
			last_progress = Time.now
		end
		person_data = [
			person.unique_id,
			"#{person.last_name}, #{person.first_name}",
			person.title,
			person.department,
			person.location,
		]
		batch.insert(person_data)
	end
end

puts "Getting database record IDs..."
db.query("SELECT ID,EmployeeID FROM UserRecord",nil,true) do |record|
	people_records[record["EmployeeID"]].database_id = record["ID"]
end

puts "Inserting email addresses..."
db.batch_insert("INSERT INTO UserAddress (UserRecordID,Address) VALUES (?,?)",1000) do |batch|
	people_records.each do |unique_id,person|
		if (Time.now - last_progress) > 1
			puts unique_id
			last_progress = Time.now
		end
		person.emails.each do |email_address|
			batch.insert([person.database_id,email_address])
		end
	end
end

puts "Inserting phone numbers..."
db.batch_insert("INSERT INTO UserPhoneNumber (UserRecordID,PhoneNumber) VALUES (?,?)",1000) do |batch|
	people_records.each do |unique_id,person|
		if (Time.now - last_progress) > 1
			puts unique_id
			last_progress = Time.now
		end
		person.phone_numbers.each do |phone_number|
			batch.insert([person.database_id,phone_number])
		end
	end
end

puts "Inserting SIDs..."
db.batch_insert("INSERT INTO UserSID (UserRecordID,SID) VALUES (?,?)",1000) do |batch|
	people_records.each do |unique_id,person|
		if (Time.now - last_progress) > 1
			puts unique_id
			last_progress = Time.now
		end
		person.sids.each do |sid|
			batch.insert([person.database_id,sid])
		end
	end
end

puts "Done"