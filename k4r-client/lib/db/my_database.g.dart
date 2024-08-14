// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_database.dart';

// ignore_for_file: type=lint
class $UsersTable extends Users with TableInfo<$UsersTable, User> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $UsersTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<int> id = GeneratedColumn<int>(
      'id', aliasedName, false,
      hasAutoIncrement: true,
      type: DriftSqlType.int,
      requiredDuringInsert: false,
      defaultConstraints:
          GeneratedColumn.constraintIsAlways('PRIMARY KEY AUTOINCREMENT'));
  static const VerificationMeta _nameMeta = const VerificationMeta('name');
  @override
  late final GeneratedColumn<String> name = GeneratedColumn<String>(
      'name', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  static const VerificationMeta _emailMeta = const VerificationMeta('email');
  @override
  late final GeneratedColumn<String> email = GeneratedColumn<String>(
      'email', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  static const VerificationMeta _playloadMeta =
      const VerificationMeta('playload');
  @override
  late final GeneratedColumn<String> playload = GeneratedColumn<String>(
      'playload', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  @override
  List<GeneratedColumn> get $columns => [id, name, email, playload];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'users';
  @override
  VerificationContext validateIntegrity(Insertable<User> instance,
      {bool isInserting = false}) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    }
    if (data.containsKey('name')) {
      context.handle(
          _nameMeta, name.isAcceptableOrUnknown(data['name']!, _nameMeta));
    } else if (isInserting) {
      context.missing(_nameMeta);
    }
    if (data.containsKey('email')) {
      context.handle(
          _emailMeta, email.isAcceptableOrUnknown(data['email']!, _emailMeta));
    } else if (isInserting) {
      context.missing(_emailMeta);
    }
    if (data.containsKey('playload')) {
      context.handle(_playloadMeta,
          playload.isAcceptableOrUnknown(data['playload']!, _playloadMeta));
    } else if (isInserting) {
      context.missing(_playloadMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  User map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return User(
      id: attachedDatabase.typeMapping
          .read(DriftSqlType.int, data['${effectivePrefix}id'])!,
      name: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}name'])!,
      email: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}email'])!,
      playload: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}playload'])!,
    );
  }

  @override
  $UsersTable createAlias(String alias) {
    return $UsersTable(attachedDatabase, alias);
  }
}

class User extends DataClass implements Insertable<User> {
  final int id;
  final String name;
  final String email;
  final String playload;
  const User(
      {required this.id,
      required this.name,
      required this.email,
      required this.playload});
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<int>(id);
    map['name'] = Variable<String>(name);
    map['email'] = Variable<String>(email);
    map['playload'] = Variable<String>(playload);
    return map;
  }

  UsersCompanion toCompanion(bool nullToAbsent) {
    return UsersCompanion(
      id: Value(id),
      name: Value(name),
      email: Value(email),
      playload: Value(playload),
    );
  }

  factory User.fromJson(Map<String, dynamic> json,
      {ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return User(
      id: serializer.fromJson<int>(json['id']),
      name: serializer.fromJson<String>(json['name']),
      email: serializer.fromJson<String>(json['email']),
      playload: serializer.fromJson<String>(json['playload']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<int>(id),
      'name': serializer.toJson<String>(name),
      'email': serializer.toJson<String>(email),
      'playload': serializer.toJson<String>(playload),
    };
  }

  User copyWith({int? id, String? name, String? email, String? playload}) =>
      User(
        id: id ?? this.id,
        name: name ?? this.name,
        email: email ?? this.email,
        playload: playload ?? this.playload,
      );
  User copyWithCompanion(UsersCompanion data) {
    return User(
      id: data.id.present ? data.id.value : this.id,
      name: data.name.present ? data.name.value : this.name,
      email: data.email.present ? data.email.value : this.email,
      playload: data.playload.present ? data.playload.value : this.playload,
    );
  }

  @override
  String toString() {
    return (StringBuffer('User(')
          ..write('id: $id, ')
          ..write('name: $name, ')
          ..write('email: $email, ')
          ..write('playload: $playload')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(id, name, email, playload);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is User &&
          other.id == this.id &&
          other.name == this.name &&
          other.email == this.email &&
          other.playload == this.playload);
}

class UsersCompanion extends UpdateCompanion<User> {
  final Value<int> id;
  final Value<String> name;
  final Value<String> email;
  final Value<String> playload;
  const UsersCompanion({
    this.id = const Value.absent(),
    this.name = const Value.absent(),
    this.email = const Value.absent(),
    this.playload = const Value.absent(),
  });
  UsersCompanion.insert({
    this.id = const Value.absent(),
    required String name,
    required String email,
    required String playload,
  })  : name = Value(name),
        email = Value(email),
        playload = Value(playload);
  static Insertable<User> custom({
    Expression<int>? id,
    Expression<String>? name,
    Expression<String>? email,
    Expression<String>? playload,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (name != null) 'name': name,
      if (email != null) 'email': email,
      if (playload != null) 'playload': playload,
    });
  }

  UsersCompanion copyWith(
      {Value<int>? id,
      Value<String>? name,
      Value<String>? email,
      Value<String>? playload}) {
    return UsersCompanion(
      id: id ?? this.id,
      name: name ?? this.name,
      email: email ?? this.email,
      playload: playload ?? this.playload,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<int>(id.value);
    }
    if (name.present) {
      map['name'] = Variable<String>(name.value);
    }
    if (email.present) {
      map['email'] = Variable<String>(email.value);
    }
    if (playload.present) {
      map['playload'] = Variable<String>(playload.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('UsersCompanion(')
          ..write('id: $id, ')
          ..write('name: $name, ')
          ..write('email: $email, ')
          ..write('playload: $playload')
          ..write(')'))
        .toString();
  }
}

class $AppConfigsTable extends AppConfigs
    with TableInfo<$AppConfigsTable, AppConfig> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $AppConfigsTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<int> id = GeneratedColumn<int>(
      'id', aliasedName, false,
      hasAutoIncrement: true,
      type: DriftSqlType.int,
      requiredDuringInsert: false,
      defaultConstraints:
          GeneratedColumn.constraintIsAlways('PRIMARY KEY AUTOINCREMENT'));
  static const VerificationMeta _configTypeMeta =
      const VerificationMeta('configType');
  @override
  late final GeneratedColumn<String> configType = GeneratedColumn<String>(
      'config_type', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  static const VerificationMeta _configValueMeta =
      const VerificationMeta('configValue');
  @override
  late final GeneratedColumn<String> configValue = GeneratedColumn<String>(
      'config_value', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  static const VerificationMeta _configTargetMeta =
      const VerificationMeta('configTarget');
  @override
  late final GeneratedColumn<String> configTarget = GeneratedColumn<String>(
      'config_target', aliasedName, false,
      type: DriftSqlType.string, requiredDuringInsert: true);
  @override
  List<GeneratedColumn> get $columns =>
      [id, configType, configValue, configTarget];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'app_configs';
  @override
  VerificationContext validateIntegrity(Insertable<AppConfig> instance,
      {bool isInserting = false}) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    }
    if (data.containsKey('config_type')) {
      context.handle(
          _configTypeMeta,
          configType.isAcceptableOrUnknown(
              data['config_type']!, _configTypeMeta));
    } else if (isInserting) {
      context.missing(_configTypeMeta);
    }
    if (data.containsKey('config_value')) {
      context.handle(
          _configValueMeta,
          configValue.isAcceptableOrUnknown(
              data['config_value']!, _configValueMeta));
    } else if (isInserting) {
      context.missing(_configValueMeta);
    }
    if (data.containsKey('config_target')) {
      context.handle(
          _configTargetMeta,
          configTarget.isAcceptableOrUnknown(
              data['config_target']!, _configTargetMeta));
    } else if (isInserting) {
      context.missing(_configTargetMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  AppConfig map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return AppConfig(
      id: attachedDatabase.typeMapping
          .read(DriftSqlType.int, data['${effectivePrefix}id'])!,
      configType: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}config_type'])!,
      configValue: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}config_value'])!,
      configTarget: attachedDatabase.typeMapping
          .read(DriftSqlType.string, data['${effectivePrefix}config_target'])!,
    );
  }

  @override
  $AppConfigsTable createAlias(String alias) {
    return $AppConfigsTable(attachedDatabase, alias);
  }
}

class AppConfig extends DataClass implements Insertable<AppConfig> {
  final int id;
  final String configType;
  final String configValue;
  final String configTarget;
  const AppConfig(
      {required this.id,
      required this.configType,
      required this.configValue,
      required this.configTarget});
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<int>(id);
    map['config_type'] = Variable<String>(configType);
    map['config_value'] = Variable<String>(configValue);
    map['config_target'] = Variable<String>(configTarget);
    return map;
  }

  AppConfigsCompanion toCompanion(bool nullToAbsent) {
    return AppConfigsCompanion(
      id: Value(id),
      configType: Value(configType),
      configValue: Value(configValue),
      configTarget: Value(configTarget),
    );
  }

  factory AppConfig.fromJson(Map<String, dynamic> json,
      {ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return AppConfig(
      id: serializer.fromJson<int>(json['id']),
      configType: serializer.fromJson<String>(json['configType']),
      configValue: serializer.fromJson<String>(json['configValue']),
      configTarget: serializer.fromJson<String>(json['configTarget']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<int>(id),
      'configType': serializer.toJson<String>(configType),
      'configValue': serializer.toJson<String>(configValue),
      'configTarget': serializer.toJson<String>(configTarget),
    };
  }

  AppConfig copyWith(
          {int? id,
          String? configType,
          String? configValue,
          String? configTarget}) =>
      AppConfig(
        id: id ?? this.id,
        configType: configType ?? this.configType,
        configValue: configValue ?? this.configValue,
        configTarget: configTarget ?? this.configTarget,
      );
  AppConfig copyWithCompanion(AppConfigsCompanion data) {
    return AppConfig(
      id: data.id.present ? data.id.value : this.id,
      configType:
          data.configType.present ? data.configType.value : this.configType,
      configValue:
          data.configValue.present ? data.configValue.value : this.configValue,
      configTarget: data.configTarget.present
          ? data.configTarget.value
          : this.configTarget,
    );
  }

  @override
  String toString() {
    return (StringBuffer('AppConfig(')
          ..write('id: $id, ')
          ..write('configType: $configType, ')
          ..write('configValue: $configValue, ')
          ..write('configTarget: $configTarget')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(id, configType, configValue, configTarget);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is AppConfig &&
          other.id == this.id &&
          other.configType == this.configType &&
          other.configValue == this.configValue &&
          other.configTarget == this.configTarget);
}

class AppConfigsCompanion extends UpdateCompanion<AppConfig> {
  final Value<int> id;
  final Value<String> configType;
  final Value<String> configValue;
  final Value<String> configTarget;
  const AppConfigsCompanion({
    this.id = const Value.absent(),
    this.configType = const Value.absent(),
    this.configValue = const Value.absent(),
    this.configTarget = const Value.absent(),
  });
  AppConfigsCompanion.insert({
    this.id = const Value.absent(),
    required String configType,
    required String configValue,
    required String configTarget,
  })  : configType = Value(configType),
        configValue = Value(configValue),
        configTarget = Value(configTarget);
  static Insertable<AppConfig> custom({
    Expression<int>? id,
    Expression<String>? configType,
    Expression<String>? configValue,
    Expression<String>? configTarget,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (configType != null) 'config_type': configType,
      if (configValue != null) 'config_value': configValue,
      if (configTarget != null) 'config_target': configTarget,
    });
  }

  AppConfigsCompanion copyWith(
      {Value<int>? id,
      Value<String>? configType,
      Value<String>? configValue,
      Value<String>? configTarget}) {
    return AppConfigsCompanion(
      id: id ?? this.id,
      configType: configType ?? this.configType,
      configValue: configValue ?? this.configValue,
      configTarget: configTarget ?? this.configTarget,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<int>(id.value);
    }
    if (configType.present) {
      map['config_type'] = Variable<String>(configType.value);
    }
    if (configValue.present) {
      map['config_value'] = Variable<String>(configValue.value);
    }
    if (configTarget.present) {
      map['config_target'] = Variable<String>(configTarget.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('AppConfigsCompanion(')
          ..write('id: $id, ')
          ..write('configType: $configType, ')
          ..write('configValue: $configValue, ')
          ..write('configTarget: $configTarget')
          ..write(')'))
        .toString();
  }
}

abstract class _$AppDatabase extends GeneratedDatabase {
  _$AppDatabase(QueryExecutor e) : super(e);
  $AppDatabaseManager get managers => $AppDatabaseManager(this);
  late final $UsersTable users = $UsersTable(this);
  late final $AppConfigsTable appConfigs = $AppConfigsTable(this);
  @override
  Iterable<TableInfo<Table, Object?>> get allTables =>
      allSchemaEntities.whereType<TableInfo<Table, Object?>>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [users, appConfigs];
}

typedef $$UsersTableCreateCompanionBuilder = UsersCompanion Function({
  Value<int> id,
  required String name,
  required String email,
  required String playload,
});
typedef $$UsersTableUpdateCompanionBuilder = UsersCompanion Function({
  Value<int> id,
  Value<String> name,
  Value<String> email,
  Value<String> playload,
});

class $$UsersTableTableManager extends RootTableManager<
    _$AppDatabase,
    $UsersTable,
    User,
    $$UsersTableFilterComposer,
    $$UsersTableOrderingComposer,
    $$UsersTableCreateCompanionBuilder,
    $$UsersTableUpdateCompanionBuilder> {
  $$UsersTableTableManager(_$AppDatabase db, $UsersTable table)
      : super(TableManagerState(
          db: db,
          table: table,
          filteringComposer:
              $$UsersTableFilterComposer(ComposerState(db, table)),
          orderingComposer:
              $$UsersTableOrderingComposer(ComposerState(db, table)),
          updateCompanionCallback: ({
            Value<int> id = const Value.absent(),
            Value<String> name = const Value.absent(),
            Value<String> email = const Value.absent(),
            Value<String> playload = const Value.absent(),
          }) =>
              UsersCompanion(
            id: id,
            name: name,
            email: email,
            playload: playload,
          ),
          createCompanionCallback: ({
            Value<int> id = const Value.absent(),
            required String name,
            required String email,
            required String playload,
          }) =>
              UsersCompanion.insert(
            id: id,
            name: name,
            email: email,
            playload: playload,
          ),
        ));
}

class $$UsersTableFilterComposer
    extends FilterComposer<_$AppDatabase, $UsersTable> {
  $$UsersTableFilterComposer(super.$state);
  ColumnFilters<int> get id => $state.composableBuilder(
      column: $state.table.id,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get name => $state.composableBuilder(
      column: $state.table.name,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get email => $state.composableBuilder(
      column: $state.table.email,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get playload => $state.composableBuilder(
      column: $state.table.playload,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));
}

class $$UsersTableOrderingComposer
    extends OrderingComposer<_$AppDatabase, $UsersTable> {
  $$UsersTableOrderingComposer(super.$state);
  ColumnOrderings<int> get id => $state.composableBuilder(
      column: $state.table.id,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get name => $state.composableBuilder(
      column: $state.table.name,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get email => $state.composableBuilder(
      column: $state.table.email,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get playload => $state.composableBuilder(
      column: $state.table.playload,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));
}

typedef $$AppConfigsTableCreateCompanionBuilder = AppConfigsCompanion Function({
  Value<int> id,
  required String configType,
  required String configValue,
  required String configTarget,
});
typedef $$AppConfigsTableUpdateCompanionBuilder = AppConfigsCompanion Function({
  Value<int> id,
  Value<String> configType,
  Value<String> configValue,
  Value<String> configTarget,
});

class $$AppConfigsTableTableManager extends RootTableManager<
    _$AppDatabase,
    $AppConfigsTable,
    AppConfig,
    $$AppConfigsTableFilterComposer,
    $$AppConfigsTableOrderingComposer,
    $$AppConfigsTableCreateCompanionBuilder,
    $$AppConfigsTableUpdateCompanionBuilder> {
  $$AppConfigsTableTableManager(_$AppDatabase db, $AppConfigsTable table)
      : super(TableManagerState(
          db: db,
          table: table,
          filteringComposer:
              $$AppConfigsTableFilterComposer(ComposerState(db, table)),
          orderingComposer:
              $$AppConfigsTableOrderingComposer(ComposerState(db, table)),
          updateCompanionCallback: ({
            Value<int> id = const Value.absent(),
            Value<String> configType = const Value.absent(),
            Value<String> configValue = const Value.absent(),
            Value<String> configTarget = const Value.absent(),
          }) =>
              AppConfigsCompanion(
            id: id,
            configType: configType,
            configValue: configValue,
            configTarget: configTarget,
          ),
          createCompanionCallback: ({
            Value<int> id = const Value.absent(),
            required String configType,
            required String configValue,
            required String configTarget,
          }) =>
              AppConfigsCompanion.insert(
            id: id,
            configType: configType,
            configValue: configValue,
            configTarget: configTarget,
          ),
        ));
}

class $$AppConfigsTableFilterComposer
    extends FilterComposer<_$AppDatabase, $AppConfigsTable> {
  $$AppConfigsTableFilterComposer(super.$state);
  ColumnFilters<int> get id => $state.composableBuilder(
      column: $state.table.id,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get configType => $state.composableBuilder(
      column: $state.table.configType,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get configValue => $state.composableBuilder(
      column: $state.table.configValue,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));

  ColumnFilters<String> get configTarget => $state.composableBuilder(
      column: $state.table.configTarget,
      builder: (column, joinBuilders) =>
          ColumnFilters(column, joinBuilders: joinBuilders));
}

class $$AppConfigsTableOrderingComposer
    extends OrderingComposer<_$AppDatabase, $AppConfigsTable> {
  $$AppConfigsTableOrderingComposer(super.$state);
  ColumnOrderings<int> get id => $state.composableBuilder(
      column: $state.table.id,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get configType => $state.composableBuilder(
      column: $state.table.configType,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get configValue => $state.composableBuilder(
      column: $state.table.configValue,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));

  ColumnOrderings<String> get configTarget => $state.composableBuilder(
      column: $state.table.configTarget,
      builder: (column, joinBuilders) =>
          ColumnOrderings(column, joinBuilders: joinBuilders));
}

class $AppDatabaseManager {
  final _$AppDatabase _db;
  $AppDatabaseManager(this._db);
  $$UsersTableTableManager get users =>
      $$UsersTableTableManager(_db, _db.users);
  $$AppConfigsTableTableManager get appConfigs =>
      $$AppConfigsTableTableManager(_db, _db.appConfigs);
}
