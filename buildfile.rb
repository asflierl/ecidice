Buildr.settings.build['scala.version'] = '2.8.0.RC1'
repositories.remote << 'http://scala-tools.org/repo-snapshots'

require 'buildr/scala'

ENV['USE_FSC'] ||= 'yes'
JME = %w[jme lwjgl jorbis].map { |lib| Dir["lib/#{lib}/*.jar"] } 

define 'ecidice' do
  compile.options.optimise = true
  compile.with JME
  
  test.with artifacts(:scala, :specs, :scalacheck, :mockito, :cglib, :asm,
                      :objenesis, :hamcrest)
  test.include 'ecidice.CompositeSpec'
end
