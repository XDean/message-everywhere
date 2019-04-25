import 'package:flutter/material.dart';
import 'package:english_words/english_words.dart';

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
  @override
  Widget build(BuildContext context) => new Scaffold(
        appBar: AppBar(
          title: Text('Startup Name Generator'),
        ),
        body: Text("text"),
      );
}
