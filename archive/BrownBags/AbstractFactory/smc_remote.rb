class SMCRemote
  def initialize(factory)
    @factory = factory
  end
  
  def selectLanguage(language)  
    case language
      when "C++"
        @generator = @factory.createCpp
      when "Java"
        @generator = @factory.createJava
    end
  end
  
  def generator
    @generator
  end
end