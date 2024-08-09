import 'package:flutter/widgets.dart';

class UserSessionProvider extends ChangeNotifier {
  UserSession? _userSession;

  UserSession? get session => _userSession;

  set session(UserSession? value) {
    _userSession = value;
    notifyListeners();
  }

  void clear() {
    _userSession = null;
    notifyListeners();
  }
}

class UserSession {
  int? userId;
  String? username;
  String? nickname;
  String? accessToken;

  UserSession(this.accessToken, [this.userId, this.username, this.nickname]);

  static UserSession fromJson(Map<String, dynamic> json) {
    String accessToken = json['accessToken'];
    int? userId = json['userId'];
    String? username = json['username'];
    String? nickname = json['nickname'];
    UserSession userSession =
        UserSession(accessToken, userId, username, nickname);
    return userSession;
  }
}
