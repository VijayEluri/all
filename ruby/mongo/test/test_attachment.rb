require 'rubygems'
require 'mongo_record'

require 'lib/storage'
require 'lib/attachment'

require 'test/unit'

class TestAttachment < Test::Unit::TestCase

  include DMS
  
  def setup
    MongoRecord::Base.connection = XGen::Mongo::Driver::Mongo.new.db('localhost')
    storage = Storage.find(:first, :conditions => { :name => '_default' })
    storage = Storage.new(:name => '_default', :location => '/tmp', :index => 0) unless storage
    storage.save
  end
  
  def teardown
    Storage.delete_all()
  end
  
  def test_attachment
    a = Attachment.new
    p a.storage
  end
  
end