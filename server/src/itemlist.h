#ifndef _ITEMLIST_H_
#define _ITEMLIST_H_

#include "item.h"

typedef void(*VisitItemFunc)(const char *path, const struct stat * item_stat);

typedef struct ItemListStruct
{
    struct Item * first;
    int size;
}ItemListStruct, * ItemList;

/**
 * initialize ItemList
 */
ItemList NewItemList();

/**
 * add Item to ItemList
 */
void AddItem(ItemList list, const char *path);

/**
 * sort ItemList according to last visit time
 */
void SortItemList(ItemList list);

/**
 * free all Items in ItemList
 */
void FreeItemList(ItemList list);

void ItemListForEach(ItemList list, VisitItemFunc visitFunc);

#endif