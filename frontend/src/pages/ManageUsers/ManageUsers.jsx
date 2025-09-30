import React, { useEffect, useState } from "react";
import "./ManageUsers.css";
import UserForm from "../../components/UserForm/UserForm";
import UsersList from "../../components/UsersList/UsersList";
import { getUsers } from "../../service/UserService";
import toast from "react-hot-toast";

const ManageUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function fetchUsers() {
      try {
        setLoading(true);
        const response = await getUsers();
        setUsers(response.data);
      } catch (ex) {
        toast.error("Unable to fetch users");
        console.error(ex);
      } finally {
        setLoading(false);
      }
    }

    fetchUsers();
  }, []);

  return (
    <div className="users-container text-light">
      <div className="left-column">
        <UserForm setUsers={setUsers} />
      </div>
      <div className="right-column">
        <UsersList users={users} setUsers={setUsers} />
      </div>
    </div>
  );
};

export default ManageUsers;
