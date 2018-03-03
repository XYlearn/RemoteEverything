#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>
#include <unistd.h>

#include "item.h"
#include "itemlist.h"

#define MAX_ITEMS 200

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
        if(buf[0] == '#')
        {
            if(StartWith(buf, "#exit"))
                break;
            else if(StartWith(buf, "#prev"))
                ItemListForEach(list, PrintItem);
            else if(StartWith(buf, "#msc"))
                Printf("Welcom to MSC!\n");
            else    
                FreeItemList(list);
        }
        else
        {
            list = Locate(buf, MAX_ITEMS);
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
    strcpy(buf, "locate -i ");
    strcat(buf, part_name);
    FILE *pipe = popen(buf, "r");
    if(pipe == NULL)
    {
        Printf("[-] can't exec locate\n");
        exit(0);
    }

    ItemList list = NewItemList();
    int item_num = 0;
    // get items(less than max_item)
    while(fgets(buf, sizeof(buf), pipe) && item_num < max_item)
    {
        int path_length = strlen(buf) - 1;
        buf[path_length] = '\0';
        if(!Filt(buf))
            continue;
        AddItem(list, buf);
        item_num++;
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