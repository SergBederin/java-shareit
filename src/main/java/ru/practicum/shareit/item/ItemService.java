package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorageInMemory repositoryItem;

    public Item add(Item item) {
        repositoryItem.createItem(item);
        return item;
    }

    public Item update(Long useId, Item item, Long itemId) {
        return repositoryItem.updateItem(useId, item);
    }

    public Item getById(long id) {
        return repositoryItem.getItemById(id);
    }

    public List<Item> getAllItem(long userId) {
        return repositoryItem.getAllItemUserId(userId);
    }

    public List<Item> search(String text) {
        return repositoryItem.searchItem(text);
    }
}
