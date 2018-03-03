#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "item.h"
#include "itemlist.h"

ItemList NewItemList()
{
    ItemList list = (ItemList)malloc(sizeof(ItemListStruct));
    if(list == NULL)
    {
        printf("[-] NewItemList Fail\n");
        exit(0);
    }
    memset(list, 0, sizeof(ItemListStruct));
    return list;
}

void AddItem(ItemList list, const char *path)
{
    Item *pItem = list->first;
    Item *pNewItem = NewItem(path);
    if(pNewItem == NULL)
    {
        printf("[-] AddItem Fail\n");
        exit(0);
    }
    if (!pItem)
        list->first = pNewItem;
    else
    {
        pNewItem->next = pItem->next;
        pItem->next = pNewItem;
    }
}

/**
 * QuickSort swap
 */
void swap(Item *pItem1, Item *pItem2)
{
    char *tempStr = pItem2->path;
    pItem2->path = pItem1->path;
    pItem1->path = tempStr;
    struct stat tempStat = pItem2->item_stat;
    pItem2->item_stat = pItem1->item_stat;
    pItem1->item_stat = tempStat;
}

/**
 * QuickSort GetPartition
 */
Item *GetPartition(Item *pBegin, Item *pEnd)
{
    Item tempItem = *pBegin;
    Item *p = pBegin;
    Item *q = p->next;
    while (q != pEnd)
    {
        if (LaterThan(q, &tempItem))
        {
            p = p->next;
            swap(p, q);
        }
        q = q->next;
    }
    swap(p, pBegin);
    return p;
}

/**
 * QuickSort
 */
void QuickSort(Item *pBegin, Item *pEnd)
{
    if (pBegin != pEnd)
    {
        Item *partition = GetPartition(pBegin, pEnd);
        QuickSort(pBegin, partition);
        if (partition->next)
            QuickSort(partition->next, pEnd);
    }
}

void SortItemList(ItemList list)
{
    QuickSort(list->first, NULL);
}

void FreeNextItem(Item *pItem)
{
    Item *pNextItem = pItem->next;
    pItem->next = pNextItem->next;
    FreeItem(pNextItem);
}

void FreeItemList(ItemList list)
{
    Item *pItem = list->first;
    if(pItem)
    {
        while (pItem->next)
            FreeNextItem(pItem);
        FreeItem(pItem);
    }
    free(list);
}

void ItemListForEach(ItemList list, VisitItemFunc visitFunc)
{
    Item *pItem = list->first;
    while(pItem)
    {
        visitFunc(pItem->path, &pItem->item_stat);
        pItem = pItem->next;
    }
}