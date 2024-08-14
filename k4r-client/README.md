# k4r_client

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://docs.flutter.dev/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://docs.flutter.dev/cookbook)

For help getting started with Flutter development, view the
[online documentation](https://docs.flutter.dev/), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Sqlite

在 db/my_database.dart 文件中增加 class，运行 dart run build_runner build 命令更新 my_database.g.dart 文件，在 migration 中增加 migration 代码（记得增加 version）

int get schemaVersion => xxx;
xxx 替换成当前更新的版本，一般是递增
