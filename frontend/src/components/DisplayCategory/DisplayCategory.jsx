import React from "react";
import "./DisplayCategory.css";
import Category from "../Category/Category";
import assets from "../../assets/assets";

const DisplayCategory = ({
  categories,
  selectedCategory,
  setSelectedCategory,
}) => {
  return (
    <div className="row g-3" style={{ width: "100%", margin: 0 }}>
      <div
        key="all"
        className="col-md-3 col-sm-6"
        style={{ padding: "0 10px" }}
      >
        <Category
          categoryName="All Items"
          imageUrl={assets.SelectAllicon}
          numberOfItems={categories.reduce((total, category) => {
            total + category.items;
          }, 0)}
          bgColor="#6c757d"
          isSelected={selectedCategory === ""}
          onClick={() => setSelectedCategory("")}
        />
      </div>

      {categories.map((category) => (
        <div
          key={category.categoryId}
          className="col-md-3 col-sm-6"
          style={{ padding: "0 10px" }}
        >
          <Category
            categoryName={category.name}
            imageUrl={category.imageUrl}
            numberOfItems={category.items}
            bgColor={category.bgColor}
            isSelected={selectedCategory == category.categoryId}
            onClick={() => setSelectedCategory(category.categoryId)}
          />
        </div>
      ))}
    </div>
  );
};

export default DisplayCategory;
