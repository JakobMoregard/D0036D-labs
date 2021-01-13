#include <iostream>
#include <string>
#include <thread>
#include <stdio.h>
#include <conio.h>
#include <WS2tcpip.h>
#include <winsock2.h>
#include "Lab4_client.h"

#pragma comment (lib, "ws2_32.lib")

//define program global variables
#define DEFAULT_BUFFERLENGTH 512
const int arraySize = 201;
int gameGrid[arraySize][arraySize];
int playerID = 0;


void updateServer(int x, int y, int playerID, int sequence, SOCKET connect)
{
	//creates new move event
	MoveEvent moveEvent;
	char sendBuffer[sizeof(MoveEvent)];

	//sets up some variables
	int temp;
	int xCoordinate = 0;
	int yCoordinate = 0;

	//loops through local grid and places the player on postition
	for (int i = 0; i < arraySize; i++)
	{
		for (int n = 0; n < arraySize; n++)
		{
			if (gameGrid[i][n] == playerID)
			{
				xCoordinate = i;
				yCoordinate = n;
			}
		}
	}

	//fills the move event with info about the player and position
	moveEvent.pos.x = xCoordinate + x - 100;
	moveEvent.pos.y = yCoordinate + y - 100;
	moveEvent.event.type = Move;
	moveEvent.event.head.id = playerID;
	moveEvent.event.head.length = sizeof(MoveEvent);
	moveEvent.event.head.seq_num = sequence;
	moveEvent.event.head.type = Event;

	//copies the message and sends the copy to the server
	memcpy((void*)sendBuffer, (void*)&moveEvent, sizeof(moveEvent));
	temp = send(connect, sendBuffer, sizeof(sendBuffer), 0);
}


void updateGUI(int x, int y, int color)
{
	//starts winsock
	WSADATA wsaData2;
	int winSockOK = WSAStartup(MAKEWORD(2, 2), &wsaData2);

	//check if winsock is OK
	if (winSockOK != 0)
	{
		std::cout << "Winsock not starting\n";
		return;
	}

	//sets up arguments for network connection
	sockaddr_in6 guiServer;
	guiServer.sin6_family = AF_INET6;
	guiServer.sin6_port = htons(49000);
	guiServer.sin6_flowinfo = 0;
	guiServer.sin6_scope_id = 0;

	const char* ipv6Addr = "::1"; //ipv6 address to local host

	inet_pton(AF_INET6, ipv6Addr, &guiServer.sin6_addr);
	//creates UDP socket
	SOCKET guiSocket = socket(AF_INET6, SOCK_DGRAM, 0);

	//create string to send to the GUI
	std::string guiMessage = std::to_string(x) + ":" + std::to_string(y) + ":" + std::to_string(color);

	//sends message to GUI
	int sendOK = sendto(guiSocket, guiMessage.c_str(), guiMessage.size(), 0, (sockaddr*)&guiServer, sizeof(guiServer));

	//check if send is OK
	if (sendOK == SOCKET_ERROR)
	{
		std::cout << "Error: " << WSAGetLastError() << "\n";
	}

	//close the socket and winsock
	closesocket(guiSocket);
	WSACleanup();
}


void moveSend(SOCKET connectSocket)
{
//define keybourd keys with ascii code
#define UP 72
#define DOWN 80
#define RIGHT 75
#define LEFT 77
#define LEAVE 120

	//sets up messages
	NewPlayerMsg messageNP;
	JoinMsg messageJoin;
	int messageSequence = 0;
	int temp;

	//creates two buffers
	char sendBuffer[sizeof(messageJoin)];
	char recvBuffer[DEFAULT_BUFFERLENGTH];

	//fills join message with info of player
	messageJoin.head.id = playerID;
	messageJoin.head.length = sizeof(messageJoin);
	messageJoin.head.type = Join;
	messageJoin.head.seq_num = messageSequence;

	//copies message and sends the copy
	memcpy((void*)sendBuffer, (void*)&messageJoin, sizeof(messageJoin));
	temp = send(connectSocket, sendBuffer, sizeof(sendBuffer), 0);
	messageSequence++; //increase sequence

	//sets up for receiving message with player server id
	MsgHead* messageHead = (MsgHead*)recvBuffer;
	temp = recv(connectSocket, recvBuffer, DEFAULT_BUFFERLENGTH, 0);

	//give player server id
	playerID = messageHead->id;
	std::cout << "Your player ID is: " << playerID << "\n";

	//keyboard input setup
	char key = _getch();

	while (true)
	{
		//checks for inputs
		switch (_getch())
		{
		case UP:
			//local player moves up
			updateServer(0, -1, playerID, messageSequence, connectSocket);
			messageSequence++;
			break;

		case DOWN:
			//local player moves down
			updateServer(0, 1, playerID, messageSequence, connectSocket);
			messageSequence++;
			break;

		case LEFT:
			//local player moves left
			updateServer(1, 0, playerID, messageSequence, connectSocket);
			messageSequence++;
			break;

		case RIGHT:
			//local player moves right
			updateServer(-1, 0, playerID, messageSequence, connectSocket);
			messageSequence++;
			break;

		case LEAVE:
			//local player leaves game
			std::cout << "Leaving\n";

			//sets up leave message
			LeaveMsg leaveMessage;
			char leaveBuffer[sizeof(leaveMessage)];

			//fills message with info of player leaving
			leaveMessage.head.id = playerID;
			leaveMessage.head.length = sizeof(leaveMessage);
			leaveMessage.head.type = Leave;
			leaveMessage.head.seq_num = messageSequence;

			//copies message and send the copy
			memcpy((void*)leaveBuffer, (void*)&leaveMessage, sizeof(leaveBuffer));
			temp = send(connectSocket, leaveBuffer, sizeof(leaveBuffer), 0);
			messageSequence++; //increase sequence

			exit(EXIT_FAILURE); //exit the program
			break;
		}

		key = _getch();
	}
	return;
}


void moveRecv(SOCKET connectSocket)
{
	//sets up messages
	MsgHead* messageHead;
	NewPlayerPositionMsg* messageNPP;

	while (true) //don't stop listening for messages
	{
		int messageLength = 0; //received buffer length
		char* recvBuffer = new char[DEFAULT_BUFFERLENGTH];

		//listening for messages
		std::cout << "Listening for message...\n";
		messageLength = recv(connectSocket, recvBuffer, DEFAULT_BUFFERLENGTH, 0); //saves length of buffer
		std::cout << "Message recieved\n";

		do
		{
			//sets up more messages
			messageHead = (MsgHead*)recvBuffer;
			messageNPP = (NewPlayerPositionMsg*)recvBuffer;
			ChangeMsg* messageChange = (ChangeMsg*)recvBuffer;

			//check type of message received
			switch (messageHead->type)
			{
			case 0: //join message
				std::cout << "Join\n";
				break;

			case 1: //leave message
				std::cout << "Leave\n";
				break;

			case 2: //change message
				std::cout << "Change\n";

				//check type of change message
				switch (messageChange->type)
				{
				case 0: //new player change
					std::cout << "New player: " << messageChange->head.id << "\n";
					break;

				case 1: //player left change
					std::cout << "Player left: " << messageChange->head.id << "\n";

					//loops through local grid
					for (int i = 0; i < arraySize; i++)
					{
						for (int n = 0; n < arraySize; n++)
						{
							//removes player from local grid
							if (gameGrid[i][n] == messageChange->head.id)
							{
								gameGrid[i][n] = 0;
								updateGUI(i, n, 0);
							}
						}
					}
					break;

				case 2: //player moved
					std::cout << "Player " << messageNPP->msg.head.id << " moved\n";

					//loops through local grid
					for (int i = 0; i < arraySize; i++)
					{
						for (int n = 0; n < arraySize; n++)
						{
							//removes old position
							if (gameGrid[i][n] == messageNPP->msg.head.id)
							{
								gameGrid[i][n] = 0;
							}
						}
					}
					//adds new position on local grid
					gameGrid[messageNPP->pos.x + 100][messageNPP->pos.y + 100] = messageNPP->msg.head.id;
					//update GUI with new position
					updateGUI(messageNPP->pos.x + 100, messageNPP->pos.y + 100, messageNPP->msg.head.id);
					break;

				case 3:
					std::cout << "Event\n";
					break;

				case 4:
					std::cout << "Textmessage\n";
				}

				break;
			}

			//create new temporary buffer
			char* tempBuffer = new char[DEFAULT_BUFFERLENGTH];

			//loops through length of change message
			for (int i = 0; i < messageLength - messageChange->head.length; i++)
			{
				//adds all indexes after current message indexes to temp buffer
				tempBuffer[i] = recvBuffer[i + messageChange->head.length];
			}
			recvBuffer = tempBuffer; //writes over receive buffer with rest of contents
			messageLength = messageLength - messageChange->head.length; //reduces length

		} while (messageLength > 0); //checks if received buffer contains more messages
	}
}


int main()
{
	//starts winsock
	WSADATA wsaData;
	int temp = WSAStartup(MAKEWORD(2, 2), &wsaData);

	//sets up ip address
	std::string ipAddress = "130.240.40.7";
	const char* CharIPAddress = ipAddress.c_str();

	//sets up port number
	std::string portNumber;
	std::cout << "Server port number: ";
	std::cin >> portNumber;
	const char* intPortNumber = portNumber.c_str();

	struct addrinfo* addrRes = NULL, * addrPtr = NULL, addr;

	//repert the memory
	ZeroMemory(&addr, sizeof(addr));
	addr.ai_family = AF_UNSPEC;
	addr.ai_socktype = SOCK_STREAM;
	addr.ai_protocol = IPPROTO_TCP;

	getaddrinfo(CharIPAddress, intPortNumber, &addr, &addrRes);

	addrPtr = addrRes;

	//create the socket
	SOCKET connectSocket = INVALID_SOCKET;
	connectSocket = socket(addrPtr->ai_family, addrPtr->ai_socktype, addrPtr->ai_protocol);

	//connect the socket
	temp = connect(connectSocket, addrPtr->ai_addr, (int)addrPtr->ai_addrlen);
	freeaddrinfo(addrRes);

	//checks if socket is OK
	if (connectSocket == INVALID_SOCKET)
	{
		std::cout << "Cannot connect to server\n";
		WSACleanup();
		return 1;
	}

	JoinMsg join; //new joinmessage

	int recvBufferLength = DEFAULT_BUFFERLENGTH;

	//two buffers for sending and receiving 
	char sendBuffer[sizeof(join)];
	char recvBuffer[DEFAULT_BUFFERLENGTH];

	//two threads, one for sending messages and one for receiving
	std::thread moveSend(moveSend, connectSocket);
	std::thread moveRecv(moveRecv, connectSocket);

	//to stop the program from running
	while (true)
	{
	}

	return 0;
}


