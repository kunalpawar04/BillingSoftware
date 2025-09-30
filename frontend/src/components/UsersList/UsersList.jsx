import React, { useState } from "react";
import { deleteUser } from "../../service/UserService";
import toast from "react-hot-toast";

const UsersList = ({ users, setUsers }) => {
  const [searchTxt, setSearchTxt] = useState("");

  const filteredUsers = users.filter((user) =>
    user.name.toLowerCase().includes(searchTxt.toLowerCase())
  );

  const deleteByUserId = async (id) => {
    try {
      const response = await deleteUser(id);
      if (response.status == 204) {
        toast.success(`User with ID - ${id} deleted successfully`);
        setUsers((prevUsers) => prevUsers.filter((user) => user.userId !== id));
      } else {
        toast.error(`Failed to delete user with ID - ${id}`);
      }
    } catch (ex) {
      toast.error(`Error occured deleting the user with ID - ${id}`);
      console.error(ex);
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
        {filteredUsers.map((user, index) => (
          <div key={index} className="col-12">
            <div className="card p-3 bg-dark">
              <div className="d-flex align-items-center">
                <div className="flex-grow-1">
                  <h5 className="mb-1 text-white">{user.name}</h5>
                  <p className="mb-0 text-white">{user.email}</p>
                </div>
                <div>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => deleteByUserId(user.userId)}
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

export default UsersList;
