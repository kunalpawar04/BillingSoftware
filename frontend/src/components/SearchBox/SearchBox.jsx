import React, { useState } from "react";

const SearchBox = ({ onSearch }) => {
  const [searchText, setSearchText] = useState("");

  const handleInputChange = (e) => {
    const txt = e.target.value;
    setSearchText(txt);
    onSearch(txt);
  };

  return (
    <div className="input-group mb-3">
      <input
        type="text"
        className="form-control"
        placeholder="Search Items..."
        value={searchText}
        onChange={handleInputChange}
      />
      <span className="input-group-text bg-warning">
        <i className="bi bi-search"></i>
      </span>
    </div>
  );
};

export default SearchBox;
