require 'savon'

HTTPI.log = false

Savon.configure do |config|
  config.log = false            # disable logging
  config.log_level = :error
end

client = Savon::Client.new do
  wsdl.document = "http://192.168.20.1:8080/jira/rpc/soap/jirasoapservice-v2?wsdl"
end

response = client.request(:login) do
  soap.body = { :username => 'admin', :password => 'admin' }
end

token = response[:login_response][:login_return]

response = client.request(:getGroup) do
  soap.body = { :token => token, :groupName => 'jira-users' }
end

users_refs = response[:multi_ref][0][:users][:users]
users = response[:multi_ref][1..users_refs.size]

p users[users.index {|u| u[:@id] == "id71" }]