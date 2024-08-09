import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:k4r_client/component/api_caller.dart';
import 'package:k4r_client/providers/logged_sate_provider.dart';
import 'package:k4r_client/providers/user_session.dart';
import 'package:provider/provider.dart';

class SignInPage extends StatefulWidget {
  const SignInPage({super.key});

  @override
  SignInPageState createState() => SignInPageState();
}

class SignInPageState extends State<SignInPage> {
  final _formKey = GlobalKey<FormState>();
  late String _username;
  late String _password;
  bool loggedIn = false;

  void _submitForm() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState?.save();
      ApiCaller apiCaller = ApiCaller();
      Response? response = await apiCaller.signin(_username, _password, false);
      if (response != null) {
        print(response.data);
        setState(() {
          loggedIn = true;
          UserSessionProvider sessionProvider =
              Provider.of<UserSessionProvider>(context, listen: false);
          sessionProvider.session ??= UserSession(response.data.data);
          LoggedSateProvider loggedSateProvider =
          Provider.of<LoggedSateProvider>(context, listen: false);
          loggedSateProvider.login();

          GoRouter.of(context).go('/');
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Center(
          child: Text('登录'),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 22,horizontal: 16.0),
                child: TextFormField(
                  decoration: const InputDecoration(labelText: '用户名'),
                  validator: (value) {
                    if (value!.isEmpty) {
                      return '请输入用户名';
                    }
                    return null;
                  },
                  onFieldSubmitted: (value) =>
                      FocusScope.of(context).nextFocus(),
                  onSaved: (value) => _username = value!,
                ),
              ),
              Padding(
                  padding: const EdgeInsets.symmetric(vertical: 22,horizontal: 16.0),
                  child: TextFormField(
                    decoration: const InputDecoration(labelText: '密码'),
                    obscureText: true,
                    validator: (value) {
                      if (value!.isEmpty) {
                        return '请输入密码';
                      }
                      return null;
                    },
                    onFieldSubmitted: (value) => _submitForm(),
                    onSaved: (value) => _password = value!,
                  )),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 26.0,vertical: 22.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    MaterialButton(
                      padding: const EdgeInsets.symmetric(horizontal: 36.0),
                      color: Colors.black38,
                      onPressed: () {
                        GoRouter.of(context).go('/sign-up');
                      },
                      child: const Text('注册'),
                    ),
                    MaterialButton(
                      padding: const EdgeInsets.symmetric(horizontal: 36.0),
                      color: Colors.black38,
                      onPressed: _submitForm,
                      child: const Text('登录'),
                    ),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}