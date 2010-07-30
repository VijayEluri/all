module DMS
  
class Document < MongoRecord::Base
  collection_name :documents
  # fields :title, :author, :created_at, :updated_at, :created_by, :updated_by
  # has_many :tags, :class_name => 'DMS::Tag'
  # has_many :attachments, :class_name => 'DMS::Attachment'
  
  def add_tag(value)
    tag = Tag.find(:first, :conditions => {:tag => value})
    if tag.nil?
      tag = Tag.new(:tag => value)
      tag.save
    end
    @tags ||= []
    @tags << Tag.new(:tag => value) unless @tags.collect{ |t| t.tag }.include?(value)
  end  
end

end