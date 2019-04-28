import 'package:flutter/material.dart';

void main() => runApp(new MyApp());

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
  bool _value = false;

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
                      decoration: InputDecoration(
                          labelText: "Server URL",
                          hintText: "http://xxx:xxx/xxx")),
                  TextFormField(
                    obscureText: true,
                    decoration: InputDecoration(
                        labelText: "Passphrase (Optional)",
                        helperText:
                            "Passphrase is to protect your message\nIt never submit to server"),
                  ),
                  Container(
                      child: new Switch(
                          value: _value,
                          onChanged: (newValue) {
                            setState(() {
                              _value = newValue;
                            });
                          },
                          inactiveThumbColor: Colors.grey,
                          inactiveTrackColor: Colors.grey,
                          activeColor: Colors.blue)),
                ],
              ),
            )));
  }
}
