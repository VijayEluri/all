require 'rubygems'
require 'mongo_record'

require 'lib/tag'
require 'lib/document'

require 'test/unit'

class TestDocument < Test::Unit::TestCase

  include DMS
  
  def setup
    MongoRecord::Base.connection = XGen::Mongo::Driver::Mongo.new.db('localhost')

    d = Document.new(:title => 'title_tag1', :author => 'author_tag')
    d.add_tag('t1')
    d.add_tag('t2')
    d.add_tag('t2')
    d.save

    d = Document.new(:title => 'title_tag2', :author => 'author_tag')
    d.add_tag('t2')
    d.add_tag('t3')
    d.save

  end
  
  def teardown
    Document.delete_all()
    Tag.delete_all()
  end

  def test_query_tags    
    t1 = Tag.find(:first, :conditions => { :tag => 't1' })
    assert t1.tag == 't1'
    
    d = Document.find(:first, :conditions => { "tags.tag" => { '$in' => ['t1', 't2'] } } )
    assert d.title == 'title_tag1'
  end
  
  def test_tags_cloud
    js = <<'TAG_JS'
function(){
  var counts = {};
  db.documents.find().forEach(
    function(p) {
      if (p.tags) {
        for (var i=0; i<p.tags.length; i++) {
          var name = p.tags[i].tag;
          counts[name] = 1 + ( counts[name] || 0 );
        }
      }
    }
  );
  return counts;
}
TAG_JS
    
    db = MongoRecord::Base.connection    
    count = db.eval(js)
    assert count['t1'] == 1
    assert count['t2'] == 2
    assert count['t3'] == 1
  end
    
end
