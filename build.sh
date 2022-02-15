set -e
NDK_VERSION_IF_MISSING=r23b
GOST_VERSION=2.11.1
GOLANG_VERSION=1.17.7
cd $( cd "$( dirname "$0"  )" && pwd  )
git submodule update --init --recursive
if [ ! -e build ]
then
mkdir build
fi
cd build
if [ ! -e go ]
then
curl "https://dl.google.com/go/go$GOLANG_VERSION.linux-amd64.tar.gz" -L | tar -zx || exit $?
cd go
patch -p1 -r . < ../../go.patch
cd ..
fi
export PATH=$PWD/go/bin:$PATH
export GOROOT=$PWD/go
go version
if [ ! -e gost ] && [ -d ../gost ]
then
mv -v ../gost .
fi
IS_NDK_MISSING=true
if find $ANDROID_NDK_ROOT | grep clang$
then
IS_NDK_MISSING=false
fi
echo "IS_NDK_MISSING=$IS_NDK_MISSING"
if $IS_NDK_MISSING
then
mkdir -p ndk
cd ndk
curl https://dl.google.com/android/repository/android-ndk-${NDK_VERSION_IF_MISSING}-linux.zip -L -o ndk.zip
unzip ndk.zip > /dev/null || exit $?
rm -f ndk.zip
[ ! -d android-ndk-${NDK_VERSION_IF_MISSING} ] && echo "Missing directory: android-ndk-${NDK_VERSION_IF_MISSING}" && exit 1
export ANDROID_NDK_ROOT=$PWD/android-ndk-${NDK_VERSION_IF_MISSING}
cd ..
fi
echo "ANDROID_NDK_ROOT=$ANDROID_NDK_ROOT"
cd gost
CC=$(find $ANDROID_NDK_ROOT | grep 'armv7a-linux-androideabi21-clang$') \
GOOS="android" GOARCH="arm" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../../app/src/main/jniLibs/armeabi-v7a/libgost-plugin.so ./cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'aarch64-linux-android21-clang$') \
GOOS="android" GOARCH="arm64" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../../app/src/main/jniLibs/arm64-v8a/libgost-plugin.so ./cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'i686-linux-android21-clang$') \
GOOS="android" GOARCH="386" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../../app/src/main/jniLibs/x86/libgost-plugin.so ./cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'x86_64-linux-android21-clang$') \
GOOS="android" GOARCH="amd64" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../../app/src/main/jniLibs/x86_64/libgost-plugin.so ./cmd/gost
