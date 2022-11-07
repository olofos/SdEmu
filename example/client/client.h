#ifndef CLIENT_H_
#define CLIENT_H_

void client_cs_assert(void);
void client_cs_deassert(void);
int client_transfer(int val);
void client_quit(void);
void client_close(void);
int client_init(int port);

#endif
