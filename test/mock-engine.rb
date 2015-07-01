# The purpose of this app is to return an example XML Feed Display result so that it's possible to
# do some kinds of simple testing without a running Engine instance.

require 'sinatra'

filename = ARGV[0] || "sample-output.xml" 

if not File.exist? filename
  puts "Add the path to a file containing the response to return as an argument or create sample-output.xml in this directory."
  exit
end

set :mock, File.open(filename, 'r').read

get '/*' do
  settings.mock
end

post '/*' do
 settings.mock
end