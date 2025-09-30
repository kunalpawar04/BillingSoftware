import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import { deleteItem } from "../../service/ItemService";
import "./itemsList.css";
import toast from "react-hot-toast";

const ItemsList = () => {
  const { items, setItems } = useContext(AppContext);
  const [searchTxt, setSearchTxt] = useState("");

  const filteredItems = items.filter((item) =>
    item.name.toLowerCase().includes(searchTxt.toLowerCase())
  );

  const deleteByItemId = async (itemId) => {
    try {
      const response = await deleteItem(itemId);
      if (response.status === 204) {
        const updatedItems = items.filter((item) => item.itemId !== itemId);
        setItems(updatedItems);
        toast.success("Category deleted");
      } else {
        toast.error("Unable to delete the category");
      }
    } catch (ex) {
      console.error(ex.message);
      toast.error(ex);
    }
  };

  return (
    <div
      className="item-list-container"
      style={{ height: "100vh", overflowY: "auto", overflowX: "hidden" }}
    >
      <div className="input-group mb-3">
        <input
          type="text"
          name="keyword"
          id="keyword"
          placeholder="Search by keyword"
          className="form-control"
          onChange={(e) => setSearchTxt(e.target.value)}
          value={searchTxt}
        />
        <span className="input-group-text bg-warning">
          <i className="bi bi-search"></i>
        </span>
      </div>
      <div className="row g-3">
        {filteredItems.map((item, index) => (
          <div key={index} className="col-12">
            <div className="card p-3 bg-dark">
              <div className="d-flex align-items-center">
                <div style={{ marginRight: "15px" }}>
                  <img
                    src={`public/${item.imageUrl}`}
                    alt={item.name}
                    className="item-image"
                  />
                </div>
                <div className="flex-grow-1">
                  <h6 className="mb-1 text-white">{item.name}</h6>
                  <p className="mb-0 text-white">
                    Category: {item.categoryName}
                  </p>
                  <span className="mb-0 text-block badge rounded-pill text-bg-warning">
                    &#8377;{item.price == "" ? 0 : item.price}
                  </span>
                </div>
                <div>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => deleteByItemId(item.itemId)}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ItemsList;
