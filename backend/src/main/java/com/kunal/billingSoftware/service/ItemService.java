package com.kunal.billingSoftware.service;

import com.kunal.billingSoftware.io.ItemRequest;
import com.kunal.billingSoftware.io.ItemResponse;
import com.kunal.billingSoftware.repository.ItemRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse add(ItemRequest request, MultipartFile file);
    List<ItemResponse> fetchItems();
    void deleteItem(String itemId);
}
