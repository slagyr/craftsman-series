require 'rubygems'
gem 'rspec'
require 'spec'

require 'generator_factory'

describe GeneratorFactory do
  before do
    @factory = GeneratorFactory.new
  end
  
  it "should return a CppGenerator if createCpp is called" do
    generator = @factory.createCpp
    generator.class.name.should == "CppGenerator"
  end
  
  it "should return a JavaGenerator if createJava is called" do
    generator = @factory.createJava
    generator.class.name.should == "JavaGenerator"
  end
  
end
