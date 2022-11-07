#include <arpa/inet.h> // inet_addr()
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h> // bzero()
#include <sys/socket.h>
#include <unistd.h> // read(), write(), close()

#include "client.h"

static int sockfd;

void client_cs_assert(void)
{
    const char buf[] = "CS ASSERT\n";
    // printf(buf);
    write(sockfd, buf, strlen(buf));
}

void client_cs_deassert(void)
{
    const char buf[] = "CS DEASSERT\n";
    write(sockfd, buf, strlen(buf));
}

int client_transfer(int val)
{
    char buf[16];
    sprintf(buf, "XFER %02X\n", val);

    write(sockfd, buf, strlen(buf));

    char in_buf[8] = { 0 };
    read(sockfd, in_buf, sizeof(in_buf) - 1);

    int n = strtol(in_buf, NULL, 16);
    return n;
}

void client_quit(void)
{
    const char buf[] = "QUIT\n";
    write(sockfd, buf, strlen(buf));
}

void client_close(void)
{
    close(sockfd);
}

int client_init(int port)
{
    struct sockaddr_in servaddr;

    // socket create and verification
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        printf("Socket creation failed.\n");
    } else {
        printf("Socket successfully created.\n");
    }

    bzero(&servaddr, sizeof(servaddr));

    // assign IP, PORT
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
    servaddr.sin_port = htons(port);

    // connect the client socket to server socket
    if (connect(sockfd, (struct sockaddr*)&servaddr, sizeof(servaddr)) != 0) {
        printf("Connection with the server failed.\n");
        close(sockfd);
        sockfd = -1;
    } else {
        printf("Connected to the server at port %d.\n", port);
    }
    return sockfd;
}
