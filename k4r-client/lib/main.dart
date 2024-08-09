import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:k4r_client/component/api_caller.dart';
import 'package:k4r_client/component/response_result.dart';
import 'package:k4r_client/pages/home_page.dart';
import 'package:k4r_client/pages/profile_page.dart';
import 'package:k4r_client/pages/sign_in_page.dart';
import 'package:k4r_client/pages/sign_up_page.dart';
import 'package:k4r_client/providers/user_session.dart';
import 'package:k4r_client/providers/logged_sate_provider.dart';
import 'package:provider/provider.dart';

import 'global.dart';

void main() {
  runApp(MultiProvider(
    providers: [
      ChangeNotifierProvider(create: (context) => UserSessionProvider()),
    ],
    child: MaterialApp.router(
      debugShowCheckedModeBanner: false,
      debugShowMaterialGrid: false,
      scaffoldMessengerKey: GlobalKeys.scaffoldMessengerKey,
      theme: ThemeData(
          primaryColor: Colors.white,
          primaryColorLight: Colors.white,
          primaryColorDark: Colors.black),
      routerConfig: GoRouter(
          routes: [
            GoRoute(path: "/", builder: (context, state) => const HomePage()),
            GoRoute(
              path: '/sign-in',
              builder: (context, state) => const SignInPage(),
            ),
            GoRoute(
                path: "/sign-up",
                builder: (context, state) => const SignUpPage()),
            GoRoute(
                path: "/profile",
                builder: (context, state) => const ProfilePage()),
          ],
          redirect: (context, state) async {
            ApiCaller apiCaller = ApiCaller();
            Response? response = await apiCaller.getSession();

            UserSession session = UserSession.fromJson(response?.data.data);
            UserSessionProvider sessionProvider =
            Provider.of<UserSessionProvider>(context,listen: false);
            sessionProvider.session = session;

            LoggedSateProvider loggedState =
            Provider.of<LoggedSateProvider>(context,listen: false);

            Set<Uri> whiteList = {Uri.parse('/sign-in'), Uri.parse('/sign-up')};
            if (!loggedState.isLoggedIn && !whiteList.contains(state.uri)) {
              print("current user has not logged,redirect to login page.");
              return '/sign-in';
            }
            return null;
          }),
    ),
  ));
}