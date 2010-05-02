repositories.remote << 'http://scala-tools.org/repo-snapshots'

require 'buildr/scala'

ENV['USE_FSC'] ||= 'yes'
ENV['JAVA_OPTS'] ||= '-Xmx1024m -Xms128m -Xss1m' # more memory for the scala compiler

JME = %w[jme lwjgl jorbis].map { |lib| Dir["lib/#{lib}/*.jar"] } 

define 'ecidice' do
  compile.options.optimise = true
  compile.with JME
  
  test.with artifacts(:specs, :scalacheck, :mockito, :cglib, :asm,
                      :objenesis, :hamcrest)
  test.include 'ecidice.CompositeSpec'
end
