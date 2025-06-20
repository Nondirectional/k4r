#!/bin/bash

# Android 应用快速构建脚本
# 使用系统安装的Java和Gradle进行打包

set -e  # 遇到错误时退出

echo "=== Android 应用构建脚本 ==="
echo "时间: $(date)"
echo ""

# 设置环境变量
export JAVA_HOME=/home/nondirectional/.sdkman/candidates/java/current
export ANDROID_HOME=/home/nondirectional/Android

echo "环境变量设置:"
echo "JAVA_HOME: $JAVA_HOME"
echo "ANDROID_HOME: $ANDROID_HOME"
echo ""

# 检查环境
echo "检查环境..."
if ! command -v gradle &> /dev/null; then
    echo "错误: Gradle 未安装或不在 PATH 中"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "错误: Java 未安装或不在 PATH 中"
    exit 1
fi

echo "✓ Gradle 版本: $(gradle --version | head -n 1)"
echo "✓ Java 版本: $(java -version 2>&1 | head -n 1)"
echo ""

# 进入项目目录
cd "$(dirname "$0")"
echo "项目目录: $(pwd)"
echo ""

# 清理项目
echo "步骤 1: 清理项目..."
gradle clean
echo "✓ 清理完成"
echo ""

# 构建类型选择
BUILD_TYPE=${1:-debug}

case $BUILD_TYPE in
    debug|Debug|DEBUG)
        echo "步骤 2: 构建 Debug 版本..."
        gradle assembleDebug
        APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
        ;;
    release|Release|RELEASE)
        echo "步骤 2: 构建 Release 版本..."
        gradle assembleRelease
        APK_PATH="app/build/outputs/apk/release/app-release.apk"
        ;;
    *)
        echo "错误: 无效的构建类型 '$BUILD_TYPE'"
        echo "用法: $0 [debug|release] [--install]"
        exit 1
        ;;
esac

echo "✓ 构建完成"
echo ""

# 检查APK文件
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo "✓ APK 文件生成成功:"
    echo "  路径: $APK_PATH"
    echo "  大小: $APK_SIZE"
    echo ""
    
    # 显示文件信息
    echo "APK 文件详细信息:"
    ls -lh "$APK_PATH"
    echo ""

    # 安装APK
    install_apk() {
        echo "步骤 3: 安装应用..."
        if ! command -v adb &> /dev/null; then
            echo "错误: adb 未找到。请确保 Android SDK platform-tools 在您的 PATH 中。"
            return 1
        fi

        # 检查是否有设备连接
        if ! adb devices | grep -q "device$"; then
            echo "错误: 未找到连接的设备或设备未授权。"
            adb devices
            return 1
        fi
        
        echo "正在将应用安装到以下设备:"
        adb devices

        echo "正在安装 $APK_PATH..."
        if adb install -r "$APK_PATH"; then
            echo "✓ 应用安装成功"
        else
            echo "✗ 应用安装失败"
            return 1
        fi
    }

    # shellcheck disable=SC2154
    if [ "$2" == "--install" ]; then
        install_apk
    else
        read -p "是否要使用 adb 安装应用? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            install_apk
        fi
    fi
    echo ""

    echo "=== 构建成功完成 ==="
    echo "APK 文件已生成: $APK_PATH"
else
    echo "错误: APK 文件未生成"
    echo "请检查构建日志以获取更多信息"
    exit 1
fi 