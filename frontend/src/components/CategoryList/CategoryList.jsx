import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import "./CategoryList.css";
import { deleteCategory } from "../../service/CategoryService";
import toast from "react-hot-toast";

const CategoryList = () => {
  const { categories, setCategories } = useContext(AppContext);
  const [searchTxt, setSearchTxt] = useState("");

  const filteredCategories = categories.filter((category) =>
    category.name.toLowerCase().includes(searchTxt.toLowerCase())
  );

  const deleteByCategoryId = async (categoryId) => {
    try {
      const response = await deleteCategory(categoryId);
      if (response.status === 204) {
        const updatedCategories = categories.filter(
          (category) => category.categoryId !== categoryId
        );
        setCategories(updatedCategories);
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
      className="category-list-container"
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
        {filteredCategories.map((category, index) => (
          <div key={index} className="col-12">
            <div
              className="card p-3"
              style={{ backgroundColor: category.bgColor }}
            >
              <div className="d-flex align-items-center">
                <div style={{ marginRight: "15px" }}>
                  <img
                    src={`public/${category.imageUrl}`}
                    alt={category.name}
                    className="category-image"
                  />
                </div>
                <div className="flex-grow-1">
                  <h5 className="mb-1 text-white">{category.name}</h5>
                  <p className="mb-0 text-white">{category.items} Items</p>
                </div>
                <div>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => deleteByCategoryId(category.categoryId)}
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

export default CategoryList;
