# Copyright (c) 2009, Andreas Flierl
# All rights reserved.
#  
# Redistribution and use in source and binary forms, with or without 
# modification, are permitted provided that the following conditions are met:
# 
# - Redistributions of source code must retain the above copyright notice, 
#   this list of conditions and the following disclaimer.
# - Redistributions in binary form must reproduce the above copyright notice, 
#   this list of conditions and the following disclaimer in the documentation 
#  and/or other materials provided with the distribution.
# - Neither the names of the copyright holders nor the names of its 
#   contributors may be used to endorse or promote products derived from this 
#   software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
# POSSIBILITY OF SUCH DAMAGE.

repositories.remote << 'http://www.ibiblio.org/maven2/'

JME = struct(
  :audio => 'lib/jme/jme-audio.jar',
  :awt => 'lib/jme/jme-awt.jar',
  :collada => 'lib/jme/jme-collada.jar',
  :colladabinding => 'lib/jme/jme-colladabinding.jar',
  :editors => 'lib/jme/jme-editors.jar',
  :effects => 'lib/jme/jme-effects.jar',
  :font => 'lib/jme/jme-font.jar',
  :gamestates => 'lib/jme/jme-gamestates.jar',
  :model => 'lib/jme/jme-model.jar',
  :ogrexml => 'lib/jme/jme-ogrexml.jar',
  :scene => 'lib/jme/jme-scene.jar',
  :terrain => 'lib/jme/jme-terrain.jar',
  :xml => 'lib/jme/jme-xml.jar',
  :jme => 'lib/jme/jme.jar')

LWJGL = struct(
  :jinput => 'lib/lwjgl/jinput.jar',
  :applet => 'lib/lwjgl/lwjgl_util_applet.jar',
  :util => 'lib/lwjgl/lwjgl_util.jar',
  :lwjgl => 'lib/lwjgl/lwjgl.jar'
)

JORBIS = 'lib/jorbis/jorbis-0.0.17.jar'

SPECS = struct(
  :specs => 'org.scala-tools.testing:specs:jar:1.6.0',
  :mockito => 'org.mockito:mockito-all:jar:1.8.0',
  :cglib => 'cglib:cglib:jar:2.1_3',
  :asm => 'asm:asm:jar:1.5.3',
  :objenesis => 'org.objenesis:objenesis:jar:1.1',
  :hamcrest => 'org.hamcrest:hamcrest-all:jar:1.1'  
)