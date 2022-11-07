#include "client.h"
#include "ff.h" /* Declarations of FatFs API */
#include <stdio.h>
#include <stdlib.h>

FATFS FatFs; /* FatFs work area needed for each volume */
FIL Fil; /* File object needed for each open file */

int main(int argc, char* argv[])
{
    UINT bw;
    FRESULT fr;

    int port = 6800;

    if (argc > 1) {
        port = atoi(argv[1]);
    }

    if (client_init(port) < 0) {
        return 1;
    }

    f_mount(&FatFs, "", 0); /* Give a work area to the default drive */

    /* Get volume label of the default drive */
    char str[12];
    f_getlabel("", str, 0);
    printf("Label: %s\n", str);

    fr = f_open(&Fil, "lorem.txt", FA_READ);
    if (fr == FR_OK) {
        char buf[10000] = { 0 };
        UINT n;
        fr = f_read(&Fil, buf, 4096, &n);

        if (fr == FR_OK) {
            buf[n] = 0;
            printf("Got: %s\n", buf);

            f_close(&Fil);
        }
    }

    fr = f_open(&Fil, "newfile.txt", FA_WRITE | FA_CREATE_ALWAYS); /* Create a file */
    if (fr == FR_OK) {
        char buf[10000];
        for (int i = 0; i < sizeof(buf); i++) {
            if (i % 27 == 26) {
                buf[i] = '\n';
            } else {
                buf[i] = 'A' + (i % 27);
            }
        }
        fr = f_write(&Fil, buf, sizeof(buf), &bw); /* Write data to the file */
        fr = f_close(&Fil); /* Close the file */
    }

    fr = f_open(&Fil, "newfile.txt", FA_READ);
    if (fr == FR_OK) {
        char buf[100];
        UINT n;
        fr = f_read(&Fil, buf, sizeof(buf) - 1, &n);

        if (fr == FR_OK) {
            buf[n] = 0;
            printf("Got: %s", buf);

            f_close(&Fil);
        }
    }

    client_quit();
    client_close();
}
