import React, { useContext, useState } from "react";
import { AppContext } from "../../context/AppContext";
import assets from "../../assets/assets";
import toast from "react-hot-toast";
import { addItem } from "../../service/ItemService";

const ItemForm = () => {
  const { categories, setCategories, items, setItems } = useContext(AppContext);
  const [image, setImage] = useState(false);
  const [data, setData] = useState({
    name: "",
    categoryId: "",
    price: "",
    description: "",
  });
  const [loading, setLoading] = useState(false);

  const onChangeHandler = (e) => {
    const value = e.target.value;
    const name = e.target.name;
    setData((data) => ({
      ...data,
      [name]: value,
    }));
  };

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formData = new FormData();
    formData.append("item", JSON.stringify(data));
    formData.append("file", image);

    try {
      if (!image) {
        toast.error("Please select image");
        return;
      }

      const response = await addItem(formData);
      if (response.status == 201) {
        setItems([...items, response.data]);
        toast.success("Item successfully added!");

        setData({
          name: "",
          categoryId: "",
          price: "",
          description: "",
        });

        setImage(false);

        // Update the Category count
        setCategories((prevCategories) =>
          prevCategories.map((category) =>
            category.categoryId == data.categoryId
              ? { ...category, items: category.items + 1 }
              : category
          )
        );
      } else {
        toast.error("Failed to add Item!");
      }
    } catch (ex) {
      console.error(ex);
      toast.error("Error occured while adding Item! Please try again");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="item-form-container"
      style={{ height: "100vh", overflowY: "auto", overflowX: "hidden" }}
    >
      <div className="mx-2 mt-2">
        <div className="row">
          <div className="card col-md-8 form-container">
            <div className="card-body">
              <form onSubmit={onSubmitHandler}>
                <div className="mb-3">
                  <label htmlFor="image" className="form-label">
                    <img
                      src={image ? URL.createObjectURL(image) : assets.upload}
                      alt=""
                      width={48}
                    />
                  </label>
                  <input
                    type="file"
                    name="image"
                    id="image"
                    className="form-control"
                    hidden
                    onChange={(e) => setImage(e.target.files[0])}
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="name" className="form-label">
                    Name
                  </label>
                  <input
                    type="text"
                    name="name"
                    id="name"
                    className="form-control"
                    placeholder="Item Name"
                    onChange={onChangeHandler}
                    value={data.name}
                    required
                  />
                </div>
                <div className="mb3">
                  <label htmlFor="category" className="form-label">
                    Category
                  </label>
                  <select
                    name="categoryId"
                    id="categoryId"
                    className="form-control"
                    onChange={onChangeHandler}
                    value={data.categoryId}
                    required
                  >
                    <option value="" id="value">
                      -- Select Category --
                    </option>
                    {categories.map((category, index) => (
                      <option key={index} value={category.categoryId}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label htmlFor="price" className="form-label">
                    Price
                  </label>
                  <input
                    type="number"
                    name="price"
                    id="price"
                    className="form-control"
                    placeholder="Enter price (&#8377;)"
                    min={0}
                    onChange={onChangeHandler}
                    value={data.price}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="description" className="form-label">
                    Description
                  </label>
                  <textarea
                    rows="5"
                    type="text"
                    name="description"
                    id="description"
                    className="form-control"
                    placeholder="Write details here.."
                    onChange={onChangeHandler}
                    value={data.description}
                  ></textarea>
                </div>
                <button
                  type="submit"
                  className="btn btn-warning"
                  disabled={loading}
                >
                  {loading ? "Loading..." : "Submit"}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ItemForm;
