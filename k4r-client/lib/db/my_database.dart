import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';

import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as p;

part 'my_database.g.dart';

// demo
class Users extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get name => text()();
  TextColumn get email => text()();
  TextColumn get playload => text()();
}

class AppConfigs extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get configType => text()();
  TextColumn get configValue => text()();
  TextColumn get configTarget => text()();
}

// 运行 dart run build_runner build

@DriftDatabase(tables: [Users, AppConfigs])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 3;

  @override
  MigrationStrategy get migration {
    return MigrationStrategy(
      onCreate: (m) async {
        await m.createAll();
      },
      onUpgrade: (Migrator m, int from, int to) async {
        if (from < 2) {
          await m.addColumn(users, users.playload);
        }
        if (from < 3) {
          m.createTable(appConfigs);
        }
      },
    );
  }

  static QueryExecutor _openConnection() {
    return LazyDatabase(() async {
      final dbDir = await getApplicationDocumentsDirectory();
      final file = File(p.join(dbDir.path, 'data.db'));
      return NativeDatabase(file);
    });
  }
}
