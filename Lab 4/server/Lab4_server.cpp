#include<iostream>
#include<string.h>
#include<sys/socket.h>
#include<stdio.h>
#include<arpa/inet.h>
#include "server.h"
#include<vector>
#include<pthread.h>

#define DEFAULT_BUFFERLENGTH 512

unsigned int sequence;


//method the listens for incoming messages from clients
void* inputListen(void* arg)
{
    int playerID;
    JoinMsg messageJoin;
    void* temp;
    bool runFlag = true;

    char sendBuffer[sizeof(messageJoin)];
    char recvBuffer[DEFAULT_BUFFERLENGTH];

    //parses the information and gets player id from the vector
    InfoToClient* infoTC2 = (InfoToClient*)arg;
    int socket = infoTC2->Socket;
    int* sockets = infoTC2->Sockets;
    std::vector<Player>& playersInGame = infoTC2->players;
    playerID = playersInGame.back().id;
    std::cout << "Thread created for player: " << playerID << "\n";

    //creates a received join message
    JoinMsg* recvJoined = (JoinMsg*)recvBuffer;
    recv(socket, recvJoined, DEFAULT_BUFFERLENGTH, 0);

    //fills out message information
    messageJoin.head.id = playerID;
    messageJoin.head.type = Join;
    messageJoin.head.length = sizeof(messageJoin);
    messageJoin.head.seq_num = sequence;

    //creates copy of received join message and sends the copy
    memcpy((void*)sendBuffer, (void*)&messageJoin, sizeof(messageJoin));
    send(socket, sendBuffer, sizeof(sendBuffer), MSG_NOSIGNAL);
    sequence++; //increase message sequence

    //loops through player currently in the game
    for(unsigned int i = 0; i < playersInGame.size(); i++)
    {
        //creates change message to send to old players
        ChangeMsg messageChange;
        char sendUpdateAllBuffer[sizeof(messageChange)];

        //fills message with information of the new player
        messageChange.head.id = playerID;
        messageChange.head.length = sizeof(messageChange);
        messageChange.head.seq_num = sequence;
        messageChange.head.type = Change;
        messageChange.type = NewPlayer;

        //copies message and sends the copy to older player
        memcpy((void*)sendUpdateAllBuffer, (void*)&messageChange, sizeof(messageChange));
        send(sockets[i], sendUpdateAllBuffer, sizeof(sendUpdateAllBuffer), MSG_NOSIGNAL);
        sequence++; //increase message sequence
    }

    //loops through players currently in the game
    for(unsigned int i = 0; i < playersInGame.size(); i++)
    {
        //creates change message to send to the new player
        ChangeMsg messageChange;
        char sendUpdateAllBuffer[sizeof(messageChange)];

        //fills message with information of older players
        messageChange.head.id = playersInGame[i].id;
        messageChange.head.length = sizeof(messageChange);
        messageChange.head.seq_num = sequence;
        messageChange.head.type = Change;
        messageChange.type = NewPlayer;

        //copies message and sends the copy to the new player
        memcpy((void*)sendUpdateAllBuffer, (void*)&messageChange, sizeof(messageChange));
        send(sockets[i], sendUpdateAllBuffer, sizeof(sendUpdateAllBuffer), MSG_NOSIGNAL);
        sequence++; //increase message sequence
    }

    //creates a newPlayerPosition message and fills message with player position
    NewPlayerPositionMsg messageNPP;
    messageNPP.msg.head.id = playerID;
    messageNPP.msg.head.length = sizeof(messageNPP);
    messageNPP.msg.head.seq_num = sequence;
    messageNPP.msg.head.type = Change;
    messageNPP.msg.type = NewPlayerPosition;

    //position of new player
    messageNPP.pos.x = -100;
    messageNPP.pos.y = -100;
    playersInGame[playerID - 4].coordinates.x = -100;
    playersInGame[playerID - 4].coordinates.y = -100;

    //buffer for NPP message
    char messageNPPBuffer[sizeof(messageNPP)];

    //copies message and sends the copy
    memcpy((void*)messageNPPBuffer, (void*)&messageNPP, sizeof(messageNPP));
    send(socket, messageNPPBuffer, sizeof(messageNPPBuffer), MSG_NOSIGNAL);
    sequence++; //increase message sequence

    std::cout << "Sent coordinates x: " << messageNPP.pos.x << ", and y: " << messageNPP.pos.y << ", to player: " << playerID << "\n";

    //creates a buffer for request messages
    char recvRequestBuffer[sizeof(DEFAULT_BUFFERLENGTH)];

    while(runFlag)
    {
        //prepares different messages
        MsgHead* messageHEAD = (MsgHead*)recvRequestBuffer;
        LeaveMsg* messageLeave = (LeaveMsg*)recvRequestBuffer;
        MoveEvent* moveEvent = (MoveEvent*)recvRequestBuffer;

        //receives message from players
        recv(socket, recvRequestBuffer, DEFAULT_BUFFERLENGTH, 0);

        switch(messageHEAD->type)
        {
        case 0: //join
            break;

        case 1: //leave
            //creates message for player leaving
            PlayerLeavingMsg messagePL;
            char sendLeaveUpdate[sizeof(messagePL)];

            //removes player that left from vector
            playersInGame.erase(playersInGame.begin() + messageLeave->head.id - 4);
            std::cout << "Player: " << messageLeave->head.id << ", left game\n";

            //loops through player currently in game
            for(unsigned int i = 0; i < playersInGame.size(); i++)
            {
                //fill message with information of player that left
                messagePL.msg.head.id = messageLeave->head.id;
                messagePL.msg.head.length = sizeof(messagePL);
                messagePL.msg.head.seq_num = sequence;
                messagePL.msg.head.type = Change;
                messagePL.msg.type = PlayerLeave;

                //copies message and sends the copy
                memcpy((void*)sendLeaveUpdate, (void*)&messagePL, sizeof(messagePL));
                send(sockets[i], sendLeaveUpdate, sizeof(sendLeaveUpdate), MSG_NOSIGNAL);
                sequence++; //increase message sequence
            }
            runFlag = false;
            break;

        case 2: //change
            break;

        case 3: //event
            std::cout << "New event\n";
            //creates message for player that moved and grabs new position of that player
            NewPlayerPositionMsg messageNPP2;
            int x = moveEvent->pos.x;
            int y = moveEvent->pos.y;

            //buffer for sending message
            char sendUpdate[sizeof(messageNPP2)];
            bool occupiedFlag = false; //flag if position is occupied

            //loops through players currently in the game
            for(unsigned int i = 0; i < playersInGame.size(); i++)
            {
                std::cout << x << " = " << playersInGame[i].coordinates.x << "\n";
                std::cout << y << " = " << playersInGame[i].coordinates.y << "\n";

                //checks if the position is already occupied
                if(x == playersInGame[i].coordinates.x && y == playersInGame[i].coordinates.y)
                {
                    std::cout << "Position occupied\n";
                    occupiedFlag = true;
                }
                else
                {
                    std::cout << "Position not occupied\n";
                }
            }

            if(occupiedFlag)
            {
                break;
            }

            //checks if requested position is outside the gamegrid
            if((x > 100 || y > 100) || (x < -100 || y < -100))
            {
                std::cout << "Tried to move outside the grid\n";
            }
            else
            {
                std::cout << "Player: " << playerID << ", moved to coordinate x: " << x << ", and y: " << y << "\n";
                //move was approved and players currently in the game needs to updated
                for(unsigned int i = 0; i < playersInGame.size(); i++)
                {
                    //fills message with information of player that moved with new position
                    messageNPP2.msg.head.id = playerID;
                    messageNPP2.msg.head.length = sizeof(messageNPP2);
                    messageNPP2.msg.head.seq_num = sequence;
                    messageNPP2.msg.head.type = Change;
                    messageNPP2.msg.type = NewPlayerPosition;
                    messageNPP2.pos.x = x;
                    messageNPP2.pos.y = y;

                    //updates the vector with new position
                    playersInGame[playerID - 4].coordinates.x = x;
                    playersInGame[playerID - 4].coordinates.y = y;

                    //copies message and sends copy
                    memcpy((void*)sendUpdate, (void*)&messageNPP2, sizeof(messageNPP2));
                    send(sockets[i], sendUpdate, sizeof(sendUpdate), MSG_NOSIGNAL);
                    sequence++; //increase message sequence
                }
            }
            break;
        }
    }
    return (void*)temp;
}


//main function
int main()
{
    //declares variables
    int clientSockets;
    int serverSocket;
    int bindSocket;
    int addressSize;
    int socketArray[12];
    struct sockaddr_in server, client;
    pthread_t threads[12];
    std::vector<Player> players; //vector of players

    int port = 42069; //portnumber
    sequence = 0;
    int numOfPlayers = 0;

    //sets up IPv4 arguments
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(port); //correct endian

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);

    //check if socket is OK
    if(serverSocket == -1)
    {
        std::cout << "Socket create error\n";
    }

    //bind socket to port and ip
    bindSocket = bind(serverSocket, (struct sockaddr*) & server, sizeof(server));

    //check if bind is OK
    if(bindSocket == -1)
    {
        std::cout << "Socket bind error\n";
    }

    //outputs server port
    std::cout << "Server port is: " << port << "\n";

    while(true)
    {
        //listens for tcp connection
        listen(serverSocket, 3);
        std::cout << "Listening for message\n";

        addressSize = sizeof(struct sockaddr_in);
        //accepts tcp connection, starts three-way handshake
        clientSockets = accept(serverSocket, (struct sockaddr*) & client, (socklen_t*)&addressSize);

        //checks if accept is OK
        if(clientSockets == -1)
        {
            std::cout << "Client accept error\n";
        }

        //adds that client socket to array
        socketArray[numOfPlayers] = clientSockets;

        //creates new player with information
        Player newPlayer;
        newPlayer.id = numOfPlayers + 4;
        players.push_back(newPlayer);
        std::cout << "New player, ID: " << newPlayer.id << "\n";

        InfoToClient infoTC;

        //loops through avalable player slots
        for(int i = 0; i < 12; i++)
        {
            infoTC.Sockets[i] = socketArray[i];
        }

        infoTC.players = players;
        infoTC.Socket = clientSockets;

        //creates thread for each player
        pthread_create(&threads[numOfPlayers], NULL, inputListen, &infoTC);
        std::cout << "Thread created\n";

        numOfPlayers++; //increases number of players
    }
    return 0;
}