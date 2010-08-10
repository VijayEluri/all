#!/usr/bin/env ruby

require 'fileutils'

MoinData = '/Users/hu/wiki/moin/wikiserver.app/Contents/Resources/wiki/data/pages'

FileUtils.mkdir_p 'text'

class String
  def gsub1(pattern, str)
    old = self
    result = self.gsub(pattern, str)
    result.nil? ? old : result
  end
end

def processTitle(lines)
  pattern = /^(=+)(.+?)=+/
  lines.gsub!(pattern) do |match|
    "\nh#{$1.length}\. #{$2.strip}\n"
  end
end

def processList(lines)
  pattern = /^( +)\* (.*)/
  lines.gsub!(pattern) do |match|
    "#{'*' * $1.length } #{$2}"
  end
end

def processOrderedList(lines)
  pattern = /^( +)\d+\. (.*)/
  lines.gsub!(pattern) do |match|
    "#{'#' * $1.length } #{$2}"
  end
end

def processNote(lines)
  pattern = /^\{\{\{#!wiki.*?\'\'\'.*?\'\'\'(.*?)^\}\}\}/m
  lines.gsub!(pattern) do |match|
    "\n??#{$1.strip}??\n"
  end
end

def processPre(lines)
  pattern = /^\{\{\{(.*?)^\}\}\}/m
  lines.gsub!(pattern) do |match|
    "\npre.. #{$1}\np. \n"
  end
end

def processComments(lines)
  pattern = /^##.*/
  lines.gsub!(pattern) do |match|
    ""
  end
end

def processPage(pageDir)
  latestPage = Dir["#{pageDir}/revisions/*"].sort.pop
  return if latestPage.nil?
  pageName = File.basename(pageDir).gsub1(/\(20\)/, ' ').gsub1(/\(2e\)/, '.').gsub1(/\(2d\)/, '-')
  # print "Processing \"#{pageName}\" ...\n"
  open(latestPage) { |f| @lines = f.readlines.join }
  
  processComments(@lines)
  processTitle(@lines)
  processList(@lines)
  processOrderedList(@lines)
  processNote(@lines)
  processPre(@lines)
  
  open("text/#{pageName}.text", 'w') { |f| f << @lines }
end

Dir["#{MoinData}/*"].each do |entry|
  Next unless FileTest.directory?(entry)
  processPage(entry)
end

require 'compile'