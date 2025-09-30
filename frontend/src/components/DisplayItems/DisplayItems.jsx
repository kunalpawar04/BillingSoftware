import React, { useContext, useState } from "react";
import "./DisplayItems.css";
import { AppContext } from "../../context/AppContext";
import Item from "../Item/Item";
import SearchBox from "../SearchBox/SearchBox";

const DisplayItems = ({ selectedCategory }) => {
  const { items } = useContext(AppContext);
  const [searchTxt, setSearchTxt] = useState("");

  const filteredItems = items
    .filter((item) => {
      if (!selectedCategory) return true;
      return item.categoryId == selectedCategory;
    })
    .filter((item) =>
      item.name.toLowerCase().includes(searchTxt.toLowerCase())
    );

  return (
    <div className="p-3">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div></div>
        <div>
          <SearchBox onSearch={setSearchTxt}></SearchBox>
        </div>
      </div>
      <div className="row g-3">
        {filteredItems.map((item, index) => (
          <div key={index} className="col-md-4 col-sm-6">
            <Item
              itemName={item.name}
              itemPrice={item.price}
              itemImage={item.imageUrl}
              itemId={item.itemId}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default DisplayItems;
