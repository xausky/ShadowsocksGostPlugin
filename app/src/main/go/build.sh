cd $( cd "$( dirname "$0"  )" && pwd  )
CC=$(find $ANDROID_NDK_ROOT | grep 'armv7a-linux-androideabi21-clang$') \
GOOS="android" GOARCH="arm" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../jniLibs/armeabi-v7a/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'aarch64-linux-android21-clang$') \
GOOS="android" GOARCH="arm64" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../jniLibs/arm64-v8a/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'i686-linux-android21-clang$') \
GOOS="android" GOARCH="386" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../jniLibs/x86/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$(find $ANDROID_NDK_ROOT | grep 'x86_64-linux-android21-clang$') \
GOOS="android" GOARCH="amd64" CGO_ENABLED="1" \
go build -ldflags "-s -w" -a -o ../jniLibs/x86_64/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost