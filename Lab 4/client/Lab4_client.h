#pragma once
#define MAXNAMELEN 32

class game
{
public:
	int init();
};


struct players
{
	int id;
};

enum ObjectDesc
{
	Human,
	NonHuman,
	Vehicle,
	StaticObject
};

enum ObjectForm
{
	Cube,
	Sphere,
	Pyramid,
	Cone
};

struct Coordinate
{
	int x;
	int y;
};

enum MsgType
{
	Join,
	Leave,
	Change,
	Event,
	TextMessage
};

struct MsgHead
{
	unsigned int length;
	unsigned int seq_num;
	unsigned int id;
	MsgType type;
};

struct JoinMsg
{
	MsgHead head;
	ObjectDesc desc;
	ObjectForm form;
	char name[MAXNAMELEN];
};

struct LeaveMsg
{
	MsgHead head;
};

enum ChangeType
{
	NewPlayer,
	PlayerLeave,
	NewPlayerPosition
};

struct ChangeMsg
{
	MsgHead head;
	ChangeType type;
};

struct NewPlayerMsg
{
	ChangeMsg msg;
	ObjectDesc desc;
	ObjectForm form;
	char name[MAXNAMELEN];
};

struct PlayerLeavingMsg
{
	ChangeMsg msg;
};

struct NewPlayerPositionMsg
{
	ChangeMsg msg;
	Coordinate pos;
	Coordinate dir;
};

enum EventType
{
	Move
};

struct EventMsg
{
	MsgHead head;
	EventType type;
};

struct MoveEvent
{
	EventMsg event;
	Coordinate pos;
	Coordinate dir;
};

struct TextMessageMsg
{
	MsgHead head;
	char text[1];
};