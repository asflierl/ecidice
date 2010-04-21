require 'buildr/scala'
require :artifacts

ENV['USE_FSC'] = 'yes'

define 'ecidice' do
  compile.options.optimise = true
  compile.with JME
  
  test.with SPECS
  test.include 'ecidice.CompositeSpec'
end
