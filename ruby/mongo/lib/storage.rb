module DMS

class Storage < MongoRecord::Base
  collection_name :storages
  # fields :name, :location
end

end