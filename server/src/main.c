#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>
#include <unistd.h>

#include "item.h"
#include "itemlist.h"

#define MAX_ITEMS 2000

/**
 * exec locate to print filepaths containing part_name
 */
ItemList Locate(char *part_name, int max_item);

/**
 * Print ItemName
 */
void PrintItem(const char *path, const struct stat *item_stat);

/**
 * Check if a string starts with head_str
 */
int StartWith(const char *str, const char *head_str);

/**
 * if string doesn't corespond to rule, return 0, else return 1
 */
int Filt(const char *str);

void Printf(const char *format, ...)
{
    va_list var_arg;
    va_start(var_arg, format);
    vfprintf(stdout, format, var_arg);
    fflush(stdout);
    va_end(var_arg);
}

/**
 * main function 
 */
int main(int argc, char *argv[])
{
    ItemList list = NULL;
    while(1)
    {
        char buf[4096];
        fgets(buf, sizeof(buf), stdin);
        /* commands are started with # */
        if(buf[0] == '#')
        {
            /* exit command
            if(StartWith(buf, "#exit"))
                break;
            */

            /* print previous results*/
            if(StartWith(buf, "#prev"))
                ItemListForEach(list, PrintItem);
            /* print welcome */
            else if(StartWith(buf, "#msc"))
                Printf("#Welcom to MSC!\n");
            else    
                FreeItemList(list);
        }
        /* locate cmd */
        else
        {
            list = Locate(buf, MAX_ITEMS);
            /* sort according to access time */
            SortItemList(list);
            ItemListForEach(list, PrintItem);
            Printf("#\n");
        }
    }
    return 0;
}


ItemList Locate(char *part_name, int max_item)
{
    char buf[4096];
    
    sprintf(buf, "locate --ignore-case --limit %d ", max_item);
    // vulnerable
    strcat(buf, part_name);
    FILE *pipe = popen(buf, "r");
    if(pipe == NULL)
    {
        Printf("[-] can't exec locate\n");
        exit(0);
    }

    ItemList list = NewItemList();
    // get items(less than max_item)
    while(fgets(buf, sizeof(buf), pipe))
    {
        buf[strlen(buf) - 1] = '\0';
        if(!Filt(buf))
            continue;
        AddItem(list, buf);
        buf[0] = '\0';
    }
    pclose(pipe);
    return list;
}

void PrintItem(const char *path, const struct stat *item_stat)
{
    if(item_stat->st_uid != 0)
        Printf("%s\n", path);
    fflush(stdout);
}

int StartWith(const char *str, const char *head_str)
{
    int length = strlen(head_str);
    for(int i = 0; i < length; i++)
    {
        if(str[i] == head_str[i])
            continue;
        else
            return 0;
    }
    return 1;
}

int Filt(const char *str)
{
    if(StartWith(str, "/home/"))
        return 1;
    else
        return 0;
}
