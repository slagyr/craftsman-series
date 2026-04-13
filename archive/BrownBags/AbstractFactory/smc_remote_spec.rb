require 'rubygems'
gem 'rspec'
require 'spec'

require 'generator_factory'
require 'smc_remote'

describe SMCRemote do
  before do
    @factory = GeneratorFactory.new
    @smc = SMCRemote.new(@factory);
  end
  
  it "should create a JavaGenerator when passed 'java'" do
    @smc.selectLanguage("Java")
    @smc.generator.class.name.should == "JavaGenerator"
  end
  
  it "should create a CppGenreator when pass 'C++'" do
    @smc.selectLanguage("C++") 
    @smc.generator.class.name.should == "CppGenerator"
  end
end