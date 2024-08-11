import 'package:flutter/material.dart';
import 'package:k4r_client/component/home_drawer.dart';
import 'package:timeline_list/timeline.dart';
import 'package:timeline_list/timeline_model.dart';

class HomePage extends StatefulWidget {
  final String appBarTitle = 'HOME';
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.appBarTitle),
        actions: [IconButton(onPressed: () {}, icon: const Icon(Icons.search))],
      ),
      drawer: const HomeDrawer(),
      body: RecordList(),
      floatingActionButton: FloatingActionButton(
          child: const Icon(Icons.add),
          onPressed: () {
            showDialog(
                context: context,
                builder: (context) {
                  return AlertDialog(
                    title: Text("xxx"),
                  );
                });
          }),
    );
  }
}

class RecordList extends StatefulWidget {
  RecordList({super.key});

  @override
  State<RecordList> createState() => _RecordListState();
}

class _RecordListState extends State<RecordList> {
  GlobalKey _timelineKey = GlobalKey();
  List<TimelineModel> items = [];
  ScrollController _timelineController = ScrollController();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    // 请求数据

    // 构造数据，默认10条
    for (var i = 0; i < 10; i++) {
      // items
      items.add(TimelineModel(
          RecordCard(
            data: {
              'title': "Record $i",
              'subTitle': "subTitle 今晚聚众干饭，太好吃了，干了八大碗，减肥计划又要延迟了。"
            },
          ),
          position: TimelineItemPosition.right,
          iconBackground: const Color.fromARGB(255, 82, 255, 226),
          icon: const Icon(Icons.face_4)));
    }

    // scrollController
    _timelineController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_isLoading) return;

    print(_timelineController.position);

    if (_timelineController.position.pixels ==
        _timelineController.position.maxScrollExtent) {
      _loadMore();
      _isLoading = true;
    }
  }

  Future<void> _loadMore() async {
    print('到底了，加载数据中...');
    await Future.delayed(const Duration(seconds: 1));
    setState(() {
      var len = items.length;
      for (var i = len; i < len + 10; i++) {
        // items
        items.add(TimelineModel(
            RecordCard(
              data: {
                'title': "Record $i",
                'subTitle': "subTitle 今晚又聚众干饭，太好吃了，干了八大碗，减肥计划又要延迟了。"
              },
            ),
            position: TimelineItemPosition.right,
            iconBackground: const Color.fromARGB(255, 58, 203, 255),
            icon: const Icon(Icons.access_alarms_rounded)));
      }
      print(items.length);
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Timeline(
        controller: _timelineController,
        iconSize: 16,
        position: TimelinePosition.Left,
        children: items);
  }
}

class RecordCard extends StatefulWidget {
  final Map data;
  const RecordCard({super.key, required this.data});

  @override
  State<RecordCard> createState() => _RecordCardState();
}

class _RecordCardState extends State<RecordCard> {
  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 10,
      margin: const EdgeInsets.all(8),
      child: Column(
        children: [
          ListTile(
            leading: widget.data['leading'] ?? widget.data['leading'],
            title: Text(widget.data['title']),
            subtitle: Text(widget.data['subTitle']),
          )
        ],
      ),
    );
  }
}
