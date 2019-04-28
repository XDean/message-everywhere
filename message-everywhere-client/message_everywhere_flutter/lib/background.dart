import 'dart:async';
import 'dart:isolate';

String url = "";
String encode = "";

main() async {
  final response = ReceivePort();
  Isolate.spawn(run, response.sendPort);
}

void run(SendPort sendPort) {
}
