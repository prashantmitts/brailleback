Points to be noted :-
use older java ie jdk 8 not jdk 9
use ndk version not later than 13b
simply follow the instructions for the betterment of you :-


* Make sure to have a copy of brltty, version 4.5 checked out.  This can be
  checked out from subversion as follows (run from within the eyes-free
  directory as per above):
  $ svn checkout svn://mielke.cc//releases/brltty-4.5 \
    braille/service/jni/brlttywrapper/brltty

* Make sure to have a copy of liblouis, version 2.6.0 checked out.  This can 
  be checked out from Github as follows 
  (run under directory braille/service/jni/liblouiswrapper/liblouis):

  $ git clone --branch v2.6.0 https://github.com/liblouis/liblouis.git

* Apply patches to the dependencies:
  $ (cd braille/service/jni/brlttywrapper && patch -p1 < brltty.patch)
  $ (cd braille/service/jni/liblouiswrapper && patch -p1 < liblouis.patch)

* Update the local ant property files:
  $ android update project -p libraries/utils -t android-18
  $ android update project -p libraries/compatutils -t android-18
  $ android update project -p braille/client -t android-18
  $ android update project -p braille/service -t android-18
  $ android update project -p braille/brailleback -t android-18

* Build the native libraries:
  $ (cd braille/service && ndk-build -j16)

* Build the BrailleBack apk:
  $ (cd braille/brailleback && ant debug)

* Alternatively, build and install on a connected device:
  $ (cd braille/brailleback && ant debug install)

