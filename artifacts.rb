repositories.remote << 'http://www.ibiblio.org/maven2/'
repositories.remote << 'http://nexus.scala-tools.org/content/repositories/public'

JME = %w[jme lwjgl jorbis].map { |lib| Dir["lib/#{lib}/*.jar"] } 

# All the stuff below is only necessary because this project lives on Scala's 
# edge versions.

Buildr::Scala::Scalac::REQUIRES.library = '2.8.0.RC1'
Buildr::Scala::Scalac::REQUIRES.compiler = '2.8.0.RC1'

SPECS = struct(
  :scala => 'org.scala-lang:scala-library:jar:2.8.0.RC1',
  :specs => 'org.scala-tools.testing:specs_2.8.0.RC1:jar:1.6.5-SNAPSHOT',
  :scalacheck => 'org.scala-tools.testing:scalacheck_2.8.0.RC1:jar:1.7-SNAPSHOT',
  :mockito => 'org.mockito:mockito-all:jar:1.8.4',
  :cglib => 'cglib:cglib:jar:2.1_3',
  :asm => 'asm:asm:jar:1.5.3',
  :objenesis => 'org.objenesis:objenesis:jar:1.1',
  :hamcrest => 'org.hamcrest:hamcrest-all:jar:1.1'  
)