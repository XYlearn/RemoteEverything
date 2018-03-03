#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "item.h"

Item *NewItem(const char *path)
{
    if(path == NULL)
        return NULL;
    Item *pitem = (Item *)malloc(sizeof(Item));
    if(pitem == NULL)
    {
        printf("[-] NewItem Fail\n");
        exit(0);
    }
    memset(pitem, 0, sizeof(Item));
    size_t path_size = strlen(path);
    pitem->path = (char *)malloc(path_size + 1);
    strcpy(pitem->path, path);
    if(GetItemStat(pitem) < 0)
    {
        FreeItem(pitem);
        pitem = NULL;
    }
    return pitem;
}

inline int GetItemStat(Item *pitem)
{
    return stat(pitem->path, &(pitem->item_stat));
}

inline void FreeItem(Item *pitem)
{
    if(!pitem)
        return;
    free(pitem->path);
    free(pitem);
}

inline int LaterThan(Item *pItem1, Item *pItem2)
{
    return pItem1->item_stat.st_atime > pItem2->item_stat.st_atime;
}
