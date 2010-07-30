module DMS

class Attachment < MongoRecord::Base
  collection_name :attachments

  def initialize(storage = '_default')
    @storage = Storage.find(:first, :conditions => { :name => storage })
    raise "Cannot find storage #{storage}" unless @storage
  end
  
  def path
  end
end

end