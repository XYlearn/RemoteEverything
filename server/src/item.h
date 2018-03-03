#ifndef _ITEM_H_
#define _ITEM_H_

#include <sys/stat.h>

typedef struct Item
{
    char *path;
    struct stat item_stat;
    struct Item *next;
} Item;

/**
 * Create a new Item with full path
 */
Item *NewItem(const char *path);

/**
 * aquire file stat. path of item must have been set
 */
int GetItemStat(Item *pItem);

/**
 * Free memory of Item
 */
void FreeItem(Item *pItem);

/**
 * check if item1 is visited later than item2
 */
int LaterThan(Item *pItem1, Item *pItem2);

#endif