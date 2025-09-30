import React, { useContext, useEffect, useState } from "react";
import assets from "../../assets/assets";
import { addCategory } from "../../service/CategoryService";
import { AppContext } from "../../context/AppContext";
import { toast } from "react-hot-toast";

const CategoryForm = () => {
  const { categories, setCategories } = useContext(AppContext);

  const [loading, setLoading] = useState(false);
  const [image, setImage] = useState(false);
  const [data, setData] = useState({
    name: "",
    bgColor: "#ffffff",
    description: "",
  });

  useEffect(() => {
    // console.log(data);
  }, [data]);

  const onChangeHandler = (e) => {
    const value = e.target.value;
    const name = e.target.name;

    setData((data) => ({ ...data, [name]: value }));
  };

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    if (!image) {
      toast.error("Please select image for category");
      return;
    }

    setLoading(true);

    const formData = new FormData();
    formData.append("category", JSON.stringify(data));
    formData.append("file", image);

    try {
      const response = await addCategory(formData);
      if (response.status === 201) {
        setCategories([...categories, response.data]);
        toast.success("Category added successfully");
        setLoading(false);
      } else {
        toast.error("Category upload failed!");
      }
    } catch (err) {
      console.log(err);
      toast.error("Error: Category upload failed!!");
    }
  };

  return (
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
                  required
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
                  placeholder="Category Name"
                  onChange={onChangeHandler}
                  value={data.name}
                  required
                />
              </div>
              <div className="mb-3">
                <label htmlFor="name" className="form-label">
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
              <div className="mb-3">
                <label htmlFor="bgColor" className="form-label">
                  Background color
                </label>
                <br />
                <input
                  type="color"
                  name="bgColor"
                  id="bgColor"
                  placeholder="#ffffff"
                  onChange={onChangeHandler}
                  value={data.image}
                />
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
  );
};

export default CategoryForm;
