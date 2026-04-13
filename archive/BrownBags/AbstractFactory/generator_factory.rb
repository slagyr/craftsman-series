require 'cpp_generator'
require 'java_generator'

class GeneratorFactory
  def createCpp
    return CppGenerator.new
  end
  
  def createJava
    return JavaGenerator.new
  end
end
