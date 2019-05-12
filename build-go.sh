DIR="$( cd "$( dirname "$0"  )" && pwd  )"
export GOPATH=$DIR/go
CC=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi21-clang \
GOOS="android" GOARCH="arm" CGO_ENABLED="1" \
go build -a -o $DIR/app/src/main/jniLibs/armeabi-v7a/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android21-clang \
GOOS="android" GOARCH="arm64" CGO_ENABLED="1" \
go build -a -o $DIR/app/src/main/jniLibs/arm64-v8a/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/i686-linux-android21-clang \
GOOS="android" GOARCH="386" CGO_ENABLED="1" \
go build -a -o $DIR/app/src/main/jniLibs/x86/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost

CC=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android21-clang \
GOOS="android" GOARCH="amd64" CGO_ENABLED="1" \
go build -a -o $DIR/app/src/main/jniLibs/x86_64/libgost-plugin.so github.com/ginuerzh/gost/cmd/gost