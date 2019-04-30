import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:isolate';

void main() async {
  runApp(new MyApp());
}

class MyApp extends StatelessWidget {
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Message Everywhere',
      home: new Settings(),
    );
  }
}

class Settings extends StatefulWidget {
  @override
  createState() => new SettingsState();
}

class SettingsState extends State<Settings> {
  static const platform = const MethodChannel("message-everywhere-fetch");

  String _url = "";
  String _encode = "";
  bool _run = false;
  SendPort _port;

  @override
  Widget build(BuildContext context) {
    Size screenSize = MediaQuery.of(context).size;
    return new Scaffold(
        appBar: AppBar(
          title: Text('Message Everywhere'),
        ),
        body: Container(
            padding: new EdgeInsets.all(25.0),
            child: Form(
              child: ListView(
                children: [
                  TextFormField(
                      keyboardType: TextInputType.url,
                      onSaved: (s) => _url = s,
                      decoration: InputDecoration(
                          labelText: "Server URL",
                          hintText: "http://xxx:xxx/xxx")),
                  TextFormField(
                    obscureText: true,
                    onSaved: (s) => _encode = s,
                    decoration: InputDecoration(
                        labelText: "Passphrase (Optional)",
                        helperText:
                            "Passphrase is to protect your message\nIt never submit to server"),
                  ),
                  Container(
                      child: new Switch(
                          value: _run,
                          onChanged: (newValue) {
                            setState(() {
                              _run = newValue;
                              run();
                            });
                          },
                          inactiveThumbColor: Colors.grey,
                          inactiveTrackColor: Colors.grey,
                          activeColor: Colors.blue)),
                ],
              ),
            )));
  }

  void run() async {
    if (_run && _port == null) {
      print('To run message fetch');
      var receivePort = ReceivePort();
      receivePort.listen((msg) {
        _port = msg as SendPort;
        if (_run == false) {
          _port.send(true);
        }
      });
//      AndroidAlarmManager.oneShot(Duration(milliseconds: 500), 99,
//              () => runFetchMsg(_url, _encode, receivePort.sendPort))
//          .catchError((e) => {print(e)})
//          .then((bool) {
//        print('run $bool');
//      });
    } else if (_port != null) {
      print('To stop message fetch');
      _port.send(true);
    }
  }
}

//void runFetchMsg(String url, String encode, SendPort sendPort) {
//  print('Init sms receiver');
//  SmsReceiver receiver = SmsReceiver();
//  var listen = receiver.onSmsReceived.listen((SmsMessage msg) {
//    print(msg.body);
//  });
//  var receivePort = ReceivePort();
//  receivePort.listen((msg) {
//    print('Cancel task');
//    listen.cancel();
//  });
//  sendPort.send(receivePort.sendPort);
//  print('Send port');
//}
