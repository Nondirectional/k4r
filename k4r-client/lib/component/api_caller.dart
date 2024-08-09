import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:k4r_client/component/request_uris.dart';
import 'package:k4r_client/component/response_result.dart';
import 'package:k4r_client/component/snack_bar_reminder.dart';
import 'package:k4r_client/providers/user_session.dart';
import 'package:provider/provider.dart';

import '../global.dart';

class ApiCaller {
  static const String _host = "http://localhost:8080";
  static final ApiCaller _instance = ApiCaller._internal();
  late Dio _dio;

  ApiCaller._internal() {
    _dio = Dio(BaseOptions(
      baseUrl: _host,
      connectTimeout: const Duration(seconds: 5),
      receiveTimeout: const Duration(seconds: 5),
    ));
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          // 动态获取AccessToken
          final context = options.extra['context'] != null
              ? options.extra['context'] as BuildContext
              : null;

          if (context != null) {
            final sessionProvider =
            Provider.of<UserSessionProvider>(context, listen: false);
            // 将AccessToken添加到请求头中
            options.headers['Access-Token'] =
                sessionProvider.session?.accessToken;
          }

          return handler.next(options); // 继续请求
        },
          onResponse: (response, handler) {
            Result? result = parseResponse(response);

            response.data = result;
            if (result.errorCode != 0) {
              SnackBarReminder.showSnackBarRemindByScaffoldMessengerState(
                  GlobalKeys.scaffoldMessengerKey,
                  RemindType.error,
                  "${result.message}",
                  const Duration(milliseconds: 1500));
              handler.reject(
                  DioException(requestOptions: response.requestOptions));
            } else {
              handler.next(response);
            }
          },
          onError: (DioException err, handler) {
            // 处理错误
            switch (err.type) {
              case DioExceptionType.badResponse:
              // 根据状态码处理
                switch (err.response?.statusCode) {
                  case 400:
                    SnackBarReminder.showSnackBarRemindByScaffoldMessengerState(
                        GlobalKeys.scaffoldMessengerKey,
                        RemindType.error,
                        "Request fail,message: ${err.response
                            ?.data['message']}",
                        const Duration(milliseconds: 1500));
                    print("Request fail,message: ${err.response
                        ?.data['message']}");
                    break;
                // 其他状态码...
                  default:
                    SnackBarReminder.showSnackBarRemindByScaffoldMessengerState(
                        GlobalKeys.scaffoldMessengerKey,
                        RemindType.error,
                        "Request fail,message: $err.message",
                        const Duration(milliseconds: 1500));
                    print("Request fail,message: ${err.response
                        ?.data['message']}");
                }
                break;
              default:
              // 默认错误处理
                SnackBarReminder.showSnackBarRemindByScaffoldMessengerState(
                    GlobalKeys.scaffoldMessengerKey,
                    RemindType.error,
                    "Request fail,message: $err.message",
                    const Duration(milliseconds: 1500));
                print("Request fail,message: ${err.response?.data['message']}");
                break;
            }

            if (err.response != null) {
              var response = err.response!;
              Result? result = parseResponse(response.data);
              if (result.errorCode != 0) {
                SnackBarReminder.showSnackBarRemindByScaffoldMessengerState(
                    GlobalKeys.scaffoldMessengerKey,
                    RemindType.error,
                    "Request fail,message: ${result.message}",
                    const Duration(milliseconds: 1500));
              }
              handler.resolve(err.response!);
            } else {
              handler.next(err);
            }
          }
      ),
    );
  }

  factory ApiCaller([BuildContext? context]) {
    if (context != null) {
      _instance._dio.options.extra = {'context': context};
    }
    return _instance;
  }

  /// 登录
  Future<Response>? signin(
      String identifier, String password, bool neverExpire) {
    return _dio.post(RequestUris.signin.uri,
        data: {"identifier": identifier, "password": password});
  }

  /// 注册
  Future<Response>? signup(
      String username, String nickname, String password, String email) {
    return _dio.post(RequestUris.signup.uri, data: {
      "username": username,
      "password": password,
      "nickname": nickname,
      "email": email
    });
  }

  /// 获取用户信息
  Future<Response>? getProfile() {
    return _dio.get(RequestUris.getProfile.uri);
  }

  /// 更新用户信息
  Future<Response>? updateProfile(FormData formData) {
    return _dio.put(RequestUris.updateProfile.uri, data: formData);
  }

  /// 获取Session信息
  Future<Response>? getSession() {
    return _dio.get(
        RequestUris.getSession.uri);
  }
}
