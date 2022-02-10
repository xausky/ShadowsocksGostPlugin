set -e
GOST_VERSION=2.11.1
GOLANG_VERSION=1.13.8
cd $( cd "$( dirname "$0"  )" && pwd  )
if [ ! -e build ]
then
mkdir build
fi
cd build
if [ ! -e go ]
then
curl "https://dl.google.com/go/go$GOLANG_VERSION.linux-amd64.tar.gz" -L -o go.tar.gz
tar -zxvf go.tar.gz
cd go
patch -p1 -r . < ../../go.patch
cd ..
fi
export PATH=$PWD/go/bin:$PATH
export GOROOT=$PWD/go
go version
if [ ! -e gost ]
then
curl "https://github.com/ginuerzh/gost/archive/v$GOST_VERSION.tar.gz" -L -o gost.tar.gz
tar -zxvf gost.tar.gz
mv gost-$GOST_VERSION gost
cd gost
patch -p1 -r . < ../../gost.patch
cd ..
fi
cd gost
echo $ANDROID_NDK_ROOT
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
